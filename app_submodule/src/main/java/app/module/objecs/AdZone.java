package app.module.objecs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import app.module.R;
import app.module.enums.AdFormat;

public abstract class AdZone {

    Context context;

    public AdZone(Context context) {
        this.context = context;
    }

    public abstract AdZoneConfig getAdZoneConfig();

    public void showAdZone(MyAd myAd, ViewGroup viewContainer) {

        try {
            if (myAd.getAdFormat().equals(AdFormat.Native) && myAd.getAd_tag().equals("medium")) {
                addZoneNative(myAd, viewContainer);
            } else if (myAd.getAd_tag() != null && myAd.getAd_tag().equals("320x250")
                    || myAd.getAd_tag().equals("inline")) {
                addZoneNative(myAd, viewContainer);
            } else addZoneBanner(myAd, viewContainer);
        } catch (Exception e) {
            addZoneBanner(myAd, viewContainer);
        }
    }

    private void addZoneNative(MyAd myAd, ViewGroup viewContainer) {
        if (viewContainer != null) viewContainer.removeAllViews();
        View viewAdContainer =
                View.inflate(context, getAdZoneConfig().getLayoutAdNativeContainer(), null);
        ViewGroup viewGroupAdContainer = viewAdContainer.findViewById(R.id.ll_ad_container);
        if (viewContainer != null) viewContainer.addView(viewAdContainer);

        View viewNative = View.inflate(context, getAdZoneConfig().getLayoutNativeAdZone(), null);
        viewGroupAdContainer.addView(viewNative);
    }

    private void addZoneBanner(MyAd myAd, ViewGroup viewContainer) {
        if (viewContainer != null) viewContainer.removeAllViews();
        View viewAdContainer =
                View.inflate(context, getAdZoneConfig().getLayoutAdBannerContainer(), null);
        ViewGroup viewGroupAdContainer = viewAdContainer.findViewById(R.id.ll_ad_container);
        if (viewContainer != null) viewContainer.addView(viewAdContainer);

        View viewBanner = View.inflate(context, getAdZoneConfig().getLayoutBannerAdZone(), null);
        viewGroupAdContainer.addView(viewBanner);
    }

    public class AdZoneConfig {
        int layoutAdNativeContainer;
        int layoutAdBannerContainer;

        int layoutNativeAdZone;
        int layoutBannerAdZone;

        public AdZoneConfig(
                int layoutAdNativeContainer,
                int layoutAdBannerContainer,
                int layoutNativeAdZone,
                int layoutBannerAdZone) {
            this.layoutAdNativeContainer = layoutAdNativeContainer;
            this.layoutAdBannerContainer = layoutAdBannerContainer;
            this.layoutNativeAdZone = layoutNativeAdZone;
            this.layoutBannerAdZone = layoutBannerAdZone;
        }

        public int getLayoutAdNativeContainer() {
            return layoutAdNativeContainer;
        }

        public int getLayoutAdBannerContainer() {
            return layoutAdBannerContainer;
        }

        public int getLayoutNativeAdZone() {
            return layoutNativeAdZone;
        }

        public int getLayoutBannerAdZone() {
            return layoutBannerAdZone;
        }
    }
}
