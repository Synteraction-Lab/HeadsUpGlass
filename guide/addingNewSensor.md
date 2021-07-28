
## Adding new sensor

- Implement your sensor from 'base.sensor.Sensor'
- Add the required functionality based on sensor 
(e.g. 'android.sensors.AccelerometerSensor')
- Remember to implement 'deactivate()' and 'close()' property so that 
Android resources are RELEASED properly (i.e. to prevent memory leaks)
- Finally add the sensor to 'base.DeviceManager' (e.g. 'android.VuxizBladeManager')

**NOTE**: Sensor implementation highly depends on the device. So make 
sure you implement the sensor according to your device.
