package wts.com.mitrsewa.activities;

import static org.json.JSONObject.wrap;
import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.fingpay.microatmsdk.MATMHistoryScreen;
import com.fingpay.microatmsdk.MicroAtmLoginScreen;
import com.fingpay.microatmsdk.data.MiniStatementModel;
import com.fingpay.microatmsdk.utils.Constants;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.BuildConfig;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class FingPayMicroAtmActivity extends AppCompatActivity {
    String userId, deviceId, deviceInfo, mobileNumber;
    SharedPreferences sharedPreferences;

    //////////////MATM//////////////
    String merchantId, merchantPassword, superMerchantId, selectedTransactionType = "select", lat, longi;
    EditText etMobile, etAmount, etRemarks;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final int CODE = 1;

    String[] permissionsRequired = new String[]{
            android.Manifest.permission.READ_PHONE_STATE
    };

    Button btnLaunchMatm, btnHistory;

    RadioGroup rgTransactionType;
    RadioButton rbCashWithdrawal, rbCashDeposit, rbBalanceEnquiry, rbMiniStatement, rbCardActivation, rbResetPin, rbChangePin;
    String uniqueId, enteredMobileNo;

    //////////////MATM//////////////

    //////////////MATM RESPONSE DATA//////////////
    boolean status;
    String matmResponse;
    double transAmount;
    double balAmount;
    String bankRrn;
    String transType;
    int type;
    String cardNum;
    String bankName;
    String cardType;
    String terminalId;
    String fpId;
    String transId;
    String RESPONSE_CODE;
    long STATUS_CODE;
    //////////////MATM RESPONSE DATA//////////////
    String versionCodeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fing_pay_micro_atm);
        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FingPayMicroAtmActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mobileNumber = sharedPreferences.getString("mobileno", null);
        versionCodeStr = BuildConfig.VERSION_NAME;

        lat = getIntent().getStringExtra("lat");
        longi = getIntent().getStringExtra("longi");

        etMobile.setText(mobileNumber);

        getMatmCredentials();
        checkPermissions();

        rgTransactionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbCashWithdrawal.isChecked()) {
                    selectedTransactionType = "cw";
                    etAmount.setVisibility(View.VISIBLE);
                } else if (rbCashDeposit.isChecked()) {
                    selectedTransactionType = "cd";
                    etAmount.setVisibility(View.VISIBLE);
                } else if (rbBalanceEnquiry.isChecked()) {
                    selectedTransactionType = "be";
                    etAmount.setVisibility(View.GONE);
                } else if (rbMiniStatement.isChecked()) {
                    selectedTransactionType = "mn";
                    etAmount.setVisibility(View.GONE);
                } else if (rbCardActivation.isChecked()) {
                    selectedTransactionType = "Card Activation";
                    etAmount.setVisibility(View.GONE);
                } else if (rbResetPin.isChecked()) {
                    selectedTransactionType = "Reset Pin";
                    etAmount.setVisibility(View.GONE);
                } else if (rbChangePin.isChecked()) {
                    etAmount.setVisibility(View.GONE);
                    selectedTransactionType = "Change pin";
                }
            }
        });

        btnLaunchMatm.setOnClickListener(view -> {
            if (checkInputs()) {
                uniqueId = "fingpay" + new Date().getTime();
                if (selectedTransactionType.equalsIgnoreCase("cw") ||
                        selectedTransactionType.equalsIgnoreCase("cd") ||
                        selectedTransactionType.equalsIgnoreCase("be") ||
                        selectedTransactionType.equalsIgnoreCase("mn")
                ) {
                    insertMatmData();

                } else {
                    launchFingPayMatm();

                }
            } else {
                showMessageDialog("Please fill above all fields.");
            }
        });

        btnHistory.setOnClickListener(view -> {
            getHistory();
        });
    }

    private void insertMatmData() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingPayMicroAtmActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String transAmount = etAmount.getText().toString().trim();
        enteredMobileNo = etMobile.getText().toString().trim();

        String requestDataStr = null;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", "NA");
            jsonObject.put("message", "NA");
            jsonObject.put("txnAmount", transAmount);
            jsonObject.put("balAmount", "NA");
            jsonObject.put("bankRRN", "NA");
            jsonObject.put("transType", selectedTransactionType);
            jsonObject.put("type", "NA");
            jsonObject.put("cardNo", "NA");
            jsonObject.put("cardType", "NA");
            jsonObject.put("bankName", "NA");
            jsonObject.put("terminalId", "NA");
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("mobileNo", enteredMobileNo);
            jsonObject.put("merchantLoginId", merchantId);
            jsonObject.put("merchantPassword", merchantPassword);
            jsonObject.put("superMerchantId", superMerchantId);
            jsonObject.put("insertOrUpdate", "insert");

            requestDataStr = jsonObject.toString();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().fingpayMatmCallback(AUTH_KEY, userId, deviceId, deviceInfo, requestDataStr
                    , "1.2.2");
            Log.d("myTag", requestDataStr);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");

                            if (responseCode.equalsIgnoreCase("TXN")) {
                                pDialog.dismiss();
                                launchFingPayMatm();
                            } else {
                                String responseMessage = responseObject.getString("data");
                                pDialog.dismiss();
                                showMessageDialog(responseMessage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            showMessageDialog("Something went wrong.");
                        }
                    } else {
                        pDialog.dismiss();
                        showMessageDialog("Something went wrong.");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    showMessageDialog(t.getMessage());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            pDialog.dismiss();
            showMessageDialog("Something went wrong please contact to admin");
        }

    }

    private void getHistory() {
        String imei = getImei();
        // Intent intent = new Intent(FingPayMicroAtmActivity.this, HistoryScreen.class);   //old version
        Intent intent = new Intent(FingPayMicroAtmActivity.this, MATMHistoryScreen.class);    //  from latest sdk of fingpay MATM
        intent.putExtra(Constants.MERCHANT_USERID, merchantId);
        // this MERCHANT_USERID be given by FingPay depending on the merchant, only that value need to sent from App to SDK

        intent.putExtra(Constants.MERCHANT_PASSWORD, merchantPassword);
        // this MERCHANT_PASSWORD be given by FingPay depending on the merchant, only that value need to sent from App to SDK

        intent.putExtra(Constants.SUPER_MERCHANTID, superMerchantId);
        // this SUPER_MERCHANT_ID be given by FingPay to you, only that value need to sent from App to SDK

        intent.putExtra(Constants.IMEI, imei);

        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    private void launchFingPayMatm() {
        String imei = getImei();

        String amount = etAmount.getText().toString().trim();
        String remarks = etRemarks.getText().toString().trim();
        enteredMobileNo = etMobile.getText().toString().trim();

        Intent intent = new Intent(FingPayMicroAtmActivity.this, MicroAtmLoginScreen.class);

        intent.putExtra(Constants.MERCHANT_USERID, merchantId);
        intent.putExtra(Constants.MERCHANT_PASSWORD, merchantPassword);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.REMARKS, remarks);
        intent.putExtra(Constants.MOBILE_NUMBER, enteredMobileNo);
        intent.putExtra(Constants.AMOUNT_EDITABLE, false);
        intent.putExtra(Constants.TXN_ID, uniqueId);
        intent.putExtra(Constants.SUPER_MERCHANTID, superMerchantId);
        intent.putExtra(Constants.IMEI, imei);
        intent.putExtra(Constants.LATITUDE, Double.valueOf(lat));
        intent.putExtra(Constants.LONGITUDE, Double.valueOf(longi));

        int id = rgTransactionType.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rb_cw:
                intent.putExtra(Constants.TYPE, Constants.CASH_WITHDRAWAL);
                break;

            case R.id.rb_cd:
                intent.putExtra(Constants.TYPE, Constants.CASH_DEPOSIT);
                break;

            case R.id.rb_be:
                intent.putExtra(Constants.TYPE, Constants.BALANCE_ENQUIRY);
                break;

            case R.id.rb_ms:
                intent.putExtra(Constants.TYPE, Constants.MINI_STATEMENT);
                break;

            case R.id.rb_rp:
                intent.putExtra(Constants.TYPE, Constants.PIN_RESET);
                break;

            case R.id.rb_cp:
                intent.putExtra(Constants.TYPE, Constants.CHANGE_PIN);
                break;

            case R.id.rb_ca:
                intent.putExtra(Constants.TYPE, Constants.CARD_ACTIVATION);
                break;
        }
        intent.putExtra(Constants.MICROATM_MANUFACTURER, Constants.MoreFun);
        startActivityForResult(intent, CODE);

    }

    @SuppressLint("HardwareIds")
    public String getImei() {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(FingPayMicroAtmActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            imei = telephonyManager.getDeviceId();

        } catch (Exception e) {
            if (!isValidString(imei))
                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return imei;
    }

    public static boolean isValidString(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 0)
                return true;
        }
        return false;
    }

    private boolean checkInputs() {

        if (rbCashWithdrawal.isChecked()) {
            selectedTransactionType = "cw";
        } else if (rbCashDeposit.isChecked()) {
            selectedTransactionType = "cd";
        } else if (rbBalanceEnquiry.isChecked()) {
            selectedTransactionType = "be";
            etAmount.setText("0");
        } else if (rbMiniStatement.isChecked()) {
            selectedTransactionType = "mn";
        } else if (rbCardActivation.isChecked()) {
            selectedTransactionType = "Card Activation";
        } else if (rbResetPin.isChecked()) {
            selectedTransactionType = "Reset Pin";
        } else if (rbChangePin.isChecked()) {
            selectedTransactionType = "Change pin";
        }

        if (!selectedTransactionType.equalsIgnoreCase("select")) {
            if (selectedTransactionType.equalsIgnoreCase("be") ||
                    selectedTransactionType.equalsIgnoreCase("mn") ||
                    selectedTransactionType.equalsIgnoreCase("Card Activation") ||
                    selectedTransactionType.equalsIgnoreCase("Reset Pin") ||
                    selectedTransactionType.equalsIgnoreCase("Change pin")
            ) {
                return etMobile.getText().toString().trim().length() == 10 &&
                        !TextUtils.isEmpty(etRemarks.getText().toString());


            } else {
                return etMobile.getText().toString().trim().length() == 10 &&
                        !TextUtils.isEmpty(etAmount.getText().toString()) &&
                        !TextUtils.isEmpty(etRemarks.getText().toString());
            }
        }
        return false;

    }

    private void checkPermissions() {
        List<String> permissions = getUngrantedPermissions();
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(FingPayMicroAtmActivity.this,
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
            if (ContextCompat.checkSelfPermission(FingPayMicroAtmActivity.this, s) != PackageManager.PERMISSION_GRANTED)
                permissions.add(s);
        }

        return permissions;
    }

    private void initViews() {
        etMobile = findViewById(R.id.et_mobile);
        etAmount = findViewById(R.id.et_amount);
        etRemarks = findViewById(R.id.et_remarks);
        rbCashWithdrawal = findViewById(R.id.rb_cw);
        rbCashDeposit = findViewById(R.id.rb_cd);
        rbBalanceEnquiry = findViewById(R.id.rb_be);
        rbMiniStatement = findViewById(R.id.rb_ms);
        rbCardActivation = findViewById(R.id.rb_ca);
        rbResetPin = findViewById(R.id.rb_rp);
        rbChangePin = findViewById(R.id.rb_cp);
        rgTransactionType = findViewById(R.id.rg_type);
        btnLaunchMatm = findViewById(R.id.btn_fingpay);
        btnHistory = findViewById(R.id.btn_history);
    }

    private void getMatmCredentials() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingPayMicroAtmActivity.this).create();
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
                        pDialog.dismiss();

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

    private void showMessageDialog(String message) {
        final AlertDialog messageDialog = new AlertDialog.Builder(FingPayMicroAtmActivity.this).create();
        final LayoutInflater inflater = LayoutInflater.from(FingPayMicroAtmActivity.this);
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
        if (resultCode == RESULT_OK && requestCode == CODE) {
            Bundle bundle = data.getExtras();
            String responseData = getJson(bundle);
            Log.d("dataResponse", responseData);

            status = data.getBooleanExtra(Constants.TRANS_STATUS, false);
            matmResponse = data.getStringExtra(Constants.MESSAGE);
            transAmount = data.getDoubleExtra(Constants.TRANS_AMOUNT, 0);
            balAmount = data.getDoubleExtra(Constants.BALANCE_AMOUNT, 0);
            bankRrn = data.getStringExtra(Constants.RRN);
            transType = data.getStringExtra(Constants.TRANS_TYPE);
            type = data.getIntExtra(Constants.TYPE, Constants.CASH_WITHDRAWAL);
            cardNum = data.getStringExtra(Constants.CARD_NUM);
            bankName = data.getStringExtra(Constants.BANK_NAME);
            cardType = data.getStringExtra(Constants.CARD_TYPE);
            terminalId = data.getStringExtra(Constants.TERMINAL_ID);
            fpId = data.getStringExtra(Constants.FP_TRANS_ID);
            transId = data.getStringExtra(Constants.TRANS_ID);
            STATUS_CODE = data.getLongExtra(Constants.STATUS_CODE, 555);
            RESPONSE_CODE = data.getStringExtra(Constants.RESPONSE_CODE);

            //TODO : status code dena h

            if (type == Constants.MINI_STATEMENT) {
                List<MiniStatementModel> l = data.getParcelableArrayListExtra(Constants.LIST);
                /*if (Utils.isValidArrayList((ArrayList<?>) l)) {
                    Utils.logD(l.toString());
                }*/
            }

            if (isValidString(matmResponse)) {
                if (!isValidString(bankRrn))
                    bankRrn = "";
                if (!isValidString(transType))
                    transType = "";

              /*  String s = "Status :" + status + "\n" + "Message : " + response + "\n"
                        + "Trans Amount : " + transAmount + "\n" + "Balance Amount : " + balAmount + "\n"
                        + "Bank RRN : " + bankRrn + "\n" + "Trand Type : " + transType + "\n"
                        + "Type : " + type + "\n" + "Card Num :" + cardNum + "\n" + "CardType :" + cardType + "\n" + "Bank Name :" + bankName + "\n" + "Terminal Id :" + terminalId;
*/
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("status", status);
                    jsonObject.put("message", matmResponse);
                    jsonObject.put("txnAmount", transAmount);
                    jsonObject.put("balAmount", balAmount);
                    jsonObject.put("bankRRN", bankRrn);
                    jsonObject.put("transType", selectedTransactionType);
                    jsonObject.put("type", type);
                    jsonObject.put("cardNo", cardNum);
                    jsonObject.put("cardType", cardType);
                    jsonObject.put("bankName", bankName);
                    jsonObject.put("terminalId", terminalId);
                    jsonObject.put("uniqueId", uniqueId);
                    jsonObject.put("mobileNo", enteredMobileNo);
                    jsonObject.put("merchantLoginId", merchantId);
                    jsonObject.put("merchantPassword", merchantPassword);
                    jsonObject.put("superMerchantId", superMerchantId);
                    jsonObject.put("statusCode", STATUS_CODE);
                    jsonObject.put("responseCode", RESPONSE_CODE);
                    jsonObject.put("fingpayTransId", fpId);
                    jsonObject.put("insertOrUpdate", "update");

                    String requestDataStr = jsonObject.toString();
                    updateMatmData(requestDataStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_PERMISSION_SETTING) {


            if (ActivityCompat.checkSelfPermission(FingPayMicroAtmActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {

            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(FingPayMicroAtmActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private String getJson(final Bundle bundle) {
        if (bundle == null) return null;
        JSONObject jsonObject = new JSONObject();

        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            try {
                jsonObject.put(key, wrap(bundle.get(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    private void updateMatmData(String requestDataStr) {
        final AlertDialog pDialog = new AlertDialog.Builder(FingPayMicroAtmActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().fingpayMatmCallback(AUTH_KEY, userId, deviceId, deviceInfo,
                requestDataStr, "1.2.2");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        pDialog.dismiss();
                        Intent intent = new Intent(FingPayMicroAtmActivity.this, MatmReceiptActivity.class);
                        intent.putExtra("cardNumber", cardNum);
                        intent.putExtra("cardType", cardType);
                        intent.putExtra("amount", transAmount + "");
                        intent.putExtra("message", matmResponse);
                        intent.putExtra("bankBalance", balAmount + "");
                        intent.putExtra("bankRRN", bankRrn);
                        intent.putExtra("transactionType", selectedTransactionType);
                        intent.putExtra("bank", bankName);
                        intent.putExtra("uniqueTransactionId", uniqueId);
                        intent.putExtra("date", "date");
                        intent.putExtra("time", "time");
                        intent.putExtra("openingBalance", "openingBalance");
                        intent.putExtra("closingBalance", "closingBalance");
                        intent.putExtra("isMatmReport", false);
                        String statusStr = null;
                        if (status)
                            statusStr = "Success";
                        else
                            statusStr = "Failed";

                        intent.putExtra("status", statusStr);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showMessageDialog("Something went wrong.");
                    }
                } else {
                    pDialog.dismiss();
                    showMessageDialog("Something went wrong.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showMessageDialog(t.getMessage());
            }
        });
    }
}