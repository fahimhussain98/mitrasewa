package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.activities.HomeDashBoardActivity.aepsBalance;
import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.BuildConfig;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.device.Opts;
import wts.com.mitrsewa.device.Param;
import wts.com.mitrsewa.device.PidData;
import wts.com.mitrsewa.device.PidOptions;
import wts.com.mitrsewa.retrofit.RetrofitClient;
import wts.com.mitrsewa.retrofit.WebServiceInterface;

public class FingPayAepsActivity extends AppCompatActivity {
    ConstraintLayout bankContainer;

    LinearLayout transactionTypeLayout, userDetailLayout, deviceLayout;

    String userId, lat = "0.0", longi = "0.0", deviceId, deviceInfo;

    FusedLocationProviderClient mFusedLocationClient;

    TextView tvBalance;
    SharedPreferences sharedPreferences;

    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
    LinearLayout cashWithDrawLayout, balanceEnquiryLayout, miniStateLayout, aadharPayLayout;
    ImageView imgCashWithdraw, imgBalanceEnquiry, imgMiniStatement, imgAadharPay;
    TextView tvCashWithdraw, tvBalanceEnquiry, tvMiniStatement, tvAadharPay;
    String selectedTransactionType = "select", selectedAepsType = "select";
    AppCompatButton btnProceedTransactionType;
    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////
    String selectedBankName = "select", selectedBankIIN;
    TextView tvBankName, tv500, tv1000, tv2000, tv3000, tv5000;
    EditText etAmount, etMobile, etAadharCard;
    Button btnProceedUserDetail;
    CheckBox ckbTermsAndCondition;
    LinearLayout fastAmountLayout;
    TextInputLayout tilAmount;

    ArrayList<String> bankNameArrayList, bankIINArrayList;
    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
    ImageView imgMorpho,imgMorphoL1, imgStartek, imgMantra, imgMantraL1, imgEvolute;
    LinearLayout morphoLayout,morphoL1Layout, startekLayout, mantraLayout,mantraL1Layout,evoluteLayout;
    TextView tvMorpho,tvMorphoL1, tvMantra,tvMantraL1, tvStartek,tvEvolute;
    AppCompatButton btnProceedDeviceLayout;

    String selectedDevice = "select";
    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

    ////////////////FINGER PRINT DATA/////////////////////
    Serializer serializer = null;
  //  PidData pidData = null;
    String pidDataStr = null;

    String ci, dc, dpId, errCode, errInfo = "null", fcount, hmac, mc, mi,
            nmPoints, qScore, rdsId, rdsVer,
            serialNo, pCount = "null", sessionKey;
    ////////////////FINGER PRINT DATA/////////////////////

    /////////////////TRANSACTION REQUEST DATA//////////////////////////////////////
    String merchantTransactionId;
    String merchantLoginUserName, merchantEncryptedPassword;
    String generatedHashByte, timeStamp;
    JSONObject requestObject;

    /////////////////TRANSACTION REQUEST DATA//////////////////////////////////////

    String versionCodeStr;

    boolean isTwoFADone = false;

    String shpMobileNo = "";
    String shpAadharNo = "";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fing_pay_aeps);
        initViews();

        //////CHANGE COLOR OF STATUS BAR///////////////
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(FingPayAepsActivity.this, R.color.purple1));
        //////CHANGE COLOR OF STATUS BAR///////////////

        //////////////////////////////////////////////////////////////////
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FingPayAepsActivity.this);
        userId = sharedPreferences.getString("userid", null);
        shpMobileNo = sharedPreferences.getString("mobileno", null);
        shpAadharNo = sharedPreferences.getString("adharcard", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        String balance = getIntent().getStringExtra("balance");
        selectedTransactionType = getIntent().getStringExtra("transactionType");
        selectedAepsType = getIntent().getStringExtra("aepsType");
        tvBalance.setText("₹ " + balance);
//       if (selectedTransactionType.equalsIgnoreCase("select")) {
//           etMobile.setText(mobileNo);
//           etAadharCard.setText(aadharNo);
//       }

        lat = getIntent().getStringExtra("lat");
        longi = getIntent().getStringExtra("longi");
        //////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////////
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(FingPayAepsActivity.this);
        serializer = new Persister();
        /////////////////////////////////////////////////////////////////////////////////

        versionCodeStr = BuildConfig.VERSION_NAME;

        //getUserFingPayCredentials();
        showSelectedTransactionType();
        transactionTypeLayoutListeners();
        userDetailLayoutListeners();
        deviceLayoutListeners();

    }

    /**
     * FIRST STEP
     * selects transaction type(Cash withdraw,Balance enquiry etc. and then proceed to Second Step.
     * after proceed hide TRANSACTION TYPE LAYOUT and show USER DETAILS LAYOUT
     */
    private void transactionTypeLayoutListeners() {

        cashWithDrawLayout.setOnClickListener(view -> {

            selectedTransactionType = "cw";
            showSelectedTransactionType();

        });

        balanceEnquiryLayout.setOnClickListener(view -> {

            selectedTransactionType = "be";
            showSelectedTransactionType();
        });

        miniStateLayout.setOnClickListener(view -> {

            selectedTransactionType = "ms";
            showSelectedTransactionType();
        });

        aadharPayLayout.setOnClickListener(view -> {

            selectedTransactionType = "m";
            showSelectedTransactionType();
        });

//        btnProceedTransactionType.setOnClickListener(view -> {
            if (!selectedTransactionType.equalsIgnoreCase("select")) {
                getFingPayBankDetails();

                transactionTypeLayout.setVisibility(View.GONE);
                userDetailLayout.setVisibility(View.VISIBLE);
                if (selectedTransactionType.equalsIgnoreCase("be") || selectedTransactionType.equalsIgnoreCase("ms")) {

                    tilAmount.setVisibility(View.GONE);
                    fastAmountLayout.setVisibility(View.GONE);
                }
//                else if (selectedTransactionType.equalsIgnoreCase("cw") || selectedTransactionType.equalsIgnoreCase("m")) {
//                    tilAmount.setVisibility(View.GONE);
//                    fastAmountLayout.setVisibility(View.GONE);
//                    bankContainer.setVisibility(View.GONE);
//                    etMobile.setText(shpMobileNo);
//                    etAadharCard.setText(shpAadharNo);
//
//                }
                else {
                    tilAmount.setVisibility(View.VISIBLE);
                    fastAmountLayout.setVisibility(View.VISIBLE);
                }

            } else {
                showMessageDialog("Hey!!! You forgot to select transaction type.");
            }
//        });
    }

    /**
     * SECOND STEP
     * get Aadhar details from user input and then proceed to third step.
     * after proceed hide USER DETAIL LAYOUT and show DEVICE LAYOUT
     */
    private void userDetailLayoutListeners() {

        setFastSelectAmount();

        btnProceedUserDetail.setOnClickListener(v -> {
//
//            if (selectedTransactionType.equalsIgnoreCase("cw") || selectedTransactionType.equalsIgnoreCase("m")) {
//                if (checkUserDetailsCW()) {
//                    userDetailLayout.setVisibility(View.GONE);
//                    deviceLayout.setVisibility(View.VISIBLE);
//                }
//
//            }
//            else {
//                if (checkUserDetails()) {
//                    userDetailLayout.setVisibility(View.GONE);
//                    deviceLayout.setVisibility(View.VISIBLE);
//                }
//            }

            if (checkUserDetails()) {
                userDetailLayout.setVisibility(View.GONE);
                deviceLayout.setVisibility(View.VISIBLE);

            }
        });

    }

    private boolean checkUserDetailsCW() {
        if (etMobile.getText().toString().length() == 10) {
            if (etAadharCard.getText().toString().trim().length() == 12) {
                hideKeyBoard();
                return true;
            }
            else {
                showMessageDialog("Please enter valid aadhar number.");
                return false;
            }
        }
        else {
            showMessageDialog("Please enter valid mobile number.");
            return false;
        }

    }


    /**
     * THIRD STEP
     * get device name from user and get pid data from RD Service
     * after that do final transaction
     */
    private void deviceLayoutListeners() {

        mantraLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_selected);
            imgMantraL1.setImageResource(R.drawable.mantral1_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Mantra";
        });

        mantraL1Layout.setOnClickListener(view -> {
            imgMantraL1.setImageResource(R.drawable.mantral1_selected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);

            tvMantraL1.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "MantraL1";
        });

        evoluteLayout.setOnClickListener(view -> {
            imgEvolute.setImageResource(R.drawable.evolute_selected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantral1_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);

            tvEvolute.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Evolute";
        });

        startekLayout.setOnClickListener(view -> {
            imgStartek.setImageResource(R.drawable.startek_selected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantral1_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);

            tvStartek.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Startek";
        });

        morphoLayout.setOnClickListener(view -> {
            imgMorpho.setImageResource(R.drawable.morpho_selected);
            imgMorphoL1.setImageResource(R.drawable.mantra_unselected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantral1_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);

            tvMorpho.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Morpho";
        });

        morphoL1Layout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_selected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "MorphoL1";
        });

        btnProceedDeviceLayout.setOnClickListener(view -> {

            if (!selectedDevice.equalsIgnoreCase("select")) {
                String packageName = null;
                if (selectedDevice.equalsIgnoreCase("Morpho"))
                    packageName = "com.scl.rdservice";
                else if (selectedDevice.equalsIgnoreCase("MorphoL1"))
                    packageName = "com.idemia.l1rdservice";
                else if (selectedDevice.equalsIgnoreCase("Startek"))
                    packageName = "com.acpl.registersdk";
                else if (selectedDevice.equalsIgnoreCase("Mantra"))
                    packageName = "com.mantra.rdservice";
                else if (selectedDevice.equalsIgnoreCase("MantraL1"))
                    packageName = "com.mantra.mfs110.rdservice";
                else if (selectedDevice.equalsIgnoreCase("Evolute"))
                    packageName = "com.evolute.rdservice";

                try {
                    String pidOption = getPIDOptions();
                    Intent intent2 = new Intent();
                    intent2.setPackage(packageName);
                    intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                    intent2.putExtra("PID_OPTIONS", pidOption);
                    startActivityForResult(intent2, 1);
                } catch (Exception e) {
                    showMessageDialog("Please install " + selectedDevice + " Rd Service first.");
                }
            } else {
                showMessageDialog("Please Select Your Device");
            }
        });
    }

    private void showSelectedTransactionType() {
        if (selectedTransactionType.equalsIgnoreCase("cw")) {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_selected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));
        } else if (selectedTransactionType.equalsIgnoreCase("be")) {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_selected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));
        } else if (selectedTransactionType.equalsIgnoreCase("ms")) {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_selected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));
        } else if (selectedTransactionType.equalsIgnoreCase("m")) {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_selected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.selected_text_color));
        }

    }

    private void getFingPayBankDetails() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingPayAepsActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        WebServiceInterface webServiceInterface = WebServiceInterface.retrofit.create(WebServiceInterface.class);

        Call<JsonObject> call = webServiceInterface.getFingPayBankDetails();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String status = responseObject.getString("status");
                        if (status.equalsIgnoreCase("true")) {
                            bankNameArrayList = new ArrayList<>();
                            bankIINArrayList = new ArrayList<>();
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {

                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String bankName = dataObject.getString("bankName");
                                String bankIIN = dataObject.getString("iinno");

                                bankNameArrayList.add(bankName);
                                bankIINArrayList.add(bankIIN);
                            }
                            tvBankName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog bankDialog = new SpinnerDialog(FingPayAepsActivity.this, bankNameArrayList, "Select Bank", in.galaxyofandroid.spinerdialog.R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    bankDialog.setCancellable(true); // for cancellable
                                    bankDialog.setShowKeyboard(false);// for open keyboard by default
                                    bankDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvBankName.setText(item);
                                            selectedBankName = bankNameArrayList.get(position);
                                            selectedBankIIN = bankIINArrayList.get(position);
                                        }
                                    });

                                    bankDialog.showSpinerDialog();
                                }
                            });

                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                                    .setMessage("Please try after sometime.")
                                    .setTitle("Message!!!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                                .setMessage("Please try after sometime.")
                                .setTitle("Message!!!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                            .setMessage("Please try after sometime.")
                            .setTitle("Message!!!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                        .setMessage("Please try after sometime.\n" + t.getMessage())
                        .setTitle("Message!!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void getUserFingPayCredentials() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingPayAepsActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUserFingPayCredentials(AUTH_KEY, userId, deviceId, deviceInfo, "NA");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String status = responseObject.getString("statuscode");
                        if (status.equalsIgnoreCase("TXN")) {

                            JSONObject dataObject = responseObject.getJSONObject("data");
                            merchantLoginUserName = dataObject.getString("loginUserName");
                            merchantEncryptedPassword = dataObject.getString("encryptedPassword");

                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                                    .setMessage("Please try after sometime.")
                                    .setTitle("Message!!!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                                .setMessage("Please try after sometime.")
                                .setTitle("Message!!!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                            .setMessage("Please try after sometime.")
                            .setTitle("Message!!!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                        .setMessage("Please try after sometime.\n" + t.getMessage())
                        .setTitle("Message!!!")
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

    private String getPIDOptions() {
        try {
            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "2";
            opts.format = "0";
            opts.timeout = "15000";
            opts.wadh = "";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.pidVer = "2.0";
            opts.env = "P";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return null;
    }

    private boolean checkUserDetails() {
        if (etMobile.getText().toString().length() == 10) {
            if (!selectedBankName.equalsIgnoreCase("select")) {
                if (etAadharCard.getText().toString().trim().length() == 12) {
                    if (selectedTransactionType.equalsIgnoreCase("be") || selectedTransactionType.equalsIgnoreCase("ms")) {
                        if (ckbTermsAndCondition.isChecked()) {
                            hideKeyBoard();
                            return true;
                        } else {
                            showMessageDialog("Please accept terms and condition to continue.");
                            return false;
                        }
                    } else {
                        if (!TextUtils.isEmpty(etAmount.getText())) {
                            if (ckbTermsAndCondition.isChecked()) {
                                hideKeyBoard();
                                return true;
                            } else {
                                showMessageDialog("Please accept terms and condition to continue.");
                                return false;
                            }
                        } else {
                            showMessageDialog("Please Enter Amount");
                            return false;
                        }
                    }

                } else {
                    showMessageDialog("Please enter valid aadhar number.");
                    return false;
                }
            } else {
                showMessageDialog("Please select your bank.");
                return false;
            }
        } else {
            showMessageDialog("Please enter valid mobile number.");
            return false;
        }
    }

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFastSelectAmount() {

        tv500.setOnClickListener(view -> {
            etAmount.setText("500");
        });

        tv1000.setOnClickListener(view -> {
            etAmount.setText("1000");
        });

        tv2000.setOnClickListener(view -> {
            etAmount.setText("2000");
        });

        tv3000.setOnClickListener(view -> {
            etAmount.setText("3000");
        });

        tv5000.setOnClickListener(view -> {
            etAmount.setText("5000");
        });
    }

    private void showMessageDialog(String message) {
        final AlertDialog messageDialog = new AlertDialog.Builder(FingPayAepsActivity.this).create();
        final LayoutInflater inflater = LayoutInflater.from(FingPayAepsActivity.this);
        View convertView = inflater.inflate(R.layout.message_dialog, null);
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        messageDialog.setCancelable(false);
        messageDialog.setView(convertView);

        ImageView imgClose = convertView.findViewById(R.id.img_close);
        TextView tvMessage = convertView.findViewById(R.id.tv_message);
        Button btnTryAgain = convertView.findViewById(R.id.btn_try_again);

        imgClose.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        btnTryAgain.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        tvMessage.setText(message);

        messageDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result;
        List<Param> params = new ArrayList<>();
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    result = data.getStringExtra("PID_DATA");
                    if (result.contains("Device not ready")) {
                        showMessageDialog("Device Not Connected");
                    } else {
                        if (result.contains("srno") && result.contains("rdsId") && result.contains("rdsVer")) {
                            try {
                                pidDataStr = result;
                                getLastLocation();

                                /*pidData = serializer.read(PidData.class, result);
                                pidDataStr = pidData._Data.value;
                                Log.e("xml_data_show", pidDataStr);
                                hmac = pidData._Hmac;
                                sessionKey = pidData._Skey.value;
                                dpId = pidData._DeviceInfo.dpId;
                                rdsId = pidData._DeviceInfo.rdsId;
                                rdsVer = pidData._DeviceInfo.rdsVer;
                                dc = pidData._DeviceInfo.dc;
                                mc = pidData._DeviceInfo.mc;
                                mi = pidData._DeviceInfo.mi;
                                errCode = pidData._Resp.errCode;
                                errInfo = pidData._Resp.errInfo;
                                errCode = pidData._Resp.errCode;
                                fcount = pidData._Resp.fCount;
                                qScore = pidData._Resp.qScore;
                                nmPoints = pidData._Resp.nmPoints;
                                ci = pidData._Skey.ci;
                                params = pidData._DeviceInfo.add_info.params;
                                for (int i = 0; i < params.size(); i++) {
                                    String name = params.get(i).name;
                                    if (name.equalsIgnoreCase("srno")) {
                                        serialNo = params.get(i).value;
                                    } else if (name.equalsIgnoreCase("sysid")) {
                                        String systemId = params.get(i).value;
                                    }
                                }

                                merchantTransactionId="MS@AEPS"+new Date().getTime();

                                createJsonRequestBody();*/

                            } catch (Exception e) {
                                e.printStackTrace();
                                showMessageDialog("There are some issues please contact to administration.");
                            }
                        } else {
                            showMessageDialog("Device Not Connected");
                        }
                    }

                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude() + "";
                                    longi = location.getLongitude() + "";

//                                    if (selectedTransactionType.equalsIgnoreCase("cw") || selectedTransactionType.equalsIgnoreCase("m")) {
//                                        if (isTwoFADone) {
//                                            doAepsTransaction();
//                                        }
//                                        else {
//                                            checkCW2FA();
//                                        }
//                                    }
//                                    else {
//                                        doAepsTransaction();
//                                    }


                                    doAepsTransaction();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(FingPayAepsActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(FingPayAepsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(FingPayAepsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(FingPayAepsActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            //launchWTSAEPS();

//            if (selectedTransactionType.equalsIgnoreCase("cw") || selectedTransactionType.equalsIgnoreCase("m")) {
//                if (isTwoFADone) {
//                    doAepsTransaction();
//                }
//                else {
//                    checkCW2FA();
//                }
//            }
//            else {
//                doAepsTransaction();
//            }

            doAepsTransaction();

        }
    };

    private void checkCW2FA() {
        ProgressDialog progressDialog = new ProgressDialog(FingPayAepsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...Don't press back button");
        progressDialog.show();

       String aadharNo = etAadharCard.getText().toString().trim();
       String mobileNo = etMobile.getText().toString().trim();

        Call<JsonObject> call;

        call = RetrofitClient.getInstance().getApi().checkCW2FA(AUTH_KEY, userId, deviceId, deviceInfo, pidDataStr, lat, longi, aadharNo, mobileNo, selectedAepsType);  // paySprint nsdl aeps 16/12/23

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            isTwoFADone = true;
                            progressDialog.dismiss();

                            transactionTypeLayout.setVisibility(View.GONE);
                            deviceLayout.setVisibility(View.GONE);

                            userDetailLayout.setVisibility(View.VISIBLE);
                            tilAmount.setVisibility(View.VISIBLE);
                            fastAmountLayout.setVisibility(View.VISIBLE);
                            bankContainer.setVisibility(View.VISIBLE);

                            etMobile.setText("");
                            etAadharCard.setText("");

                        } else {
                            isTwoFADone = false;
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");
                            showMessageDialog(message);

                            deviceLayout.setVisibility(View.GONE);
                            userDetailLayout.setVisibility(View.GONE);
                            transactionTypeLayout.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        isTwoFADone = false;
                        progressDialog.dismiss();
                        showMessageDialog("Something went wrong.");
                        deviceLayout.setVisibility(View.GONE);
                        userDetailLayout.setVisibility(View.GONE);
                        transactionTypeLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    isTwoFADone = false;
                    progressDialog.dismiss();
                    showMessageDialog(response.message());
                    deviceLayout.setVisibility(View.GONE);
                    userDetailLayout.setVisibility(View.GONE);
                    transactionTypeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                isTwoFADone = false;
                progressDialog.dismiss();
                showMessageDialog(t.getMessage());

                deviceLayout.setVisibility(View.GONE);
                userDetailLayout.setVisibility(View.GONE);
                transactionTypeLayout.setVisibility(View.VISIBLE);

            }
        });
    }

    private void doAepsTransaction() {
        ProgressDialog progressDialog = new ProgressDialog(FingPayAepsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...Don't press back button");
        progressDialog.show();

        String aadharNo = etAadharCard.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();
        String mobileNo = etMobile.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doAepsTransaction(AUTH_KEY, userId, deviceId,
                deviceInfo, aadharNo, selectedBankIIN,
                mobileNo, amount, selectedTransactionType, lat, longi, pidDataStr, "1.2.2");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            String responseAmount = null;
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);
                                String transactionType = transactionObject.getString("transactionType");
                                String bankName = transactionObject.getString("bankname");
                                String responseMobileNumber = transactionObject.getString("mobileno");
                                String responseAadharNumber = transactionObject.getString("aadharno");
                                String responseBankRRN = transactionObject.getString("bankRrno");
                                String transactionId = transactionObject.getString("agentId");
                                String status = transactionObject.getString("transactionStatus");
                                String message = transactionObject.getString("message");

                                if (!transactionType.equalsIgnoreCase("MINI STATEMENT"))
                                    responseAmount = transactionObject.getString("transactionAmount");
                                String transactionDate = transactionObject.getString("transactiondate");

                                String outputDate = null;
                                String outputTime = null;
                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = transactionDate.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (transactionType.equalsIgnoreCase("MINI STATEMENT")) {

                                    String miniStatement = transactionObject.getString("ministatement");
                                    String accountBalance = transactionObject.getString("accountBalance");

                                    Intent intent = new Intent(FingPayAepsActivity.this, MiniStatementPaySprintActivity.class);
                                    intent.putExtra("transactionType", transactionType);
                                    intent.putExtra("bankName", bankName);
                                    intent.putExtra("responseMobileNumber", responseMobileNumber);
                                    intent.putExtra("responseAadharNumber", responseAadharNumber);
                                    intent.putExtra("responseBankRRN", responseBankRRN);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("status", status);
                                    intent.putExtra("responseAmount", responseAmount);
                                    intent.putExtra("accountBalance", accountBalance);
                                    intent.putExtra("outputDate", outputDate);
                                    intent.putExtra("outputTime", outputTime);
                                    intent.putExtra("miniStatement", miniStatement);
                                    intent.putExtra("message", message);

                                    startActivity(intent);
                                    finish();

                                } else {
                                    String accountBalance = transactionObject.getString("accountBalance");
                                    Intent intent = new Intent(FingPayAepsActivity.this, AepsTransactionActivity.class);
                                    intent.putExtra("transactionType", transactionType);
                                    intent.putExtra("bankName", bankName);
                                    intent.putExtra("responseMobileNumber", responseMobileNumber);
                                    intent.putExtra("responseAadharNumber", responseAadharNumber);
                                    intent.putExtra("responseBankRRN", responseBankRRN);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("status", status);
                                    intent.putExtra("responseAmount", responseAmount);
                                    intent.putExtra("message", message);
                                    intent.putExtra("outputDate", outputDate);
                                    intent.putExtra("outputTime", outputTime);
                                    intent.putExtra("accountBalance", accountBalance);

                                    startActivity(intent);
                                    finish();
                                }

                                progressDialog.dismiss();

                            }
                        } else if (responseCode.equalsIgnoreCase("TFA")) {

                            String aepsType = responseObject.getString("status");
                            String data = responseObject.getString("data");

                            new androidx.appcompat.app.AlertDialog.Builder(FingPayAepsActivity.this)
                                    .setCancelable(false)
                                    .setMessage(data)
                                    .setTitle("Message")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

//                                            Intent intent = new Intent(FingPayAepsActivity.this, TwoFactorAuthenticationActivity.class);
//                                            intent.putExtra("lat", lat);
//                                            intent.putExtra("longi", longi);
//                                            intent.putExtra("balance", aepsBalance);
//                                            intent.putExtra("title","TwoFactorAuth");
//                                            intent.putExtra("aepsType", aepsType);
//
//                                            startActivity(intent);
                                            isTwoFADone = false;
                                            deviceLayout.setVisibility(View.GONE);
                                            userDetailLayout.setVisibility(View.VISIBLE);
                                            etMobile.setText(shpMobileNo);
                                            etAadharCard.setText(shpAadharNo);
                                            tilAmount.setVisibility(View.GONE);
                                            fastAmountLayout.setVisibility(View.GONE);
                                            bankContainer.setVisibility(View.GONE);
                                        }
                                    }).show();
                            progressDialog.dismiss();

                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            showMessageDialog(message);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        showMessageDialog("Something went wrong.");

                    }
                } else {
                    progressDialog.dismiss();
                    showMessageDialog(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showMessageDialog(t.getMessage());
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(FingPayAepsActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    /*private void createJsonRequestBody() {
        requestObject = new JSONObject();
        JSONObject captureResponseObject = new JSONObject();
        JSONObject cardNumberORUIDObject = new JSONObject();

        String aadharNumber = etAadharCard.getText().toString().trim();
        String mobileNumber = etMobile.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();
        Calendar fromCalender = Calendar.getInstance();
        SimpleDateFormat webServiceDateFormat;
        //webServiceDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
        timeStamp = webServiceDateFormat.format(fromCalender.getTime());

        try {
            captureResponseObject.put("PidDatatype", "X");
            captureResponseObject.put("Piddata", pidDataStr);
            captureResponseObject.put("ci", ci);
            captureResponseObject.put("dc", dc);
            captureResponseObject.put("dpID", dpId);
            captureResponseObject.put("errCode", errCode);
            captureResponseObject.put("errInfo", errInfo);
            captureResponseObject.put("fCount", fcount);
            captureResponseObject.put("fType", "0");
            captureResponseObject.put("hmac", hmac);
            captureResponseObject.put("iCount", "0");
            captureResponseObject.put("mc", mc);
            captureResponseObject.put("mi", mi);
            captureResponseObject.put("nmPoints", nmPoints);
            captureResponseObject.put("pCount", pCount);
            captureResponseObject.put("pType", "0");
            captureResponseObject.put("qScore", qScore);
            captureResponseObject.put("rdsID", rdsId);
            captureResponseObject.put("rdsVer", rdsVer);
            captureResponseObject.put("sessionKey", sessionKey);

            cardNumberORUIDObject.put("adhaarNumber", aadharNumber);
            cardNumberORUIDObject.put("indicatorforUID", 0);
            cardNumberORUIDObject.put("nationalBankIdentificationNumber", selectedBankIIN);

            requestObject.put("merchantTranId", merchantTransactionId);
            requestObject.put("captureResponse", captureResponseObject);
            requestObject.put("cardnumberORUID", cardNumberORUIDObject);
            requestObject.put("languageCode", "en");
            requestObject.put("latitude", Double.valueOf(lat));
            requestObject.put("longitude", Double.valueOf(longi));
            requestObject.put("mobileNumber", mobileNumber);
            requestObject.put("paymentType", "B");
            requestObject.put("requestRemarks", "No Remarks");
            requestObject.put("timestamp", timeStamp);
            requestObject.put("transactionAmount", Double.valueOf(amount));
            //TODO : Change transaction type after CW testing
            requestObject.put("transactionType", "CW");
            requestObject.put("merchantUserName", merchantLoginUserName);
            requestObject.put("merchantPin", merchantEncryptedPassword);
            requestObject.put("superMerchantId", 1060);

            String hash = requestObject.toString() + "8d0032e3d4fed4cc4f9061c72075a9ca719f9058362f88f946b5d09807033c5c" + timeStamp;
            byte[] hashByte = hash.getBytes();
            generatedHashByte = generateSha256Hash(hashByte);
            doAepsCashWithdraw();

        } catch (Exception e) {
            showMessageDialog("Something went wrong\nWhile creating json request body.");
        }

    }*/

    /*private void doAepsCashWithdraw() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingPayAepsActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        //TODO : ADD PERMISSION FOR PHONE STATE
        String imei = getImei();

        WebServiceInterface webServiceInterface = WebServiceInterface.retrofit.create(WebServiceInterface.class);
        Call<JsonObject> call = webServiceInterface.doAepsCashWithdrawal(timeStamp, generatedHashByte, imei, requestObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String status = responseObject.getString("status");
                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showMessageDialog("Something went wrong.");
                    }
                } else {
                    pDialog.dismiss();
                    showMessageDialog(response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showMessageDialog(t.getMessage());
            }
        });

    }*/

    /*@SuppressLint("HardwareIds")
    public String getImei() {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(FingPayAepsActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            imei = telephonyManager.getDeviceId();

        } catch (Exception e) {
            if (!isValidString(imei))
                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return imei;
    }*/

    /*private String generateSha256Hash(byte[] hashByte) {

        String newHash = null;
        try {

            SHA256Digest sha256Digest = new SHA256Digest();
            sha256Digest.reset();
            byte[] buffer = new byte[sha256Digest.getDigestSize()];
            sha256Digest.update(hashByte, 0, hashByte.length);
            sha256Digest.doFinal(buffer, 0);

            newHash = Base64.toBase64String(buffer);
        } catch (Exception e) {
            Log.d("myExceptionNew", e.getMessage());
        }
        return newHash;
    }*/

    private void initViews() {
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
        cashWithDrawLayout = findViewById(R.id.cash_withdraw_layout);
        balanceEnquiryLayout = findViewById(R.id.balance_enquiry_layout);
        miniStateLayout = findViewById(R.id.mini_statement_layout);
        aadharPayLayout = findViewById(R.id.aadhar_pay_layout);

        imgCashWithdraw = findViewById(R.id.img_cash_withdraw);
        imgBalanceEnquiry = findViewById(R.id.img_balance_enquiry);
        imgMiniStatement = findViewById(R.id.img_mini_statement);
        imgAadharPay = findViewById(R.id.img_aadhar_pay);

        tvCashWithdraw = findViewById(R.id.tv_cash_withdraw);
        tvBalanceEnquiry = findViewById(R.id.tv_balance_enquiry);
        tvMiniStatement = findViewById(R.id.tv_mini_statement);
        tvAadharPay = findViewById(R.id.tv_aadhar_pay);

        transactionTypeLayout = findViewById(R.id.transaction_type_layout);

        btnProceedTransactionType = findViewById(R.id.btn_proceed_transaction_type);
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

        userDetailLayout = findViewById(R.id.user_detail_layout);
        bankContainer = findViewById(R.id.bankNameContainer);

        tvBankName = findViewById(R.id.tv_bank);

        tv500 = findViewById(R.id.tv_500);
        tv1000 = findViewById(R.id.tv_1000);
        tv2000 = findViewById(R.id.tv_2000);
        tv3000 = findViewById(R.id.tv_3000);
        tv5000 = findViewById(R.id.tv_5000);

        etAmount = findViewById(R.id.et_amount);
        etMobile = findViewById(R.id.et_mobile_number);
        etAadharCard = findViewById(R.id.et_aadhar_number);

        tilAmount = findViewById(R.id.til_amount);

        ckbTermsAndCondition = findViewById(R.id.ckb_terms_condition);

        fastAmountLayout = findViewById(R.id.fast_amount_layout);

        btnProceedUserDetail = findViewById(R.id.btn_proceed_user_details);

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////


        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
        deviceLayout = findViewById(R.id.device_layout);

        imgMorpho = findViewById(R.id.img_morpho);
        imgMorphoL1 = findViewById(R.id.img_morphoL1);
        imgMantra = findViewById(R.id.img_mantra);
        imgMantraL1 = findViewById(R.id.img_mantra2);
        imgStartek = findViewById(R.id.img_startek);
        imgEvolute = findViewById(R.id.img_evolute);

        morphoLayout = findViewById(R.id.morpho_layout);
        morphoL1Layout = findViewById(R.id.morphoL1_layout);
        mantraLayout = findViewById(R.id.mantra_layout);
        mantraL1Layout = findViewById(R.id.mantra_layout2);
        evoluteLayout = findViewById(R.id.evolute_layout);
        startekLayout = findViewById(R.id.startek_layout);

        tvStartek = findViewById(R.id.tv_startek);
        tvMorpho = findViewById(R.id.tv_morpho);
        tvMorphoL1 = findViewById(R.id.tv_morphoL1);
        tvMantra = findViewById(R.id.tv_mantra);
        tvMantraL1 = findViewById(R.id.tv_mantra2);
        tvEvolute = findViewById(R.id.tv_evolute);

        btnProceedDeviceLayout = findViewById(R.id.btn_proceed_device);
        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

        tvBalance = findViewById(R.id.tv_aeps_balance);

    }
}