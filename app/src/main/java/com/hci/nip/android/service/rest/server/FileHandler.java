package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.android.util.FileUtil;
import com.hci.nip.base.model.FileInfo;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.util.JsonUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileHandler extends UrlHandler {

    private static final String TAG = FileHandler.class.getName();

    private static final String PARAM_FILE_NAME = "file_name";

    // NOTE: order matters
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/files/",
            "/files/:" + PARAM_FILE_NAME
    ));

    @Override
    public List<String> getStaticUrls() {
        return STATIC_URLS;
    }

    @Override
    public RestServer.Response get(RestServer.Request request) {
        Log.v(TAG, "GET request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String fileName = params.get(PARAM_FILE_NAME);

        if (parameterSize == 0) {
            // url: files
            List<FileInfo> fileInfoList = FileUtil.getAllFiles(FileUtil.getApplicationFolder());
            return RestServer.Response.getSuccessResponse(JsonUtil.getWrappedJsonString("files", JsonUtil.getJsonString(fileInfoList)));
        } else if (parameterSize == 1 && fileName != null) {
            // url: files/{file_name}
            FileInfo fileInfo = new FileInfo(fileName);
            return getRequestedFileResponse(fileInfo);
        }
        return getRequestNotSupportedResponse();
    }


    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);

        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        if (parameterSize > 0) {
            // url: files/{UNKNOWN}
            return getRequestNotSupportedResponse();
        }

        // url: files/
        FileInfo fileInfo = JsonUtil.getObjectFromJson(request.getRequestBody(), FileInfo.class);
        return getRequestedFileResponse(fileInfo);
    }

    private RestServer.Response getRequestedFileResponse(FileInfo fileInfo) {
        String absoluteFileName = FileUtil.getAbsoluteFilePath(fileInfo.getSrc());
        if (FileUtil.isFileExists(absoluteFileName)) {
            try {
                return RestServer.Response.getSuccessResponse(FileUtil.getMimeType(absoluteFileName, "application/json"), new FileInputStream(absoluteFileName));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "[FILE] File not found", e);
                return getFileNotFoundResponse();
            }
        }

        return getFileNotFoundResponse();
    }

    private RestServer.Response getFileNotFoundResponse() {
        return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(ErrorCodes.FILE_NOT_FOUND)));
    }

}
