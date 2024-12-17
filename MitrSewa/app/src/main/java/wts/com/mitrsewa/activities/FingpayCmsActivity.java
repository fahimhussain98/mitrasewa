package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.tapits.ubercms_bc_sdk.LoginScreen;
import com.tapits.ubercms_bc_sdk.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class FingpayCmsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    String userId, deviceId, deviceInfo, name, mobileNo;
    String lat, longi;
    String merchantId, merchantPassword, superMerchantId;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingpay_cms);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FingpayCmsActivity.this);

        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        name = sharedPreferences.getString("username", null);
        mobileNo = sharedPreferences.getString("mobileno", null);

        lat = getIntent().getStringExtra("lat");
        longi = getIntent().getStringExtra("longi");

        getMatmCredentials();

    }

    private void getMatmCredentials() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingpayCmsActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getMATMCredential(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        JSONObject dataObject = responseObject.getJSONObject("data");
                        merchantId = dataObject.getString("userloginid");
                        merchantPassword = dataObject.getString("userloginpassword");
                        superMerchantId = dataObject.getString("supermerchatid");

                        String imei = getImei();

                        double dLat = Double.parseDouble(lat);
                        double dLong = Double.parseDouble(longi);

                        //    String uniqueId ="UNID"+ System.currentTimeMillis();
                        String uniqueId = generateUniqueId(mobileNo);

                        Intent in = new Intent(FingpayCmsActivity.this, LoginScreen.class);
                        in.putExtra(Constants.MERCHANT_ID, merchantId);
                        in.putExtra(Constants.SECRET_KEY, "8d0032e3d4fed4cc4f9061c72075a9ca719f9058362f88f946b5d09807033c5c");
                        in.putExtra(Constants.TYPE_REF, Constants.BILLERS);
                        //   in.putExtra(Constants.TYPE_REF, Constants.REF_ID);
                        in.putExtra(Constants.AMOUNT, "0");
                        in.putExtra(Constants.REMARKS, "Testing");
                        in.putExtra(Constants.MOBILE_NUMBER, mobileNo);
                        in.putExtra(Constants.SUPER_MERCHANTID, superMerchantId);
                        in.putExtra(Constants.IMEI, imei);
                        in.putExtra(Constants.LATITUDE, dLat);
                        in.putExtra(Constants.LONGITUDE, dLong);
                        in.putExtra(Constants.NAME, name);
                        in.putExtra(Constants.REFERENCE_ID, uniqueId);
                        startActivityForResult(in, REQUEST_CODE);

                        pDialog.dismiss();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showMessageDialog("Something went wrong. Please try after some time");
                    }
                } else {
                    pDialog.dismiss();
                    showMessageDialog("Something went wrong. Please try after some time");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showMessageDialog("Something went wrong." + t.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            Bundle bundle = data.getExtras();

            //   Toast.makeText(FingpayCmsActivity.this, "" + bundle.toString(), Toast.LENGTH_SHORT).show();
            String message = data.getStringExtra(Constants.MESSAGE);
            boolean status = data.getBooleanExtra(Constants.TRANS_STATUS, false);
            String txnId = data.getStringExtra(Constants.TXN_ID);
            String timestamp = data.getStringExtra(Constants.TRANS_TIMESTAMP);
            String txnAmount = data.getStringExtra(Constants.TRANS_AMOUNT);
            String remarks = data.getStringExtra(Constants.REMARKS);
            String rrn = data.getStringExtra(Constants.RRN);
            new androidx.appcompat.app.AlertDialog.Builder(FingpayCmsActivity.this).setMessage("Message : " + message + "\nStatus : " + status + "\nTxnId : " + txnId +
                            "\nDate : " + timestamp + "\nAmount : " + txnAmount + "\nRemarks : " + remarks + "\nRRN : " + rrn)
                    .setTitle("Message!!!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
            //   .show()
            ;

            finish();

        }
    }

    private void showMessageDialog(String message) {
        final AlertDialog messageDialog = new AlertDialog.Builder(FingpayCmsActivity.this).create();
        final LayoutInflater inflater = LayoutInflater.from(FingpayCmsActivity.this);
        View convertView = inflater.inflate(R.layout.message_dialog, null);
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        messageDialog.setCancelable(false);
        messageDialog.setView(convertView);

        ImageView imgClose = convertView.findViewById(R.id.img_close);
        TextView tvMessage = convertView.findViewById(R.id.tv_message);
        Button btnTryAgain = convertView.findViewById(R.id.btn_try_again);

        imgClose.setOnClickListener(view -> {
            messageDialog.dismiss();
            finish();
        });
        btnTryAgain.setOnClickListener(view -> {
            messageDialog.dismiss();
            finish();
        });
        tvMessage.setText(message);

        messageDialog.show();
    }

    public static boolean isValidString(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 0)
                return true;
        }
        return false;
    }

    public static String generateUniqueId(String mobile) {

        long timeStamp = System.currentTimeMillis();
        String timeStampStr = String.valueOf(timeStamp);
        int length = timeStampStr.length();
        int subLength = length - 6;
        timeStampStr = timeStampStr.substring(subLength, length);

        String subMobileNo = mobile.substring(0, 6);
        //  return "UNID" + subMobileNo + "_" + timeStampStr;
        return subMobileNo + timeStampStr;  // because in txnid special character not allow  like _ , /

    }

    @SuppressLint("HardwareIds")
    public String getImei() {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(FingpayCmsActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            imei = telephonyManager.getDeviceId();

        } catch (Exception e) {
            if (!isValidString(imei))
                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return imei;
    }

}