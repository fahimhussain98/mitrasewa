package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import wts.com.mitrsewa.adapters.ViewCustomersAdapter;
import wts.com.mitrsewa.models.ViewUsersModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class ViewCustomerActivity extends AppCompatActivity {

    ImageView imgClose;
    RecyclerView recyclerView;
    String  userid;
    SharedPreferences sharedPreferences;
    ArrayList<ViewUsersModel> viewUsersModelArrayList;
    String deviceId,deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);

        initViews();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ViewCustomerActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        if (checkInternetState()) {
            getUsers();

        } else {
            showSnackbar("No Internet");
        }

    }

    private void getUsers() {
        final AlertDialog pDialog = new AlertDialog.Builder(ViewCustomerActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUsers(AUTH_KEY,deviceId,deviceInfo,userid,"ALL");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");


                        if (responseCode.equalsIgnoreCase("TXN")) {
                            viewUsersModelArrayList=new ArrayList<>();
                            JSONArray transactionArray=jsonObject.getJSONArray("data");

                            for (int i=0;i<transactionArray.length();i++)
                            {
                                ViewUsersModel viewUsersModel=new ViewUsersModel();

                                JSONObject transactionObject=transactionArray.getJSONObject(i);

                                String name=transactionObject.getString("UserName");
                                String userId=transactionObject.getString("ID");
                                String userType=transactionObject.getString("UserType1");
                                String number=transactionObject.getString("MobileNo");
                                String date=transactionObject.getString("CreatedDate");


                                String[] dateArr=date.split("T");
                                date=dateArr[0];

                                viewUsersModel.setName(name);
                                viewUsersModel.setUserid(userId);
                                viewUsersModel.setMobileNo(number);
                                viewUsersModel.setDate(date);
                                viewUsersModel.setUserType(userType);

                                viewUsersModelArrayList.add(viewUsersModel);

                            }

                            pDialog.dismiss();

                            recyclerView.setLayoutManager(new LinearLayoutManager(ViewCustomerActivity.this,
                                    RecyclerView.VERTICAL, false));

                            ViewCustomersAdapter viewCustomersAdapter = new ViewCustomersAdapter(viewUsersModelArrayList);
                            recyclerView.setAdapter(viewCustomersAdapter);


                        } else
                        {
                            pDialog.dismiss();
                            showSnackbar("No User Found!!!");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showSnackbar("No User Found!!!");

                    }
                } else {
                    pDialog.dismiss();
                    showSnackbar("No User Found!!!");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackbar("No User Found!!!");
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

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.view_users_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void initViews() {
        imgClose = findViewById(R.id.img_close);
        recyclerView = findViewById(R.id.recycler_view);
    }
}