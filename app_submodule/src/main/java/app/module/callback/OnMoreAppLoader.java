package app.module.callback;

import java.util.ArrayList;

import app.module.objecs.MoreApp;

public interface OnMoreAppLoader {
    void onAdxLoaded(ArrayList<MoreApp> moreApps);

    void onAdxLoadFailed();
}
