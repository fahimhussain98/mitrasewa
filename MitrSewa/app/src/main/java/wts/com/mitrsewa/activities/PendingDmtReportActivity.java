package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.adapters.PendingDmtReportAdapter;
import wts.com.mitrsewa.models.MoneyReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class PendingDmtReportActivity extends AppCompatActivity {

    String userId;
    SharedPreferences sharedPreferences;
    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    RecyclerView creditDebitRecycler;
    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_dmt_report);

        inhitViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PendingDmtReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);


        if (checkInternetState()) {
            getReport();
        } else {
            showSnackbar();
        }
    }

    private void getReport() {

        final AlertDialog pDialog = new AlertDialog.Builder(PendingDmtReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().pendingDmtReport(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            creditDebitRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            moneyReportModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                MoneyReportModel moneyReportModel = new MoneyReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String amount = jsonObject.getString("Amount");
                                String cost = jsonObject.getString("PayableAmount");
                                String comm = jsonObject.getString("Commission");
                                String surcharge = jsonObject.getString("Surcharge");
                                String balance = jsonObject.getString("ClosingBalance");
                                String tdatetime = jsonObject.getString("CreateDate");
                                String accountno = jsonObject.getString("AccountNo");
                                String beniname = jsonObject.getString("BenificiaryName");
                                String bank = jsonObject.getString("BankName");
                                String ifsc = jsonObject.getString("IfscCode");
                                String status = jsonObject.getString("Status");
                                String transactionid = jsonObject.getString("UniqueID");
                                String bankRrnNo = jsonObject.getString("BankRrnNo");
                                String tds = jsonObject.getString("Tds");
                                String gst = jsonObject.getString("Gst");

                                moneyReportModel.setAmount(amount);
                                moneyReportModel.setCost(cost);
                                moneyReportModel.setBalance(balance);
                                moneyReportModel.setDate(tdatetime);
                                moneyReportModel.setAccountNumber(accountno);
                                moneyReportModel.setBeniName(beniname);
                                moneyReportModel.setBank(bank);
                                moneyReportModel.setIfsc(ifsc);
                                moneyReportModel.setStatus(status);
                                moneyReportModel.setTransactionId(transactionid);
                                moneyReportModel.setCommission(comm);
                                moneyReportModel.setSurcharge(surcharge);
                                moneyReportModel.setBankRRN(bankRrnNo);
                                moneyReportModel.setTds(tds);
                                moneyReportModel.setGst(gst);

                                moneyReportModelArrayList.add(moneyReportModel);
                            }

                            creditDebitRecycler.setLayoutManager(new LinearLayoutManager(PendingDmtReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            PendingDmtReportAdapter pendingDmtReportAdapter = new PendingDmtReportAdapter(moneyReportModelArrayList,
                                    PendingDmtReportActivity.this, PendingDmtReportActivity.this, userId, deviceId, deviceInfo);
                            creditDebitRecycler.setAdapter(pendingDmtReportAdapter);
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();

                            creditDebitRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        creditDebitRecycler.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    creditDebitRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                pDialog.dismiss();
                creditDebitRecycler.setVisibility(View.GONE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                imgNoDataFound.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.money_report_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void inhitViews() {

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        creditDebitRecycler = findViewById(R.id.credit_debit_recycler);
    }
}