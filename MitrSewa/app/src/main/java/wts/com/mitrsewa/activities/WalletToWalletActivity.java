package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class WalletToWalletActivity extends AppCompatActivity {

    EditText etMobileNumber, etAmount, etRemarks;
    TextView tvUsername, tvCompany, tvEmailId;
    AppCompatButton btnSubmit, btnProceedToPay;
    Spinner spinnerFromWallet, spinnerToWallet;
    String selectedFromWallet = "select", selectedToWallet = "select";

    String mobileNumber;

    String userId, deviceId, deviceInfo;

    SharedPreferences sharedPreferences;

    String transferTo, userName, companyName, targetMobileNumber, emailId;

    ArrayList<String> walletList;
    TextView tvFromBalance, tvToBalance;
    String fromMainBalance, fromAepsBalance, toMainWallet, toAepsWallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_to_wallet);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WalletToWalletActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        fromMainBalance = getIntent().getStringExtra("mainBalance");
        fromAepsBalance = getIntent().getStringExtra("aepsBalance");


        HintSpinner<String> hintSpinnerFrom = new HintSpinner<>(
                spinnerFromWallet,
                new HintAdapter<String>(WalletToWalletActivity.this, "From Wallet"
                        , walletList),
                new HintSpinner.Callback<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {

                        selectedFromWallet = walletList.get(position);

                        if (selectedFromWallet.equalsIgnoreCase("Main Wallet"))
                            tvFromBalance.setText("₹ " + fromMainBalance);
                        else {
                            tvFromBalance.setText("₹ " + fromAepsBalance);
                        }

                    }
                });

        hintSpinnerFrom.init();

        HintSpinner<String> hintSpinnerTo = new HintSpinner<>(
                spinnerToWallet,
                new HintAdapter<String>(WalletToWalletActivity.this, "To Wallet"
                        , walletList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {

                        selectedToWallet = walletList.get(position);

                        if (selectedToWallet.equalsIgnoreCase("Main Wallet"))
                            tvToBalance.setText("₹ " + toMainWallet);
                        else {
                            tvToBalance.setText("₹ P" + toAepsWallet);
                        }
                    }
                });

        hintSpinnerTo.init();

        etAmount.setVisibility(View.GONE);
        etRemarks.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        tvCompany.setVisibility(View.GONE);
        tvEmailId.setVisibility(View.GONE);
        btnProceedToPay.setVisibility(View.GONE);
        spinnerFromWallet.setVisibility(View.GONE);
        spinnerToWallet.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(view -> {
            mobileNumber = etMobileNumber.getText().toString().trim();
            if (mobileNumber.length() == 10) {
                getUserDetails();
            } else {
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage("Please enter valid mobile number.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        btnProceedToPay.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(etAmount.getText().toString()) && !selectedFromWallet.equalsIgnoreCase("select")
                    && !selectedToWallet.equalsIgnoreCase("select"))
                showTpinDialog();
               // doWalletToWalletTransaction();
            else
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage("All fields are mandatory")
                        .setPositiveButton("OK", null)
                        .show();
        });

    }

    private void doWalletToWalletTransaction() {
        ProgressDialog progressDialog = new ProgressDialog(WalletToWalletActivity.this);
        progressDialog.setMessage("Please wait\nGetting Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (selectedFromWallet.equalsIgnoreCase("Main Wallet"))
            selectedFromWallet = "1";
        else
            selectedFromWallet = "0";

        if (selectedToWallet.equalsIgnoreCase("Main Wallet"))
            selectedToWallet = "1";
        else
            selectedToWallet = "0";

        String amount = etAmount.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doWalletToWalletTransaction(AUTH_KEY, userId, deviceId, deviceInfo, transferTo,
                selectedFromWallet, selectedToWallet, amount, "NA");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        //String responseCode=responseObject.getString("statuscode");

                        progressDialog.dismiss();
                        String message = responseObject.getString("data");
                        new AlertDialog.Builder(WalletToWalletActivity.this)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(WalletToWalletActivity.this)
                                .setMessage("Please try after sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(WalletToWalletActivity.this)
                            .setMessage("Please try after sometime.")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void getUserDetails() {
        ProgressDialog progressDialog = new ProgressDialog(WalletToWalletActivity.this);
        progressDialog.setMessage("Please wait\nGetting Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUserDetails(AUTH_KEY, userId, deviceId, deviceInfo, mobileNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            transferTo = dataObject.getString("ID");
                            userName = dataObject.getString("UserName");
                            companyName = dataObject.getString("CompanyName");
                            targetMobileNumber = dataObject.getString("MobileNo");
                            emailId = dataObject.getString("EmailID");
                            toMainWallet = dataObject.getString("MainWalletBalance");
                            toAepsWallet = dataObject.getString("AePSWalletBalance");

                            tvUsername.setText(userName);
                            tvCompany.setText(companyName);
                            tvEmailId.setText(emailId);

                            tvUsername.setVisibility(View.VISIBLE);
                            etAmount.setVisibility(View.VISIBLE);
                            //etRemarks.setVisibility(View.VISIBLE);
                            tvCompany.setVisibility(View.VISIBLE);
                            tvEmailId.setVisibility(View.VISIBLE);
                            btnProceedToPay.setVisibility(View.VISIBLE);
                            spinnerFromWallet.setVisibility(View.VISIBLE);
                            spinnerToWallet.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.GONE);
                            etMobileNumber.setVisibility(View.GONE);

                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");
                            new AlertDialog.Builder(WalletToWalletActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();

                            tvUsername.setText(userName);
                            tvCompany.setText(companyName);
                            tvEmailId.setText(emailId);

                            tvUsername.setVisibility(View.GONE);
                            etAmount.setVisibility(View.GONE);
                            //etRemarks.setVisibility(View.GONE);
                            tvCompany.setVisibility(View.GONE);
                            tvEmailId.setVisibility(View.GONE);
                            btnProceedToPay.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.VISIBLE);
                            etMobileNumber.setVisibility(View.VISIBLE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(WalletToWalletActivity.this)
                                .setMessage("Please try after sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();

                        tvUsername.setText(userName);
                        tvCompany.setText(companyName);
                        tvEmailId.setText(emailId);

                        tvUsername.setVisibility(View.GONE);
                        etAmount.setVisibility(View.GONE);
                        //etRemarks.setVisibility(View.GONE);
                        tvCompany.setVisibility(View.GONE);
                        tvEmailId.setVisibility(View.GONE);
                        btnProceedToPay.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        etMobileNumber.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(WalletToWalletActivity.this)
                            .setMessage("Please try after sometime.")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();

                    tvUsername.setText(userName);
                    tvCompany.setText(companyName);
                    tvEmailId.setText(emailId);

                    tvUsername.setVisibility(View.GONE);
                    etAmount.setVisibility(View.GONE);
                    //etRemarks.setVisibility(View.GONE);
                    tvCompany.setVisibility(View.GONE);
                    tvEmailId.setVisibility(View.GONE);
                    btnProceedToPay.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    etMobileNumber.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();

                tvUsername.setText(userName);
                tvCompany.setText(companyName);
                tvEmailId.setText(emailId);

                tvUsername.setVisibility(View.GONE);
                etAmount.setVisibility(View.GONE);
                //etRemarks.setVisibility(View.GONE);
                tvCompany.setVisibility(View.GONE);
                tvEmailId.setVisibility(View.GONE);
                btnProceedToPay.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                etMobileNumber.setVisibility(View.VISIBLE);
            }
        });

    }

    private void initViews() {
        etMobileNumber = findViewById(R.id.et_mobile_number);
        etAmount = findViewById(R.id.et_amount);
        etRemarks = findViewById(R.id.et_remarks);
        tvUsername = findViewById(R.id.tv_username);
        tvCompany = findViewById(R.id.tv_company);
        tvEmailId = findViewById(R.id.tv_email_id);
        btnSubmit = findViewById(R.id.btn_submit);
        btnProceedToPay = findViewById(R.id.btn_proceed);
        spinnerFromWallet = findViewById(R.id.spinner_from_wallet);
        spinnerToWallet = findViewById(R.id.spinner_to_wallet);
        tvFromBalance = findViewById(R.id.tv_from_balance);
        tvToBalance = findViewById(R.id.tv_to_balance);

        walletList = new ArrayList<>();
        walletList.add("Main Wallet");
        walletList.add("AEPS Wallet");
    }

    private void showTpinDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(WalletToWalletActivity.this).create();
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
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(WalletToWalletActivity.this).create();
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
                            // doSettlement();
                             doWalletToWalletTransaction();

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

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}


