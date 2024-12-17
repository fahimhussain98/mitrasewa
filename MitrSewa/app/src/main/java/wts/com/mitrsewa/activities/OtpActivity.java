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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chaos.view.PinView;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class OtpActivity extends AppCompatActivity {

    AppCompatButton btnSubmit;
    PinView otpPinView;

    String userId,ownerName,userName,userType,mobileNo,pancard,aadharCard,emailId,loginUserName,password,deviceId,deviceInfo;
    TextView tvOtpMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initViews();

        userId=getIntent().getStringExtra("userId");
        ownerName=getIntent().getStringExtra("ownername");
        userName=getIntent().getStringExtra("username");
        userType=getIntent().getStringExtra("userType");
        mobileNo=getIntent().getStringExtra("mobileNo");
        pancard=getIntent().getStringExtra("panCard");
        aadharCard=getIntent().getStringExtra("aadharCard");
        emailId=getIntent().getStringExtra("emailId");
        loginUserName=getIntent().getStringExtra("loginUsername");
        password=getIntent().getStringExtra("password");
        deviceId=getIntent().getStringExtra("deviceId");
        deviceInfo=getIntent().getStringExtra("deviceInfo");

        sendOtp();


    }

    private void sendOtp() {
        final AlertDialog pDialog = new AlertDialog.Builder(OtpActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().sendOtp(AUTH_KEY,userId);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String responseCode=responseObject.getString("response_code");
                        if (responseCode.equalsIgnoreCase("TXN"))
                        {
                            pDialog.dismiss();
                            String responseOtp=responseObject.getString("transactions");
                            tvOtpMail.setText("Otp sent on "+emailId);
                            Toast.makeText(OtpActivity.this, responseOtp, Toast.LENGTH_SHORT).show();
                            Toast.makeText(OtpActivity.this, responseOtp, Toast.LENGTH_SHORT).show();
                            Toast.makeText(OtpActivity.this, responseOtp, Toast.LENGTH_SHORT).show();
                            Toast.makeText(OtpActivity.this, responseOtp, Toast.LENGTH_SHORT).show();
                            Toast.makeText(OtpActivity.this, responseOtp, Toast.LENGTH_SHORT).show();
                            checkOtp(responseOtp);
                        }

                        else
                        {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(OtpActivity.this)
                                    .setMessage("Something went wrong.\nPlease try after some time.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(OtpActivity.this)
                                .setMessage("Something went wrong.\nPlease try after some time.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(OtpActivity.this)
                            .setMessage("Something went wrong.\nPlease try after some time.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(OtpActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    private void checkOtp(String responseOtp) {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(otpPinView.getText())) {
                    String userInputOtp = Objects.requireNonNull(otpPinView.getText()).toString().trim();
                    if (userInputOtp.length() < 6) {
                        Toast.makeText(OtpActivity.this, "Enter 6 digit Otp", Toast.LENGTH_SHORT).show();
                    } else {
                        if (responseOtp.equalsIgnoreCase(userInputOtp))
                        {
                            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(OtpActivity.this);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("userId",userId);
                            editor.putString("ownername",ownerName);
                            editor.putString("username",userName);
                            editor.putString("userType",userType);
                            editor.putString("mobileNo",mobileNo);
                            editor.putString("panCard",pancard);
                            editor.putString("aadharCard",aadharCard);
                            editor.putString("emailId",emailId);
                            editor.putString("loginUsername",loginUserName);
                            editor.putString("password",password);
                            editor.putString("deviceId",deviceId);
                            editor.putString("deviceInfo",deviceInfo);
                            editor.apply();

                            Intent intent=new Intent(OtpActivity.this,HomeDashBoardActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.righttoorigin,R.anim.origintoright);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(OtpActivity.this, "Invalid Otp", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(OtpActivity.this, "Enter Otp", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initViews() {
        btnSubmit = findViewById(R.id.btn_submit);
        otpPinView = findViewById(R.id.otp_pin_view);
        tvOtpMail = findViewById(R.id.tv_otp_mail);
    }
}