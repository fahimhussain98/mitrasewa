package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.adapters.CmsReportAdapter;
import wts.com.mitrsewa.models.CMSReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class CmsReportActivity extends AppCompatActivity {
    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromDate, tvToDate;
    SharedPreferences sharedPreferences;
    String fromDate = "", toDate = "";

    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    RecyclerView allReportsRecycler;

    ArrayList<CMSReportModel> cmsReportModelArrayList;

    boolean isInitialReport = true;

    String userId, deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matm_report);
        initViews();

        ////////////////////////////////////Select from date, to date and search by

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        fromDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(CmsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromDate.setText(simpleDateFormat.format(newDate1.getTime()));

                        fromDate = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();

            }
        });

        toDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(CmsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

                        toDate = webServiceDateFormat.format(newDate1.getTime());
                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();

            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CmsReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        ////////////////////////////////////Select from date, to date and search by

        //////////////////////////////////////////////////////////////////
        Calendar fromCalender = Calendar.getInstance();
        fromDate = webServiceDateFormat.format(fromCalender.getTime());
        tvFromDate.setText(simpleDateFormat.format(fromCalender.getTime()));


        Calendar toCalender = Calendar.getInstance();
        toDate = webServiceDateFormat.format(toCalender.getTime());
        tvToDate.setText(simpleDateFormat.format(toCalender.getTime()));


        if (checkInternetState()) {
            getReports();
        } else {
            showSnackBar();
        }
        //////////////////////////////////////////////////////////////////
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromDate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(CmsReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        isInitialReport = false;
                        getReports();
                    }

                } else {
                    showSnackBar();
                }
            }
        });

    }

    private void getReports() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(CmsReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getCmsReport(AUTH_KEY, userId, deviceId, deviceInfo, fromDate, toDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {

                            cmsReportModelArrayList = new ArrayList<>();
                            JSONObject dataObject = responseObject.getJSONObject("data");
                            JSONArray tableArray = dataObject.getJSONArray("Table");

                            for (int i = 0; i < tableArray.length(); i++) {
                                CMSReportModel cmsReportModel = new CMSReportModel();
                                JSONObject tableObject = tableArray.getJSONObject(i);
                                String transactionId = tableObject.getString("Transactionid");
                                String agentName = tableObject.getString("Network");
                                String billerName = tableObject.getString("OperatorName");
                                String userId = tableObject.getString("UserName");
                                String ownerName = tableObject.getString("OwnerName");
                                String amount = tableObject.getString("Amount");
                                String responseDate = tableObject.getString("CreatedOn");
                                String commission = tableObject.getString("Commission");
                                String openingBalance = tableObject.getString("OpeningBal");
                                String closingBalance = tableObject.getString("ClosingBal");
                                String surcharge = tableObject.getString("Surcharge");
                                String status = tableObject.getString("status");

                                status = status.replaceAll("\\<.*?\\>", "");

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = responseDate.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);

                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    cmsReportModel.setDate(outputDate);
                                    cmsReportModel.setTime(outputTime);

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                                cmsReportModel.setTransactionId(transactionId);
                                cmsReportModel.setAmount(amount);
                                cmsReportModel.setCommission(commission);
                                cmsReportModel.setOpeningBalance(openingBalance);
                                cmsReportModel.setClosingBalance(closingBalance);
                                cmsReportModel.setSurcharge(surcharge);
                                cmsReportModel.setStatus(status);
                                cmsReportModel.setAgentName(agentName);
                                cmsReportModel.setBillerName(billerName);
                                cmsReportModel.setUserId(userId);
                                cmsReportModel.setOwnerName(ownerName);

                                cmsReportModelArrayList.add(cmsReportModel);

                            }

                            CmsReportAdapter cmsReportAdapter=new CmsReportAdapter(cmsReportModelArrayList);
                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(CmsReportActivity.this,
                                    RecyclerView.VERTICAL,false));
                            allReportsRecycler.setAdapter(cmsReportAdapter);

                            pDialog.dismiss();
                            allReportsRecycler.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.GONE);

                        } else {
                            pDialog.dismiss();
                            allReportsRecycler.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        allReportsRecycler.setVisibility(View.GONE);
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    pDialog.dismiss();
                    allReportsRecycler.setVisibility(View.GONE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                allReportsRecycler.setVisibility(View.GONE);
                imgNoDataFound.setVisibility(View.VISIBLE);
                tvNoDataFound.setVisibility(View.VISIBLE);
            }
        });

    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackBar() {
        Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {

        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromDate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        allReportsRecycler = findViewById(R.id.all_report_recycler);
    }
}