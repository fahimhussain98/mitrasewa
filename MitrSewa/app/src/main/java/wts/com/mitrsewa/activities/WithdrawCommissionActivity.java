package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class WithdrawCommissionActivity extends AppCompatActivity {

    Spinner spinnerPaymentType, spinnerAccountType, spinnerTransactionType, spinnerBankName;
    ArrayList<String> paymentTypeList, accountTypeList, transactionTypeList, bankNameList, accountHolderNameList, ifscCodeList, accountNumberList;
    String selectedPaymentType = "select", selectedTransactionType = "select", selectedAccountType = "select", userId, userName, selectedBankName = "NA";
    ConstraintLayout bankDetailsContainer;
    boolean isWallet = true;
    ImageView imgBack;
    EditText etAmount, etAccountHolderName, etAccountNumber, etIfscNumber;
    Button btnSubmit, btnAddMoreBanks;
    SharedPreferences sharedPreferences;
    String amount, selectedAccountHolderName, bankName, selectedAccountNumber, selectedIfscNumber, panCard, mobileNo;
    SimpleDateFormat webServiceDateFormat;
    String currentDate;
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_commission);

        initViews();
        setViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WithdrawCommissionActivity.this);
        userId = sharedPreferences.getString("userid", null);
        panCard = sharedPreferences.getString("pancard", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        userName = sharedPreferences.getString("username", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        imgBack.setOnClickListener(v -> onBackPressed());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        currentDate = webServiceDateFormat.format(newDate1.getTime());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (checkInputs()) {
                     //   doSettlement();
                        showTpinDialog();
                    } else {
                        showSnackBar("Select Above Details");
                    }
                } else {
                    showSnackBar("No Internet");
                }
            }
        });

        btnAddMoreBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(WithdrawCommissionActivity.this, AddBankDetailActivity.class));
            }
        });

    }

    private void getAccountDetails() {
        final AlertDialog pDialog = new AlertDialog.Builder(WithdrawCommissionActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAccountDetails(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            bankNameList = new ArrayList<>();
                            accountHolderNameList = new ArrayList<>();
                            accountNumberList = new ArrayList<>();
                            ifscCodeList = new ArrayList<>();

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String bankName = dataObject.getString("BankName");
                                String accountHolderName = dataObject.getString("AccountHolderName");
                                String accountNo = dataObject.getString("AccountNo");
                                String ifscCode = dataObject.getString("IfscCode");

                                bankNameList.add(bankName);
                                accountHolderNameList.add(accountHolderName);
                                accountNumberList.add(accountNo);
                                ifscCodeList.add(ifscCode);

                            }

//                            if (bankNameList.size() < 3) {
//                                btnAddMoreBanks.setVisibility(View.VISIBLE);
//                            }

                            HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerBankName, new HintAdapter<String>(WithdrawCommissionActivity.this, "Bank Name", bankNameList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedBankName = bankNameList.get(position);
                                            selectedAccountHolderName = accountHolderNameList.get(position);
                                            selectedAccountNumber = accountNumberList.get(position);
                                            selectedIfscNumber = ifscCodeList.get(position);

                                            etAccountHolderName.setText(selectedAccountHolderName);
                                            etAccountNumber.setText(selectedAccountNumber);
                                            etIfscNumber.setText(selectedIfscNumber);

                                            etAccountHolderName.setEnabled(false);
                                            etAccountNumber.setEnabled(false);
                                            etIfscNumber.setEnabled(false);
                                        }
                                    });
                            hintSpinner1.init();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(WithdrawCommissionActivity.this)
                                    .setCancelable(false)
                                    .setMessage("Please add bank details first.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(WithdrawCommissionActivity.this, AddBankDetailActivity.class));
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(WithdrawCommissionActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
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
                    new AlertDialog.Builder(WithdrawCommissionActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
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
                new AlertDialog.Builder(WithdrawCommissionActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong.")
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

    private void doSettlement() {
        final AlertDialog pDialog = new AlertDialog.Builder(WithdrawCommissionActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        if (selectedTransactionType.equalsIgnoreCase("IMPS"))
            selectedTransactionType = "2";
        else if (selectedTransactionType.equalsIgnoreCase("RTGS"))
            selectedTransactionType = "4";
        else if (selectedTransactionType.equalsIgnoreCase("NEFT"))
            selectedTransactionType = "3";

        if (selectedPaymentType.equalsIgnoreCase("WALLET"))
        {
            selectedPaymentType = "2";
            selectedTransactionType="NA";
        }
        else if (selectedPaymentType.equalsIgnoreCase("Bank"))
            selectedPaymentType = "1";


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().withdrawCommission(AUTH_KEY, userId, deviceId, deviceInfo, selectedPaymentType,
                amount, selectedAccountNumber, selectedIfscNumber, selectedAccountType, selectedTransactionType, selectedAccountHolderName,
                selectedBankName);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            String transactionId = dataObject.getString("UniqueTransactionId");
                            String amount = dataObject.getString("Amount");
                            String comm = dataObject.getString("Commission");
                            String balance = dataObject.getString("ClosingBal");
                            String date = dataObject.getString("CreatedOn");
                            String status = dataObject.getString("Status");
                            String transactionType = dataObject.getString("TransactionType");
                            String oldBalance = dataObject.getString("OpeningBal");
                            String accountName = dataObject.getString("AccountHolderName");
                            String accountNo = dataObject.getString("AccountNumber");
                            String bankName = dataObject.getString("BankName");
                            String surcharge = dataObject.getString("Surcharge");

                            Intent intent = new Intent(WithdrawCommissionActivity.this, SharePayoutReportActivity.class);
                            intent.putExtra("transactionId", transactionId);
                            intent.putExtra("amount", amount);
                            intent.putExtra("comm", comm);
                            intent.putExtra("balance", balance);
                            intent.putExtra("dateTime", date);
                            intent.putExtra("status", status);
                            intent.putExtra("transactionType", transactionType);
                            intent.putExtra("oldBalance", oldBalance);
                            intent.putExtra("accountName", accountName);
                            intent.putExtra("accountNo", accountNo);
                            intent.putExtra("bankName", bankName);
                            intent.putExtra("surcharge", surcharge);
                            intent.putExtra("serviceType", "payout");
                            startActivity(intent);

                        } else {
                            pDialog.dismiss();
                            String message=responseObject.getString("data");
                            new AlertDialog.Builder(WithdrawCommissionActivity.this).setTitle("Message")
                                    .setMessage(message)
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
                        new AlertDialog.Builder(WithdrawCommissionActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(WithdrawCommissionActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(WithdrawCommissionActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showTpinDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(WithdrawCommissionActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        final EditText etTpin = addSenderDialogView.findViewById(R.id.et_otp);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp=addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setVisibility(View.GONE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etTpin.getText())) {
                    String tpin = etTpin.getText().toString().trim();
                    checkTpin(tpin);
                    addSenderDialog.dismiss();
                } else {

                    etTpin.setError("Required");
                }
            }
        });

    }

    private void checkTpin(String tpin) {
        final AlertDialog pDialog = new AlertDialog.Builder(WithdrawCommissionActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().checkMpinOrTPIN(AUTH_KEY,userId,deviceId,deviceInfo
                ,"tpin",tpin);
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
                            doSettlement();
                        }
                        else
                        {
                            pDialog.dismiss();
                            String transaction=responseObject.getString("status");
                            showSnackBar(transaction);

                        }

                    }
                    catch (Exception e)
                    {
                        pDialog.dismiss();
                        showSnackBar("Something went wrong");
                    }
                }
                else
                {
                    pDialog.dismiss();
                    showSnackBar("Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackBar("Something went wrong");

            }
        });
    }

    private boolean checkInputs() {

        if (!selectedPaymentType.equalsIgnoreCase("select")) {

            if (selectedPaymentType.equalsIgnoreCase("WALLET")) {
                selectedAccountHolderName = "NA";
                selectedAccountType = "NA";
                bankName = "NA";
                selectedAccountNumber = "NA";
                selectedIfscNumber = "NA";

                amount = etAmount.getText().toString().trim();

                return !TextUtils.isEmpty(etAmount.getText());
            } else if (selectedPaymentType.equalsIgnoreCase("BANK")) {
                if (!TextUtils.isEmpty(etAmount.getText()) && !selectedTransactionType.equalsIgnoreCase("select")
                        && !TextUtils.isEmpty(etAccountHolderName.getText()) && !selectedBankName.equalsIgnoreCase("NA")
                        && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscNumber.getText())
                        && !selectedAccountType.equalsIgnoreCase("select")) {
                    amount = etAmount.getText().toString().trim();
                    selectedAccountHolderName = etAccountHolderName.getText().toString().trim();
                    selectedAccountNumber = etAccountNumber.getText().toString().trim();
                    selectedIfscNumber = etIfscNumber.getText().toString().trim();

                    return true;
                } else {
                    return false;
                }
            } else {
                selectedAccountHolderName = "NA";
                selectedAccountType = "NA";
                bankName = "NA";
                selectedAccountNumber = "NA";
                selectedIfscNumber = "NA";

                amount = etAmount.getText().toString().trim();

                return !TextUtils.isEmpty(etAmount.getText());
            }

           /* if (isWallet) {
                selectedAccountHolderName = "NA";
                selectedAccountType = "NA";
                bankName = "NA";
                selectedAccountNumber = "NA";
                selectedIfscNumber = "NA";

                amount = etAmount.getText().toString().trim();

                return !TextUtils.isEmpty(etAmount.getText());
            }

            else {
                if (!TextUtils.isEmpty(etAmount.getText()) && !selectedTransactionType.equalsIgnoreCase("select")
                        && !TextUtils.isEmpty(etAccountHolderName.getText()) && !selectedBankName.equalsIgnoreCase("select")
                        && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscNumber.getText())
                        && !selectedAccountType.equalsIgnoreCase("select")) {
                    amount = etAmount.getText().toString().trim();
                    selectedAccountHolderName = etAccountHolderName.getText().toString().trim();
                    selectedAccountNumber = etAccountNumber.getText().toString().trim();
                    selectedIfscNumber = etIfscNumber.getText().toString().trim();
                    return true;
                } else {
                    return false;
                }
            }*/

        } else {
            return false;
        }
    }

    private void setViews() {
        paymentTypeList = new ArrayList<>();
        accountTypeList = new ArrayList<>();
        transactionTypeList = new ArrayList<>();

        paymentTypeList.add("WALLET");
        paymentTypeList.add("BANK");

        accountTypeList.add("Saving");
        accountTypeList.add("Current");

        transactionTypeList.add("IMPS");
        transactionTypeList.add("NEFT");
        transactionTypeList.add("RTGS");

        HintSpinner<String> hintSpinner = new HintSpinner<>(spinnerPaymentType, new HintAdapter<String>(WithdrawCommissionActivity.this, "Payment Mode", paymentTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedPaymentType = paymentTypeList.get(position);
                        if (selectedPaymentType.equalsIgnoreCase("WALLET")) {
                            isWallet = true;
                            bankDetailsContainer.setVisibility(View.GONE);

                        } else {
                            getAccountDetails();
                            isWallet = false;
                            bankDetailsContainer.setVisibility(View.VISIBLE);
                        }
                    }
                });
        hintSpinner.init();

        HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerAccountType, new HintAdapter<String>(WithdrawCommissionActivity.this, "Account type", accountTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedAccountType = itemAtPosition;
                    }
                });
        hintSpinner1.init();


        HintSpinner<String> hintSpinner3 = new HintSpinner<>(spinnerTransactionType, new HintAdapter<String>(WithdrawCommissionActivity.this, "Transaction Mode", transactionTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedTransactionType = itemAtPosition;

                    }
                });
        hintSpinner3.init();

    }

    private void initViews() {

        etAmount = findViewById(R.id.et_amount);
        etAccountHolderName = findViewById(R.id.et_account_holder_name);
        etAccountNumber = findViewById(R.id.et_account_number);
        etIfscNumber = findViewById(R.id.et_ifsc_number);
        imgBack = findViewById(R.id.img_back);
        spinnerPaymentType = findViewById(R.id.payment_type_spinner);
        spinnerAccountType = findViewById(R.id.account_type_spinner);
        spinnerTransactionType = findViewById(R.id.transaction_type_spinner);
        spinnerBankName = findViewById(R.id.bank_name_spinner);
        bankDetailsContainer = findViewById(R.id.bank_details_container);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAddMoreBanks = findViewById(R.id.btn_add_banks);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.settlement_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
}