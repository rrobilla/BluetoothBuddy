package ca.camosun.bluetoothbuddy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Set;
import java.util.Iterator;

/* This class controls an activity that displays a list of
 * the BT paired devices your phone has saved on it
 */

public class PairedDeviceList extends AppCompatActivity {

    BluetoothAdapter myBT;

    Set<BluetoothDevice> pairedDevices;
    Iterator deviceIterator;

    TextView deviceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_device_list);

        //Create an accessable object to the bluetooth adaptor
        myBT = BluetoothAdapter.getDefaultAdapter();

        //Create an accessable object to the TextView
        deviceList = (TextView) findViewById(R.id.dText);
        deviceList.setText("");

        //Create a set of paired devices and an iterator for the set
        pairedDevices = myBT.getBondedDevices();
        deviceIterator = pairedDevices.iterator();

        //Populate the main TextView with the paired devices
        int i = 1;
        for (BluetoothDevice pairedDevice : pairedDevices) {
            deviceList.append("[Device " + String.valueOf(i) + "]\r\n"
                                + "Device Name: "
                                + pairedDevice.getName()
                                + "\r\nAddress: "
                                + pairedDevice.getAddress()
                                + "\r\n\r\n");
        }

    }
}
