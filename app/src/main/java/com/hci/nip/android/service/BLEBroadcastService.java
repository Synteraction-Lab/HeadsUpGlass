package com.hci.nip.android.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.util.NetworkUtils;

import java.util.UUID;

/**
 * ref: https://developer.android.com/reference/android/bluetooth/le/BluetoothLeAdvertiser
 * https://developer.android.com/reference/android/bluetooth/BluetoothAdapter
 * https://developer.android.com/reference/android/bluetooth/BluetoothManager?hl=en
 */
public class BLEBroadcastService extends Service {
    private static final String TAG = BLEBroadcastService.class.getName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private BluetoothGattServer mBluetoothGattServer;
    private final IBinder mBinder = new LocalBLEBroadcastBinder();

    // customizable
    private final static String UUID_SERVICE = "2093b22e-dc83-4e43-a1d1-587a9012d3ee";
    private final static String UUID_CHARACTERISTIC_IP = "74655694-74d8-4b4e-9dea-c4a6745bede3";

    public BLEBroadcastService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        initialize();
        startAdvertising();
    }

    private void initialize() {
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (mBluetoothManager != null && mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }
        if (mBluetoothAdvertiser == null) {
            Toast.makeText(this, "Advertiser is not found", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "[BLE] UUID_SERVICE: " + UUID_SERVICE);
        Log.d(TAG, "[BLE] UUID_CHARACTERISTIC_IP: " + UUID_CHARACTERISTIC_IP);
    }

    private void startAdvertising() {
        mBluetoothAdvertiser.startAdvertising(
                new AdvertiseSettings.Builder()
                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                        .setConnectable(true)
                        .build(),
                new AdvertiseData.Builder()
                        .setIncludeDeviceName(true)
                        .setIncludeTxPowerLevel(true)
                        .build(),
                new AdvertiseData.Builder()
                        .addServiceUuid(new ParcelUuid(UUID.fromString(UUID_SERVICE)))
                        .setIncludeTxPowerLevel(true)
                        .build(),
                new SampleAdvertiseCallback()
        );
        Log.i(TAG, "[BLE] Starting advertising");
    }

    private class SampleAdvertiseCallback extends AdvertiseCallback {

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            // ADVERTISE_FAILED_TOO_MANY_ADVERTISERS, errorCode=2
            Log.e(TAG, "[BLE] startAdvertising failed, reason: " + errorCode);
            Toast.makeText(BLEBroadcastService.this, "Bluetooth advertising failed. Reason: " + errorCode, Toast.LENGTH_SHORT).show();
//            sendFailureIntent(errorCode);
//            stopSelf();
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(TAG, "[BLE]  startAdvertising successfully started");
            Toast.makeText(BLEBroadcastService.this, "Bluetooth Broadcasting", Toast.LENGTH_SHORT).show();
            mBluetoothGattServer = mBluetoothManager.openGattServer(BLEBroadcastService.this, mGattServerCallback);
            addDeviceInfoService();
        }
    }

    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.i(TAG, String.format("1.onConnectionStateChange：device name = %s, address = %s, " +
                    "status = %s, newState = %s", device.getName(), device.getAddress(), status, newState));

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                sendBroadcast(new Intent(IntentActionType.BLE_STATE_CONNECTED.getMessage()));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                sendBroadcast(new Intent(IntentActionType.BLE_STATE_DISCONNECTED.getMessage()));
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            Log.i(TAG, String.format("onServiceAdded：status = %s", status));
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i(TAG, String.format("onCharacteristicReadRequest：device name = %s, address = %s, " +
                            "requestId = %s, offset = %s, value = %s", device.getName(), device.getAddress(),
                    requestId, offset, new String(characteristic.getValue())));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] requestBytes) {
            Log.i(TAG, String.format("3.onCharacteristicWriteRequest：device name = %s, address = %s," +
                            " requestId = %s, preparedWrite=%s, responseNeeded=%s, offset=%s, value=%s",
                    device.getName(), device.getAddress(), requestId, preparedWrite, responseNeeded, offset, new String(requestBytes)));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, requestBytes);
            //4. To response to clients
            onResponseToClient(requestBytes, device, requestId, characteristic);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.i(TAG, String.format("2.onDescriptorWriteRequest：device name = %s, address = %s, requestId = %s," +
                            " preparedWrite = %s, responseNeeded = %s, offset = %s, value = %s,", device.getName(),
                    device.getAddress(), requestId, preparedWrite, responseNeeded, offset, new String(value)));
            // now tell the connected device that this was all successful
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.i(TAG, String.format("onDescriptorReadRequest：device name = %s, address = %s, requestId = %s", device.getName(), device.getAddress(), requestId));
//            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.i(TAG, String.format("5.onNotificationSent：device name = %s, address = %s, status = %s", device.getName(), device.getAddress(), status));
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
            Log.i(TAG, String.format("onMtuChanged：mtu = %s", mtu));
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
            Log.i(TAG, String.format("onExecuteWrite：requestId = %s", requestId));
        }

    };

    /**
     * Add a service + characteristic in GATT server
     */
    private void addDeviceInfoService() {
        // FIXME: Where is this UUID?
        BluetoothGattService service = new BluetoothGattService(UUID.fromString(UUID_SERVICE), BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(UUID_CHARACTERISTIC_IP),
                BluetoothGattCharacteristic.PROPERTY_WRITE |
                        BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE |
                        BluetoothGattCharacteristic.PERMISSION_READ
        );
        characteristic.setValue(NetworkUtils.getIpAddress(getApplicationContext()));

        service.addCharacteristic(characteristic);
        mBluetoothGattServer.addService(service);
    }


    private void onResponseToClient(byte[] requestBytes, BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, String.format("4.onResponseToClient：device name = %s, address = %s, requestId = %s", device.getName(), device.getAddress(), requestId));

        // FIXME: Why not directly set the request bytes to characteristic?
        String str = new String(requestBytes);
        characteristic.setValue(str.getBytes());
        mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
        Log.i(TAG, "4.onResponseToClient：" + str);
    }

    public class LocalBLEBroadcastBinder extends Binder {
        public BLEBroadcastService getService() {
            return BLEBroadcastService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdvertiser.stopAdvertising(new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }
        });
        if (mBluetoothGattServer != null) {
            mBluetoothGattServer.close();
        }
    }
}
