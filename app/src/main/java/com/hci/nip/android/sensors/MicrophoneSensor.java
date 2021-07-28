package com.hci.nip.android.sensors;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.util.FileUtil;
import com.hci.nip.android.util.NetworkUtils;
import com.hci.nip.base.model.DeviceStatus;
import com.hci.nip.base.sensor.Microphone;
import com.hci.nip.base.sensor.SensorLocation;
import com.hci.nip.base.sensor.SensorType;
import com.hci.nip.base.sensor.model.AudioRecordInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MicrophoneSensor implements Microphone {

    private static final String TAG = MicrophoneSensor.class.getName();

    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    private static final int SAMPLING_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLING_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private final String id;
    private final AudioRecorder audioRecorder;
    private final AudioStreamer audioStreamer;

    public MicrophoneSensor(String id) {
        this.id = id;
        audioRecorder = new AudioRecorder();
        audioStreamer = new AudioStreamer();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_MICROPHONE;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.microphone";
    }

    @Override
    public SensorLocation getLocation() {
        return SensorLocation.SENSOR_LOCATION_HEAD;
    }

    @Override
    public long getSampleRate() {
        return SAMPLING_RATE;
    }

    @Override
    public long getBufferSize() {
        return BUFFER_SIZE;
    }

    @Override
    public String getResolution() {
        return "";
    }

    @Override
    public String getDataFormat() {
        return "";
    }

    @Override
    public void open() {
        // DO NOTHING
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void activate() {
        // DO NOTHING
    }

    @Override
    public void deactivate() {
        AudioRecordInfo currentRecordingInfo = getRecordingInfo();
        if (isStatusRunningOrPaused(currentRecordingInfo.getStatus())) {
            stopRecording(currentRecordingInfo);
        }

        AudioRecordInfo currentStreamingInfo = getLiveStreamingInfo();
        if (isStatusRunningOrPaused(currentStreamingInfo.getStatus())) {
            stopLiveStreaming(currentStreamingInfo);
        }
    }

    private static boolean isStatusRunningOrPaused(DeviceStatus status) {
        return DeviceStatus.RUNNING == status || DeviceStatus.PAUSED == status;
    }

    @Override
    public void close() {
        deactivate();
    }

    @Override
    public List<?> readData() {
        return Collections.EMPTY_LIST;
    }


    @Override
    public void startRecording(AudioRecordInfo audio) {
        Log.v(TAG, "[AUDIO RECORD] start:" + audio);
        audioRecorder.startRecording(audio);
    }

    @Override
    public void pauseRecording(AudioRecordInfo audio) {
        Log.v(TAG, "[AUDIO RECORD] pause:" + audio);
        audioRecorder.pauseRecording(audio);
    }

    @Override
    public void stopRecording(AudioRecordInfo audio) {
        Log.v(TAG, "[AUDIO RECORD] stop:" + audio);
        audioRecorder.stopRecording(audio);
    }

    @Override
    public AudioRecordInfo getRecordingInfo() {
        Log.v(TAG, "[AUDIO RECORD] get");
        return audioRecorder.getRecordingInfo();
    }


    @Override
    public void startLiveStreaming(AudioRecordInfo audio) {
        Log.v(TAG, "[AUDIO STREAM] start:" + audio);
        audioStreamer.startLiveStreaming(audio);
    }

    @Override
    public void stopLiveStreaming(AudioRecordInfo audio) {
        Log.v(TAG, "[AUDIO STREAM] stop:" + audio);
        audioStreamer.stopLiveStreaming(audio);
    }

    @Override
    public AudioRecordInfo getLiveStreamingInfo() {
        Log.v(TAG, "[AUDIO STREAM] get");
        return audioStreamer.getLiveStreamingInfo();
    }


    private static class AudioRecorder {
        private MediaRecorder recorder;
        private volatile DeviceStatus recorderStatus;
        private volatile AudioRecordInfo recorderAudioInfo;

        AudioRecorder() {
            recorder = null;
            recorderStatus = DeviceStatus.IDLE;
            recorderAudioInfo = new AudioRecordInfo("0");
        }

        private AudioRecordInfo getRecorderAudioInfo() {
            return recorderAudioInfo;
        }

        private void updateRecorderAudioInfo(AudioRecordInfo recorderAudioInfo) {
            setRecorderAudioInfo(new AudioRecordInfo(recorderAudioInfo));
        }

        private void setRecorderAudioInfo(AudioRecordInfo recorderAudioInfo) {
            this.recorderAudioInfo = recorderAudioInfo;
        }

        private DeviceStatus getRecorderStatus() {
            return recorderStatus;
        }

        private void setRecorderStatus(DeviceStatus recorderStatus) {
            this.recorderStatus = recorderStatus;
            setRecorderAudioInfo(getRecorderAudioInfo().setStatus(getRecorderStatus()));
        }

        void startRecording(AudioRecordInfo audio) {
            switch (getRecorderStatus()) {
                case PAUSED:
                    validateNewAudioInfo(audio);
                    resumePausedRecorder();
                    break;
                case IDLE:
                case STOPPED:
                    String destination = audio.getDest();
                    validateRecorderDestinationFile(destination);
                    try {
                        startNewRecording(audio);
                    } catch (IOException e) {
                        Log.e(TAG, "[AUDIO RECORD] starting", e);
                        throw new MicrophoneException(ErrorCodes.AUDIO_RECORD_FAILED, "Failed to start recording", e);
                    }
                    break;
                default:
                    validateRecorderInState(DeviceStatus.IDLE);
            }
        }

        private void validateRecorderDestinationFile(String fileName) {
            boolean isAudioFile = FileUtil.isAudioFile(fileName);
            if (!isAudioFile) {
                throw new MicrophoneException(ErrorCodes.UNSUPPORTED_AUDIO_FORMAT, fileName + " is not supported format");
            }
        }

        private void startNewRecording(AudioRecordInfo newAudioInfo) throws IOException {
            setRecorderStatus(DeviceStatus.RUNNING);

            String destination = newAudioInfo.getDest();
            try {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                recorder.setOutputFile(FileUtil.getAbsoluteFilePath(destination));
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                recorder.prepare();
                recorder.start();

                Log.v(TAG, "[RECORDER] starting");

                updateRecorderAudioInfo(newAudioInfo);
            } catch (IOException e) {
                setRecorderStatus(DeviceStatus.IDLE);
                recorder = null;
                throw e;
            }
        }

        private void resumePausedRecorder() {
            setRecorderStatus(DeviceStatus.RUNNING);
            recorder.start();
            Log.d(TAG, "[RECORDER] resuming");
        }

        private void pausePlayingRecorder() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setRecorderStatus(DeviceStatus.PAUSED);
                recorder.pause();
                Log.d(TAG, "[RECORDER] pausing");
            }
        }

        private void stopRecorder() {
            setRecorderStatus(DeviceStatus.STOPPED);
            recorder.stop();
            recorder.release();
            recorder = null;
            Log.d(TAG, "[RECORDER] stopping");
        }

        private void validateNewAudioInfo(AudioRecordInfo audio) {
            String expectedId = getRecorderAudioInfo().getId();
            String receivedId = audio.getId();
            if (!expectedId.equals(receivedId)) {
                throw new MicrophoneException(ErrorCodes.AUDIO_ID_MISMATCH, "Expect:" + expectedId + ", received:" + receivedId);
            }
        }

        private void validateRecorderInState(DeviceStatus expected) {
            if (getRecorderStatus() != expected) {
                throw new MicrophoneException(ErrorCodes.UNSUPPORTED_AUDIO_FORMAT, "Recorder is in " + getRecorderStatus());
            }
        }

        void pauseRecording(AudioRecordInfo audio) {
            validateRecorderInState(DeviceStatus.RUNNING);
            validateNewAudioInfo(audio);
            pausePlayingRecorder();
        }

        void stopRecording(AudioRecordInfo audio) {
            switch (getRecorderStatus()) {
                case RUNNING:
                case PAUSED:
                    validateNewAudioInfo(audio);
                    stopRecorder();
                    break;
                default:
                    validateRecorderInState(DeviceStatus.RUNNING);
            }
        }

        AudioRecordInfo getRecordingInfo() {
            return new AudioRecordInfo(recorderAudioInfo);
        }
    }

    /**
     * Since the streaming task is asynchronous and continuous process, if failure occurs it will not notify the failure directly
     * <p>
     * Ref: https://stackoverflow.com/questions/15349987/stream-live-android-audio-to-server
     * NOTE: https://www.vuzix.com/Developer/KnowledgeBase/Detail/1073
     */
    private static class AudioStreamer implements AudioStreamTaskListener {
        private volatile AudioRecordInfo streamAudioInfo;
        private final AtomicBoolean isAudioStreamingActive;
        private AudioStreamTask audioStreamTask;

        AudioStreamer() {
            streamAudioInfo = new AudioRecordInfo("0").setStream(true);
            isAudioStreamingActive = new AtomicBoolean(false);
            audioStreamTask = null;
        }

        private AudioRecordInfo getStreamAudioInfo() {
            return streamAudioInfo;
        }

        private void setStreamAudioInfo(AudioRecordInfo streamAudioInfo) {
            this.streamAudioInfo = streamAudioInfo;
        }

        private void updateStreamAudioInfo(AudioRecordInfo streamAudioInfo) {
            setStreamAudioInfo(new AudioRecordInfo(streamAudioInfo));
        }

        void startLiveStreaming(AudioRecordInfo audio) {
            validateStreamDestination(audio.getDest());
            if (isAudioStreamingActive.compareAndSet(false, true)) {
                updateStreamAudioInfo(audio);
                audioStreamTask = getNewAudioStreamTask(audio, this);
                new Thread(audioStreamTask).start();
            } else {
                throw new MicrophoneException(ErrorCodes.AUDIO_OPERATION_AT_ILLEGAL_STATE, "Stream service is already running");
            }
        }

        void stopLiveStreaming(AudioRecordInfo audio) {
            validateNewAudioInfo(audio);
            if (isAudioStreamingActive.compareAndSet(true, false)) {
                setStreamAudioInfo(getStreamAudioInfo().setStatus(DeviceStatus.STOPPED));
                audioStreamTask.stop();
            } else {
                throw new MicrophoneException(ErrorCodes.AUDIO_OPERATION_AT_ILLEGAL_STATE, "Stream service is not running");
            }
        }

        private static AudioStreamTask getNewAudioStreamTask(AudioRecordInfo newAudioInfo, AudioStreamTaskListener listener) {
            String dest = newAudioInfo.getDest();
            String ipAddress = NetworkUtils.getIpAddress(dest);
            int port = NetworkUtils.getPort(dest);

            return new AudioStreamTask(getNewAudioRecord(newAudioInfo), ipAddress, port, listener);
        }


        private static AudioRecord getNewAudioRecord(AudioRecordInfo newAudioInfo) {
            // TODO: update according to 'newAudioInfo'
            return new AudioRecord(AUDIO_SOURCE, SAMPLING_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
        }

        private void validateStreamDestination(String destination) {
            if (!NetworkUtils.isIpAddressWithPort(destination)) {
                throw new MicrophoneException(ErrorCodes.UNSUPPORTED_AUDIO_FORMAT, destination + "is not a supported format");
            }
        }

        private void validateNewAudioInfo(AudioRecordInfo audio) {
            String expectedId = getStreamAudioInfo().getId();
            String receivedId = audio.getId();
            if (!expectedId.equals(receivedId)) {
                throw new MicrophoneException(ErrorCodes.AUDIO_ID_MISMATCH, "Expect:" + expectedId + ", received:" + receivedId);
            }
        }

        AudioRecordInfo getLiveStreamingInfo() {
            return new AudioRecordInfo(streamAudioInfo);
        }

        @Override
        public void onError(Exception e) {
            // DO NOTHING
            Log.i(TAG, "[STREAMING] error");
        }

        @Override
        public void onStart() {
            setStreamAudioInfo(getStreamAudioInfo().setStatus(DeviceStatus.RUNNING));
            Log.d(TAG, "[STREAMING] starting");
        }

        @Override
        public void onStop() {
            setStreamAudioInfo(getStreamAudioInfo().setStatus(DeviceStatus.STOPPED));
            // FIXME: This can create race conditions
            if (audioStreamTask != null && !audioStreamTask.isRunning()) {
                isAudioStreamingActive.set(false);
            }
            Log.d(TAG, "[STREAMING] stopping");
        }
    }

    private interface AudioStreamTaskListener {
        void onError(Exception e);

        void onStart();

        void onStop();
    }

    private static class AudioStreamTask implements Runnable {

        private final AudioRecord recorder;
        private final int bufferSize;
        private final int destPort;
        private final String destIP;
        private final AtomicBoolean recordStatus;
        private final AudioStreamTaskListener listener;

        AudioStreamTask(AudioRecord recorder, String destIP, int destPort, AudioStreamTaskListener listener) {
            this.recorder = recorder;
            this.bufferSize = AudioRecord.getMinBufferSize(recorder.getSampleRate(), recorder.getChannelConfiguration(), recorder.getAudioFormat());
            this.destIP = destIP;
            this.destPort = destPort;
            this.listener = listener;

            this.recordStatus = new AtomicBoolean(true);
        }

        @Override
        public void run() {
            try {
                Log.d(TAG, "[AUDIO STREAM TASK] starting");
                recorder.startRecording();

                byte[] buffer = new byte[bufferSize];
                DatagramSocket socket = new DatagramSocket();

                InetAddress destination = InetAddress.getByName(destIP);

                DatagramPacket packet;

                listener.onStart();
                while (recordStatus.get()) {
                    // read from MIC to buffer
                    int readSize = recorder.read(buffer, 0, buffer.length);
                    // send via socket
                    packet = new DatagramPacket(buffer, readSize, destination, destPort);
                    socket.send(packet);
                }
            } catch (IOException e) {
                Log.e(TAG, "[AUDIO STREAM TASK] failure", e);
                listener.onError(e);
            } finally {
                Log.d(TAG, "[AUDIO STREAM TASK] stopping");
                recordStatus.set(false);
                recorder.release();
                listener.onStop();
            }
        }

        void stop() {
            recordStatus.set(false);
        }

        boolean isRunning() {
            return recordStatus.get();
        }
    }
}
