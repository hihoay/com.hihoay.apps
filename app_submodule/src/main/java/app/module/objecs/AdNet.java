package app.module.objecs;

import android.view.ViewGroup;

import app.module.callback.OnAdStateEvent;


public abstract class AdNet {


    public abstract void cacheAd(
            String screen, int index, MyAd myAd, ViewGroup viewContainer, OnAdStateEvent onAdStateEvent);

    public abstract void showAdCachedToView(
            MyAd myAdToShow, ViewGroup viewContainer, OnAdStateEvent onAdStateEvent);
}
