package com.mcmaster.wiser.idyll.model.EKFLocationService.Interface;

import android.location.Location;

public interface LocationNotifier {

    void getLocationChanged(Location loc);
}
