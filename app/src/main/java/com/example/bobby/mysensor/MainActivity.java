package com.example.bobby.mysensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final long SCAN_PERIOD = 10000;

    //private DeviceScanner mDeviceScanner;
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 0x01;
    Handler mHandler;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback startScanCallback;
    private ArrayList<String> mAddressCache;
    short MY_COMPANY_ID = 0x02fe;
    ArrayList<ScanRecord> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddressCache = new ArrayList<>();
        records = new ArrayList<>();

        Button scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressCache.clear();
                startScanning();
            }
        });

        mHandler = new Handler();

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //
        startScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                String address = result.getDevice().getAddress();
                if (!mAddressCache.contains(address)) {
                    mAddressCache.add(address);

                    ScanRecord record = result.getScanRecord();
                    records.add(record);

                    if (record != null) {
                        byte[] bytes = record.getBytes();

                        AdvDataElement[] data = AdvertisingDataParser.parse(bytes);
                        for (AdvDataElement elem: data) {
                            if (elem.getType() == AdvDataElement.Types.MANUFACTURER_DATA && elem.getCompanyId() == MY_COMPANY_ID) {
                                Log.d(TAG, elem.toString());
                                Log.d(TAG, Boolean.toString(elem.getCompanyId() == MY_COMPANY_ID));
                            }

                        }
                    }
                }
            }
        };

        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_ENABLE_BT) {
            startScanning();
        }
    }

    public void finishedScanning() {
        Log.d(TAG, "finishedScanning()");
    }

    private void startScanning() {
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(startScanCallback);
        setStopScanTimer();
    }

    private void setStopScanTimer() {
        final ScanCallback stopScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
            }
        };

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(startScanCallback);
            }
        }, SCAN_PERIOD);
    }
}
