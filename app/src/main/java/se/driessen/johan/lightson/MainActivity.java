package se.driessen.johan.lightson;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ProximityChangedListener {
    private final static String TAG = "Main";

    private Hue hue;
    private ProximityWatcher proximityWatcher;
    private long lastContact = 0;
    private TextView textProximity;
    private TextView textStatus;
    private TextView textLastContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hue = new Hue(getApplicationContext());
        proximityWatcher = new ProximityWatcher(new Region(
                "monitored region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                34241, 7662
        ));
        proximityWatcher.addListener(this);

        textProximity = (TextView)findViewById(R.id.textProximity);
        textStatus = (TextView)findViewById(R.id.textStatus);
        textLastContact = (TextView)findViewById(R.id.textContact);
    }


    @Override
    protected void onResume() {
        super.onResume();

        proximityWatcher.start();
    }

    @Override
    public void ProximityChanged(Utils.Proximity proximity) {
        textProximity.setText(proximity.name());

        if (proximity == Utils.Proximity.IMMEDIATE || proximity == Utils.Proximity.NEAR) {
            long now = System.currentTimeMillis();
            lastContact = now;
            textLastContact.setText(DateFormat.format("HH:mm:ss",new Date(now)));
            hue.turnLightOn(1);
            textStatus.setText("ON");
        }
        else if(lastContact == 0)
            return; //If we've never had contact, we don't want to turn the light off.
        else if (proximity == Utils.Proximity.UNKNOWN && (System.currentTimeMillis() - lastContact > 10000)) {
            hue.turnLightOff(1);
            textStatus.setText("OFF");
        }

    }

    @Override
    public void outOfRange() {
        if (lastContact > 0 && (System.currentTimeMillis() - lastContact > 10000)) {
            hue.turnLightOff(1);
            textStatus.setText("OFF");
            textProximity.setText("OUT OF RANGE");
        }

    }
}
