package com.shyam.payudemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String strKey = "gtKFFx";
    String strSalt = "wia56q6O";
    //  String strKey = "gtKFFx";
    //  String strSalt = "4R38IvwiV57FwVpsgOvTXBdLE4tHUXFW";


    String TAG = "TAG123";
    String txnid = "001", amount = "1.0", productinfo = "test", firstname = "shyam", email = "shyam@entitcs.com", user_credentials = "", udf1 = "1", udf2 = "1", udf3 = "1", udf4 = "1", udf5 = "1", offerKey = "0", cardBin = "0", phone = "7224857968";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateHashFromServer();
    }

    public void startPayment(View v) {
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, udf1);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, udf2);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, phone);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, udf4);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, udf5);

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setKey(strKey).setIsProduction(false).setTransactionId(txnid).setAmount(amount).setProductInfo(productinfo).setFirstName(firstname).setEmail(email)
                //.setPhone(phone)
                .setSurl("https://payuresponse.firebaseapp.com/success").setFurl("https://payuresponse.firebaseapp.com/failure").setAdditionalParams(additionalParams);

        PayUPaymentParams payUPaymentParams = builder.build();

        PayUCheckoutPro.open(this, payUPaymentParams, new PayUCheckoutProListener() {

            @Override
            public void onPaymentSuccess(Object response) {
                //Cast response object to HashMap
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                Log.e(TAG, "onPaymentSuccess: ");
                Log.e(TAG, ":payuResponse " + payuResponse);
                Log.e(TAG, ":merchantResponse " + merchantResponse);
            }

            @Override
            public void onPaymentFailure(Object response) {
                //Cast response object to HashMap
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                Log.e(TAG, "onPaymentFailure: " + payuResponse);
            }

            @Override
            public void onPaymentCancel(boolean isTxnInitiated) {
                Log.e(TAG, "onPaymentCancel: " + isTxnInitiated);
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Log.d(TAG, "onError: " + errorResponse.getErrorMessage());
                Toast.makeText(MainActivity.this, String.valueOf(errorResponse.getErrorMessage()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                Log.e(TAG, "setWebViewProperties: ");
                //For setting webview properties, if any. Check Customized Integration section for more details on this
            }

            @Override
            public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                Log.e(TAG, "values: " +valueMap);
                if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                    /**
                     * Do not generate hash from local, it needs to be calculated from server side only
                     * Here, hashString contains hash created from your server side
                     */
                    hashGenerationListener.onHashGenerated(dataMap);
                }
            }
        });
    }

    HashMap<String, String> dataMap = new HashMap<>();

    void generateHashFromServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.generateHashForPayment), response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                dataMap.put("get_sdk_configuration", jsonObject.getString("get_sdk_configuration_hash"));
                dataMap.put("payment_related_details_for_mobile_sdk", jsonObject.getString("payment_related_details_for_mobile_sdk_hash"));
//                dataMap.put("payment", jsonObject.getString("payment_hash"));
//                dataMap.put("get_merchant_ibibo_codes", jsonObject.getString("get_merchant_ibibo_codes_hash"));
//                dataMap.put("vas_for_mobile_sdk", jsonObject.getString("vas_for_mobile_sdk_hash"));
//                dataMap.put("emi", jsonObject.getString("emi_hash"));
//                dataMap.put("verify_payment", jsonObject.getString("verify_payment_hash"));
                Log.e(TAG, "dataMap: "+dataMap );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(TAG, "generateHashFromServer: " + error.toString());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("txnid", txnid);
                param.put("amount", amount);
                param.put("productinfo", productinfo);
                param.put("firstname", firstname);
                param.put("email", email);
                param.put("phone", phone);
                param.put("user_credentials", user_credentials);
                param.put("udf1", udf1);
                param.put("udf2", udf2);
                param.put("udf3", udf3);
                param.put("udf4", udf4);
                param.put("udf5", udf5);
                param.put("offerKey", offerKey);
                param.put("cardBin", cardBin);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}