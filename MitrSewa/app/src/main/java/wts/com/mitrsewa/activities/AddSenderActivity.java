package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.chaos.view.PinView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class AddSenderActivity extends AppCompatActivity {

    ImageView imgClose;
    TextView tvMobile;
    EditText etFirstName, etLastName, etAddress, etPinCode,etOtp;
    Button btnRegister;
    String mobileNumber,stateWiseCode;
    String remitterId;
    AlertDialog addSenderOTPDialog;

    SharedPreferences sharedPreferences;
    String deviceId,deviceInfo;
    String userId;

    boolean isExpressDmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sender);

        inhitViews();

        isExpressDmt=SenderValidationActivity.isExpressDmt;

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AddSenderActivity.this);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);
        userId=sharedPreferences.getString("userid",null);

        mobileNumber = getIntent().getStringExtra("mobileNo");
        stateWiseCode = getIntent().getStringExtra("stateWiseCode");
        tvMobile.setText(mobileNumber);

        //_____________________data come from GetSenderWithEKYC API__________________________________________________
        Intent intent = getIntent();
        String otpStatus = intent.getStringExtra("otpStatus");
        String stateResp = intent.getStringExtra("stateResp");
        String ekycId = intent.getStringExtra("ekycId");
        String remitterId = intent.getStringExtra("remitterId");

        Log.d("EKYCActivity","dataFromSenderValidattion::"
                +" otpStatus : \n "+ otpStatus
                +" stateResp : \n"+ stateResp
                +" stateResp : \n"+ stateResp
                +" ekycId : \n"+ ekycId);
        //______________________data come from GetSenderWithEKYC API END_____________________________________________

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (!TextUtils.isEmpty(etFirstName.getText()) && !TextUtils.isEmpty(etLastName.getText())
                            && !TextUtils.isEmpty(etAddress.getText()) && !TextUtils.isEmpty(etOtp.getText()) ) {
                        if (etPinCode.getText().length() == 6) {


                            addSender();
                        } else {
                            etPinCode.setError("Invalid pincode");
                        }
                    } else {
                        showSnackbar("All Fields Are Mandatory!!!");
                    }
                } else {
                    showSnackbar("No Internet");
                }
            }
        });
    }

    private void addSender() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddSenderActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();


        Call<JsonObject> call=null;

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pincode = etPinCode.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();

        if (isExpressDmt)
        {

            call = RetrofitClient.getInstance().getApi().addSenderExpress(AUTH_KEY,deviceId,deviceInfo,userId,
                    mobileNumber, firstName, lastName, pincode, address, otp);

        }
        else
        {
            pincode=pincode+"$"+stateWiseCode;

            call = RetrofitClient.getInstance().getApi().addSender(AUTH_KEY,deviceId,deviceInfo,userId,
                    mobileNumber, firstName, lastName, pincode, address, otp);
        }


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN") || responseCode.equalsIgnoreCase("ERR")) {

                            String message=jsonObject.getString("status");
                            //showAddSenderOtpDialog();
                            new AlertDialog.Builder(AddSenderActivity.this).
                                    setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();

                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddSenderActivity.this).
                                    setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();
                        new AlertDialog.Builder(AddSenderActivity.this).
                                setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();

                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddSenderActivity.this).
                            setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddSenderActivity.this).
                        setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();

            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showAddSenderOtpDialog() {
        final View addSenderOTPDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        addSenderOTPDialog = new AlertDialog.Builder(AddSenderActivity.this).create();
        addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderOTPDialog.setCancelable(false);
        addSenderOTPDialog.setView(addSenderOTPDialogView);
        addSenderOTPDialog.show();

        ImageView imgClose = addSenderOTPDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderOTPDialogView.findViewById(R.id.text_user_registration);
        Button btnCancel = addSenderOTPDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderOTPDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderOTPDialogView.findViewById(R.id.btn_resend_otp);
        final EditText etOTP = addSenderOTPDialogView.findViewById(R.id.et_otp);

        etOTP.setHint("OTP");
        tvTitle.setText("OTP Verification");

        btnResendOtp.setVisibility(View.GONE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderOTPDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderOTPDialog.dismiss();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = etOTP.getText().toString().trim();
                verifySenderOtp(otp);
            }
        });
    }

    private void verifySenderOtp(String otp) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddSenderActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call=RetrofitClient.getInstance().getApi().verifySender(AUTH_KEY,deviceId,deviceInfo,remitterId,userId,mobileNumber,otp);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {


                           /* int status=transactionObject.getInt("statuscode");
                            if (status==1)
                            {

                            }
                            else
                            {

                            }*/
                            String message=jsonObject.getString("data");
                            new AlertDialog.Builder(AddSenderActivity.this).
                                    setTitle("Status")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();


                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddSenderActivity.this).
                                    setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();
                        new AlertDialog.Builder(AddSenderActivity.this).
                                setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();

                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddSenderActivity.this).
                            setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddSenderActivity.this).
                        setTitle("Alert!!!")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();

            }
        });
    }

    private void inhitViews() {
        imgClose = findViewById(R.id.img_close);
        tvMobile = findViewById(R.id.tv_mobile_number);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etAddress = findViewById(R.id.et_address);
        etPinCode = findViewById(R.id.et_pin_code);
        btnRegister = findViewById(R.id.btn_register);
        etOtp = findViewById(R.id.et_otp);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.add_sender_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

}