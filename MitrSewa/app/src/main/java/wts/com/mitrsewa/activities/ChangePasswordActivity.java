package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class ChangePasswordActivity extends AppCompatActivity {

    ImageView imgHideShowPassword,imgHideShowPassword2,imgHideShowPassword3;
    boolean isShowPassword = true;
    boolean isShowPassword2 = true;
    boolean isShowPassword3 = true;
    Button btnChangePassword;
    EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    String currentPassword, newPassword;

    SharedPreferences sharedPreferences;
    String userid, deviceId, deviceInfo, emailId, mobileno;

    AlertDialog authCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ChangePasswordActivity.this, R.color.purple1));
        //////CHANGE COLOR OF STATUS BAR

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        emailId = sharedPreferences.getString("email", null);
        mobileno = sharedPreferences.getString("mobileno", null);

        imgHideShowPassword.setOnClickListener(view -> {
            if (isShowPassword) {
                etCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isShowPassword = false;

            } else {
                etCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isShowPassword = true;

            }
        });
        imgHideShowPassword2.setOnClickListener(view -> {
            if (isShowPassword2) {
                etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isShowPassword2 = false;

            } else {
                etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isShowPassword2 = true;

            }
        });
        imgHideShowPassword3.setOnClickListener(view -> {
            if (isShowPassword3) {
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isShowPassword3 = false;

            } else {
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isShowPassword3 = true;

            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPassword = etCurrentPassword.getText().toString().trim();
                newPassword = etNewPassword.getText().toString().trim();

                if (checkInputs()) {

                    if (checkInternetState()) {
                      //  changePassword();
                      //  checkOtp();
                        showAuthCodeDialog();
                    } else {
                        showSnackBar();
                    }
                }

            }
        });
    }

    private void checkOtp() {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangePasswordActivity.this).create();
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
                           String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setMessage(message)
                                    .show();
                        }

                        else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ChangePasswordActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(ChangePasswordActivity.this).create();
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
                    changePassword();


                } else {
                    new AlertDialog.Builder(ChangePasswordActivity.this)
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
        authCodeDialog = new AlertDialog.Builder(ChangePasswordActivity.this).create();
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
                            changePassword();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(ChangePasswordActivity.this)
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
                            new androidx.appcompat.app.AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(ChangePasswordActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(ChangePasswordActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    private void changePassword() {
        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangePasswordActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().changePassword(AUTH_KEY, deviceId, deviceInfo, userid, currentPassword, newPassword);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        progressDialog.dismiss();

                        String statusCode = jsonObject.getString("statuscode");
                        String dialogTitle = jsonObject.getString("status");
                        String dialogMessage = jsonObject.getString("data");
                       if (statusCode.equalsIgnoreCase("TXN"))
                       {
                           new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(dialogTitle)
                                   .setMessage(dialogMessage)
                                   .setCancelable(false)
                                   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           finish();
                                       }
                                   }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                       @Override
                                       public void onCancel(DialogInterface dialog) {
                                           finish();
                                       }
                                   }).show();

                       }
                       else
                       {
                           new AlertDialog.Builder(ChangePasswordActivity.this).setTitle("Alert")
                                   .setMessage(dialogMessage)
                                   .setPositiveButton("Ok", null).show();
                       }
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangePasswordActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangePasswordActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong")
                        .setPositiveButton("Ok", null).show();
            }
        });

    }

    private void initViews() {

        imgHideShowPassword = findViewById(R.id.img_hide_show_pass);
        imgHideShowPassword2 = findViewById(R.id.img_hide_show_pass2);
        imgHideShowPassword3 = findViewById(R.id.img_hide_show_pass3);

        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(etCurrentPassword.getText())) {
            if (!TextUtils.isEmpty(etNewPassword.getText())) {
                if (etNewPassword.getText().toString().length() > 5 ){
                    if (isValidPassword(etNewPassword.getText().toString()))
                    {
                        if (!TextUtils.isEmpty(etConfirmPassword.getText())) {
                            if (etNewPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
                                if (!currentPassword.equals(newPassword)) {
                                    return true;

                                } else {
                                    new androidx.appcompat.app.AlertDialog.Builder(ChangePasswordActivity.this).setMessage("password could not be same as previous password").show();
                                    return false;
                                }
                            } else {
                                Toast.makeText(this, "Password and Confirm Password must be same", Toast.LENGTH_LONG).show();
                                return false;
                            }
                        } else {
                            etConfirmPassword.setError("Confirm Password can't be empty.");
                            return false;
                        }
                    }
                    else
                    {
                        etNewPassword.setError("New password must contain capital letter, number and special character");
                        return false;
                    }
                }
                else
                {
                    etNewPassword.setError("New password should be minimum six character");
                    return false;
                }

            } else {
                etNewPassword.setError("New Password can't be empty.");
                return false;
            }
        } else {
            etCurrentPassword.setError("Current Password can't be empty.");
            return false;
        }
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

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.change_password_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

}