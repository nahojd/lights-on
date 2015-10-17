package se.driessen.johan.lightson;

import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProximityWatcher {
    private static final String TAG = "ProximityWatcher";

    private final BeaconManager beaconManager;
    private final List<ProximityChangedListener> listeners = new ArrayList<ProximityChangedListener>();
    private final Region region;

    private Utils.Proximity lastProximity = Utils.Proximity.UNKNOWN;

    public ProximityWatcher(Region region) {
        this.region = region;

        beaconManager = LightsOnApplication.getBeaconManager();
    }

    public void start() {
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.i(TAG, "Entered region, staring ranging.");
                beaconManager.startRanging(region);
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.i(TAG, "Exited region, stopping ranging");
                beaconManager.stopRanging(region);

                for (ProximityChangedListener l : listeners)
                    l.outOfRange();
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (list.isEmpty())
                    return;

                Beacon nearestBeacon = list.get(0);
                Utils.Proximity proximity = Utils.computeProximity(nearestBeacon);

                if (proximity == lastProximity)
                    return;

                Log.d(TAG, "Proximity changed: " + proximity.name());

                for (ProximityChangedListener l : listeners)
                    l.ProximityChanged(proximity);

                lastProximity = proximity;
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(region);
            }
        });
    }

    public void addListener(ProximityChangedListener listener) {
        listeners.add(listener);
    }
}
