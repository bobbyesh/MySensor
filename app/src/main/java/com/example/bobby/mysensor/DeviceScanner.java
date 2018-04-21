package com.example.bobby.mysensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;

class DeviceScanner {
    private static final String TAG = DeviceScanner.class.getSimpleName();
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 3000;
    private static final int REQUEST_ENABLE_BT = 0x01;
    private HashSet<BluetoothDevice> mDevices = new HashSet<BluetoothDevice>();
    private HashSet<Integer> rssis = new HashSet<Integer>();
    private MainActivity mContext;

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
                    final int rssi_ = rssi;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean added = mDevices.add(device);

                            rssis.add(rssi_);
                            Log.d(TAG, "Device found: " + device.toString() + "  rssi: " + rssi_);
                            Log.d(TAG, "scanRecord (" + scanRecord.length + ")" + ": " + Arrays.toString(scanRecord));
                        }
                    });
                }
            };

    DeviceScanner(MainActivity context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());

        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            CharSequence text = "bluetoothManager is null!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            Log.e(TAG, text.toString());

        }
    }

    public HashSet<BluetoothDevice> getDevices() {
        return mDevices;
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.d(TAG, "postDelayed()");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mContext.finishedScanning();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            Log.d(TAG, "scanLeDevice(): mScanning == true");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            Log.d(TAG, "scanLeDevice(): mScanning == false");
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            for (BluetoothDevice device: mDevices) {
                Log.d(TAG, "Found: " + device.toString());
            }
        }
    }
}
