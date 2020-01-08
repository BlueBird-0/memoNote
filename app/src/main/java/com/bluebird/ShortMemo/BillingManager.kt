package com.bluebird.ShortMemo

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.android.billingclient.api.*


class BillingManager(val activity: Activity) : PurchasesUpdatedListener {
    val billingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build()
    fun processToPurchase() {
//        connectBillingClient()

        AlertDialog.Builder(activity)
                //.setTitle(R.string.app_name)
                .setTitle(R.string.alert)
                .setMessage(R.string.alert_billing_message)
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    showPurchaseDialog(billingClient)
                })
                .show()
    }


    fun connectBillingClient() {
        billingClient.startConnection(object: BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    //결제 확인(환불)
//                    billingClient.querySkuDetailsAsync(makeBillingParams().build()) {billingResult, purchaseHistoryRecordList ->
//                        if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                            if(purchaseHistoryRecordList != null && purchaseHistoryRecordList.size>0){
//                                Log.d("test001", "billing 2(list):"+purchaseHistoryRecordList)
//                                Log.d("test001", "billing 2-1")
//                                val sharedPref = activity.getSharedPreferences(activity.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
//                                sharedPref.edit().putBoolean(activity.getString(R.string.option_windowShortcut), true).commit()
//                            }else {
//                                Log.d("test001", "billing 2-2")
//                                val sharedPref = activity.getSharedPreferences(activity.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
//                                sharedPref.edit().putBoolean(activity.getString(R.string.option_windowShortcut), false).commit()
//                            }
//                        }
//                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("test001", "billing 3")
            }
        })
    }

    private fun makeBillingParams(): SkuDetailsParams.Builder {
        val skuList = ArrayList<String>()
        skuList.add("remove_ad")
        return SkuDetailsParams.newBuilder().apply {
            setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        }
    }

    fun showPurchaseDialog(billingClient: BillingClient) {
        Log.d("test001", "billing 3-1")
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        billingClient.querySkuDetailsAsync(makeBillingParams().build()) {billingResult, skuDetailsList ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                Log.d("test001", "billing 4")
                Log.d("test001", "billing 4-1 : "+skuDetailsList)
                for(skuDetails in skuDetailsList) {
                    Log.d("test001", "billing 4-2 : "+skuDetails)
                    val sku = skuDetails.sku
                    val price = skuDetails.price
                    if(sku == "remove_ad") {
                        Log.d("test001", "billing 5")
                        val flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build()
                        val responseCode = billingClient.launchBillingFlow(activity, flowParams)
                    }
                }
            }

        }
    }

    //결제 확인 창
    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        Log.d("test001", "billing 6")
        if(billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                //handle data
                Log.d("test001", "billing 7")
                val sharedPref = activity.getSharedPreferences(activity.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
                sharedPref.edit().putBoolean(activity.getString(R.string.option_windowShortcut), true).commit()

                handlePurchase(purchase)
            }
        }else if(billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("test001", "billing 8")
            // Handle an error caused by a user cancelling the purchase flow.
        }else {
            if(purchases != null) {
                Log.d("test001", "billing 10 : delete purchase")
                for (purchase in purchases)
                    handlePurchase(purchase)
            }
            Log.d("test001", "billing 9 : responseCode = "+ billingResult?.responseCode)
            // Handle any other error codes.
        }
    }

    //구매 확인 로직
    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams
                .newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .setDeveloperPayload(purchase.developerPayload)
                .build()

        billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->  }
    }
}