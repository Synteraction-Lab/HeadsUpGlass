package com.hci.nip.android.actuators;

import android.util.Log;

import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.service.BroadcastService;
import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.service.ServiceProvider;
import com.hci.nip.base.actuator.ActuatorLocation;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.actuator.Display;
import com.hci.nip.base.actuator.model.DisplayData;

import java.util.List;

public class DisplayActuator extends ServiceProvider implements Display {

    private static String TAG = DisplayActuator.class.getName();

    private final String id;

    public DisplayActuator(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ActuatorType getType() {
        return ActuatorType.ACTUATOR_TYPE_DISPLAY;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.display";
    }

    @Override
    public ActuatorLocation getLocation() {
        return ActuatorLocation.ACTUATOR_LOCATION_HEAD;
    }

    @Override
    public String getResolution() {
        return "480px,480px";
    }

    @Override
    public String getDataFormat() {
        return "List<Text>,List<Image>,List<Video>";
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
        // DO NOTHING
    }

    @Override
    public void close() {
        // DO NOTHING
    }

    @Override
    public boolean processData(List<?> data) {
        // TODO: implement
        return false;
    }

    @Override
    public void changeDisplay(DisplayData displayData) throws DisplayException {
        // TODO: validation
        Log.d(TAG, "changeDisplay: " + displayData);

        Long uniqueKey = dataRepository.getUniqueKey();
        addDisplayUpdateAndBroadcast(uniqueKey, displayData);
        waitUntilDisplayUpdate(uniqueKey);
    }

    private void waitUntilDisplayUpdate(Long uniqueKey) throws DisplayException {
        // wait until someone process the request and add a response
        DisplayData displayDataResponse = (DisplayData) dataRepository.waitForResponse(uniqueKey, 500);
        // check whether a correct response has received
        if (displayDataResponse == null) {
            throw new DisplayException(ErrorCodes.DISPLAY_REQUEST_TIMEOUT, "Display update timeout");
        }
    }

    @Override
    public void changeDisplayAsync(DisplayData displayData) throws DisplayException {
        Log.d(TAG, "changeDisplayAsync: " + displayData);
        Long uniqueKey = dataRepository.getUniqueKey();
        addDisplayUpdateAndBroadcast(uniqueKey, displayData);
    }

    private void addDisplayUpdateAndBroadcast(Long uniqueKey, DisplayData displayData) {
        // add a request to dataRepository to store
        dataRepository.addRequest(uniqueKey, displayData);
        // broadcast an Intent to ask  to process the request
        broadcastService.sendBroadcast(BroadcastService.getBroadcastIntent(IntentActionType.DISPLAY_UPDATE, uniqueKey));
    }
}
