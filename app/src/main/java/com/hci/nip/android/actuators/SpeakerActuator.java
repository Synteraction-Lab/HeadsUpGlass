package com.hci.nip.android.actuators;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.util.FileUtil;
import com.hci.nip.base.actuator.ActuatorLocation;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.actuator.Speaker;
import com.hci.nip.base.actuator.model.AudioPlayInfo;
import com.hci.nip.base.model.DeviceStatus;

import java.io.IOException;
import java.util.List;

public class SpeakerActuator implements Speaker {

    private static final String TAG = SpeakerActuator.class.getName();

    private final String id;
    private final AudioPlayer audioPlayer;

    public SpeakerActuator(String id) {
        this.id = id;
        this.audioPlayer = new AudioPlayer();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ActuatorType getType() {
        return ActuatorType.ACTUATOR_TYPE_SPEAKER;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.speaker";
    }

    @Override
    public ActuatorLocation getLocation() {
        return ActuatorLocation.ACTUATOR_LOCATION_HEAD;
    }

    @Override
    public String getResolution() {
        return "";
    }

    @Override
    public String getDataFormat() {
        return "FileURL";
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
        AudioPlayInfo currentPlayingInfo = getPlayingInfo();
        if (isStatusRunningOrPaused(currentPlayingInfo.getStatus())) {
            stopPlaying(currentPlayingInfo);
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
    public boolean processData(List<?> data) {
        // TODO: implement
        return false;
    }

    @Override
    public void startPlaying(AudioPlayInfo audio) throws SpeakerException {
        Log.v(TAG, "[AUDIO PLAY] start:" + audio);
        audioPlayer.startPlaying(audio);
    }

    @Override
    public void pausePlaying(AudioPlayInfo audio) throws SpeakerException {
        Log.v(TAG, "[AUDIO PLAY] pause:" + audio);
        audioPlayer.pausePlaying(audio);
    }

    @Override
    public void stopPlaying(AudioPlayInfo audio) throws SpeakerException {
        Log.v(TAG, "[AUDIO PLAY] stop:" + audio);
        audioPlayer.stopPlaying(audio);
    }

    @Override
    public AudioPlayInfo getPlayingInfo() {
        Log.v(TAG, "[AUDIO PLAY] get");
        return audioPlayer.getPlayingInfo();
    }

    private static class AudioPlayer {

        private MediaPlayer player;
        // TODO: make sure this is fully supporting multithreaded operations
        private volatile DeviceStatus playerStatus;
        private volatile AudioPlayInfo playerAudioInfo;

        AudioPlayer() {
            player = null;
            playerStatus = DeviceStatus.IDLE;
            playerAudioInfo = new AudioPlayInfo("0");
        }

        void startPlaying(AudioPlayInfo audio) {
            switch (getPlayerStatus()) {
                case PAUSED:
                    validateNewAudioInfo(audio);
                    resumePausedPlayer();
                    break;
                case IDLE:
                case STOPPED:
                    String source = audio.getSrc();
                    validatePlayerSourceFile(source);
                    try {
                        startNewMediaPlayer(audio);
                    } catch (IOException e) {
                        Log.e(TAG, "[AUDIO PLAY] starting", e);
                        throw new SpeakerException(ErrorCodes.AUDIO_PLAYBACK_FAILED, "Failed to start playing" + source, e);
                    }
                    break;
                default:
                    validatePlayerInState(DeviceStatus.IDLE);
            }
        }

        private void updatePlayerAudioInfo(AudioPlayInfo audio) {
            setPlayerAudioInfo(new AudioPlayInfo(audio));
        }

        private void setPlayerAudioInfo(AudioPlayInfo audio) {
            playerAudioInfo = audio;
        }

        private AudioPlayInfo getPlayerAudioInfo() {
            return playerAudioInfo;
        }

        private DeviceStatus getPlayerStatus() {
            return playerStatus;
        }

        private void setPlayerStatus(DeviceStatus newState) {
            playerStatus = newState;
            setPlayerAudioInfo(getPlayerAudioInfo().setStatus(getPlayerStatus()));
        }

        private void validatePlayerSourceFile(String source) {
            boolean isAudioFile = FileUtil.isAudioFile(source);
            boolean isUrl = FileUtil.isUrl(source);
            if (isAudioFile && !FileUtil.isFileExists(FileUtil.getAbsoluteFilePath(source))) {
                throw new SpeakerException(ErrorCodes.AUDIO_FILE_NOT_FOUND, source + " is not found");
            }
            if (!isAudioFile && !isUrl) {
                throw new SpeakerException(ErrorCodes.UNSUPPORTED_AUDIO_FORMAT, source + " is not a supported format");
            }
        }

        private void validatePlayerInState(DeviceStatus expected) {
            if (getPlayerStatus() != expected) {
                throw new SpeakerException(ErrorCodes.AUDIO_OPERATION_AT_ILLEGAL_STATE, "Player is in " + getPlayerStatus());
            }
        }

        private void startNewMediaPlayer(AudioPlayInfo newAudioInfo) throws IOException {
            setPlayerStatus(DeviceStatus.RUNNING);

            String srcFile = newAudioInfo.getSrc();
            float volume = newAudioInfo.getVolume() > 0 ? newAudioInfo.getVolume() : 1;
            boolean isStreamUrl = FileUtil.isUrl(srcFile);

            try {
                player = new MediaPlayer();
                if (isStreamUrl) {
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(srcFile);
                } else {
                    player.setDataSource(FileUtil.getAbsoluteFilePath(srcFile));
                }
                player.setVolume(volume, volume);
                player.setOnCompletionListener(mp -> stopPlayer());
                if (isStreamUrl) {
                    player.prepareAsync();
                    player.setOnPreparedListener(mp -> player.start());
                } else {
                    player.prepare();
                    player.start();
                }
                Log.d(TAG, "[PLAYER] starting");
                updatePlayerAudioInfo(newAudioInfo);
            } catch (IOException e) {
                setPlayerStatus(DeviceStatus.IDLE);
                player = null;
                throw e;
            }
        }

        private void resumePausedPlayer() {
            setPlayerStatus(DeviceStatus.RUNNING);
            player.start();
            Log.d(TAG, "[PLAYER] resuming");
        }

        private void pausePlayingPlayer() {
            setPlayerStatus(DeviceStatus.PAUSED);
            player.pause();
            Log.d(TAG, "[PLAYER] pausing");
        }

        private void stopPlayer() {
            setPlayerStatus(DeviceStatus.STOPPED);
            player.stop();
            player.release();
            player = null;
            Log.d(TAG, "[PLAYER] stopping");
        }

        void pausePlaying(AudioPlayInfo audio) {
            validatePlayerInState(DeviceStatus.RUNNING);
            validateNewAudioInfo(audio);
            pausePlayingPlayer();
        }

        private void validateNewAudioInfo(AudioPlayInfo audio) {
            String expectedId = getPlayerAudioInfo().getId();
            String receivedId = audio.getId();
            if (!expectedId.equals(receivedId)) {
                throw new SpeakerException(ErrorCodes.AUDIO_ID_MISMATCH, "Expect:" + expectedId + ", received:" + receivedId);
            }
        }

        void stopPlaying(AudioPlayInfo audio) {
            switch (getPlayerStatus()) {
                case RUNNING:
                case PAUSED:
                    validateNewAudioInfo(audio);
                    stopPlayer();
                    break;
                default:
                    validatePlayerInState(DeviceStatus.RUNNING);
            }
        }

        AudioPlayInfo getPlayingInfo() {
            return new AudioPlayInfo(playerAudioInfo);
        }
    }
}
