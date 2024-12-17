package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.adapters.MyPagerAdapter;
import wts.com.mitrsewa.commissionFragments.AadharPayCommissionFragment;
import wts.com.mitrsewa.commissionFragments.AepsCommissionFragment;
import wts.com.mitrsewa.commissionFragments.DthFragment;
import wts.com.mitrsewa.commissionFragments.ElectricityFragment;
import wts.com.mitrsewa.commissionFragments.FastagCommissionFragment;
import wts.com.mitrsewa.commissionFragments.GasCommissionFragment;
import wts.com.mitrsewa.commissionFragments.InsuranceCommissionFragment;
import wts.com.mitrsewa.commissionFragments.LandlineCommissionFragment;
import wts.com.mitrsewa.commissionFragments.LoanFragment;
import wts.com.mitrsewa.commissionFragments.MatmCommissionFragment;
import wts.com.mitrsewa.commissionFragments.MobileFragment;
import wts.com.mitrsewa.commissionFragments.MoneyCommisssionFragment;
import wts.com.mitrsewa.commissionFragments.PayoutCommissionFragment;
import wts.com.mitrsewa.commissionFragments.PostpaidFragment;
import wts.com.mitrsewa.models.MyCommissionModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class MyCommissionActivity extends AppCompatActivity {

    String userId;
    SharedPreferences sharedPreferences;
    public static ArrayList<MyCommissionModel> mobileCommissionList, postpaidCommissionList, dthCommissionList, electricityCommissionList,
            loanCommissionList,fastagCommissionList,insuranceCommissionList, landlineCommissionList, gasCommissionList,moneyCommissionList,aepsCommissionList,aadharPayCommissionList,payoutCommissionList,
            matmCommissionList;

    ViewPager viewPager;
    TabLayout tabLayout;

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commission);

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MyCommissionActivity.this, R.color.purple));
        //////CHANGE COLOR OF STATUS BAR

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyCommissionActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        if (checkInternetState()) {
            getMyCommission();
        } else {
            showSnackbar();
        }

    }

    private void getMyCommission() {
        /*final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(MyCommissionActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();*/

        ProgressDialog pDialog=new ProgressDialog(MyCommissionActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting Data...");
        pDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getMyCommission(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            mobileCommissionList = new ArrayList<>();
                            postpaidCommissionList = new ArrayList<>();
                            dthCommissionList = new ArrayList<>();
                            electricityCommissionList = new ArrayList<>();
                            loanCommissionList = new ArrayList<>();
                            fastagCommissionList = new ArrayList<>();
                            insuranceCommissionList = new ArrayList<>();
                            moneyCommissionList = new ArrayList<>();
                            aepsCommissionList = new ArrayList<>();
                            aadharPayCommissionList = new ArrayList<>();
                            landlineCommissionList = new ArrayList<>();
                            payoutCommissionList = new ArrayList<>();
                            gasCommissionList = new ArrayList<>();
                            matmCommissionList = new ArrayList<>();


                            myPagerAdapter.addFragment(new MobileFragment(), "Mobile");
                            myPagerAdapter.addFragment(new DthFragment(), "DTH");
                            myPagerAdapter.addFragment(new PostpaidFragment(), "Postpaid");
                            myPagerAdapter.addFragment(new MoneyCommisssionFragment(), "DMT");
                            myPagerAdapter.addFragment(new AepsCommissionFragment(), "AEPS");
                            myPagerAdapter.addFragment(new AadharPayCommissionFragment(), "Aadhar Pay");
                            myPagerAdapter.addFragment(new PayoutCommissionFragment(), "Payout");
                            myPagerAdapter.addFragment(new MatmCommissionFragment(), "MATM");
                            myPagerAdapter.addFragment(new ElectricityFragment(),"Electricity");
                            myPagerAdapter.addFragment(new LoanFragment(),"Loan");
                            myPagerAdapter.addFragment(new FastagCommissionFragment(),"Fastag");
                            myPagerAdapter.addFragment(new InsuranceCommissionFragment(),"Insurance");
                            myPagerAdapter.addFragment(new LandlineCommissionFragment(),"Landline");
                            myPagerAdapter.addFragment(new GasCommissionFragment(),"Gas");



                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {

                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String service = transactionObject.getString("ServiceName");
                                String operator = transactionObject.getString("OperatorName");
                                String commPer = transactionObject.getString("Commission");
                                String chargePer = transactionObject.getString("Surcharge");

                                if (service.equalsIgnoreCase("MOBILE")) {

                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    mobileCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("DTH")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    dthCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("PostPaid")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    postpaidCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Electricity")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    electricityCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Loan Repayment")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    loanCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Fastag")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    fastagCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Insurance")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    insuranceCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Money Transfer")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    moneyCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("AEPS")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    aepsCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("AePS AdharPay")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    aadharPayCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("LANDLINE")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    landlineCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Payouts")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    payoutCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Gas")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    gasCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("MATM")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    matmCommissionList.add(myCommissionModel);
                                }

                            }
                            viewPager.setAdapter(myPagerAdapter);
                            tabLayout.setupWithViewPager(viewPager);

                            pDialog.dismiss();
                            
                        } else {
                            new AlertDialog.Builder(MyCommissionActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(MyCommissionActivity.this)
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(MyCommissionActivity.this)
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
            }
        });
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_commission_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}