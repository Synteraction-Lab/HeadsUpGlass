# change log (git versions)


## v1.1
- Separated the base into separate library

## v1.0
- Initial VuzixBlade Platform architecture

## v0.0
- Initial version


### TODO
- Update new Touch events for VuzixBlade
- Convert all data (Rest, Websocket, External Intent) to common format and handle them
- Fix the host interceptor using reflection
- Remove BLEBroadcast Activity, Refactor CameraActivity, BLEBroadcastService
- Enable no preview, during recording
- Rename the `VuzixBladeManager` to `SmartGlassManager`
- Replace speech recognition with [Google New API](https://cloud.google.com/speech-to-text/docs/streaming-recognize#speech-streaming-mic-recognize-java)
- Remove keeping credentials inside app


### Known issues/limitations
- There is no separation between the data from REST api and model
- Dagger dependency injection is not used properly 
- May not fully support Android 9 and above (API level 28) e.g. access microphone
- Android 9 and above, by default does not allow send network request as cleartext. But we have enabled it on "res/xml/network_security_config.xml"
- Not using correct format of serialized names for REST API (e.g. 'dataX' should be 'data_x')
- If you override the key (board) events, in activities TouchBarSensor may not be able to detect them
- Current implementation assume sensor count is fixed and all are working
- CameraSensor is not properly synced with activity

