package com.someapp.vishnu.bluetoothproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView statusTextView;
    Button searchButton;
    ArrayList<String> bluetoothDevices = new ArrayList<String>();
    ArrayList<String> addresses = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    BluetoothAdapter bluetoothAdapter;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        // How to take action when given an intent
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action", action);

            // This means we have finished our search
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                statusTextView.setText("Finished!");
                searchButton.setEnabled(true);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) { // Found a bluetooth device

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();

                // The more negative the number is, the stronger the connection is
                String rssi = Integer.toString(intent.
                        getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));

//                Log.i("Device Found", "Name: "
//                        + name + "Address: " + address + "RSSI: " + rssi);

                if (addresses.contains(address)) {

                    addresses.add(address);

                    String deviceString = "";

                    // Short-circuit
                    if (name == null || name.equals("")) {

                        // dBm is unit for RSSI
                        deviceString = address + " - " + rssi + "dBm";

                    } else {
                        deviceString = name + " - " + rssi + "dBm";
                    }

                    bluetoothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void searchClicked(View view) {

        statusTextView.setText("Searching...");
        searchButton.setEnabled(false);

        bluetoothDevices.clear(); // Clear for repeat searches
        addresses.clear();
        bluetoothAdapter.startDiscovery(); // Requires manifest permission

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        statusTextView = findViewById(R.id.statusTextView);
        searchButton = findViewById(R.id.searchButton);

        arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, bluetoothDevices);

        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // Allows us to work w/ bluetooth

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND); // Tells us when we find a device
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver, intentFilter);

    }
}
