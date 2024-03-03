package app.module.iap;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IAPControl {
    Activity context;
    BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListener =
            new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && purchases != null) {
                        ////Debug.elog("onPurchasesUpdated", "BillingClient.BillingResponseCode.OK");
                        for (Purchase purchase : purchases) {
                            ////Debug.elog(purchase.toString());
                            handlePurchase(purchase);
                        }
                    } else if (billingResult.getResponseCode()
                            == BillingClient.BillingResponseCode.USER_CANCELED) {
                        ////Debug.elog("onPurchasesUpdated", "BillingClient.BillingResponseCode.USER_CANCELED");
                    } else {
                        ////Debug.elog("onPurchasesUpdated", "else");
                    }
                }
            };

    public IAPControl(Activity context) {
        this.context = context;
        // khởi tạo thanh toán
        billingClient =
                BillingClient.newBuilder(context)
                        .setListener(purchasesUpdatedListener)
                        .enablePendingPurchases()
                        .build();

        List<String> skuList = new ArrayList<>();
        skuList.add("android.test.purchased");
        skuList.add("rm_ads_01");
        getListPruducts(
                skuList,
                new OnLoadProducts() {
                    @Override
                    public void onLoaded(List<SkuDetails> products) {
                        launchPurchase(
                                products.get(0),
                                new OnLaunchPurchase() {
                                    @Override
                                    public void onPay_OK() {
                                    }

                                    @Override
                                    public void onPay_Cancel() {
                                    }
                                });
                    }

                    @Override
                    public void onLoadFail() {
                    }
                });

        fetchPurchaseHistory("android.test.purchased");
        //    fetchPurchaseHistory("rm_ads_01");
    }

    public Context getContext() {
        return context;
    }

    public BillingClient getBillingClient() {
        return billingClient;
    }

    private void fetchPurchaseHistory(String sku) {
        billingClient.queryPurchaseHistoryAsync(
                sku,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(
                            @NonNull @NotNull BillingResult billingResult,
                            @Nullable @org.jetbrains.annotations.Nullable List<PurchaseHistoryRecord> list) {

                        if (list != null)
                            for (PurchaseHistoryRecord purchaseHistoryRecord : list) {
                                ////Debug.elog("purchaseHistoryRecord", purchaseHistoryRecord.getSkus());
                            }
                    }
                });
    }

    private void launchPurchase(SkuDetails skuDetails, OnLaunchPurchase onLaunchPurchase) {
        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
        int responseCode =
                billingClient.launchBillingFlow(context, billingFlowParams).getResponseCode();
    }

    /**
     * @param skuList
     * @param onLoadProducts
     */
    public void getListPruducts(List<String> skuList, OnLoadProducts onLoadProducts) {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.startConnection(
                new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            billingClient.querySkuDetailsAsync(
                                    params.build(),
                                    new SkuDetailsResponseListener() {
                                        @Override
                                        public void onSkuDetailsResponse(
                                                BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                                            if (skuDetailsList.size() > 0) {

                                                onLoadProducts.onLoaded(skuDetailsList);

                                            } else onLoadProducts.onLoadFail();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {
                        onLoadProducts.onLoadFail();
                    }
                });
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(
                        acknowledgePurchaseParams,
                        new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(
                                    @NonNull @NotNull BillingResult billingResult) {
                                switch (billingResult.getResponseCode()) {
                                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "SERVICE_TIMEOUT");
                                        break;
                                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "FEATURE_NOT_SUPPORTED");

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "SERVICE_DISCONNECTED");

                                        break;
                                    case BillingClient.BillingResponseCode.OK:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "OK");

                                        break;
                                    case BillingClient.BillingResponseCode.USER_CANCELED:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "USER_CANCELED");

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "SERVICE_UNAVAILABLE");

                                        break;
                                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "BILLING_UNAVAILABLE");

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "ITEM_UNAVAILABLE");

                                        break;
                                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "DEVELOPER_ERROR");

                                        break;
                                    case BillingClient.BillingResponseCode.ERROR:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "ERROR");

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "ITEM_ALREADY_OWNED");

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                                        ////Debug.elog("onAcknowledgePurchaseResponse", "ITEM_NOT_OWNED");

                                        break;
                                }
                            }
                        });
            }
        }
    }

    public interface OnLoadProducts {
        void onLoaded(List<SkuDetails> products);

        void onLoadFail();
    }

    public interface OnLaunchPurchase {

        void onPay_OK();

        void onPay_Cancel();
    }
}
