package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class AddBankDetailActivity extends AppCompatActivity {

    TextView tvBankName;
    EditText etAccountHolderName,etAccountNumber,etIfscCode;
    Button btnAddDetails;

    String userId,deviceId,deviceInfo;
    SharedPreferences sharedPreferences;

    ArrayList<String> bankNameArrayList, ifscArrayList;
    String selectedBankName="select";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_detail);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AddBankDetailActivity.this);
        userId= sharedPreferences.getString("userid",null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo= sharedPreferences.getString("deviceInfo",null);

        initViews();
        getBanks();

        btnAddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInputs())
                {
                    addBankDetails();
                }
                else
                {
                    Toast.makeText(AddBankDetailActivity.this, "All Fields Are Mandatory!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void addBankDetails() {
        ProgressDialog progressDialog =new ProgressDialog(AddBankDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        String accountHolderName=etAccountHolderName.getText().toString().trim();
        String accountHolderNumber=etAccountNumber.getText().toString().trim();
        String ifsc=etIfscCode.getText().toString().trim();
        Call<JsonObject> call=RetrofitClient.getInstance().getApi().addbankDetails(AUTH_KEY,userId,deviceId,deviceInfo,selectedBankName,
                accountHolderNumber,accountHolderName,ifsc);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setCancelable(false)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK",null)
                                .show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddBankDetailActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(AddBankDetailActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK",null)
                        .show();
            }
        });
    }

    private boolean checkInputs() {
        return !selectedBankName.equalsIgnoreCase("select") && !TextUtils.isEmpty(etAccountHolderName.getText())
                && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscCode.getText());
    }

    private void initViews() {
        tvBankName=findViewById(R.id.tv_bank_name);
        etAccountHolderName=findViewById(R.id.et_account_holder_name);
        etAccountNumber=findViewById(R.id.et_account_number);
        etIfscCode=findViewById(R.id.et_ifsc_number);
        btnAddDetails=findViewById(R.id.btn_add_details);
    }

    private void getBanks() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBankDmt(AUTH_KEY, deviceId, deviceInfo, userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray dataArray=jsonObject.getJSONArray("data");

                            bankNameArrayList = new ArrayList<>();
                            ifscArrayList = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject transactionObject = dataArray.getJSONObject(i);

                                String responsebankName = transactionObject.getString("BankName");
                                String responseIfsc = transactionObject.getString("IfscCode");

                                bankNameArrayList.add(responsebankName);
                                ifscArrayList.add(responseIfsc);

                            }

                            tvBankName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(AddBankDetailActivity.this, bankNameArrayList, "Select Bank", in.galaxyofandroid.spinerdialog.R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvBankName.setText(item);
                                            selectedBankName = bankNameArrayList.get(position);
                                            etIfscCode.setText(ifscArrayList.get(position));
                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });


                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddBankDetailActivity.this)
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddBankDetailActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddBankDetailActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }
}