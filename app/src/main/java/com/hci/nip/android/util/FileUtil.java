package com.hci.nip.android.util;

import android.os.Environment;
import android.util.Log;

import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.base.error.BaseException;
import com.hci.nip.base.error.ErrorCode;
import com.hci.nip.base.model.FileInfo;
import com.hci.nip.base.util.MimeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Pattern: https://developer.android.com/reference/java/util/regex/Pattern
 * DataStorage: https://developer.android.com/training/data-storage/files#java
 */
public final class FileUtil {

    private static final String TAG = FileUtil.class.getName();

    private static final String APP_FOLDER = "NIP";

    private static final String AUDIO_FILE_FORMAT = "([^\\s]+(\\.(?i)(mp3|flac|aac|wav|ogg|3gp))$)";
    private static final Pattern AUDIO_PATTERN = Pattern.compile(AUDIO_FILE_FORMAT);

    private static final String URL_FORMAT = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_FORMAT);

    private static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static boolean isAudioFile(String fileUrl) {
        return AUDIO_PATTERN.matcher(fileUrl).find();
    }

    public static boolean isUrl(String fileUrl) {
        return URL_PATTERN.matcher(fileUrl).find();
    }


    /**
     * @param fileUrl
     * @param defaultValue
     * @return the MimeType if found else return the {@code defaultValue}
     */
    public static String getMimeType(String fileUrl, String defaultValue) {
        String mimeType = MimeUtils.guessMimeTypeFromExtension(fileUrl.substring(fileUrl.lastIndexOf('.') + 1));
        return mimeType != null ? mimeType : defaultValue;
    }

    /**
     * @param fileName
     * @return the absolute file name (w.r.t. application folder)
     */
    public static String getAbsoluteFilePath(String fileName) {
        File appFolder = getApplicationFolder();
        return appFolder.getAbsolutePath() + File.separator + fileName;
    }

    /**
     * @return application folder
     */
    public static File getApplicationFolder() {
        File appFolder = new File(Environment.getExternalStorageDirectory(), APP_FOLDER);
        if (!appFolder.exists()) {
            if (!appFolder.mkdirs()) {
                Log.e(TAG, "[FILE] Creating application folder failed");
            }
        }
        return appFolder;
    }

    /**
     * @param directory (e.g. {@link #getApplicationFolder()}
     * @return all fileNames in application folder
     * @throws FileException if failed
     */
    public static List<FileInfo> getAllFiles(File directory) throws FileException {
        File[] files;
        try {
            files = directory.listFiles();
        } catch (SecurityException e) {
            throw new FileException(ErrorCodes.FILE_READ_FAILED, e);
        }

        List<FileInfo> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(new FileInfo(file.getName(), file.length()));
        }
        return fileNames;
    }

    public static boolean isFileExists(String absolutePath) {
        File file = new File(absolutePath);
        return file.exists();
    }

    public static void createDirectory(String absolutePath) {
        File folder = new File(absolutePath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.e(TAG, "[FILE] Creating folder failed");
            }
        }
    }

    /**
     * Write input stream to given file name in application folder
     *
     * @param in
     * @param outputFileName file name with extension
     * @throws FileException
     */
    public static void writeFile(InputStream in, String outputFileName) throws FileException {
        try (
                InputStream inputStream = in;
                OutputStream outputStream = new FileOutputStream(new File(getAbsoluteFilePath(outputFileName)))
        ) {
            byte[] dataBytes = new byte[4096];
            while (true) {
                int read = inputStream.read(dataBytes);
                if (read == -1) {
                    break;
                }
                outputStream.write(dataBytes, 0, read);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new FileException(ErrorCodes.FILE_WRITE_FAILED, e);
        }
    }

    /**
     * Write input stream to given file name in application folder
     *
     * @param data           byte array of data to be written
     * @param outputFileName file name with extension
     * @throws FileException
     */
    public static void writeFile(byte[] data, String outputFileName) throws FileException {
        try (
                OutputStream outputStream = new FileOutputStream(new File(getAbsoluteFilePath(outputFileName)))
        ) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            throw new FileException(ErrorCodes.FILE_WRITE_FAILED, e);
        }
    }

    /**
     * Write input stream to given file name in application folder
     *
     * @param data           byte array of data to be written
     * @param outputFileName file name with extension
     * @throws FileException
     */
    public static void appendFile(byte[] data, String outputFileName) throws FileException {
        try (
                OutputStream outputStream = new FileOutputStream(new File(getAbsoluteFilePath(outputFileName)), true)
        ) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            throw new FileException(ErrorCodes.FILE_WRITE_FAILED, e);
        }
    }

    /**
     * @return the current DateTime in "YYYYMMDD_HHMMSS" format
     */
    public static String getFormattedCurrentDateTime() {
        return DATE_FORMAT_YYYYMMDD_HHMMSS.format(new Date());
    }

    /**
     * @param fileName
     * @return the content of file as a String
     */
    public static String readFile(String fileName) {
        String data = null;
        try (InputStream in = new FileInputStream(new File(getAbsoluteFilePath(fileName)))) {
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            data = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileException(ErrorCodes.FILE_READ_FAILED, e);
        }
        return data;
    }

    /**
     * @param fileName
     * @return the content of file as a {@link List}
     */
    public static List<String> readFileLines(String fileName) {
        String line;
        List<String> lineList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(FileUtil.getAbsoluteFilePath(fileName))))) {
            while ((line = br.readLine()) != null) {
                lineList.add(line);
            }
        } catch (IOException e) {
            throw new FileException(ErrorCodes.FILE_READ_FAILED, e);
        }
        return lineList;
    }

    public static class FileException extends BaseException {
        public FileException(ErrorCode errorCode) {
            super(errorCode);
        }

        public FileException(ErrorCode errorCode, Throwable cause) {
            super(errorCode, cause);
        }
    }

}
