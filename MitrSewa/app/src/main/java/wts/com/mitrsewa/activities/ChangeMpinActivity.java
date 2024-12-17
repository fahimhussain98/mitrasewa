package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

public class ChangeMpinActivity extends AppCompatActivity {

    Button btnChangeMpin;
    EditText  etNewMpin, etConfirmMpin;
    String newMpin;

    SharedPreferences sharedPreferences;
    String userid,emailId,mobileno;
    String deviceId, deviceInfo;
    String pinType;

    AlertDialog authCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mpin);

        inhitViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangeMpinActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        emailId = sharedPreferences.getString("email", null);
        mobileno = sharedPreferences.getString("mobileno", null);

        pinType=getIntent().getStringExtra("pinType");

        btnChangeMpin.setOnClickListener(v -> {
            if (checkInputs()) {

                if (checkInternetState()) {
                  //  checkOtp();
                    showAuthCodeDialog();

                } else {
                    showSnackbar();
                }
            }
        });
    }
    private void checkOtp() {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangeMpinActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(AUTH_KEY, userid, deviceId, deviceInfo, emailId, mobileno);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
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
                                changeMpin();
                            }

                        else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangeMpinActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangeMpinActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(ChangeMpinActivity.this).create();
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

        btnResendOtp.setOnClickListener(v -> {
            checkOtp();
            addSenderDialog.dismiss();
        });

        imgClose.setOnClickListener(v -> addSenderDialog.dismiss());
        tvTitle.setText("OTP Verification");
        etOtp.setHint("OTP");
        btnCancel.setOnClickListener(v -> addSenderDialog.dismiss());
        btnSubmit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etOtp.getText())) {
                String userOtp = etOtp.getText().toString().trim();
                if (userOtp.equals(Otp)) {
                    changeMpin();


                } else {
                    new AlertDialog.Builder(ChangeMpinActivity.this)
                            .setMessage("Wrong Otp")
                            .show();
                }
            } else {
                etOtp.setError("Required");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showAuthCodeDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        authCodeDialog = new AlertDialog.Builder(ChangeMpinActivity.this).create();
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
                            changeMpin();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(ChangeMpinActivity.this)
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
                            new androidx.appcompat.app.AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(ChangeMpinActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(ChangeMpinActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(ChangeMpinActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    private void changeMpin() {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangeMpinActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();

        newMpin = etNewMpin.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().generateMpin(AUTH_KEY, userid, deviceId, deviceInfo, pinType, newMpin);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();


                            String dialogTitle = jsonObject.getString("status");
                            String dialogMessage = jsonObject.getString("data");
                            new AlertDialog.Builder(ChangeMpinActivity.this).setTitle(dialogTitle)
                                    .setMessage(dialogMessage)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", (dialog, which) -> finish()).setOnCancelListener(dialog -> finish()).show();


                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();
                            String dialogMessage = jsonObject.getString("data");

                            new AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setMessage(dialogMessage)
                                    .setPositiveButton("Ok", (dialog, which) -> {

                                    }).show();

                        } else {
                            progressDialog.dismiss();

                            new AlertDialog.Builder(ChangeMpinActivity.this).setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangeMpinActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangeMpinActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong")
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private void inhitViews() {
        etNewMpin = findViewById(R.id.et_new_mpin);
        etConfirmMpin = findViewById(R.id.et_confirm_mpin);
        btnChangeMpin = findViewById(R.id.btn_change_mpin);
    }

    private boolean checkInputs() {
            if (etNewMpin.getText().toString().trim().length()==6) {

                if (!TextUtils.isEmpty(etConfirmMpin.getText())) {
                    if (etNewMpin.getText().toString().trim().equals(etConfirmMpin.getText().toString().trim())) {
                        return true;
                    } else {
                        Toast.makeText(this, "PIN and Confirm PIN must be same", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    etConfirmMpin.setError("Required");
                    return false;
                }

            } else {
                etNewMpin.setError("PIN must be of 6 digit");
                return false;
            }

    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.change_password_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}