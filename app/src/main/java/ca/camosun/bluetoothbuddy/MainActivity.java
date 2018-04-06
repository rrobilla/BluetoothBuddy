package ca.camosun.bluetoothbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.bluetooth.*;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Bluetooth Buddy
 * Created by Ryan Robillard on 4/5/2018.
 *
 * This app monitors the state of bluetooth and when a connected device disconnects, it shuts bluetooth off.
 * It also can be used to turn bluetooth on, and see the external devices that your phone is paired to.
 */


public class MainActivity extends AppCompatActivity implements OnClickListener {
    private final int deviceConnected = 1;
    private final int deviceDisconnected = 0;
    private final GlobalSettings appSettings = new GlobalSettings();

    //Bluetooth Adaptor
    BluetoothAdapter myBT;
    //private final static int REQUEST_ENABLE_BT = 1; //Used for BT enable request
    BroadcastReceiver br;

    //BT Status
    TextView btStatus;
    ToggleButton btToggle;
    CompoundButton.OnCheckedChangeListener toggleListener;

    //Paired List Button
    Button pairedButton;

    /* Options */
    //Options Menu button
    ImageButton optionsMenuButton;
    //Enable BT Auto shut-off when device disconnects
    protected boolean option_btDShut;

    //Connection Status
    TextView connStatus;
    ImageView connStatusImg;
    public ImageView getConnStatusImg(){return connStatusImg;}
    public void setConnStatusImg(int i){
        switch (i){
            case deviceConnected:
                connStatusImg.setImageResource(R.drawable.ok);
                break;
            case deviceDisconnected:
                connStatusImg.setImageResource(R.drawable.not);
                break;
            default:
                Log.e("Error getting the connection status: ", String.valueOf(i));
                break;
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Options Initialization
        optionInitialization();
        //GUI Initialization
        gui_setup();

        //Set BT Adaptor Controller
        myBT = BluetoothAdapter.getDefaultAdapter();

        //Create the receiver and filter, then register it
        setBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(br, filter);

        //Create toggle button listener
        toggleListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    myBT.enable();
                }
                else{
                    myBT.disable();
                }
            }
        };
        //Set listener on toggle button
        btToggle.setOnCheckedChangeListener(toggleListener);

        //Check BT status for initial GUI state
        if (myBT.isEnabled()){
            btToggle.setChecked(true);
            show_bt_on();
        }
        else{
            btToggle.setChecked(false);
            hide_bt_off();
        }
    }//end of onCreate

    /* Handles what to do when a button on screen is pressed
     * and goes to the appropriate activity
     */
    @Override
    public void onClick(View view) {
        //Clicked button
        int clicked = view.getId();

        //Buttons
        int pButton = pairedButton.getId();
        int optButton = optionsMenuButton.getId();

        //Paired Device List Button
        if (clicked == pButton){
            // use intent to go to second view and display info on what is connected via bluetooth
            Intent devicesListScreen = new Intent(this, PairedDeviceList.class);
            devicesListScreen.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(devicesListScreen);
        }

        //Options Menu Button
        if (clicked == optButton){
            Intent optionsScreen = new Intent(this, OptionsMenu.class);
            optionsScreen.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(optionsScreen);
        }
    }

    //Used to unregister the broadcast receiver
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(br);


    }

    /* DEV DEFINED FUNCTIONS */

    //Creates a BT broadcast receiver
    public void setBroadcastReceiver(){
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String theAction = intent.getAction();
                Log.d("Action: ", theAction);

                //BT adaptor state turning on / off
                if (theAction == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    Log.d("State: ", String.valueOf(myBT.getState()));
                    if (myBT.getState() == BluetoothAdapter.STATE_ON) {
                        btToggle.setChecked(true);
                        show_bt_on();
                    }
                    if (myBT.getState() == BluetoothAdapter.STATE_OFF){
                        btToggle.setChecked(false);
                        hide_bt_off();
                    }
                }

                //BT device connected
                else if(theAction == BluetoothDevice.ACTION_ACL_CONNECTED){
                    //set connImg to ok
                    setConnStatusImg(deviceConnected);
                }

                //BT device disconnected
                else if (theAction == BluetoothDevice.ACTION_ACL_DISCONNECTED){
                    //setconnImg to not
                    setConnStatusImg(deviceDisconnected);

                    if (appSettings.optionGet_autoShutoff()){
                        myBT.disable();
                        hide_bt_off();
                    }

                }
            }
        }; //end br variable assignment
    }

    /* User Defined Functions */

    //Hides all GUI not required when BT is disabled
    public void hide_bt_off(){
        pairedButton.setVisibility(View.GONE);
        connStatus.setVisibility(View.GONE);
        connStatusImg.setVisibility(View.GONE);
    }

    //Unhides all required GUI used when BT is enabled
    public void show_bt_on(){
        pairedButton.setVisibility(View.VISIBLE);
        connStatus.setVisibility(View.VISIBLE);
        connStatusImg.setVisibility(View.VISIBLE);
    }

    //Set initial options
    private void optionInitialization(){
        option_btDShut = false;
    }

    //Initial GUI setup and load
    private void gui_setup(){

        //Options Menu button setup
        optionsMenuButton = (ImageButton) findViewById(R.id.optionsButton);
        Drawable bg = getDrawable(R.drawable.cog);
        optionsMenuButton.setBackground(bg);
        optionsMenuButton.setOnClickListener(this);

        //BT Status Text view creation and setup
        btStatus = (TextView) this.findViewById(R.id.btStatusText);
        btStatus.setText("Bluetooth Status");
        //Bluetooth Toggle button
        btToggle = (ToggleButton) this.findViewById(R.id.btToggle);

        //Paired Devices Button creation and setup
        pairedButton = (Button) this.findViewById(R.id.getPairedButton);
        pairedButton.setText("List of Paired Devices");
        pairedButton.setOnClickListener(this);
        pairedButton.setVisibility(View.GONE);
        //Connection status Text creation and setup
        connStatus = (TextView) findViewById(R.id.connStatusText);
        connStatus.setText("Connection Status: ");

        //Connection status ImageView creation and setup
        connStatusImg = (ImageView) findViewById(R.id.connStatusImage);
        connStatusImg.setImageResource(R.drawable.not);
    }

}//end class
