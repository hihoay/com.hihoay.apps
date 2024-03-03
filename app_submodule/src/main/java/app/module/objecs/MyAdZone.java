package app.module.objecs;

import android.content.Context;

import app.module.R;

public class MyAdZone extends AdZone {
    public MyAdZone(Context context) {
        super(context);
    }

    @Override
    public AdZoneConfig getAdZoneConfig() {
        return new AdZoneConfig(
                R.layout.layout_ad_native_container,
                R.layout.layout_ad_banner_container,
                R.layout.layout_native_ad_zone,
                R.layout.layout_banner_ad_zone);
    }
}
