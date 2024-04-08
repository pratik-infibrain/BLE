package com.bleflutter.bleflutter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class AlarmSender {
    private static UUID MY_UUID = UUID.fromString("4013f3ae-2919-4ff5-b788-c9794600808b"); // UUID for Serial Port Profile (SPP)
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private final Handler handler;
    private final Context con;

    public AlarmSender(Handler handler, Context con) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
        this.con = con;
    }

    public void connectToDevice(String deviceAddress,String uuidAddress) {
        device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        new Thread(() -> {
            try {

                if (ActivityCompat.checkSelfPermission(con, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                socket = device.createRfcommSocketToServiceRecord(uuidAddress.isEmpty()? MY_UUID : UUID.fromString(uuidAddress));
                socket.connect();
                outputStream = socket.getOutputStream();
                // Notify the handler that the connection is successful
                handler.obtainMessage(1).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                // Notify the handler that an error occurred during connection
                handler.obtainMessage(0).sendToTarget();
            }
        }).start();
    }

    public void sendAlarmData(String data) {
        try {
            if (outputStream != null) {
                outputStream.write(data.getBytes());
                // Notify the handler that the data has been sent successfully
                handler.obtainMessage(2).sendToTarget();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Notify the handler that an error occurred during data transmission
            handler.obtainMessage(3).sendToTarget();
        }
    }

    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
