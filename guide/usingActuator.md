
## Using an actuator

1. Use 'deviceManager' instance to get all actuators (deviceManager.getActuators())
2. Obtain the required actuator by filtering using 'DeviceManagerUtil.getFilteredActuatorsByType(.)'
3. Check whether the actuator is active. If not activate it (actuator.activate())
4. Cast the generic actuator to specific type (e.g. Display display = (Display) actuator)
4. Perform the required operation (e.g display.changeDisplay(.))
5. Deactivate when you do not need it anymore (sensor.deactivate())

