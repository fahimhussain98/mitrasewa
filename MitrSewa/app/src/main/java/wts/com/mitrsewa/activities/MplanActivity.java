package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

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
import wts.com.mitrsewa.adapters.MyPlansAdaper;
import wts.com.mitrsewa.models.PlansModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class MplanActivity extends AppCompatActivity {

    ListView mPlanList;
    ArrayList<PlansModel> plansModelArrayList;
    String mobile, operator, deviceId, deviceInfo,userId;
    ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mplan);

        plansModelArrayList = new ArrayList<>();
        mPlanList = findViewById(R.id.m_plan_list);

        operator = getIntent().getStringExtra("operator");
        mobile = getIntent().getStringExtra("mobile");
        userId = getIntent().getStringExtra("userId");
        deviceId = getIntent().getStringExtra("deviceId");
        deviceInfo = getIntent().getStringExtra("deviceInfo");


        if (checkInternetState()) {
            getPlans(operator, mobile);
        } else {
            showSnackbar();
        }


    }

    private void getPlans(final String operator, String mobile) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getMyPlans(userId,deviceId,deviceInfo,operator,mobile);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String data=jsonObject.getString("data");
                        JSONObject dataObject=new JSONObject(data);


                        JSONArray recordsArray = dataObject.getJSONArray("records");
                        for (int i = 0; i < recordsArray.length(); i++) {
                            PlansModel plansModel = new PlansModel();
                            JSONObject recordsObject = recordsArray.getJSONObject(i);

                            String rs = recordsObject.getString("rs");
                            String desc = recordsObject.getString("desc");

                            plansModel.setValidityText("");
                            plansModel.setRs(rs);
                            plansModel.setDesc(desc);

                            plansModelArrayList.add(plansModel);

                        }

                        MyPlansAdaper myPlansAdaper = new MyPlansAdaper(MplanActivity.this, MplanActivity.this, plansModelArrayList);
                        mPlanList.setAdapter(myPlansAdaper);
                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(MplanActivity.this)
                                .setCancelable(false)
                                .setMessage("No Plans Found")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    new AlertDialog.Builder(MplanActivity.this)
                            .setCancelable(false)
                            .setMessage("No Plans Found")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(MplanActivity.this)
                        .setCancelable(false)
                        .setMessage("No Plans Found")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.m_plan_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}