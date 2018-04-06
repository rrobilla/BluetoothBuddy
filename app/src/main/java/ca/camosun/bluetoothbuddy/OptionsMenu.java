package ca.camosun.bluetoothbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/* This class is an activity that controls the Options Menu screen and
 * allows you to change the GlobalSettings static variables
 */

public class OptionsMenu extends AppCompatActivity {
    private final GlobalSettings appSettings = new GlobalSettings();

    TextView menuTitle;
    ToggleButton toggle_autoDisconnect;
    CompoundButton.OnCheckedChangeListener toggleListenerAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_menu);

        gui_setup();

        //Create and set listener on auto disconnect toggle
        create_toggle_autoShutoffListener();
        toggle_autoDisconnect.setOnCheckedChangeListener(toggleListenerAD);
    }//end onCreate

    //Setup initial GUI
    private void gui_setup(){
        menuTitle = (TextView) findViewById(R.id.menuTitleText);
        menuTitle.setText("Options Menu");
        toggle_autoDisconnect = findViewById(R.id.toggleAutoDisconnectButton);
        toggle_autoDisconnect.setChecked(appSettings.optionGet_autoShutoff());
    }

    //Creates the Listener for the AutoShutoff Listener
    private void create_toggle_autoShutoffListener(){
        toggleListenerAD = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    appSettings.optionSet_autoShutoff(b);
                }
                else{
                    appSettings.optionSet_autoShutoff(false);
                }
            }
        };
    }
}
