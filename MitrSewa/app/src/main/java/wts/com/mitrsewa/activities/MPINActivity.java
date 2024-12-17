package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chaos.view.PinView;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.BuildConfig;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class MPINActivity extends AppCompatActivity {
    TextView tvUserName;
    PinView mpinPinView;
    Button btnSubmit, btnForgetMpin,btnPassword;
    SharedPreferences sharedPreferences;
    String userid, username, usertype, mobileno, pancard, aadharCard, emailId, loginUserName, password,firmName,address,dob,city;
    String deviceId, deviceInfo;
    String versionCodeStr;

    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD
    EditText etForgetUsername, etForgetMobile;
    AppCompatButton btnCancel, btnOk;
    String forgetUsername, forgetMobile;
    androidx.appcompat.app.AlertDialog forgetPasswordDialog;
    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpin);
        initViews();

        versionCodeStr = BuildConfig.VERSION_NAME;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this);
        userid = sharedPreferences.getString("userid", null);
        username = sharedPreferences.getString("username", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        loginUserName = sharedPreferences.getString("loginUserName", null);
        password = sharedPreferences.getString("password", null);
        usertype = sharedPreferences.getString("usertype", null);
        mobileno = sharedPreferences.getString("mobileno", null);
        pancard = sharedPreferences.getString("pancard", null);
        aadharCard = sharedPreferences.getString("adharcard", null);
        emailId = sharedPreferences.getString("email", null);
        firmName = sharedPreferences.getString("firmName", null);
        address = sharedPreferences.getString("address", null);
        dob = sharedPreferences.getString("dob", null);
        city = sharedPreferences.getString("city", null);

        tvUserName.setText(username);


        mpinPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {

                    String mpin = mpinPinView.getText().toString().trim();
                    checkMpin(mpin);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mpin = mpinPinView.getText().toString().trim();
                if (mpin.length() == 6) {
                    checkMpin(mpin);
                } else {
                    mpinPinView.setError("Required");
                }
            }
        });

        btnForgetMpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MPINActivity.this,GenerateMpinActivity.class);
                intent.putExtra("userId",userid);
                intent.putExtra("userName",username);
                intent.putExtra("panCard",pancard);
                intent.putExtra("mobileNo",mobileno);
                intent.putExtra("userType",usertype);
                intent.putExtra("deviceId",deviceId);
                intent.putExtra("deviceInfo",deviceInfo);
                intent.putExtra("aadharCard",aadharCard);
                intent.putExtra("loginUserName",loginUserName);
                intent.putExtra("password",password);
                intent.putExtra("emailId",emailId);
                intent.putExtra("firmName",firmName);
                intent.putExtra("address",address);
                intent.putExtra("dob",dob);
                intent.putExtra("city",city);
                startActivity(intent);
            }
        });

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                finish();
            }
        });

        checkCredentials();

    }

    private void checkCredentials() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(MPINActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkCredential(AUTH_KEY, loginUserName, password, deviceInfo,"1.2.2");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            JSONObject dataObject = responseObject.getJSONObject("data");
                            String userId = dataObject.getString("userID");
                            String changePassword = dataObject.getString("changePassword");
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userId);
                            editor.apply();

                            if (changePassword.equalsIgnoreCase("true"))
                            {
                                startActivity(new Intent(MPINActivity.this, ChangePasswordActivity.class));
                            }

                        } else {
                            pDialog.dismiss();
                            String message=responseObject.getString("data");
                            new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                    .setCancelable(false)
                                    .setMessage(message+"\nYou have to login again")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                            editor.clear();
                                            editor.apply();
                                            startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                .setCancelable(false)
                                .setMessage("You have to login again.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                        editor.clear();
                                        editor.apply();
                                        startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                            .setCancelable(false)
                            .setMessage("You have to login again.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                        .setCancelable(false)
                        .setMessage("You have to login again.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void checkMpin(String mpin) {
        final AlertDialog pDialog = new AlertDialog.Builder(MPINActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkMpinOrTPIN(AUTH_KEY,userid,deviceId,deviceInfo,"mpin",mpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            startActivity(new Intent(MPINActivity.this, HomeDashBoardActivity.class));
                            pDialog.dismiss();
                            finish();
                        }
                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            pDialog.dismiss();
                            String transaction=responseObject.getString("status");
                            new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                    .setMessage(transaction)
                                    .setPositiveButton("ok", null)
                                    .show();
                        }
                        else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                    .setMessage("You have entered wrong mpin.")
                                    .setPositiveButton("ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("ok", null)
                        .show();
            }
        });

    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        mpinPinView = findViewById(R.id.otp_pin_view);
        btnSubmit = findViewById(R.id.btn_submit);
        btnForgetMpin = findViewById(R.id.btn_forget_pin);
        btnPassword = findViewById(R.id.btn_password);
    }

}