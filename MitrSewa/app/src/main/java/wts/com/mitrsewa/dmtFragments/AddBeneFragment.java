package wts.com.mitrsewa.dmtFragments;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import wts.com.mitrsewa.activities.NewMoneyTransferActivity;
import wts.com.mitrsewa.activities.SenderValidationActivity;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class AddBeneFragment extends Fragment {

    ArrayList<String> bankNameArrayList, bankIdArrayList, ifscArrayList;
    EditText etRecipientName, etRecipientNumber, etAccountNumber, tvIfsc;
    AutoCompleteTextView etBankName;
    Button btnSubmit, btnVerify;
    String mobileNumber,  selectedIfsc, selectedBankName,selectedBankId="select";
    String userId;
    SharedPreferences sharedPreferences;

    String deviceId, deviceInfo;

    boolean isExpressDmt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_add_bene, container, false);
        initViews(view);

        isExpressDmt=SenderValidationActivity.isExpressDmt;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);


        mobileNumber = NewMoneyTransferActivity.senderMobileNumber;

        if (checkInternetState()) {
            getBanks();
        } else {
            showSnackbar(view);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etRecipientName.getText()) && !TextUtils.isEmpty(etRecipientName.getText()) && !TextUtils.isEmpty(etRecipientNumber.getText())
                        && !selectedBankId.equalsIgnoreCase("select"))
                    addBeneficiary();
                else {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Select Above Details.")
                            .show();
                }

            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    verifyAccount();
                } else {
                    showSnackbar(view);
                }
            }
        });

        return view;
    }

    private void verifyAccount() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String recipientName = etRecipientName.getText().toString().trim();
        String recipientNumber = etRecipientNumber.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();
        String ifsc = tvIfsc.getText().toString().trim();

        Call<JsonObject> call=null;

        if (isExpressDmt)
        {
            call = RetrofitClient.getInstance().getApi().verifyExpressAccount(AUTH_KEY,userId,deviceId,deviceInfo,SenderValidationActivity.senderId,
                    selectedBankName,mobileNumber,ifsc,accountNumber,SenderValidationActivity.senderMobileNumber,SenderValidationActivity.sendername);
        }
        else
        {

            call = RetrofitClient.getInstance().getApi().verifyAccount(AUTH_KEY,userId,deviceId,deviceInfo,selectedBankId,
                    selectedBankName,recipientName,"",recipientNumber,ifsc,accountNumber,mobileNumber,"","","",
                    SenderValidationActivity.sendername,"ifs");

        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            String beniName = responseObject.getString("data");
                            etRecipientName.setText(beniName);

                            pDialog.dismiss();

                        } else {

                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (Exception e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Message")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(getContext())
                            .setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(getContext())
                        .setTitle("Message")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });

    }

    private void initViews(View view) {
        etBankName = view.findViewById(R.id.et_bank_name);
        etRecipientName = view.findViewById(R.id.et_recipient_name);
        etRecipientNumber = view.findViewById(R.id.et_recipient_number);
        etAccountNumber = view.findViewById(R.id.et_account_number);
        tvIfsc = view.findViewById(R.id.tv_ifsc);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnVerify = view.findViewById(R.id.btn_verify);

    }

    private void addBeneficiary() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String recipientName = etRecipientName.getText().toString().trim();
        String recipientNumber = etRecipientNumber.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();
        String ifsc = tvIfsc.getText().toString().trim();


        //Remitter Id me Bank Id jaa rhi h kuki shahanshah ne bola tha

        Call<JsonObject> call=null;

        if (isExpressDmt)
        {
            call = RetrofitClient.getInstance().getApi().addBeneficiaryExpress(AUTH_KEY, userId, deviceId, deviceInfo, SenderValidationActivity.senderId,
                    selectedBankName, recipientName, "", mobileNumber, ifsc, accountNumber, mobileNumber,
                    "NA", "NA", "NA");
        }

        else
        {
            call = RetrofitClient.getInstance().getApi().addBeneficiary(AUTH_KEY, userId, deviceId, deviceInfo, selectedBankId,
                    selectedBankName, recipientName, "", mobileNumber, selectedIfsc, accountNumber, mobileNumber,
                    "NA", "NA", "NA");
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {


                            String message = jsonObject.getString("status");

                            pDialog.dismiss();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Status")
                                    .setCancelable(false)
                                    .setMessage(message)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            //TODO : change this logic
                                            getActivity().finish();
                                            //startActivity(getActivity().getIntent());

                                        }
                                    })
                                    .show();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String responseMessage = jsonObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Alert!!!")
                                    .setMessage(responseMessage)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(getContext())
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(getContext())
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });

    }

    private void getBanks() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
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
                            JSONArray transactionArray = jsonObject.getJSONArray("data");

                            bankNameArrayList = new ArrayList<>();
                            bankIdArrayList = new ArrayList<>();
                            ifscArrayList = new ArrayList<>();

                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String responsebankName = transactionObject.getString("BankName");
                                String responseBankId = transactionObject.getString("BankId");
                                String responseIfsc = transactionObject.getString("IfscCode");

                                bankNameArrayList.add(responsebankName);
                                bankIdArrayList.add(responseBankId);
                                ifscArrayList.add(responseIfsc);

                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), com.chaos.view.R.layout.support_simple_spinner_dropdown_item, bankNameArrayList);
                            etBankName.setAdapter(adapter);
                            etBankName.setThreshold(1);

                            etBankName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    int index = bankNameArrayList.indexOf(etBankName.getText().toString());
                                    selectedIfsc = ifscArrayList.get(index);
                                    selectedBankName = bankNameArrayList.get(index);
                                    selectedBankId = bankIdArrayList.get(index);
                                    tvIfsc.setText(selectedIfsc);

                                }
                            });


                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(getContext())
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(getContext())
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void showSnackbar(View view) {
        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.add_bene_fragment), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}