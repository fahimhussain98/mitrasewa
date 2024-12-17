package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class WalletActivity extends AppCompatActivity {

    TextView tvMainWallet, tvAepsWallet, tvCommissionWallet;
    ImageView imgMainRefresh, imgAepsRefresh, imgCommissionRefresh;
    String balance = "00.00";
    String userid;
    SharedPreferences sharedPreferences;
    Animation rotateAnimation;

    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        inhitViews();


        if (checkInternetState()) {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WalletActivity.this);
            userid = sharedPreferences.getString("userid", null);
            deviceId = sharedPreferences.getString("deviceId", null);
            deviceInfo = sharedPreferences.getString("deviceInfo", null);
            rotateAnimation = AnimationUtils.loadAnimation(WalletActivity.this, R.anim.rotate);
            getBalance();
            getAepsBalance();
            getCommissionBalance();
        } else {
            showSnackbar();
        }

        imgMainRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMainRefresh.startAnimation(rotateAnimation);
                if (checkInternetState()) {
                    getBalance();
                } else {
                    showSnackbar();
                }
            }
        });

        imgAepsRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgAepsRefresh.startAnimation(rotateAnimation);
                if (checkInternetState()) {
                    getAepsBalance();
                } else {
                    showSnackbar();
                }
            }
        });

        imgCommissionRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCommissionRefresh.startAnimation(rotateAnimation);
                if (checkInternetState()) {
                    getCommissionBalance();
                } else {
                    showSnackbar();
                }
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

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.wallet_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void getBalance() {

        final AlertDialog pDialog = new AlertDialog.Builder(WalletActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBalance(AUTH_KEY, userid, deviceId, deviceInfo, "Login", "0");

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
                            String balance = jsonObject.getString("userBalance");
                            tvMainWallet.setText("₹ " + balance);


                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            tvMainWallet.setText("₹ 00.00");


                        } else {
                            pDialog.dismiss();
                            tvMainWallet.setText("₹ 00.00");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        tvMainWallet.setText("₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();
                    tvMainWallet.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();

                tvMainWallet.setText("₹ " + "00.00");

            }
        });
    }

    private void getAepsBalance() {

        final AlertDialog pDialog = new AlertDialog.Builder(WalletActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsBalance(AUTH_KEY, userid, deviceId, deviceInfo, "Login", "");

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
                            String balance = jsonObject.getString("userBalance");
                            tvAepsWallet.setText("₹ " + balance);


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

    private void getCommissionBalance() {

        final AlertDialog pDialog = new AlertDialog.Builder(WalletActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getCommissionBalance(AUTH_KEY, userid, deviceId, deviceInfo, "Login", "");

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
                            String balance = jsonObject.getString("userBalance");
                            tvCommissionWallet.setText("₹ " + balance);


                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            tvCommissionWallet.setText("₹ 00.00");


                        } else {
                            pDialog.dismiss();
                            tvCommissionWallet.setText("₹ 00.00");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        tvCommissionWallet.setText("₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();
                    tvCommissionWallet.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                tvCommissionWallet.setText("₹ " + "00.00");

            }
        });
    }

    private void inhitViews() {
        tvMainWallet = findViewById(R.id.tv_main_wallet);
        tvAepsWallet = findViewById(R.id.tv_aeps_wallet);
        tvCommissionWallet = findViewById(R.id.tv_commission_wallet);
        imgMainRefresh = findViewById(R.id.img_refresh);
        imgAepsRefresh = findViewById(R.id.img_aeps_refresh);
        imgCommissionRefresh = findViewById(R.id.img_c_refresh);
    }

}