package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
import wts.com.mitrsewa.device.PidData;
import wts.com.mitrsewa.device.PidOptions;
import wts.com.mitrsewa.fingpayKyc.FingPayUserBioMetricActivity;
import wts.com.mitrsewa.fingpayKyc.FingpayUserOnBoard;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class EKYCActivity extends AppCompatActivity {

    ImageView imgMorpho,imgMorphoL1, imgStartek, imgMantra, imgMantraL1;
    LinearLayout morphoLayout,morphoL1Layout, startekLayout, mantraLayout,mantraL1Layout;
    TextView tvMorpho,tvMorphoL1, tvMantra, tvStartek,tvMantraL1;
    AppCompatButton btnProceedDeviceLayout;
    AutoCompleteTextView et_AadharNumber;
   String aadharnumber,phoneNumber;
   EditText et_number;

    String selectedDevice = "select";
    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
    String pidDataStr = null;
    double latitude =0.0;
    double longitude =0.0;
    String latitudeString;
    String longitudeString;
    String senderMobileNo;
    String userId, deviceId, deviceInfo, aadharCard;
    SharedPreferences sharedPreferences;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekycactivity);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        deviceLayoutListeners();

        Intent intent = getIntent();
        String ekycStatus = intent.getStringExtra("ekycStatus");
        String ekycMessage = intent.getStringExtra("ekycMessage");
        String remitterId = intent.getStringExtra("remitterId");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EKYCActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        Log.d("EKYCActivity","dataFromSenderValidattion::"
                +"\n ekycStatus :"+ekycStatus
                +"\n ekycMessage :"+ ekycMessage
                +"\n remitterId :"+ remitterId);
        //__________________________________________________________________________________________
        Log.d("EKYCActivity","pidDataStr::\n"+pidDataStr);
        getLastLocation();

        //__________________________________________________________________________________________



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
        et_AadharNumber = findViewById(R.id.et_AadharNumber);
        et_number = findViewById(R.id.et_recipient_name);



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
        final AlertDialog messageDialog = new AlertDialog.Builder(EKYCActivity.this).create();
        final LayoutInflater inflater = LayoutInflater.from(EKYCActivity.this);
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
        Log.d("EKYCActivity","pidDataStr::\n"+message);

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
                                Log.d("EKYCActivity","pidDataStr::\n"+pidDataStr);


                               // showMessageDialog(pidDataStr);
                                doremitterkyc();
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

    private void doremitterkyc() {
        final AlertDialog pDialog = new AlertDialog.Builder(EKYCActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        aadharnumber = et_AadharNumber.getText().toString().trim();
        phoneNumber = et_number.getText().toString().trim();
        latitudeString = String.valueOf(latitude);
        longitudeString = String.valueOf(longitude);



        Call<JsonObject> call = RetrofitClient.getInstance().getApi().DoRemitterEKYC(AUTH_KEY, userId, deviceId, deviceInfo,phoneNumber, aadharnumber, pidDataStr,latitudeString,longitudeString);
        Log.d("DoRemitterEKYC","DoRemitterEKYC:::"+
                "\n userI:" + userId +
                "\n deviceId::" + deviceId +
                "\n deviceInfo::" + deviceInfo +
                "\n phoneNumber::" + phoneNumber +
                "\n aadharnumber::" + aadharnumber +
                "\n pidDataStr::" + pidDataStr +
                "\n latitudeString ::" + latitudeString +
                "\n longitudeString::" + longitudeString
                );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        pDialog.dismiss();
                        String statusCode=responseObject.getString("statuscode");
                        Log.d("DoRemitterEKYC","DoRemitterEKYC:"+response.body());
                        if (statusCode.equalsIgnoreCase("TXN"))
                        {
                            pDialog.dismiss();
                            String message = responseObject.getString("data ");
                            new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                    .setTitle("Message TXN")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String message = responseObject.getString("data");
                            new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                    .setTitle("Message ERR")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();

                        } else if (statusCode.equalsIgnoreCase("OTP"))
                        {
                            //showOtpDialog();
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                    .setTitle("Message OTP")
                                    .setMessage("You have to Re enter your KYC Details\nPlease try to do complete KYC in single step")
                                    .setCancelable(false)
                                    .setPositiveButton("Got It", (dialogInterface, i) -> {
                                        startActivity(new Intent(EKYCActivity.this, FingpayUserOnBoard.class));
                                        finish();
                                    }).show();
                        }
                        else
                        {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                    .setTitle("Message")
                                    .setMessage("Something went wrong\nPlease Try After Sometime.")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong\nPlease Try After Sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
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
                new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
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

    private void doUserBioMetricVerification() {
        final AlertDialog pDialog = new AlertDialog.Builder(EKYCActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doUserBioMetricVerification(AUTH_KEY, userId, deviceId, deviceInfo, aadharCard, pidDataStr);
        Log.d("EKYCActivity","pidDataStr::\n"+pidDataStr);

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
                            new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
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
                            new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                    .setTitle("Message")
                                    .setMessage("You have to Re enter your KYC Details\nPlease try to do complete KYC in single step")
                                    .setCancelable(false)
                                    .setPositiveButton("Got It", (dialogInterface, i) -> {
                                        startActivity(new Intent(EKYCActivity.this, FingpayUserOnBoard.class));
                                        finish();
                                    }).show();
                        }
                        else
                        {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                    .setTitle("Message")
                                    .setMessage("Something went wrong\nPlease Try After Sometime.")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong\nPlease Try After Sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
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
                new androidx.appcompat.app.AlertDialog.Builder(EKYCActivity.this)
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
    //_______________________________________________________________________________________
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                 latitude = location.getLatitude();
                                 longitude = location.getLongitude();

                                // Use the latitude and longitude
                                System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
                                Log.d("EKYCActivity","latlon::"+
                                        "\nlatitude:: "+ latitude+
                                        "\nlongitude:: "+ longitude
                                );
                            }
                        }
                    });
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }
    }




}