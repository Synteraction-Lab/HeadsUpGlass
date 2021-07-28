
## Using a sensor

1. Use 'deviceManager' instance to get all sensors (deviceManager.getSensors())
2. Obtain the required sensor by filtering using 'DeviceManagerUtil.getFilteredSensorsByType(.)'
3. Check whether the sensor is active, if not activate it (sensor.activate())
4. Read sensor data (sensor.readData())
5. If you need sensor specific operation (e.g. record voice using microphone), cast 
the sensor to specific type before running the operation 
5. Deactivate when you do not need it anymore (sensor.deactivate())

