package com.shyam.payudemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {
    String TAG ="TAG11";
    Button paynow;
    private final String email = "user@gmail.com";
    private final String phone = "9876543210";
    private final String merchantName = "Test";
    private final String surl = "https://payu.herokuapp.com/success";
    private final String furl = "https://payu.herokuapp.com/failure";
    private final String amount = "1.0";

    private final String prodKey = "smsplus";
    private final String prodSalt = "1b1b0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
    }

    public void initPayment(View view) {
        initUiSdk(preparePayUBizParams());
    }

    private PayUPaymentParams preparePayUBizParams() {
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");
        //additionalParams.put(PayUCheckoutProConstants.SODEXO_SOURCE_ID, sodexosrcid);


        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(amount)
                .setIsProduction(false)
                .setProductInfo("Test-Tax")
                .setKey(prodKey)
                .setPhone(phone)
                .setTransactionId("Test-"+ System.currentTimeMillis())
                .setFirstName("shyam")
                .setEmail(email)
                .setSurl(surl)
                .setFurl(furl)
                .setUserCredential(prodKey + ":user@gmail.com")
                .setAdditionalParams(additionalParams);
        PayUPaymentParams payUPaymentParams = builder.build();
        return payUPaymentParams;
    }

    private void initUiSdk(PayUPaymentParams payUPaymentParams) {
        PayUCheckoutProConfig payUCheckoutProConfig = new PayUCheckoutProConfig();
        payUCheckoutProConfig.setMerchantName("test");
        payUCheckoutProConfig.setMerchantLogo(R.mipmap.ic_launcher_round);
        payUCheckoutProConfig.setAutoApprove(true);
        PayUCheckoutPro.open(this, payUPaymentParams, payUCheckoutProConfig, new PayUCheckoutProListener() {

            @Override
            public void onPaymentSuccess(Object response) {
//                showAlertDialog(response);
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                Log.e(TAG, "CP_PAYU_RESPONSE: " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE));
                Log.e(TAG, "CP_MERCHANT_RESPONSE: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));
            }

            @Override
            public void onPaymentFailure(Object response) {
                showAlertDialog(response);
            }

            @Override
            public void onPaymentCancel(boolean isTxnInitiated) {
                Toast.makeText(PaymentActivity.this, "Transaction cancelled by user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
                Toast.makeText(PaymentActivity.this, "Msg--" + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                //For setting webview properties, if any. Check Customized Integration section for more details on this
            }

            @Override
            public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                String hashType = valueMap.get(PayUCheckoutProConstants.CP_HASH_TYPE);
                if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                    //Generate Hash from your backend here
                    String salt = prodSalt;
                    if (valueMap.containsKey(PayUCheckoutProConstants.CP_POST_SALT))
                        salt = salt + "" + (valueMap.get(PayUCheckoutProConstants.CP_POST_SALT));

                    String hash = null;

                    //Calculate SHA-512 Hash here
                    hash = calculateHash(hashData + salt);

                    HashMap<String, String> dataMap = new HashMap<>();
                    Log.e(TAG, "hashData: " + hashData);
                    dataMap.put(hashName, hash);
                    hashGenerationListener.onHashGenerated(dataMap);
                }
            }
        });
    }

    private String calculateHash(String hashString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            return getHexString(mdbytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getHexString(byte[] array) {
        StringBuilder hash = new StringBuilder();
        for (byte hashByte : array) {
            hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        return hash.toString();
    }

    private void showAlertDialog(Object response) {
        HashMap<String, Object> result = (HashMap<String, Object>) response;
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Payu's Data : " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE)
                        + "\n\n\n Merchant's Data: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE))
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Log.e(TAG, "CP_PAYU_RESPONSE: " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE));
//                    Log.e(TAG11, "CP_MERCHANT_RESPONSE: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));
                    try {
                        JSONObject jsonObject=new JSONObject(String.valueOf(result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE)));
                        if (jsonObject.has("result")) {
                            JSONObject upiJson = jsonObject.getJSONObject("result");
                            Log.e(TAG, "upiJson: "+upiJson );
                        }else {
                            Log.e(TAG, "status: " + jsonObject.getString("status"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }).show();
    }
}
