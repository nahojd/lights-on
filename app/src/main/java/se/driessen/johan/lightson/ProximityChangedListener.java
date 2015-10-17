package se.driessen.johan.lightson;

import com.estimote.sdk.Utils;

public interface ProximityChangedListener {
    void ProximityChanged(Utils.Proximity proximity);

    void outOfRange();
}
