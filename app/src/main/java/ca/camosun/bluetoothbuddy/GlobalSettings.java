package ca.camosun.bluetoothbuddy;

import android.app.Application;

/* This class contains a place to add Global setting flags to and access
 * them from any activity
 */
public class GlobalSettings extends Application {
    //Option - Auto shutoff on BT disconnect
    private static boolean option_autoShutoffOnDisconnect = true;
    public boolean optionGet_autoShutoff() {
        return option_autoShutoffOnDisconnect;
    }
    public void optionSet_autoShutoff(boolean b){
        option_autoShutoffOnDisconnect = b;
    }
}
