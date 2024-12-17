package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.BuildConfig;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.fragments.HomeFragment;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class HomeDashBoardActivity extends AppCompatActivity {

    String loginUserName, password, deviceInfo, deviceId, userId, ownerName, mobile, userType;

    SharedPreferences sharedPreferences;

    TextView tvBalance,tvAepsWallet;
    public static String balance = "0.00", aepsBalance = "";

    LinearLayout reportsLayout, serviceLayout, aboutUsLayout, assistanceLayout, homeLayout;

    ImageView imgProfile,imgWallet;

    String versionCodeStr;

    String supportNumber = null;

    // in app update

    AppUpdateManager appUpdateManager;
    int request_code = 1;

    ////////////////////////


    ////////////  permission

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    String[] permissionsRequired = new String[]{
            android.Manifest.permission.READ_PHONE_STATE
    };

    //////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);
        initViews();

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(HomeDashBoardActivity.this, R.color.purple1));
        //////CHANGE COLOR OF STATUS BAR

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeDashBoardActivity.this);
        loginUserName = sharedPreferences.getString("loginUserName", null);
        password = sharedPreferences.getString("password", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        userId = sharedPreferences.getString("userid", null);
        ownerName = sharedPreferences.getString("username", null);
        mobile = sharedPreferences.getString("mobileno", null);
        userType = sharedPreferences.getString("usertype", null);

        versionCodeStr = BuildConfig.VERSION_NAME;

        loadFragment(new HomeFragment());
        handleNavigationMenu();
        checkPermissions();

            reportsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportsLayout.setBackgroundColor(getResources().getColor(R.color.gray1));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reportsLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }, 100);

                startActivity(new Intent(HomeDashBoardActivity.this, ReportsActivity.class));
            }
        });

        serviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceLayout.setBackgroundColor(getResources().getColor(R.color.gray1));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        serviceLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }, 100);

                /*Intent intent = new Intent(HomeDashBoardActivity.this, MyWebViewActivity.class);
                intent.putExtra("url", "http://harshpay.net/service.aspx");
                startActivity(intent);*/
            }
        });

        assistanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assistanceLayout.setBackgroundColor(getResources().getColor(R.color.gray1));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        assistanceLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }, 100);

                showSupportDialog();
            }
        });

        aboutUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUsLayout.setBackgroundColor(getResources().getColor(R.color.gray1));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        aboutUsLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }, 100);

               /* Intent intent = new Intent(HomeDashBoardActivity.this, MyWebViewActivity.class);
                intent.putExtra("url", "http://harshpay.net/about.aspx");
                startActivity(intent);*/
            }
        });

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeLayout.setBackgroundColor(getResources().getColor(R.color.gray1));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }, 100);
            }
        });

        imgWallet.setOnClickListener(view -> {
            startActivity(new Intent(HomeDashBoardActivity.this,WalletActivity.class));
        });

    }

    private void showSupportDialog() {
        final View view1 = LayoutInflater.from(HomeDashBoardActivity.this).inflate(R.layout.support_dialog, null, false);
        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(HomeDashBoardActivity.this).create();
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setView(view1);
        builder.show();

        LinearLayout callLayoutOne, callLayoutTwo, callLayoutThree, whatsAppLayout, mailLayout, wwwLayout;
        callLayoutOne = view1.findViewById(R.id.call_layout1);
        callLayoutTwo = view1.findViewById(R.id.call_layout2);
        callLayoutThree = view1.findViewById(R.id.call_layout3);
        whatsAppLayout = view1.findViewById(R.id.whats_app_layout);
        mailLayout = view1.findViewById(R.id.mail_layout);
        wwwLayout = view1.findViewById(R.id.www_layout);

        callLayoutOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportNumber = "0522-4300852";
                openCaller(supportNumber);
            }
        });

        callLayoutTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportNumber = "0522-4325594";
                openCaller(supportNumber);
            }
        });

        callLayoutThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportNumber = "9088333888";
                openCaller(supportNumber);
            }
        });

        whatsAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+919151154165&text=Hello I am"));
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] emailArray = {"digisupport@mitrsewa.in"};
                intent.putExtra(Intent.EXTRA_EMAIL, emailArray);
                intent.setType("message/rfs822");
                startActivity(Intent.createChooser(intent, "Choose Email Client"));
            }
        });
    }

    private void openCaller(String supportNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + supportNumber));
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void handleNavigationMenu() {

        Dialog navMenuDialog = new Dialog(HomeDashBoardActivity.this, R.style.DialogTheme);
        navMenuDialog.setContentView(R.layout.navigation_menu);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.72);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 1.0);

        navMenuDialog.getWindow().setLayout(width, height);

        navMenuDialog.getWindow().setGravity(Gravity.START);
        navMenuDialog.getWindow().setWindowAnimations(R.style.SlidingNavDialog);

        imgProfile.setOnClickListener(view -> {

            if (!navMenuDialog.isShowing()) {
                navMenuDialog.show();
            }

            TextView tvOwnerName = navMenuDialog.findViewById(R.id.tv_owner_name);
            TextView tvUserName = navMenuDialog.findViewById(R.id.tv_user_name);
            TextView tvMobileNumber = navMenuDialog.findViewById(R.id.tv_mobile_number);
            TextView tvVersion = navMenuDialog.findViewById(R.id.tv_version);
            Button btnLogout = navMenuDialog.findViewById(R.id.btn_logout);
            LinearLayout myCommissionLayout = navMenuDialog.findViewById(R.id.my_commission_layout);
            LinearLayout changePasswordLayout = navMenuDialog.findViewById(R.id.change_password_layout);
            LinearLayout changeMpinLayout = navMenuDialog.findViewById(R.id.change_mpin_layout);
            LinearLayout changeTpinLayout = navMenuDialog.findViewById(R.id.change_tpin_layout);
            LinearLayout profileLayout = navMenuDialog.findViewById(R.id.profile_layout);
            LinearLayout reportLayout = navMenuDialog.findViewById(R.id.nav_report_layout);
            LinearLayout creditLayout = navMenuDialog.findViewById(R.id.credit_layout);
            LinearLayout debitLayout = navMenuDialog.findViewById(R.id.debit_layout);
            LinearLayout certificateLayout = navMenuDialog.findViewById(R.id.certificate_layout);
            LinearLayout privacyLayout = navMenuDialog.findViewById(R.id.privacy_layout);
            LinearLayout fundRequestLayout = navMenuDialog.findViewById(R.id.fund_transfer_layout);
            LinearLayout addUserLayout = navMenuDialog.findViewById(R.id.add_user_layout);
            LinearLayout viewUserLayout = navMenuDialog.findViewById(R.id.view_user_layout);

            tvOwnerName.setText(ownerName);
            tvUserName.setText(loginUserName);
            tvMobileNumber.setText(mobile);
            tvVersion.setText("Version : " + versionCodeStr);

            myCommissionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    startActivity(new Intent(HomeDashBoardActivity.this, MyCommissionActivity.class));
                }
            });

            changePasswordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    startActivity(new Intent(HomeDashBoardActivity.this, ChangePasswordActivity.class));
                }
            });

            changeMpinLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    Intent intent=new Intent(HomeDashBoardActivity.this,ChangeMpinActivity.class);
                    intent.putExtra("pinType","mpin");
                    startActivity(intent);
                }
            });

            changeTpinLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();

                    Intent intent=new Intent(HomeDashBoardActivity.this,ChangeMpinActivity.class);
                    intent.putExtra("pinType","tpin");
                    startActivity(intent);
                }
            });

            profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    startActivity(new Intent(HomeDashBoardActivity.this, ProfileActivity.class));
                }
            });

            reportLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeDashBoardActivity.this, ReportsActivity.class));
                    navMenuDialog.dismiss();
                }
            });

            addUserLayout.setOnClickListener(v -> {
                navMenuDialog.dismiss();
                if (!userType.equalsIgnoreCase("retailer")) {
                    startActivity(new Intent(HomeDashBoardActivity.this, AddUserActivity.class));
                }
            });

            viewUserLayout.setOnClickListener(v -> {
                navMenuDialog.dismiss();
                if (!userType.equalsIgnoreCase("retailer")) {
                    startActivity(new Intent(HomeDashBoardActivity.this, ViewCustomerActivity.class));
                }
            });

            creditLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();

                    if (!userType.equalsIgnoreCase("retailer")) {
                        Intent intent = new Intent(HomeDashBoardActivity.this, CreditDebitBalanceActivity.class);
                        intent.putExtra("title", "Credit Balance");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeDashBoardActivity.this, "You can not use this service.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            debitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    if (!userType.equalsIgnoreCase("retailer")) {
                        Intent intent = new Intent(HomeDashBoardActivity.this, CreditDebitBalanceActivity.class);
                        intent.putExtra("title", "Debit Balance");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeDashBoardActivity.this, "You can not use this service.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            certificateLayout.setOnClickListener(v -> {
                navMenuDialog.dismiss();
                startActivity(new Intent(HomeDashBoardActivity.this, CertificateActivity.class));
            });

            privacyLayout.setOnClickListener(v -> {
                navMenuDialog.dismiss();
                /*Intent intent = new Intent(HomeDashBoardActivity.this, MyWebViewActivity.class);
                intent.putExtra("url", "http://harshpay.net/privacy.aspx");
                startActivity(intent);*/
            });

            fundRequestLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    startActivity(new Intent(HomeDashBoardActivity.this, FundTransferActivity.class));
                }
            });

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navMenuDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(HomeDashBoardActivity.this)
                            .setCancelable(false)
                            .setMessage("Are you sure ? ")
                            .setTitle("Confirmation")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeDashBoardActivity.this);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent(HomeDashBoardActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }).show();
                }
            });

        });
    }

    private void initViews() {
        tvBalance = findViewById(R.id.tv_balance);
        tvAepsWallet = findViewById(R.id.tv_aeps_wallet);
        reportsLayout = findViewById(R.id.reports_layout);
        imgProfile = findViewById(R.id.img_profile);
        imgWallet = findViewById(R.id.img_wallet);
        serviceLayout = findViewById(R.id.service_layout);
        aboutUsLayout = findViewById(R.id.about_us_layout);
        assistanceLayout = findViewById(R.id.assistance_layout);
        homeLayout = findViewById(R.id.home_layout);
    }

    private void getBalance() {
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBalance(AUTH_KEY, userId, deviceId, deviceInfo, "Login", "0");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            balance = jsonObject.getString("userBalance");
                            tvBalance.setText("₹ " + balance);

                        } else {
                            tvBalance.setText("₹ 00.00");
                        }

                    } catch (JSONException e) {
                        tvBalance.setText("₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    tvBalance.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                tvBalance.setText("₹ " + "00.00");
            }
        });
    }

/*
    private void getAepsBalance() {

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsBalance(AUTH_KEY,userId,deviceId,deviceInfo,"Login","");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            aepsBalance = jsonObject.getString("userBalance");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }
*/

    private void getAepsBalance() {

        final AlertDialog pDialog = new AlertDialog.Builder(HomeDashBoardActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsBalance(AUTH_KEY, userId, deviceId, deviceInfo, "Login", "");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                             aepsBalance = jsonObject.getString("userBalance");
                            tvAepsWallet.setText("₹ " + aepsBalance);


                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            tvAepsWallet.setText("₹ 00.00");


                        } else {
                            pDialog.dismiss();
                            tvAepsWallet.setText("₹ 00.00");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        tvAepsWallet.setText("₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();
                    tvAepsWallet.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();

                tvAepsWallet.setText("₹ " + "00.00");

            }
        });
    }


    private void checkCredentials() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(HomeDashBoardActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkCredential(AUTH_KEY, loginUserName, password, deviceInfo,"1.2.2");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            JSONObject dataObject = responseObject.getJSONObject("data");
                            String userId = dataObject.getString("userID");
                            String changePassword = dataObject.getString("changePassword");
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeDashBoardActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userId);
                            editor.apply();

                            if (changePassword.equalsIgnoreCase("true")) {
                                startActivity(new Intent(HomeDashBoardActivity.this, ChangePasswordActivity.class));
                            }

                        } else {
                            pDialog.dismiss();
                            String message=responseObject.getString("data");
                            new androidx.appcompat.app.AlertDialog.Builder(HomeDashBoardActivity.this)
                                    .setCancelable(false)
                                    .setMessage(message)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashBoardActivity.this).edit();
                                            editor.clear();
                                            editor.apply();
                                            startActivity(new Intent(HomeDashBoardActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(HomeDashBoardActivity.this)
                                .setCancelable(false)
                                .setMessage("You have to login again.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashBoardActivity.this).edit();
                                        editor.clear();
                                        editor.apply();
                                        startActivity(new Intent(HomeDashBoardActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(HomeDashBoardActivity.this)
                            .setCancelable(false)
                            .setMessage("You have to login again.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashBoardActivity.this).edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent(HomeDashBoardActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(HomeDashBoardActivity.this)
                        .setCancelable(false)
                        .setMessage("You have to login again.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashBoardActivity.this).edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(HomeDashBoardActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
    }

    //  in app update ..................................................

    public void In_app_update() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if ((result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
                {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, HomeDashBoardActivity.this, request_code);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "App is ready to install", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Install App", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appUpdateManager.completeUpdate();
                        Toast.makeText(HomeDashBoardActivity.this, "App updated successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.purple_700));
                snackbar.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.WHITE);

                snackbar.show();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        In_app_update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCredentials();
        getBalance();
        getAepsBalance();
        In_app_update();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appUpdateManager != null)
        {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    ////////////////////////////////////////////////////////////

    private void checkPermissions() {
        List<String> permissions = getUngrantedPermissions();
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(HomeDashBoardActivity.this,
                    permissions.toArray(new String[permissions.size()]),
                    PERMISSION_CALLBACK_CONSTANT);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.apply();
        } else {
        }
    }

    private List<String> getUngrantedPermissions() {
        List<String> permissions = new ArrayList<>();

        for (String s : permissionsRequired) {
            if (ContextCompat.checkSelfPermission(HomeDashBoardActivity.this, s) != PackageManager.PERMISSION_GRANTED)
                permissions.add(s);
        }

        return permissions;
    }

}