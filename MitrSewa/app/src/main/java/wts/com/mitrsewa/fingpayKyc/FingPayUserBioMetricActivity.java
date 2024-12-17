package wts.com.mitrsewa.fingpayKyc;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.device.Opts;
import wts.com.mitrsewa.device.Param;
import wts.com.mitrsewa.device.PidOptions;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class FingPayUserBioMetricActivity extends AppCompatActivity {

    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
    ImageView imgMorpho,imgMorphoL1, imgStartek, imgMantra, imgMantraL1;
    LinearLayout morphoLayout,morphoL1Layout, startekLayout, mantraLayout,mantraL1Layout;
    TextView tvMorpho,tvMorphoL1, tvMantra, tvStartek,tvMantraL1;
    AppCompatButton btnProceedDeviceLayout;


    String selectedDevice = "select";
    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
    String pidDataStr = null;
    String userId, deviceId, deviceInfo, aadharCard;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fing_pay_user_bio_metric);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FingPayUserBioMetricActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        aadharCard = sharedPreferences.getString("adharcard", null);
        initViews();
        deviceLayoutListeners();

    }

    private void initViews() {

        imgMorpho = findViewById(R.id.img_morpho);
        imgMorphoL1 = findViewById(R.id.img_morphoL1);
        imgMantra = findViewById(R.id.img_mantra);
        imgMantraL1 = findViewById(R.id.img_mantra2);
        imgStartek = findViewById(R.id.img_startek);

        morphoLayout = findViewById(R.id.morpho_layout);
        morphoL1Layout = findViewById(R.id.morphoL1_layout);
        mantraLayout = findViewById(R.id.mantra_layout);
        mantraL1Layout = findViewById(R.id.mantra_layout2);
        startekLayout = findViewById(R.id.startek_layout);

        tvStartek = findViewById(R.id.tv_startek);
        tvMorpho = findViewById(R.id.tv_morpho);
        tvMorphoL1 = findViewById(R.id.tv_morphoL1);
        tvMantra = findViewById(R.id.tv_mantra);
        tvMantraL1 = findViewById(R.id.tv_mantra2);

        btnProceedDeviceLayout = findViewById(R.id.btn_proceed_device);


    }

    private void deviceLayoutListeners() {

        mantraLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_selected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Mantra";
        });

        mantraL1Layout.setOnClickListener(view -> {
            imgMantraL1.setImageResource(R.drawable.mantral1_selected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);

            tvMantraL1.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));


            selectedDevice = "MantraL1";
        });

        startekLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_selected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.selected_text_color));

            selectedDevice = "Startek";
        });

        morphoLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_selected);
            imgMorphoL1.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Morpho";
        });

        morphoL1Layout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMantraL1.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgMorphoL1.setImageResource(R.drawable.morpho_selected);
            imgStartek.setImageResource(R.drawable.startek_unselected);


            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMantraL1.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvMorphoL1.setTextColor(getResources().getColor(R.color.selected_text_color));
            tvStartek.setTextColor(getResources().getColor(R.color.white));


            selectedDevice = "MorphoL1";
        });

        btnProceedDeviceLayout.setOnClickListener(view -> {
            if (!selectedDevice.equalsIgnoreCase("select")) {
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
            opts.wadh="E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=";
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

    private void showMessageDialog(String message) {
        final AlertDialog messageDialog = new AlertDialog.Builder(FingPayUserBioMetricActivity.this).create();
        final LayoutInflater inflater = LayoutInflater.from(FingPayUserBioMetricActivity.this);
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
        List<Param> params = new ArrayList<>();
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    result = data.getStringExtra("PID_DATA");
                    if (result.contains("Device not ready")) {
                        showMessageDialog("Device Not Connected");
                    } else {
                        if (result.contains("srno") && result.contains("rdsId") && result.contains("rdsVer")) {
                            try {
                                pidDataStr = result;
                                doUserBioMetricVerification();
                                showMessageDialog(pidDataStr);
                                /*pidData = serializer.read(PidData.class, result);
                                pidDataStr = pidData._Data.value;
                                Log.e("xml_data_show", pidDataStr);
                                hmac = pidData._Hmac;
                                sessionKey = pidData._Skey.value;
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
                                    } else if (name.equalsIgnoreCase("sysid")) {
                                        String systemId = params.get(i).value;
                                    }
                                }


                                merchantTransactionId="MS@AEPS"+new Date().getTime();

                                createJsonRequestBody();*/


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

    private void doUserBioMetricVerification() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingPayUserBioMetricActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doUserBioMetricVerification(AUTH_KEY, userId, deviceId, deviceInfo, aadharCard, pidDataStr);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        pDialog.dismiss();
                        String statusCode=responseObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN") || statusCode.equalsIgnoreCase("ERR"))
                        {
                            pDialog.dismiss();
                            String message = responseObject.getString("data");
                            new androidx.appcompat.app.AlertDialog.Builder(FingPayUserBioMetricActivity.this)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                        }
                        else if (statusCode.equalsIgnoreCase("OTP"))
                        {
                            //showOtpDialog();
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(FingPayUserBioMetricActivity.this)
                                    .setTitle("Message")
                                    .setMessage("You have to Re enter your KYC Details\nPlease try to do complete KYC in single step")
                                    .setCancelable(false)
                                    .setPositiveButton("Got It", (dialogInterface, i) -> {
                                        startActivity(new Intent(FingPayUserBioMetricActivity.this,FingpayUserOnBoard.class));
                                        finish();
                                    }).show();
                        }
                        else
                        {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(FingPayUserBioMetricActivity.this)
                                    .setTitle("Message")
                                    .setMessage("Something went wrong\nPlease Try After Sometime.")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(FingPayUserBioMetricActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong\nPlease Try After Sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(FingPayUserBioMetricActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong\nPlease Try After Sometime.")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(FingPayUserBioMetricActivity.this)
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });

    }

}