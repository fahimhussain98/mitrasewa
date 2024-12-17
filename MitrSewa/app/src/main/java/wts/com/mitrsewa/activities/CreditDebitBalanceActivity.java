package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class CreditDebitBalanceActivity extends AppCompatActivity {

    String title;
    TextView activityTitle;
    ImageView backButton;

    EditText etCreditDebitAmount, etCreditDebitRemark;
    Button btnProceed;
    Spinner spinner;
    SharedPreferences sharedPreferences;
    ImageView imgCreditDebit;
    TextView tvBalance;


    ArrayList<String> usersArrayList, usersIdArrayList, ownerNameList;
    String  user, id, selectedUser = "Select User", selectedUserId= "Select User";
    boolean flag = false;
    String userId;
    Call<JsonObject> call;
    String deviceId,deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_debit_balance);

        inhitViews();

        //////////////////////////////////////////////////////////////////Toolbar
        title = getIntent().getStringExtra("title");
        activityTitle.setText(title);
        if (title.equalsIgnoreCase("Credit Balance")) {
            imgCreditDebit.setImageResource(R.drawable.credit);
        } else if (title.equalsIgnoreCase("Debit Balance")) {
            imgCreditDebit.setImageResource(R.drawable.debit);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //////////////////////////////////////////////////////////////////Toolbar
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreditDebitBalanceActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        if (checkInternetState()) {
            getUsers(userId);
        } else {
            showSnackbar("No Internet");
        }

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag) {
                    if (checkInputs()) {
                        if (checkInternetState()) {
                            checkCreditDebit();
                        } else {
                            showSnackbar("No Internet");
                        }
                    }
                }
            }
        });
    }

    private void checkCreditDebit() {
        String amount = etCreditDebitAmount.getText().toString().trim();
        String remarks = etCreditDebitRemark.getText().toString().trim();

        if (title.equalsIgnoreCase("Credit Balance")) {
            call = RetrofitClient.getInstance().getApi().doCreditBalance(AUTH_KEY,userId,deviceId,deviceInfo,amount,selectedUserId);
            doCreditDebit();
        } else if (title.equalsIgnoreCase("Debit Balance")) {
            call = RetrofitClient.getInstance().getApi().doDebitBalance(AUTH_KEY,userId,deviceId,deviceInfo,amount,selectedUserId);
            doCreditDebit();
        }
    }

    private void doCreditDebit() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(CreditDebitBalanceActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                        JSONObject jsonObject1 = null;

                        try {
                            jsonObject1 = new JSONObject(String.valueOf(response.body()));

                            String statuscode = jsonObject1.getString("statuscode");
                            String responseMessage = jsonObject1.getString("status");
                            String transactions = jsonObject1.getString("data");

                            if (statuscode.equalsIgnoreCase("TXN")) {
                                final View view1 = LayoutInflater.from(CreditDebitBalanceActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                                final AlertDialog builder = new AlertDialog.Builder(CreditDebitBalanceActivity.this).create();
                                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                builder.setCancelable(false);
                                builder.setView(view1);
                                builder.show();

                                ImageView imgRechargeDialogIcon = view1.findViewById(R.id.img_recharge_dialog_icon);
                                TextView tvRechargeDialogNumber = view1.findViewById(R.id.tv_recharge_dialogue_number);
                                TextView tvRechargeDialogStatus = view1.findViewById(R.id.tv_recharge_dialogue_status);
                                TextView tvRechargeDialogTitle = view1.findViewById(R.id.tv_recharge_dialog_title);

                                TextView tvRechargeDialogAmount = view1.findViewById(R.id.tv_recharge_dialogue_amount);
                                Button btnRechargeDialog = view1.findViewById(R.id.btn_recharge_dialog);

                                tvRechargeDialogTitle.setText("Status");

                                tvRechargeDialogNumber.setVisibility(View.INVISIBLE);
                                imgRechargeDialogIcon.setImageResource(R.drawable.success);
                                tvRechargeDialogStatus.setText("Status :" + responseMessage);
                                tvRechargeDialogAmount.setText(transactions);

                                btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        builder.dismiss();
                                        finish();
                                    }
                                });
                                pDialog.dismiss();
                            } else if (statuscode.equalsIgnoreCase("ERR")) {
                                pDialog.dismiss();
                                String status = jsonObject1.getString("data");
                                final View view1 = LayoutInflater.from(CreditDebitBalanceActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                                final AlertDialog builder = new AlertDialog.Builder(CreditDebitBalanceActivity.this).create();
                                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                builder.setCancelable(false);
                                builder.setView(view1);
                                builder.show();

                                ImageView imgRechargeDialogIcon = view1.findViewById(R.id.img_recharge_dialog_icon);
                                TextView tvRechargeDialogTitle = view1.findViewById(R.id.tv_recharge_dialog_title);

                                tvRechargeDialogTitle.setText("Status");
                                TextView tvRechargeDialogNumber = view1.findViewById(R.id.tv_recharge_dialogue_number);
                                TextView tvRechargeDialogStatus = view1.findViewById(R.id.tv_recharge_dialogue_status);
                                TextView tvRechargeDialogAmount = view1.findViewById(R.id.tv_recharge_dialogue_amount);
                                Button btnRechargeDialog = view1.findViewById(R.id.btn_recharge_dialog);

                                tvRechargeDialogAmount.setVisibility(View.INVISIBLE);
                                tvRechargeDialogNumber.setVisibility(View.INVISIBLE);

                                tvRechargeDialogStatus.setTextColor(Color.RED);
                                tvRechargeDialogStatus.setText("Status : " + status);
                                imgRechargeDialogIcon.setImageResource(R.drawable.failureicon);
                                btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        builder.dismiss();
                                    }
                                });
                            } else {
                                pDialog.dismiss();
                                new AlertDialog.Builder(CreditDebitBalanceActivity.this)
                                        .setTitle("Alert")
                                        .setMessage("Something went wrong.")
                                        .setPositiveButton("Ok", null).show();
                            }
                        }

                        catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                            new AlertDialog.Builder(CreditDebitBalanceActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null).show();
                        }
                    }
                 else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(CreditDebitBalanceActivity.this)
                            .setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(CreditDebitBalanceActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null).show();
            }
        });

    }

    private void getUsers(final String userId) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(CreditDebitBalanceActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUsers(AUTH_KEY,deviceId,deviceInfo,userId,"ALL");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            usersArrayList = new ArrayList<>();
                            usersIdArrayList = new ArrayList<>();
                            ownerNameList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                user = jsonObject.getString("UserName");
                                id = jsonObject.getString("ID");

                                usersIdArrayList.add(id);
                                usersArrayList.add(user);
                            }

                            HintSpinner<String> hintSpinner = new HintSpinner<>(
                                    spinner,
                                    new HintAdapter<String>(CreditDebitBalanceActivity.this, "Select User"
                                            , usersArrayList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {

                                            selectedUserId = usersIdArrayList.get(position);
                                        }
                                    });

                            hintSpinner.init();

                            pDialog.dismiss();
                            flag = true;
                        } /*else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String errorMessage = jsonObject1.getString("response_msg");

                            new AlertDialog.Builder(CreditDebitBalanceActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                        }*/ else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(CreditDebitBalanceActivity.this).setTitle("Alert")
                                    .setMessage("NO user found")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(CreditDebitBalanceActivity.this).setTitle("Alert")
                                .setMessage("NO user found")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(CreditDebitBalanceActivity.this).setTitle("Alert")
                            .setMessage("NO user found")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(CreditDebitBalanceActivity.this).setTitle("Alert")
                        .setMessage("NO user found")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void inhitViews() {
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title);

        etCreditDebitAmount = findViewById(R.id.et_credit_debit_amount);
        etCreditDebitRemark = findViewById(R.id.et_credit_debit_remark);
        btnProceed = findViewById(R.id.btn_proceed);
        spinner = findViewById(R.id.spinner);
        imgCreditDebit = findViewById(R.id.img_credit_debit);
        tvBalance = findViewById(R.id.tv_balance);
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(etCreditDebitAmount.getText())) {
            if (!TextUtils.isEmpty(etCreditDebitRemark.getText())) {
                if (!selectedUserId.equalsIgnoreCase("Select User")) {
                    return true;
                } else {
                    new AlertDialog.Builder(CreditDebitBalanceActivity.this).setMessage("Select User")
                            .setPositiveButton("Ok", null).show();
                    return false;
                }
            } else {
                etCreditDebitRemark.setError("Add Remark.");
                return false;
            }
        } else {
            etCreditDebitAmount.setError("Enter Amount.");
            return false;
        }
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.credit_debit_balance_layout), message, Snackbar.LENGTH_LONG);
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