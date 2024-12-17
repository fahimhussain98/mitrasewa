package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.MainActivity;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class UpiQrCodeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String userId, deviceId, deviceInfo, userName,mobileNo;
    TextView tvName, tvMobileNo;
    EditText etUpiId, etAmount;
    String mode;
    AppCompatButton btnSubmit;
    String uniqueTxnId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_qr_code);
        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UpiQrCodeActivity.this);
        userId = sharedPreferences.getString("userid", null);
        userName = sharedPreferences.getString("username", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mobileNo = sharedPreferences.getString("mobileno", null);

        tvName.setText(userName);
        tvMobileNo.setText(mobileNo);

        mode=getIntent().getStringExtra("mode");

        if (mode.equalsIgnoreCase("1")) {
            etUpiId.setVisibility(View.GONE);
        }

        btnSubmit.setOnClickListener(v ->
        {
            if (mode.equalsIgnoreCase("1")) {
                if (!TextUtils.isEmpty(etAmount.getText().toString())) {
                    doTransaction();
                } else {
                    etAmount.setError("Required");
                }
            } else {
                if (!TextUtils.isEmpty(etUpiId.getText().toString())) {
                    if (!TextUtils.isEmpty(etAmount.getText().toString())) {
                        doTransaction();
                    } else {
                        etAmount.setError("Required");
                    }
                } else {
                    etUpiId.setError("Required");
                }
            }
        });

    }

    private void doTransaction() {
        final AlertDialog pDialog = new AlertDialog.Builder(UpiQrCodeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String upiId=etUpiId.getText().toString().trim();
        String amount=etAmount.getText().toString().trim();

        if (mode.equalsIgnoreCase("1"))
        {
            upiId="NA";
        }

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().doUpiQrTransaction(AUTH_KEY,userId,deviceId,deviceInfo,userName,upiId,
                mobileNo,amount,mode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String responseCode=responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN"))
                        {
                            pDialog.dismiss();
                            String qrCodeUrl=responseObject.getString("data");
                            uniqueTxnId=responseObject.getString("status");
                            qrCodeUrl=qrCodeUrl.replace("\\/","/");
                            showDialog(qrCodeUrl);
                        }
                        else
                        {
                            String message=responseObject.getString("data");
                            pDialog.dismiss();
                            showErrorDialog(message);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showErrorDialog("Something went wrong.");
                    }
                }
                else
                {
                    pDialog.dismiss();
                    showErrorDialog("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });

    }

    private void showDialog(String qrCodeUrl) {
        final AlertDialog dialog = new AlertDialog.Builder(UpiQrCodeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.upi_qr_layout, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setView(convertView);
        dialog.show();

        ImageView imgQrCode=dialog.findViewById(R.id.img_qr_code);
        AppCompatButton btnSubmit=dialog.findViewById(R.id.btn_submit);

        //Picasso.get().load(qrCodeUrl).resize(6000,6000).onlyScaleDown().into(imgQrCode);
        Glide.with(UpiQrCodeActivity.this).load(qrCodeUrl).into(imgQrCode);

        btnSubmit.setOnClickListener(v->
        {
            //finish();
            getRecipt();

        });


    }

    private void getRecipt() {
        final AlertDialog pDialog = new AlertDialog.Builder(UpiQrCodeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().getQrReceipt(AUTH_KEY,userId,deviceId,deviceInfo,
                uniqueTxnId);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String responseCode=responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN"))
                        {
                            JSONArray dataArray=responseObject.getJSONArray("data");
                            JSONObject dataObject=dataArray.getJSONObject(0);

                            String upiId=dataObject.getString("UpiId");
                            String openingBal=dataObject.getString("OpeningBal");
                            String amount=dataObject.getString("Amount");
                            String commission=dataObject.getString("Commission");
                            String tds=dataObject.getString("Tds");
                            String surcharge=dataObject.getString("Surcharge");
                            String gst=dataObject.getString("Gst");
                            String closingBal=dataObject.getString("ClosingBal");
                            String uniqueTransactionId=dataObject.getString("UniqueTransactionId");
                            String status=dataObject.getString("Status");

                            new AlertDialog.Builder(UpiQrCodeActivity.this)
                                    .setTitle("Payment Status")
                                    .setCancelable(false)
                                    .setMessage("UPI ID : "+upiId+
                                            "\nOpening Balance : "+openingBal+
                                            "\nAmount : "+amount+
                                            "\nCommission : "+commission+
                                            "\ntds : "+tds+
                                            "\nSurcharge : "+surcharge+
                                            "\nGst : "+gst+
                                            "\nClosing Balance : "+closingBal+
                                            "\nTransaction ID : "+uniqueTransactionId+
                                            "\nStatus : "+status)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                            pDialog.dismiss();
                        }
                        else
                        {
                            String message=responseObject.getString("data");
                            pDialog.dismiss();
                            showErrorDialog(message);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showErrorDialog("Something went wrong.");
                    }
                }
                else
                {
                    pDialog.dismiss();
                    showErrorDialog("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });
    }


    private void showErrorDialog(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(UpiQrCodeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",null)
                .show();
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_name);
        tvMobileNo = findViewById(R.id.tv_mobile);
        etUpiId = findViewById(R.id.et_upi_id);
        etAmount = findViewById(R.id.et_amount);
        btnSubmit = findViewById(R.id.btn_submit);
    }
}