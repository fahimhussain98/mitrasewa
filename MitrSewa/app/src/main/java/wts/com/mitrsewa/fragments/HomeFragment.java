package wts.com.mitrsewa.fragments;

import static wts.com.mitrsewa.activities.HomeDashBoardActivity.aepsBalance;
import static wts.com.mitrsewa.activities.HomeDashBoardActivity.balance;
import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.wts.wts_aeps_release.WTS_Aeps_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.ElectricityActivity;
import wts.com.mitrsewa.activities.Electricity_wts;
import wts.com.mitrsewa.activities.FingPayAepsActivity;
import wts.com.mitrsewa.activities.FingPayMicroAtmActivity;
import wts.com.mitrsewa.activities.FingpayCmsActivity;
import wts.com.mitrsewa.activities.MoreServicesActivity;
import wts.com.mitrsewa.activities.RechargeActivity;
import wts.com.mitrsewa.activities.SenderValidationActivity;
import wts.com.mitrsewa.activities.SettlementActivity;
import wts.com.mitrsewa.activities.TwoFactorAuthenticationActivity;
import wts.com.mitrsewa.activities.UpiQrCodeActivity;
import wts.com.mitrsewa.activities.WalletToWalletActivity;
import wts.com.mitrsewa.activities.WithdrawCommissionActivity;
import wts.com.mitrsewa.adapters.AffiliateItemAdapter;
import wts.com.mitrsewa.fingpayKyc.FingPayUserBioMetricActivity;
import wts.com.mitrsewa.fingpayKyc.FingpayUserOnBoard;
import wts.com.mitrsewa.models.AffiliateModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class HomeFragment extends Fragment {
    ConstraintLayout otherServiceContainer;
    LinearLayout prepaidLayout, dthLayout, postpaidLayout, aepsLayout, aadharPayLayout, miniStatementLayout, balanceEnquiryLayout, moveToBankLayout, moveToBank2Layout,
            moveToBank3Layout, moreLayout, microAtmLayout,cmsLayout, electricityLayout,electricity_wts_layout, broadBandLayout, fastagLayout, gasLayout, dmtLayout, expressDmtLayout,
            loadWalletLayout, withdrawCommissionLayout, walletToWalletLayout;

    RecyclerView affiliateRv;
    boolean isMicroAtm;

    String pan, userId, deviceId, deviceInfo;
    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION
    SharedPreferences sharedPreferences;
    ImageSlider imageSlider;
    String city;
    String selectedAepsTransactionType, selectedAepsType;

    String whichButtonClicked = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        pan = sharedPreferences.getString("panCard", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        city = sharedPreferences.getString("city", null);

        setImageSlider();
        getOtherServices();
        handleClickEvents();

        return view;

    }

    private void setImageSlider() {
        ArrayList<SlideModel> mySliderList = new ArrayList<>();
        mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));

        imageSlider.setImageList(mySliderList, ScaleTypes.FIT);
    }

    private void handleClickEvents() {

        prepaidLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), RechargeActivity.class);
            intent.putExtra("service", "Mobile");
            startActivity(intent);
        });

        dthLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), RechargeActivity.class);
            intent.putExtra("service", "Dth");
            startActivity(intent);
        });

        dmtLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SenderValidationActivity.class);
            intent.putExtra("isExpressDmt", false);
            startActivity(intent);
        });

        expressDmtLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SenderValidationActivity.class);
            intent.putExtra("isExpressDmt", true);
            startActivity(intent);
        });

        aepsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMicroAtm = false;
            //    selectedAepsTransactionType = "select";
                selectedAepsTransactionType = "cw";     // 23/05/24
                selectedAepsType = "AEPS";    // for 2FA Authentication
                checkCityAndEKyc();
                //getLastLocation();
            }
        });

        aadharPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMicroAtm = false;
                selectedAepsTransactionType = "m";
                selectedAepsType = "AP";    // for 2FA Authentication
                //getLastLocation();
                checkCityAndEKyc();

            }
        });

        miniStatementLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMicroAtm = false;
                selectedAepsTransactionType = "ms";
                selectedAepsType = "AEPS";    // for 2FA Authentication

                //getLastLocation();
                checkCityAndEKyc();

            }
        });

        balanceEnquiryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMicroAtm = false;
                selectedAepsTransactionType = "be";
                selectedAepsType = "AEPS";    // for 2FA Authentication

                //getLastLocation();
                checkCityAndEKyc();

            }
        });

        moveToBankLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getContext(), SettlementActivity.class);
                in.putExtra("payoutType", "payout1");
                startActivity(in);
            }
        });

        moveToBank2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whichButtonClicked = "payout2Clicked";
                getLastLocation();
            }
        });

        moveToBank3Layout.setOnClickListener(view -> {
            whichButtonClicked = "payout3Clicked";
            getLastLocation();
        });

        /*covidLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cowin.gov.in/"));
                startActivity(browserIntent);*//*
            }
        });*/

        microAtmLayout.setOnClickListener(view -> {
            isMicroAtm = true;
            getLastLocation();
            Intent intent=new Intent(getContext(), FingPayMicroAtmActivity.class);
            startActivity(intent);
        });

        cmsLayout.setOnClickListener(view -> {
            whichButtonClicked = "cmsClicked";
            getLastLocation();

        });

        electricityLayout.setOnClickListener(v -> {

            new AlertDialog.Builder(getContext())
//                    .setMessage("How do you want to proceed ? ")
//                    .setTitle("Confirmation")
//                    .setPositiveButton("Online", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent intent = new Intent(getContext(), ElectricityActivity.class);
//                            intent.putExtra("service", "Electricity");
//                            intent.putExtra("operatorServiceId", "5");
//                            intent.putExtra("serviceId", "5");
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("Offline", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent intent = new Intent(getContext(), ElectricityActivity.class);
//                            intent.putExtra("service", "Electricity");
//                            intent.putExtra("operatorServiceId", "offline");
//                            intent.putExtra("serviceId", "5");
//                            startActivity(intent);
//                        }
//                    }).setNeutralButton("Cancel", null)

                    .setMessage("This Service is not Available for you!!!")
                    .setTitle(" Alert ")

                    .setPositiveButton("Ok", null)

                    .show();

        });
        electricity_wts_layout.setOnClickListener(v -> {

            new AlertDialog.Builder(getContext())
                    .setMessage("How do you want to proceed ? ")
                    .setTitle("Confirmation")
                    .setPositiveButton("Online", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getContext(), Electricity_wts.class);
                            intent.putExtra("service", "Electricity");
                            intent.putExtra("operatorServiceId", "40");
                            intent.putExtra("serviceId", "40"); //40
                            startActivity(intent);
                        }
                    })
//                    .setNegativeButton("Offline", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent intent = new Intent(getContext(), Electricity_wts.class);
//                            intent.putExtra("service", "Electricity");
//                            intent.putExtra("operatorServiceId", "offline");
//                            intent.putExtra("serviceId", "40");  //
//                            startActivity(intent);
//                        }
//                    })
                    .setNeutralButton("Cancel", null)

                    .show();

        });

        postpaidLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ElectricityActivity.class);
            intent.putExtra("service", "PostPaid");
            intent.putExtra("serviceId", "6");
            intent.putExtra("operatorServiceId", "6");

            startActivity(intent);
        });

        broadBandLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ElectricityActivity.class);
            intent.putExtra("service", "INTERNET/ISP");
            intent.putExtra("serviceId", "14");
            intent.putExtra("operatorServiceId", "14");

            startActivity(intent);
        });

        fastagLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ElectricityActivity.class);
            intent.putExtra("service", "FASTAG");
            intent.putExtra("serviceId", "17");
            intent.putExtra("operatorServiceId", "17");

            startActivity(intent);
        });

        gasLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ElectricityActivity.class);
            intent.putExtra("service", "Gas");
            intent.putExtra("serviceId", "12");
            intent.putExtra("operatorServiceId", "12");

            startActivity(intent);
        });

        moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MoreServicesActivity.class));
            }
        });

        loadWalletLayout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Payment Mode")
                    .setMessage("Please Select Your Payment Mode")
                    .setPositiveButton("Scan & Pay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getContext(), UpiQrCodeActivity.class);
                            intent.putExtra("mode", "1");
                            startActivity(intent);
                        }
                    }).setNegativeButton("Send Payment Link On my UPI APP", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getContext(), UpiQrCodeActivity.class);
                            intent.putExtra("mode", "2");
                            startActivity(intent);
                        }
                    }).setNeutralButton("Cancel", null).show();

        });

        withdrawCommissionLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), WithdrawCommissionActivity.class));

        });

        walletToWalletLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), WalletToWalletActivity.class);
            intent.putExtra("mainBalance", balance);
            intent.putExtra("aepsBalance", aepsBalance);
            startActivity(intent);
        });

    }

    private void checkCityAndEKyc() {
        if (city.equalsIgnoreCase("") || city.equalsIgnoreCase("NA"))
            new AlertDialog.Builder(getContext())
                    .setMessage("Please Ask Admin To Update Your City And Then Re Login Application")
                    .setTitle("Message")
                    .setPositiveButton("Got It!!!", null)
                    .show();
        else
            checkEKycStatus();
    }

    private void checkEKycStatus() {
        final AlertDialog pDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkEKycStatus(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String data = responseObject.getString("data");
                            if (data.equalsIgnoreCase("0")) {
                                /**
                                 * Full kyc is pending
                                 */
                                startActivity(new Intent(getContext(), FingpayUserOnBoard.class));

                            } else if (data.equalsIgnoreCase("1")) {
                                /**
                                 * Biometric is pending
                                 */
                                startActivity(new Intent(getContext(), FingPayUserBioMetricActivity.class));

                            } else if (data.equalsIgnoreCase("2")) {
                                /**
                                 * KYC is completed
                                 * Do Transaction
                                 */
                                getLastLocation();

                            } else {

                                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                        .setMessage("Please try after some time")
                                        .setTitle("Message")
                                        .show();
                            }
                            pDialog.dismiss();
                        } else {
                            String data = responseObject.getString("data");
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                    .setMessage(data)
                                    .setTitle("Message")
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                .setMessage("Please try after some time")
                                .setTitle("Message")
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(getContext())
                            .setMessage("Please try after some time")
                            .setTitle("Message")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setMessage("Please try after some time")
                        .setTitle("Message")
                        .show();
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude() + "";
                                    longi = location.getLongitude() + "";

                                    if (isMicroAtm) {
                                        Intent intent = new Intent(getContext(), FingPayMicroAtmActivity.class);
                                        intent.putExtra("lat", lat);
                                        intent.putExtra("longi", longi);
                                        startActivity(intent);
                                    } else {

                                        if (whichButtonClicked.equalsIgnoreCase("payout2Clicked")) {
                                            Intent intent = new Intent(getContext(), SettlementActivity.class);
                                            intent.putExtra("payoutType", "payout2");
                                            intent.putExtra("lat", lat);
                                            intent.putExtra("longi", longi);
                                            startActivity(intent);
                                            whichButtonClicked = "";

                                        } else if (whichButtonClicked.equalsIgnoreCase("payout3Clicked")) {
                                            Intent intent = new Intent(getContext(), SettlementActivity.class);
                                            intent.putExtra("payoutType", "payout3");
                                            intent.putExtra("lat", lat);
                                            intent.putExtra("longi", longi);
                                            startActivity(intent);
                                            whichButtonClicked = "";
                                        } else if (whichButtonClicked.equalsIgnoreCase("cmsClicked")) {
                                            Intent intent = new Intent(requireContext(), FingpayCmsActivity.class);
                                            intent.putExtra("lat", lat);
                                            intent.putExtra("longi", longi);
                                            startActivity(intent);

                                        } else {
//                                           Intent intent = new Intent(getContext(), FingPayAepsActivity.class);
//                                            intent.putExtra("lat", lat);
//                                            intent.putExtra("longi", longi);
//                                            intent.putExtra("balance", aepsBalance);
//                                            intent.putExtra("transactionType", selectedAepsTransactionType);
//                                            startActivity(intent);
                                            checkTwoFactorAuthentication();

                                        }

                                        //launchWTSAEPS();

                                    }


                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            Intent intent;
            if (isMicroAtm) {
                intent = new Intent(getContext(), FingPayMicroAtmActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("longi", longi);
                startActivity(intent);
            } else {
                //launchWTSAEPS();
                if (whichButtonClicked.equalsIgnoreCase("payout2Clicked")) {
                    intent = new Intent(getContext(), SettlementActivity.class);
                    intent.putExtra("payoutType", "payout2");
                    intent.putExtra("lat", lat);
                    intent.putExtra("longi", longi);
                    startActivity(intent);
                    whichButtonClicked = "";

                } else if (whichButtonClicked.equalsIgnoreCase("payout3Clicked")) {
                    intent = new Intent(getContext(), SettlementActivity.class);
                    intent.putExtra("payoutType", "payout3");
                    intent.putExtra("lat", lat);
                    intent.putExtra("longi", longi);
                    startActivity(intent);
                    whichButtonClicked = "";
                }
                else if (whichButtonClicked.equalsIgnoreCase("cmsClicked")) {
                     intent = new Intent(requireContext(), FingpayCmsActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("longi", longi);
                    startActivity(intent);

                }
                else {
//                    intent = new Intent(getContext(), FingPayAepsActivity.class);
//                    intent.putExtra("lat", lat);
//                    intent.putExtra("longi", longi);
//                    intent.putExtra("balance", aepsBalance);
//                    intent.putExtra("transactionType", selectedAepsTransactionType);
                    checkTwoFactorAuthentication();
                }

            }

        }
    };

    private void checkTwoFactorAuthentication() {

        ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkTwoFactorAuthStatus(AUTH_KEY, userId, deviceId, deviceInfo, selectedAepsType);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        String responseData = responseObject.getString("data");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            if (responseData.equalsIgnoreCase("true")) {
                                pDialog.dismiss();
                                Intent intent = new Intent(getContext(), FingPayAepsActivity.class);
                                intent.putExtra("lat", lat);
                                intent.putExtra("longi", longi);
                                intent.putExtra("balance", aepsBalance);
                                intent.putExtra("aepsType", selectedAepsType);
                                intent.putExtra("transactionType", selectedAepsTransactionType);
                                startActivity(intent);

                            } else if (responseData.equalsIgnoreCase("TFA")) {
                                String message = responseObject.getString("status");
                                String data = responseObject.getString("data");

                                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                        .setCancelable(false)
                                        .setMessage(message)
                                        .setTitle(data + " Please Proceed ..")
                                        .setNegativeButton("No", null)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Intent intent = new Intent(requireActivity(), TwoFactorAuthenticationActivity.class);
                                                intent.putExtra("lat", lat);
                                                intent.putExtra("longi", longi);
                                                intent.putExtra("balance", aepsBalance);
                                                intent.putExtra("title", "TwoFactorAuth");
                                                intent.putExtra("aepsType", selectedAepsType);

                                                startActivity(intent);

                                            }
                                        }).show();
                                pDialog.dismiss();

                            }

                        } else if (responseCode.equalsIgnoreCase("ERR")) {

                            pDialog.dismiss();

                            String title = responseObject.getString("status");
                            String message = responseObject.getString("data");

                            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                    .setCancelable(false)
                                    .setTitle(title)
                                    .setMessage(message)
                                    .setPositiveButton("Okay", null)
                                    .show();

                        }
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Message")
                            .setMessage(response.message())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
            }
        });

    }

    public void getOtherServices() {

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtherServices(AUTH_KEY, userId, deviceId, deviceInfo);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        ArrayList<AffiliateModel> arrayList = new ArrayList<>();

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            JSONArray dataArray = jsonObject1.getJSONArray("data");
                            for (int i=0; i<dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String name = dataObject.getString("Name");
                                String icon = dataObject.getString("PicImage");
                                icon = icon.replace("~", "");
                                icon = "http://login.paybil.co.in"+icon;
                                String link = dataObject.getString("UrlLink");

                                AffiliateModel model = new AffiliateModel();
                                model.setTitle(name);
                                model.setImageUrl(icon);
                                model.setLink(link);
                                arrayList.add(model);

                            }

                            AffiliateItemAdapter adapter = new AffiliateItemAdapter(arrayList,requireContext());
                            affiliateRv.setAdapter(adapter);
                            affiliateRv.setLayoutManager(new GridLayoutManager(requireContext(), 4));

                        } else {
                            Toast.makeText(requireContext(), "Other service not found", Toast.LENGTH_SHORT).show();
                            otherServiceContainer.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Other service not found", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        otherServiceContainer.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(requireContext(), "Other service not found", Toast.LENGTH_SHORT).show();
                    otherServiceContainer.setVisibility(View.GONE);

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Other service not found", Toast.LENGTH_SHORT).show();
                otherServiceContainer.setVisibility(View.GONE);
            }
        });
    }

    private void launchWTSAEPS() {
        if (balance.equalsIgnoreCase("0.00"))
            balance = "0.0";

        Intent intent = new Intent(getContext(), WTS_Aeps_Activity.class);
        intent.putExtra("app_Id", "125");//app Id provide by WTS Net India Pvt Ltd.
        intent.putExtra("authorise_Key", "U/mFvAEzP1yBRjjglsr1ml/n6OcwDn63J5+h6o7rkljs5sIyVLXpEQ==");//authorise_Key provide by WTS Net India Pvt Ltd.
        intent.putExtra("main_wallet_bal", balance);//This is your wallet balance, show in AEPS dashboard
        intent.putExtra("panno", pan);//This is your wallet balance, show in AEPS dashboard
        intent.putExtra("lat", lat);//latitude is mandatory
        intent.putExtra("long", longi); //longitude is mandatory
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void initViews(View view) {
        prepaidLayout = view.findViewById(R.id.prepaid_layout);
        dthLayout = view.findViewById(R.id.dth_layout);
        dmtLayout = view.findViewById(R.id.dmt_layout);
        expressDmtLayout = view.findViewById(R.id.express_dmt_layout);
        moveToBankLayout = view.findViewById(R.id.move_to_bank_layout);
        moveToBank2Layout = view.findViewById(R.id.move_to_bank2_layout);
        moveToBank3Layout = view.findViewById(R.id.move_to_bank3_layout);
        //covidLayout = view.findViewById(R.id.covid_layout);
        moreLayout = view.findViewById(R.id.more_layout);
        microAtmLayout = view.findViewById(R.id.micro_atm_layout);
        cmsLayout = view.findViewById(R.id.cmsLayout);
        imageSlider = view.findViewById(R.id.image_slider);
        //////////////////////////AEPS//////////////////////////
        aepsLayout = view.findViewById(R.id.aeps_layout);
        aadharPayLayout = view.findViewById(R.id.aadhar_pay_layout);
        miniStatementLayout = view.findViewById(R.id.mini_statement_layout);
        balanceEnquiryLayout = view.findViewById(R.id.balance_enquiry_layout);
        //////////////////////////AEPS//////////////////////////

        //////////////////////////BBPS//////////////////////////
        electricityLayout = view.findViewById(R.id.electricity_layout);
        postpaidLayout = view.findViewById(R.id.postpaid_layout);
        broadBandLayout = view.findViewById(R.id.broadband_layout);
        fastagLayout = view.findViewById(R.id.fastag_layout);
        gasLayout = view.findViewById(R.id.gas_layout);
        //////////////////////////BBPS//////////////////////////

        ////////////////////////////////////////////////////////////
        loadWalletLayout = view.findViewById(R.id.load_wallet_layout);
        withdrawCommissionLayout = view.findViewById(R.id.withdraw_commission_layout);
        walletToWalletLayout = view.findViewById(R.id.wallet_to_wallet_layout);
        ////////////////////////////////////////////////////////////

        otherServiceContainer = view.findViewById(R.id.layout11);
        affiliateRv = view.findViewById(R.id.affiliateRv);
        //-----------------------------------------------------------------
        electricity_wts_layout = view.findViewById(R.id.electricity_wts_layout);


    }
}