package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.models.RecipientModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class SenderValidationActivity extends AppCompatActivity {

    EditText etNumber;
    Button btnValidate;
    String mobileNumber;

    public static String senderMobileNumber, sendername, senderId, availablelimit, totalLimit;

    ImageView imgBack;
    TextView tvDmrWalletBalance;
    String userid;
    String deviceId, deviceInfo;

    public static boolean isBeneCountZero = true;
    public static boolean isExpressDmt = false;

    public static ArrayList<RecipientModel> recipientModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_validation);

        initViews();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SenderValidationActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        isExpressDmt = getIntent().getBooleanExtra("isExpressDmt", false);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    hideKeyBoard();
                }
            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    checkInputs();
                } else {
                    showSnackBar();
                }
            }
        });
    }

   /* private void isSenderValidate() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SenderValidationActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        mobileNumber = etNumber.getText().toString();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().GetSenderWithEKYC(AUTH_KEY, deviceId, deviceInfo, userid, mobileNumber);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("GetSenderWithEKYC","response::" + response.body());
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;


                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {

                            recipientModelArrayList = new ArrayList<>();
                            senderMobileNumber = jsonObject.getString("remitterMobile");
                            sendername = jsonObject.getString("remitterName");
                            senderId = jsonObject.getString("remitterId");
                            availablelimit = jsonObject.getString("availableLimit");
                            totalLimit = jsonObject.getString("totalLimit");

                            Float totalFloatLimit = Float.valueOf(totalLimit);
                            Float availableFloatLimit = Float.valueOf(availablelimit);
                            Float consumedLimit = totalFloatLimit - availableFloatLimit;


                            String beneListStr = jsonObject.getString("benelist");
                            if (beneListStr.equalsIgnoreCase("false")) {
                                isBeneCountZero = true;
                            } else
                            {
                                JSONArray beneListArray = new JSONArray(beneListStr);
                                for (int i = 0; i < beneListArray.length(); i++) {
                                    RecipientModel recipientModel = new RecipientModel();

                                    JSONObject beneListObject = beneListArray.getJSONObject(i);
                                    String bankAccountNumber = beneListObject.getString("accountNo");
                                    String bankName = beneListObject.getString("bankName");
                                    String ifsc = beneListObject.getString("ifscCode");
                                    String recipientId = beneListObject.getString("beneficiaryId");
                                    String recipientName = beneListObject.getString("beneficiaryName");
                                    //String beneMobileNo = beneListObject.getString("Mobileno");

                                    recipientModel.setBankAccountNumber(bankAccountNumber);
                                    recipientModel.setBankName(bankName);
                                    recipientModel.setIfsc(ifsc);
                                    recipientModel.setRecipientId(recipientId);
                                    recipientModel.setRecipientName(recipientName);
                                    //recipientModel.setMobileNumber(beneMobileNo);
                                    recipientModelArrayList.add(recipientModel);
                                }
                                isBeneCountZero = false;

                            }

                            Intent intent = new Intent(SenderValidationActivity.this, NewMoneyTransferActivity.class);
                            intent.putExtra("senderMobileNumber", senderMobileNumber);
                            intent.putExtra("senderName", sendername);
                            intent.putExtra("availableLimit", availablelimit);
                            intent.putExtra("totalLimit", totalLimit);
                            intent.putExtra("consumedLimit", consumedLimit + "");

                            pDialog.dismiss();

                            startActivity(intent);

                        }


                        else if (responseCode.equalsIgnoreCase("OTP")) {


                            String code = jsonObject.getString("remitterId");

                            Intent intent = new Intent(SenderValidationActivity.this, AddSenderActivity.class);
                            intent.putExtra("mobileNo", mobileNumber);
                            intent.putExtra("stateWiseCode", code);
                            startActivity(intent);

                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("NP")) {
                            String message = jsonObject.getString("statusMsg");
                            new android.app.AlertDialog.Builder(SenderValidationActivity.this).setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();


                        } else {
                            pDialog.dismiss();
                            String message = jsonObject.getString("data");
                            new android.app.AlertDialog.Builder(SenderValidationActivity.this).setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SenderValidationActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SenderValidationActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new AlertDialog.Builder(SenderValidationActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
                pDialog.dismiss();
            }
        });

    }
*/
    private void isSenderValidate() {
    final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SenderValidationActivity.this).create();
    LayoutInflater inflater = getLayoutInflater();
    View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
    pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    pDialog.setView(convertView);
    pDialog.setCancelable(false);
    pDialog.show();

    mobileNumber = etNumber.getText().toString();

    Call<JsonObject> call = RetrofitClient.getInstance().getApi().GetSenderWithEKYC(AUTH_KEY, deviceId, deviceInfo, userid, mobileNumber);

    call.enqueue(new Callback<JsonObject>() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Log.e("GetSenderWithEKYC", "response::" + response.body());
            if (response.isSuccessful() && response.body() != null) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                    String responseCode = jsonObject.optString("statuscode", "");
                    if (responseCode.equalsIgnoreCase("TXN")) {
                        recipientModelArrayList = new ArrayList<>();

                        senderMobileNumber = jsonObject.optString("remitterMobile", "N/A");
                        sendername = jsonObject.optString("remitterName", "N/A");
                        senderId = jsonObject.optString("remitterId", "N/A");
                        availablelimit = jsonObject.optString("availableLimit", "0");
                        totalLimit = jsonObject.optString("totalLimit", "0");

                       /* float totalFloatLimit = totalLimit.isEmpty() ? 0f : Float.parseFloat(totalLimit);
                        float availableFloatLimit = availablelimit.isEmpty() ? 0f : Float.parseFloat(availablelimit);
                        float consumedLimit = totalFloatLimit - availableFloatLimit;*/
                        float totalFloatLimit = 0f;
                        float availableFloatLimit = 0f;

                        if (totalLimit != null && !totalLimit.equalsIgnoreCase("null") && !totalLimit.isEmpty()) {
                            totalFloatLimit = Float.parseFloat(totalLimit);
                        }

                        if (availablelimit != null && !availablelimit.equalsIgnoreCase("null") && !availablelimit.isEmpty()) {
                            availableFloatLimit = Float.parseFloat(availablelimit);
                        }

                        float consumedLimit = totalFloatLimit - availableFloatLimit;


                        JSONArray beneListArray = jsonObject.optJSONArray("benelist");
                        if (beneListArray != null && beneListArray.length() > 0) {
                            for (int i = 0; i < beneListArray.length(); i++) {
                                JSONObject beneListObject = beneListArray.getJSONObject(i);
                                RecipientModel recipientModel = new RecipientModel();

                                recipientModel.setBankAccountNumber(beneListObject.optString("accountNo", "N/A"));
                                recipientModel.setBankName(beneListObject.optString("bankName", "N/A"));
                                recipientModel.setIfsc(beneListObject.optString("ifscCode", "N/A"));
                                recipientModel.setRecipientId(beneListObject.optString("beneficiaryId", "N/A"));
                                recipientModel.setRecipientName(beneListObject.optString("beneficiaryName", "N/A"));

                                recipientModelArrayList.add(recipientModel);
                            }
                            isBeneCountZero = false;
                        } else {
                            isBeneCountZero = true;
                        }

                        Intent intent = new Intent(SenderValidationActivity.this, NewMoneyTransferActivity.class);
                        intent.putExtra("senderMobileNumber", senderMobileNumber);
                        intent.putExtra("senderName", sendername);
                        intent.putExtra("availableLimit", availablelimit);
                        intent.putExtra("totalLimit", totalLimit);
                        intent.putExtra("consumedLimit", consumedLimit + "");

                        pDialog.dismiss();
                        startActivity(intent);
                    }

                    else if (responseCode.equalsIgnoreCase("EKYC")) {
                        String ekycStatus = jsonObject.optString("status", "");
                        String ekycMessage = jsonObject.optString("statusMsg", "");
                        String remitterId = jsonObject.optString("remitterId", "");

                        Intent intent = new Intent(SenderValidationActivity.this, EKYCActivity.class);
                        intent.putExtra("ekycStatus", ekycStatus);
                        intent.putExtra("ekycMessage", ekycMessage);
                        intent.putExtra("remitterId", remitterId);

                        pDialog.dismiss();
                        startActivity(intent);
                    }
                    else if (responseCode.equalsIgnoreCase("OTP")) {
                        String otpStatus = jsonObject.optString("status", ""); // Extract status
                        String stateResp = jsonObject.optString("stateresp", ""); // Extract stateresp
                        String ekycId = jsonObject.optString("ekyc_id", ""); // Extract ekyc_id
                        String remitterId = jsonObject.optString("remitterId", ""); // Extract remitterId

                        Intent intent = new Intent(SenderValidationActivity.this, AddSenderActivity.class);
                        intent.putExtra("otpStatus", otpStatus);
                        intent.putExtra("stateResp", stateResp);
                        intent.putExtra("ekycId", ekycId);
                        intent.putExtra("remitterId", remitterId);

                        pDialog.dismiss(); // Dismiss the progress dialog
                        startActivity(intent);
                    }



//                    else {
//                        pDialog.dismiss();
//                        Toast.makeText(SenderValidationActivity.this, "Failed: " + jsonObject.optString("statusMsg", "Error"), Toast.LENGTH_LONG).show();
//                    }
                } catch (JSONException e) {
                    pDialog.dismiss();
                    Log.e("JSONError", e.getMessage());
                    Toast.makeText(SenderValidationActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }
            } else {
                pDialog.dismiss();
                Toast.makeText(SenderValidationActivity.this, "API call failed!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            pDialog.dismiss();
            Toast.makeText(SenderValidationActivity.this, "Failed to connect to server!", Toast.LENGTH_SHORT).show();
            Log.e("APIError", t.getMessage());
        }
    });
}



    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.sender_validation_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void checkInputs() {
        if (etNumber.getText().length() == 10) {
            if (isExpressDmt) {
                isSenderValidateExpress();
            } else {
                isSenderValidate();
            }
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.sender_validation_layout), "Enter valid number.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void isSenderValidateExpress() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SenderValidationActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        mobileNumber = etNumber.getText().toString();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().isSenderValidateExpress(AUTH_KEY, deviceId, deviceInfo, userid, mobileNumber);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            recipientModelArrayList = new ArrayList<>();

                            String dataStr = jsonObject.getString("data");
                            JSONObject dataObject = new JSONObject(dataStr);
                            JSONObject remitterObject = dataObject.getJSONObject("remitter");

                            senderMobileNumber = remitterObject.getString("mobile");
                            sendername = remitterObject.getString("name");
                            senderId = remitterObject.getString("id");
                            availablelimit = remitterObject.getString("remaininglimit");
                            totalLimit = remitterObject.getString("consumedlimit");

                            Float totalFloatLimit = Float.valueOf(totalLimit);
                            Float availableFloatLimit = Float.valueOf(availablelimit);
                            Float consumedLimit = totalFloatLimit - availableFloatLimit;


                            if (!remitterObject.has("beneficiary")) {
                                isBeneCountZero = true;

                            } else {

                                JSONArray beniListArray = remitterObject.getJSONArray("beneficiary");
                                if (beniListArray.length() == 0) {
                                    isBeneCountZero = true;
                                } else {
                                    for (int i = 0; i < beniListArray.length(); i++) {
                                        RecipientModel recipientModel = new RecipientModel();

                                        JSONObject beneListObject = beniListArray.getJSONObject(i);
                                        String bankAccountNumber = beneListObject.getString("AccountNo");
                                        String bankName = beneListObject.getString("BankName");
                                        String ifsc = beneListObject.getString("IfscCode");
                                        String recipientId = beneListObject.getString("BeneId");
                                        String recipientName = beneListObject.getString("BeneName");
                                        //String beneMobileNo = beneListObject.getString("Mobileno");

                                        recipientModel.setBankAccountNumber(bankAccountNumber);
                                        recipientModel.setBankName(bankName);
                                        recipientModel.setIfsc(ifsc);
                                        recipientModel.setRecipientId(recipientId);
                                        recipientModel.setRecipientName(recipientName);
                                        //recipientModel.setMobileNumber(beneMobileNo);
                                        recipientModelArrayList.add(recipientModel);
                                    }
                                    isBeneCountZero = false;

                                }
                            }

                            Intent intent = new Intent(SenderValidationActivity.this, NewMoneyTransferActivity.class);
                            intent.putExtra("senderMobileNumber", senderMobileNumber);
                            intent.putExtra("senderName", sendername);
                            intent.putExtra("availableLimit", availablelimit);
                            intent.putExtra("totalLimit", totalLimit);
                            intent.putExtra("consumedLimit", consumedLimit + "");

                            pDialog.dismiss();

                            startActivity(intent);


                        } else if (responseCode.equalsIgnoreCase("RNF")) {

                            Intent intent = new Intent(SenderValidationActivity.this, AddSenderActivity.class);
                            intent.putExtra("mobileNo", mobileNumber);
                            startActivity(intent);

                            pDialog.dismiss();
                        }

                        else {

                            pDialog.dismiss();
                            String message = jsonObject.getString("data");
                            new android.app.AlertDialog.Builder(SenderValidationActivity.this).setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SenderValidationActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SenderValidationActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new AlertDialog.Builder(SenderValidationActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
                pDialog.dismiss();
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

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.img_back);
        etNumber = findViewById(R.id.et_mobile_number);
        btnValidate = findViewById(R.id.btn_validate);
        tvDmrWalletBalance = findViewById(R.id.tv_dmr_balance);


    }

}