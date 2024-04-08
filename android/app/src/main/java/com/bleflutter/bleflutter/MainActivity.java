package com.bleflutter.bleflutter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "alarm_sender";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for Serial Port Profile (SPP)

    private Handler handler;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("createSocket")) {
                                Log.d("BLE :", "createSocket callll");

                                String data = call.argument("data");
                                handler = new Handler();
                                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                device = bluetoothAdapter.getRemoteDevice("CD:B4:A8:34:9B:7E"); // Replace with your smartwatch's Bluetooth address

                                connectToDevice2();

//                                sendAlarmDataToSmartwatch(data, this);
                            } else if (call.method.equals("sendAlarmData")) {
                                Log.d("BLE :", "sendAlarmData callll");
//                                sendAlarmDataToSmartwatch(data, this);
                                sendAlarmData();
                            } else {
                                result.notImplemented();
                            }
                        }
                );
    }

    private void sendAlarmDataToSmartwatch(String data, Context con) {
//        AlarmSender alarmSender = new AlarmSender(new Handler(), con);
//        alarmSender.connectToDevice("CD:B4:A8:34:9B:7E", data);
//        alarmSender.sendAlarmData("01/04/2024 10:25:30 AM");
        // Implement your logic to send alarm data to the smartwatch
        // This might involve using Bluetooth, WiFi, or other communication methods
    }


    private void connectToDevice() {
        Log.d("BLE :", "connectToDevice callll");
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                outputStream = socket.getOutputStream();
                handler.obtainMessage(1).sendToTarget();

                if (outputStream != null) {
                    Log.d("BLE :", "sendAlarmData try if calll");
                    outputStream.write("Alarm set for 09:00 AM".getBytes());
                    handler.obtainMessage(2).sendToTarget();

                    Log.d("BLE :", "sendAlarmData try if callledddddd");
                }
                Log.d("BLE :", "connectToDevice calllledddddd");

//                sendAlarmData("Alarm set for 08:00 AM");
            } catch (IOException e) {
                e.printStackTrace();
                handler.obtainMessage(3).sendToTarget();

            }
        }).start();
    }

    // Method to send alarm data to the smartwatch
    private void sendAlarmData() {
        Log.d("BLE :", "sendAlarmData calll");
        try {
            Log.d("BLE :", "sendAlarmData try calll");
            if (outputStream != null) {
                Log.d("BLE :", "sendAlarmData try if calll");
                outputStream.write("Alarm set for 09:00 AM".getBytes());
                Log.d("BLE :", "sendAlarmData try if callledddddd");
            }
        } catch (IOException e) {
            Log.d("BLE :", "sendAlarmData catch calll");
            Log.d("BLE :", "sendAlarmData try calll = " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void connectToDevice2() {
//        device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                outputStream = socket.getOutputStream();
                // Notify the handler that the connection is successful
                handler.obtainMessage(1).sendToTarget();

                try {
                    if (outputStream != null) {
                        String s = "{\n" +
                                "  \"time\": \"08:00 AM\",\n" +
                                "  \"message\": \"Wake up!\",\n" +
                                "  \"type\": \"one-time\",\n" +
                                "  \"status\": \"active\"\n" +
                                "}";
                        outputStream.write(s.getBytes());
                        // Notify the handler that the data has been sent successfully
                        handler.obtainMessage(2).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Notify the handler that an error occurred during data transmission
                    handler.obtainMessage(3).sendToTarget();
                }

            } catch (IOException e) {
                e.printStackTrace();
                // Notify the handler that an error occurred during connection
                handler.obtainMessage(0).sendToTarget();
            }
        }).start();
    }

}
