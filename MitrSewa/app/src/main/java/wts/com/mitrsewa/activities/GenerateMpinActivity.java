package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class GenerateMpinActivity extends AppCompatActivity {

    EditText etMpin, etConfirmMpin;
    Button btnSubmit;
    String userid, username, usertype, mobileno, pancard, aadharCard, emailId, loginUserName, password,firmName,address,dob,city;

    String deviceId, deviceInfo;

    AlertDialog authCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_mpin);

        initViews();

        userid = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("userName");
        usertype = getIntent().getStringExtra("userType");
        mobileno = getIntent().getStringExtra("mobileNo");
        pancard = getIntent().getStringExtra("panCard");
        aadharCard = getIntent().getStringExtra("aadharCard");
        emailId = getIntent().getStringExtra("emailId");
        deviceId = getIntent().getStringExtra("deviceId");
        deviceInfo = getIntent().getStringExtra("deviceInfo");
        loginUserName = getIntent().getStringExtra("loginUserName");
        password = getIntent().getStringExtra("password");
        firmName = getIntent().getStringExtra("firmName");
        address = getIntent().getStringExtra("address");
        dob = getIntent().getStringExtra("dob");
        city = getIntent().getStringExtra("city");

        btnSubmit.setOnClickListener(v ->
        {
            if (checkInternetState()) {
                if (checkInputs()) {
                  //  checkOtp();
                    showAuthCodeDialog();
                }

            } else {
                showSnackbar();
            }
        });

    }

    private void checkOtp() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(AUTH_KEY, userid, deviceId, deviceInfo, emailId, mobileno);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();
                            String otp = responseObject.getString("data");
                            showOtpDialog(otp);
                        }

                        else if (responseCode.equalsIgnoreCase("NPA"))
                        {
                            generateMpin();
                        }

                        else {
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");

                            new AlertDialog.Builder(GenerateMpinActivity.this)
                                    .setMessage(message)
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(GenerateMpinActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(GenerateMpinActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(GenerateMpinActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(GenerateMpinActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        final EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOtp();
                addSenderDialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        tvTitle.setText("OTP Verification");
        etOtp.setHint("OTP");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etOtp.getText())) {
                    String userOtp = etOtp.getText().toString().trim();
                    if (userOtp.equals(Otp)) {
                        generateMpin();


                    } else {
                        new AlertDialog.Builder(GenerateMpinActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showAuthCodeDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        authCodeDialog = new AlertDialog.Builder(GenerateMpinActivity.this).create();
        authCodeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        authCodeDialog.setCancelable(false);
        authCodeDialog.setView(addSenderDialogView);
        authCodeDialog.setCancelable(false);
        authCodeDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        final EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);

        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);
        btnResendOtp.setVisibility(View.GONE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeDialog.dismiss();
            }
        });
        tvTitle.setText("AuthCode Verification");
        etOtp.setHint("Authentication Code");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etOtp.getText())) {
                    String userOtp = etOtp.getText().toString().trim();
                    validateAuthCode(userOtp);
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    private void validateAuthCode(String authCode) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().validateAuthentication(AUTH_KEY, userid, deviceId, deviceInfo, authCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();
                            authCodeDialog.dismiss();
                            generateMpin();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(GenerateMpinActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(GenerateMpinActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(GenerateMpinActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(GenerateMpinActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(GenerateMpinActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    private void generateMpin() {
        ProgressDialog progressDialog = new ProgressDialog(GenerateMpinActivity.this);
        progressDialog.setMessage("Generating MPIN...");
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String mpin = etMpin.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().generateMpin(AUTH_KEY, userid, deviceId, deviceInfo, "mpin", mpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = responseObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {
                            Toast.makeText(GenerateMpinActivity.this, "MPIN Generated Successful", Toast.LENGTH_LONG).show();

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GenerateMpinActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userid);
                            editor.putString("username", username);
                            editor.putString("pancard", pancard);
                            editor.putString("mobileno", mobileno);
                            editor.putString("usertype", usertype);
                            editor.putString("deviceId", deviceId);
                            editor.putString("deviceInfo", deviceInfo);
                            editor.putString("adharcard", aadharCard);
                            editor.putString("loginUserName", loginUserName);
                            editor.putString("password", password);
                            editor.putString("email", emailId);
                            editor.putString("firmName", firmName);
                            editor.putString("address", address);
                            editor.putString("dob", dob);
                            editor.putString("city", city);
                            editor.apply();

                            Intent intent = new Intent(GenerateMpinActivity.this, MPINActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(GenerateMpinActivity.this)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(GenerateMpinActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.\nPlease try after sometime.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(GenerateMpinActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.\nPlease try after sometime.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(GenerateMpinActivity.this)
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private boolean checkInputs() {
        if (etMpin.getText().toString().trim().length() == 6) {
            if (etMpin.getText().toString().trim().equalsIgnoreCase(etConfirmMpin.getText().toString().trim())) {
                return true;
            } else {
                etConfirmMpin.setError("Mismatched");
            }
        } else {
            new AlertDialog.Builder(GenerateMpinActivity.this)
                    .setMessage("Six digit MPIN is must")
                    .show();
        }
        return false;
    }

    private void initViews() {
        etMpin = findViewById(R.id.et_mpin);
        etConfirmMpin = findViewById(R.id.et_confirm_mpin);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.generate_mpin_layout), "No Internet", Snackbar.LENGTH_LONG);
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