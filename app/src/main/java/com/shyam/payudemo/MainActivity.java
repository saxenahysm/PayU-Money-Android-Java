package com.shyam.payudemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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
    String strKey = "oZ7oo9";
    String strSalt = "UkojH5TS";
    String TAG = "TAG123";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateHash();
    }

    public void startPayment(View v) {
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");
        // to show saved sodexo card
//        additionalParams.put(PayUCheckoutProConstants.SODEXO_SOURCE_ID, "srcid123");

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(amount).setIsProduction(true).setProductInfo(productinfo).setKey(strKey).setPhone(phone).setTransactionId(txnid).setFirstName(firstname).setEmail(email).setSurl("https://payuresponse.firebaseapp.com/success").setFurl("https://payuresponse.firebaseapp.com/failure");
//                .setUserCredential("");
//        .setAdditionalParams(<HashMap<String,Object>>); //Optional, can contain any additional PG params
        PayUPaymentParams payUPaymentParams = builder.build();

        PayUCheckoutPro.open(this, payUPaymentParams, new PayUCheckoutProListener() {

            @Override
            public void onPaymentSuccess(Object response) {
                //Cast response object to HashMap
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                Log.e(TAG, "onPaymentSuccess:payuResponse " + payuResponse);
                Log.e(TAG, "onPaymentSuccess:merchantResponse " + merchantResponse);
            }

            @Override
            public void onPaymentFailure(Object response) {
                //Cast response object to HashMap
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
            }

            @Override
            public void onPaymentCancel(boolean isTxnInitiated) {
                Log.e(TAG, "onPaymentCancel: ");
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
                Log.e(TAG, "onError: ");
            }

            @Override
            public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                //For setting webview properties, if any. Check Customized Integration section for more details on this
            }

            @Override
            public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                    //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put(hashName, hashString);
                    hashGenerationListener.onHashGenerated(dataMap);
                }
            }
        });
    }

    String txnid = "001", amount = "1.0", productinfo = "test", firstname = "shyam", email = "shyam@entitcs.com", user_credentials = "", udf1 = "", udf2 = "", udf3 = "", udf4 = "", udf5 = "", offerKey = "", cardBin = "", phone = "";
    String hashString = "";

    void generateHash() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://nagarnigamprojects.in/morraipur/websiervice/webservice/payUMoneyHashGenerater.php",
                response -> {
                    Log.e(TAG, "generateHash:response " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        hashString = jsonObject.getString("payment_hash");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e(TAG, "generateHash: " + error.toString());
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("txnid", txnid);
                param.put("amount", amount);
                param.put("productinfo", productinfo);
                param.put("firstname", firstname);
                param.put("email", email);
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