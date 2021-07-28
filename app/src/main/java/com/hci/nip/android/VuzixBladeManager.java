package com.hci.nip.android;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hci.nip.android.actuators.DisplayActuator;
import com.hci.nip.android.actuators.HapticActuator;
import com.hci.nip.android.actuators.NotificationActuator;
import com.hci.nip.android.actuators.SpeakerActuator;
import com.hci.nip.android.actuators.TouchBarActuator;
import com.hci.nip.android.sensors.AccelerometerSensor;
import com.hci.nip.android.sensors.CameraSensor;
import com.hci.nip.android.sensors.GyroscopeSensor;
import com.hci.nip.android.sensors.LightSensor;
import com.hci.nip.android.sensors.MagnetometerSensor;
import com.hci.nip.android.sensors.MicrophoneSensor;
import com.hci.nip.android.sensors.PressureSensor;
import com.hci.nip.android.sensors.TouchBarSensor;
import com.hci.nip.base.DeviceManager;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.sensor.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class VuzixBladeManager implements DeviceManager {

    private static final String TAG = VuzixBladeManager.class.getName();

    private final List<Sensor> sensors;
    private final List<Actuator> actuators;

    private int count = 0;
    private final Application app;

    public VuzixBladeManager(@NonNull Application app) {
        this.sensors = new ArrayList<>();
        this.actuators = new ArrayList<>();
        this.app = app;
    }

    @Override
    public void initialize() {
        Log.i(TAG, "[DM] Initializing");
        // add sensors
        addSensor(new AccelerometerSensor(app, getNewId()));
        addSensor(new MicrophoneSensor(getNewId()));
        addSensor(new TouchBarSensor(getNewId()));
        addSensor(new GyroscopeSensor(app, getNewId()));
        addSensor(new MagnetometerSensor(app, getNewId()));
        addSensor(new LightSensor(app, getNewId()));
        addSensor(new PressureSensor(app, getNewId()));
        addSensor(new CameraSensor(getNewId()));

        // add actuators
        addActuator(new SpeakerActuator(getNewId()));
        addActuator(new DisplayActuator(getNewId()));
        addActuator(new HapticActuator(app, getNewId()));
        addActuator(new NotificationActuator(app, getNewId()));
        addActuator(new TouchBarActuator(getNewId()));
    }

    private void addSensor(Sensor sensor) {
        sensor.open();
        this.sensors.add(sensor);
    }

    private void addActuator(Actuator actuator) {
        actuator.open();
        this.actuators.add(actuator);
    }

    @Override
    public void release() {
        Log.i(TAG, "[DM] Releasing");
        closeAllSensors();
        closeAllActuators();
        resetId();
    }

    private void closeAllActuators() {
        for (Actuator actuator : actuators) {
            actuator.close();
        }
        actuators.clear();
    }

    private void closeAllSensors() {
        for (Sensor sensor : sensors) {
            sensor.close();
        }
        sensors.clear();
    }

    private String getNewId() {
        count++;
        return String.valueOf(count);
    }

    private void resetId() {
        count = 0;
    }

    @Override
    public String getId() {
        return "vuzix.blade.1";
    }

    @Override
    public String getName() {
        return "headpiece.vuzix.blade";
    }

    @Override
    public List<Sensor> getSensors() {
        return Collections.unmodifiableList(sensors);
    }

    @Override
    public List<Actuator> getActuators() {
        return Collections.unmodifiableList(actuators);
    }

}
