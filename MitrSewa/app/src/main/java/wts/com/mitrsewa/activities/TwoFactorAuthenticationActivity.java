package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.device.Opts;
import wts.com.mitrsewa.device.PidOptions;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class TwoFactorAuthenticationActivity extends AppCompatActivity {

    LinearLayout transactionTypeLayout, userDetailLayout, deviceLayout;

    String userId, mobileNo,aadharNo, deviceId, deviceInfo;
    
    TextView tvBalance, tvTitle;

    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
    LinearLayout cashWithDrawLayout;
    ImageView imgCashWithdraw;
    TextView tvCashWithdraw;
    String selectedTransactionType = "select";
    AppCompatButton btnProceedTransactionType;
    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

    String selectedBankName = "select", selectedBankIIN;

    EditText etMobile, etAadharCard;
    Button btnProceedUserDetail;
    CheckBox ckbTermsAndCondition;


    ArrayList<String> bankNameArrayList, bankIINArrayList;
    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
    ImageView imgMorpho,imgMorphoL1, imgStartek, imgMantra,imgMantraL1, imgEvolute, imgVriddhi;
    LinearLayout morphoLayout,morphoL1Layout, startekLayout, mantraLayout,mantraL1Layout, evoluteLayout, vriddhiLayout;
    TextView tvMorpho,tvMorphoL1, tvMantra,tvMantraL1, tvStartek, tvEvolute, tvVriddhi;
    AppCompatButton btnProceedDeviceLayout;

    String selectedDevice = "select";
    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

    ////////////////FINGER PRINT DATA/////////////////////
    Serializer serializer = null;
    String pidData = null;
    ////////////////FINGER PRINT DATA/////////////////////
    SharedPreferences sharedPreferences;
    String title, lat = "0.0", longi = "0.0", aepsType;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_two_factor_authentication);

        initViews();

        //////CHANGE COLOR OF STATUS BAR///////////////
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(TwoFactorAuthenticationActivity.this, R.color.black));
        //////CHANGE COLOR OF STATUS BAR///////////////

        //////////////////////////////////////////////////////////////////

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TwoFactorAuthenticationActivity.this);
        userId = sharedPreferences.getString("userid", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        aadharNo = sharedPreferences.getString("adharcard", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        title = getIntent().getStringExtra("title");
        lat = getIntent().getStringExtra("lat");
        longi = getIntent().getStringExtra("longi");
        aepsType = getIntent().getStringExtra("aepsType");
        String balance = getIntent().getStringExtra("balance");
        tvBalance.setText("₹ " + balance);
        tvTitle.setText(title);

        activity = TwoFactorAuthenticationActivity.this;

        etMobile.setText(mobileNo);
        etAadharCard.setText(aadharNo);
        //////////////////////////////////////////////////////////////////


        //  transactionTypeLayoutListeners();
        userDetailLayoutListeners();
        deviceLayoutListeners();

        //////////////////////////////////////////////
        serializer = new Persister();
        //////////////////////////////////////////////
    }

    /**
     * FIRST STEP
     * selects transaction type(Cash withdraw,Balance enquiry etc. and then proceed to Second Step.
     * after proceed hide TRANSACTION TYPE LAYOUT and show USER DETAILS LAYOUT
     */
//    private void transactionTypeLayoutListeners() {
//        cashWithDrawLayout.setOnClickListener(view -> {
//            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_selected);
//
//
//            tvCashWithdraw.setTextColor(getResources().getColor(R.color.teal_200));
//
//            selectedTransactionType = "authentication";
//
//        });
//
//        btnProceedTransactionType.setOnClickListener(view -> {
//            if (!selectedTransactionType.equalsIgnoreCase("select")) {
//                transactionTypeLayout.setVisibility(View.GONE);
//                userDetailLayout.setVisibility(View.VISIBLE);
//
//
//            } else {
//                showMessageDialog("Hey!!!You forgot to select two factor Auth. .");
//            }
//        });
//    }

    /**
     * SECOND STEP
     * get Aadhar details from user input and then proceed to third step.
     * after proceed hide USER DETAIL LAYOUT and show DEVICE LAYOUT
     */
    private void userDetailLayoutListeners() {

        btnProceedUserDetail.setOnClickListener(v -> {
            if (checkUserDetails()) {
                userDetailLayout.setVisibility(View.GONE);
                deviceLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * THIRD STEP
     * get device name from user and get pid data from RD Service
     * after that do final transaction
     */
    private void deviceLayoutListeners() {

        mantraLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_selected);
            imgMantraL1.setImageResource(R.drawable.mantral1_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.vriddhi_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Mantra";
        });

        mantraL1Layout.setOnClickListener(view -> {
            imgMantraL1.setImageResource(R.drawable.mantral1_selected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);

            tvMantraL1.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "MantraL1";
        });

        startekLayout.setOnClickListener(view -> {
            imgStartek.setImageResource(R.drawable.startek_selected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgVriddhi.setImageResource(R.drawable.vriddhi_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Startek";
        });

        morphoLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_selected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.vriddhi_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Morpho";
        });

        morphoL1Layout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_selected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.vriddhi_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "MorphoL1";
        });

        evoluteLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_selected);
            imgVriddhi.setImageResource(R.drawable.vriddhi_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Evolute";
        });

        vriddhiLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.vriddhi_selected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.selected_text_color));

            selectedDevice = "Vriddhi";
        });

        btnProceedDeviceLayout.setOnClickListener(view -> {
            if (!selectedDevice.equalsIgnoreCase("select")) {
                //startActivity(new Intent(PaySprintActivity.this,AepsTransactionActivity.class));
                String packageName = null;
                if (selectedDevice.equalsIgnoreCase("Morpho"))
                    packageName = "com.scl.rdservice";
                else if (selectedDevice.equalsIgnoreCase("MorphoL1"))
                    packageName = "com.idemia.l1rdservice";
                else if (selectedDevice.equalsIgnoreCase("Startek"))
                    packageName = "com.acpl.registersdk";
                else if (selectedDevice.equalsIgnoreCase("Mantra"))
                    packageName = "com.mantra.rdservice";
                else if (selectedDevice.equalsIgnoreCase("MantraL1"))
                    packageName = "com.mantra.mfs110.rdservice";
                else if (selectedDevice.equalsIgnoreCase("Evolute"))
                    packageName = "com.evolute.rdservice";

                else if (selectedDevice.equalsIgnoreCase("Vriddhi"))
                    packageName = "com.nextbiometrics.onetouchrdservice";

                try {
                    String pidOption = getPIDOptions();
                    Intent intent2 = new Intent();
                    intent2.setPackage(packageName);
                    intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                    intent2.putExtra("PID_OPTIONS", pidOption);
                    startActivityForResult(intent2, 1);
                } catch (Exception e) {
                    showMessageDialog("Please install " + selectedDevice + " Rd Service first.");
                }
            } else {
                showMessageDialog("Please Select Your Device");
            }
        });
    }

    private String getPIDOptions() {

        try {
            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "2";
            opts.format = "0";
            opts.timeout = "15000";
            opts.wadh = "";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.pidVer = "2.0";
            opts.env = "P";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return null;
    }

    private boolean checkUserDetails() {
        if (etMobile.getText().toString().length() == 10) {
            if (etAadharCard.getText().toString().trim().length() == 12) {
                if (ckbTermsAndCondition.isChecked()) {
                    hideKeyBoard();
                    return true;
                } else {
                    showMessageDialog("Please accept terms and condition to continue.");
                    return false;
                }

            } else {
                showMessageDialog("Please enter valid aadhar number.");
                return false;
            }

        } else {
            showMessageDialog("Please enter valid mobile number.");
            return false;
        }
    }

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    private void showMessageDialog(String message) {
        final AlertDialog messageDialog = new AlertDialog.Builder(TwoFactorAuthenticationActivity.this).create();
        final LayoutInflater inflater = LayoutInflater.from(TwoFactorAuthenticationActivity.this);
        View convertView = inflater.inflate(R.layout.message_dialog, null);
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        messageDialog.setCancelable(false);
        messageDialog.setView(convertView);

        ImageView imgClose = convertView.findViewById(R.id.img_close);
        TextView tvMessage = convertView.findViewById(R.id.tv_message);
        Button btnTryAgain = convertView.findViewById(R.id.btn_try_again);

        imgClose.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        btnTryAgain.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        tvMessage.setText(message);

        messageDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result;
        //List<Param> params = new ArrayList<>();
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    result = data.getStringExtra("PID_DATA");
                    if (result.contains("Device not ready")) {
                        showMessageDialog("Device Not Connected");
                    } else {
                        if (result.contains("srno") && result.contains("rdsId") && result.contains("rdsVer")) {
//                        if (result.contains("rdsId") && result.contains("rdsVer")) {
                            try {
                                pidData = result;

                                doAepsAuthentication();

                                //   getLastLocation();
                               /* pidDataStr = pidData._Data.value;
                                Log.d("xml_data_show", pidDataStr);
                                sessionKey = pidData._Skey.value;
                                hmac = pidData._Hmac;
                                dpId = pidData._DeviceInfo.dpId;
                                rdsId = pidData._DeviceInfo.rdsId;
                                rdsVer = pidData._DeviceInfo.rdsVer;
                                dc = pidData._DeviceInfo.dc;
                                mc = pidData._DeviceInfo.mc;
                                mi = pidData._DeviceInfo.mi;
                                errCode = pidData._Resp.errCode;
                                errInfo = pidData._Resp.errInfo;
                                errCode = pidData._Resp.errCode;
                                fcount = pidData._Resp.fCount;
                                qScore = pidData._Resp.qScore;
                                nmPoints = pidData._Resp.nmPoints;
                                ci = pidData._Skey.ci;
                                params = pidData._DeviceInfo.add_info.params;
                                for (int i = 0; i < params.size(); i++) {
                                    String name = params.get(i).name;
                                    if (name.equalsIgnoreCase("srno")) {
                                        serialNo = params.get(i).value;
                                        Log.e("serialNu", serialNo);
                                    } else if (name.equalsIgnoreCase("sysid")) {
                                        String systemId = params.get(i).value;
                                        Log.e("systemId", systemId);
                                    }
                                }
                                //getTransactionNow();
                                tvData.setText(result);
                                Toast.makeText(MainActivity.this, "Data Collected Successfully", Toast.LENGTH_SHORT).show();*/
                            } catch (Exception e) {
                                e.printStackTrace();
                                showMessageDialog("There are some issues please contact to administration.");
                            }
                        } else {
                            showMessageDialog("Device Not Connected");
                        }
                    }

                }

            }
        }
    }

    private void doAepsAuthentication() {
        ProgressDialog progressDialog = new ProgressDialog(TwoFactorAuthenticationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...Don't press back button");
        progressDialog.show();

        String aadharNo = etAadharCard.getText().toString().trim();
        String custMobile = etMobile.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().twoFactorAuthentication(AUTH_KEY, userId, deviceId, deviceInfo, aadharNo, custMobile,
                pidData, lat, longi, aepsType);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            progressDialog.dismiss();
                            String message = responseObject.getString("status");
                            String data = responseObject.getString("data");
                            new AlertDialog.Builder(TwoFactorAuthenticationActivity.this)
                                    .setTitle(message)
                                    .setMessage(data)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();

                            String message = responseObject.getString("status");
                            String data = responseObject.getString("data");
                            new AlertDialog.Builder(TwoFactorAuthenticationActivity.this)
                                    .setTitle(message)
                                    .setMessage(data)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        } else {
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");
                            showMessageDialog(message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        showMessageDialog("Something Went Wrong.");
                    }
                } else {
                    progressDialog.dismiss();
                    showMessageDialog(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showMessageDialog(t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (userDetailLayout.getVisibility() == View.VISIBLE) {
            /*
            //////////////////RESET FIRST LAYOUT VIEWS//////////////////////
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));

            selectedTransactionType = "select";
            //////////////////RESET FIRST LAYOUT VIEWS//////////////////////

            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////
            etMobile.setText("");
            etAadharCard.setText("");
            etAmount.setText("");
            rgFirst.clearCheck();
            rgSecond.clearCheck();
            tvBankName.setText("");
            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////

             */

            userDetailLayout.setVisibility(View.GONE);
         //   transactionTypeLayout.setVisibility(View.VISIBLE);
        } else if (deviceLayout.getVisibility() == View.VISIBLE) {
            deviceLayout.setVisibility(View.GONE);
            userDetailLayout.setVisibility(View.VISIBLE);
            /*

            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////
            etMobile.setText("");
            etAadharCard.setText("");
            etAmount.setText("");
            rgFirst.clearCheck();
            rgSecond.clearCheck();
            tvBankName.setText("");
            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////

            //////////////////RESET THIRD LAYOUT VIEWS///////////////////////////////
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);

            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvMantra.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "select";
            //////////////////RESET THIRD LAYOUT VIEWS///////////////////////////////

             */

        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
        cashWithDrawLayout = findViewById(R.id.cash_withdraw_layout);

        imgCashWithdraw = findViewById(R.id.img_cash_withdraw);

        tvCashWithdraw = findViewById(R.id.tv_cash_withdraw);

    //    transactionTypeLayout = findViewById(R.id.transaction_type_layout);

        btnProceedTransactionType = findViewById(R.id.btn_proceed_transaction_type);
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

        userDetailLayout = findViewById(R.id.user_detail_layout);

        etMobile = findViewById(R.id.et_mobile_number);
        etAadharCard = findViewById(R.id.et_aadhar_number);

        ckbTermsAndCondition = findViewById(R.id.ckb_terms_condition);

        btnProceedUserDetail = findViewById(R.id.btn_proceed_user_details);

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////


        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
        deviceLayout = findViewById(R.id.device_layout);

        imgMorpho = findViewById(R.id.img_morpho);
        imgMorphoL1 = findViewById(R.id.img_morphoL1);
        imgMantra = findViewById(R.id.img_mantra);
        imgMantraL1 = findViewById(R.id.img_mantra2);
        imgStartek = findViewById(R.id.img_startek);
        imgEvolute = findViewById(R.id.img_evolute);
        imgVriddhi = findViewById(R.id.img_vriddhi);

        morphoLayout = findViewById(R.id.morpho_layout);
        morphoL1Layout = findViewById(R.id.morphoL1_layout);
        mantraLayout = findViewById(R.id.mantra_layout);
        mantraL1Layout = findViewById(R.id.mantra_layout2);
        startekLayout = findViewById(R.id.startek_layout);
        evoluteLayout = findViewById(R.id.evolute_layout);
        vriddhiLayout = findViewById(R.id.vriddhi_layout);

        tvStartek = findViewById(R.id.tv_startek);
        tvMorpho = findViewById(R.id.tv_morpho);
        tvMorphoL1 = findViewById(R.id.tv_morphoL1);
        tvMantra = findViewById(R.id.tv_mantra);
        tvMantraL1 = findViewById(R.id.tv_mantra2);
        tvEvolute = findViewById(R.id.tv_evolute);
        tvVriddhi = findViewById(R.id.tv_vriddhi);

        btnProceedDeviceLayout = findViewById(R.id.btn_proceed_device);
        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

        tvBalance = findViewById(R.id.tv_aeps_balance);
        tvTitle = findViewById(R.id.tv_title);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}