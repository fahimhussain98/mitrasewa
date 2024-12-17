package wts.com.mitrsewa.fingpayKyc;

//import static wts.com.mitrsewa.activities.FundTransferActivity.FILE_PERMISSION;
import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.FingPayAepsActivity;
import wts.com.mitrsewa.activities.FundTransferActivity;
import wts.com.mitrsewa.retrofit.RetrofitClient;
import wts.com.mitrsewa.retrofit.WebServiceInterface;
//-----------------------------------------------------------
import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FingpayUserOnBoard extends AppCompatActivity {
    EditText etUsername, etMobile, etCompany, etEmailId, etCity, etAddress, etPanCard, etAadharCard, etDistrict, etPinCode;
    Button btnNext;
    SharedPreferences sharedPreferences;
    String userId, deviceId, deviceInfo, userName, mobile, company, emailId, city, address, panCard, aadharCard, district, pinCode;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    int PERMISSION_ID = 44;
    String panCardUrl = "",frontAadharImage = "",backAadharImage = "", ShopImage ="",PassbookImage = "", aadharBackUrl = "", panUrl = "", selfieUrl = "", shopUrl = "", videoUrl = "";
    Context context;
    Activity activity;
    private static final int FILE_PERMISSION = 2;
    ArrayList<String> photoPaths;
    String primaryKey, encodeFpTxnId;
    TextView uploadPanCard,AadharFront,tvAadharBack,tvShopImage,tvUploadPassbook;
    AutoCompleteTextView et_companyType;
    EditText et_merchantState;
    CardView cardviewPan,cardviewAadharFrant,cardviewAadharback,cardviewshop,cardviewPassbok;
    ImageView imgPan,imgAadharfrant,imgAadharback,imgshop,imgPassbook;
    //-------------------------------------------------------
    boolean isVideoClicked = false;
    String whichButtonClicked = "please upload document";
    private static final int CAMERA_REQUEST = 1;
    EditText et_firstname,et_lastname;
    AutoCompleteTextView et_bankName,et_accountNo,et_ifscCode;



     String selectedState;
     String  merchantstate;// selectedStateId;//merchantstate
     String stateName, stateId ;

    List<String> statesList = new ArrayList<>();
    List<String> statesIdList = new ArrayList<>();
    //_____________________________variable________________________________
    private AutoCompleteTextView certificateOfincorporation;
    private AutoCompleteTextView physicalVerification;
    private AutoCompleteTextView cancelChequeImages;
    private AutoCompleteTextView termConditionsCheck;
    private AutoCompleteTextView videoVerificationwithLatLong;
    private AutoCompleteTextView tradeBusinessProof;
    private AutoCompleteTextView shopImageTrueOrFalse;
   // ______________________
   String merchantFirstname,merchantLastName,bankName,accountNo,ifscCode;
   String companyType;     ///mccCode;


    CheckBox cbDifferentAddress;
    String apiAddress;

    private String currentPhotoPath;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingpay_user_on_board);
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FingpayUserOnBoard.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        userName = sharedPreferences.getString("username", null);
        mobile = sharedPreferences.getString("mobileno", null);
        company = sharedPreferences.getString("firmName", null);
        emailId = sharedPreferences.getString("email", null);
        city = sharedPreferences.getString("city", null);
        address = sharedPreferences.getString("address", null);
        panCard = sharedPreferences.getString("pancard", null);
        aadharCard = sharedPreferences.getString("adharcard", null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(FingpayUserOnBoard.this);

        etUsername.setText(userName);
        etMobile.setText(mobile);
        etCompany.setText(company);
        etEmailId.setText(emailId);
        etCity.setText(city);
       // etAddress.setText(address) ;

        apiAddress = address;
        etAddress.setText(apiAddress);
       // boolean useDifferentAddress = cbDifferentAddress.isChecked(); // Checkbox for using a different address
        etPanCard.setText(panCard);
        etAadharCard.setText(aadharCard);


        btnNext.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(etDistrict.getText())) {
                if (!TextUtils.isEmpty(etPinCode.getText())) {
                    district = etDistrict.getText().toString().trim();
                    pinCode = etPinCode.getText().toString().trim();

                    //doFingPayUserOnBoard();
                    getLastLocation();
                } else {
                    etPinCode.setError("Required");
                }
            } else {
                etDistrict.setError("Required");
            }
        });

        activity = FingpayUserOnBoard.this;
        context = FingpayUserOnBoard.this;

        clickEvents();
        //--------------------------------------------
        et_merchantState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getmerchantState();
            }
        });
        et_companyType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_merchantState();
            }
        });
        //______________________________________________________________
        setupDropdowns();
        getAllValues();
        //_____________________________________________________________







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
                                    doFingPayUserOnBoard();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(FingpayUserOnBoard.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(FingpayUserOnBoard.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(FingpayUserOnBoard.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(FingpayUserOnBoard.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    //---------------------fileupload Start---------------------------------
    @SuppressLint("SetTextI18n")
    private void clickEvents(){
        uploadPanCard.setOnClickListener(view -> {
            isVideoClicked = false;
            whichButtonClicked = "uploadPanCardClicked";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);
        });
        AadharFront.setOnClickListener(view -> {
           // isVideoClicked = false;
            whichButtonClicked = "uploadAadharFrantClicked";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);
        });
        tvAadharBack.setOnClickListener(view->{
            whichButtonClicked = "uploadAadharBackClicked";//uploadAadharBackClicked
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);

        });
        tvShopImage.setOnClickListener(view->{
            whichButtonClicked  = "uploadShopImg";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);

        });
        tvUploadPassbook.setOnClickListener(view->{
            whichButtonClicked  = "uploadPassbook";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);

        });


    }


    public void checkPermission(String writePermission, String readPermission, String mediaFilePermission, String cameraPermission, int requestCode) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, mediaFilePermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{mediaFilePermission, cameraPermission}, requestCode);
            } else {
                chooseImage();
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, writePermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(context, readPermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_DENIED) {
                // Requesting the permission
                ActivityCompat.requestPermissions(activity, new String[]{writePermission, readPermission, cameraPermission}, requestCode);
            } else {
                chooseImage();
            }
        }

    }
//    public void chooseImage() {
//        Intent galleryIntent;
//        if (isVideoClicked){
//            galleryIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//            //   galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        }else {
//            galleryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        }
//        startActivityForResult(galleryIntent, CAMERA_REQUEST);
//    }



//    @SuppressLint("SetTextI18n")
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        try {
//            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
//
//                if (isVideoClicked){
//                    Uri selectedImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                    assert cursor != null;
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String mediaPath = cursor.getString(columnIndex);
//                    File file = new File(mediaPath);
//                    serverUpload(file);
//                    // Set the Image in ImageView for Previewing the Media
//                    cursor.close();
//                }
//                else {
//                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//
//                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//                    //  File file = new File(Environment.getExternalStorageDirectory() + File.separator + "testimage.jpg");   // not work in android13
//                    File file = new File(getApplicationContext().getCacheDir(), "testimage.jpg");
//                    file.createNewFile();
//                    FileOutputStream fo = new FileOutputStream(file);
//                    fo.write(bytes.toByteArray());
//                    fo.close();
//
//                    serverUpload(file);
//                }
//
//
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public void chooseImage() {
        if (isVideoClicked) {
            Intent galleryIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(galleryIntent, CAMERA_REQUEST);
        } else {
            try {
                File photoFile = createImageFile();

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(
                            this,
                            "wts.com.mitrsewa.fileprovider",  // Use your exact package name + .fileprovider
                            photoFile
                    );

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                Log.e("Camera", "Error creating image file", ex);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getApplicationContext().getCacheDir();
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                if (isVideoClicked && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String mediaPath = cursor.getString(columnIndex);
                    File file = new File(mediaPath);
                    serverUpload(file);
                    cursor.close();
                } else {
                    File file = new File(currentPhotoPath);
                    compressAndResizeImage(file);
                    serverUpload(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }


    private void compressAndResizeImage(File imageFile) {
        try {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);


            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int targetW = 2048;
            int targetH = 2048;


            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            if (scaleFactor < 1) scaleFactor = 1;


            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);


            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serverUpload(File myfile) {

        if (myfile == null || !myfile.exists() || myfile.length() == 0) {
            nonDismissErrorDialog("Message!!!", "Please select a file to upload");
            return;
        }

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody reqFile;
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        if (isVideoClicked){
            reqFile = RequestBody.create(MediaType.parse("*/*"), myfile);
        }
        else {
            reqFile = RequestBody.create(MediaType.parse("image/*"), myfile);
        }
        MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", timeStamp + myfile.getName(), reqFile);

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().uploadfile(AUTH_KEY, body);
        call.enqueue(new Callback<JsonObject>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String code = jsonObject.getString("statuscode");

                        if (code.equalsIgnoreCase("TXN")) {
                            if (whichButtonClicked.equalsIgnoreCase("uploadPanCardClicked")) {
                                uploadPanCard.setText("Pan Card Uploaded");
                                panCardUrl = jsonObject.getString("data");
                                cardviewPan.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://login.mitrdigiportal.in"+panCardUrl).into(imgPan);
                                Log.d("PanCard URL", "panCardUrl: " + panCardUrl);

                            }
                            else if (whichButtonClicked.equalsIgnoreCase("uploadAadharFrantClicked")) {
                                AadharFront.setText("Aadhar Uploaded");
                                 frontAadharImage = jsonObject.getString("data");
                                cardviewAadharFrant.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://login.mitrdigiportal.in"+frontAadharImage).into(imgAadharfrant);
                                Log.d("AadharFront URL", "Full URL: " + frontAadharImage);  // Log the full URL

                            } else if (whichButtonClicked.equalsIgnoreCase("uploadAadharBackClicked")) {
                                tvAadharBack.setText("Aadhar Back Uploaded");
                                backAadharImage  = jsonObject.getString("data");
                                cardviewAadharback.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://login.mitrdigiportal.in"+backAadharImage).into(imgAadharback);
                            } else if (whichButtonClicked.equalsIgnoreCase("uploadShopImg")) {
                                tvShopImage.setText(" Shop Image Uploaded");
                                ShopImage = jsonObject.getString("data");
                                cardviewshop.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://login.mitrdigiportal.in"+cardviewshop).into(imgshop);
                            } else if (whichButtonClicked.equalsIgnoreCase("uploadPassbook")) {
                                tvUploadPassbook.setText("Passbook Uploaded");
                                PassbookImage = jsonObject.getString("data");
                                cardviewPassbok.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://login.mitrdigiportal.in"+cardviewPassbok).into(imgPassbook);
                            }

                            pDialog.dismiss();

                        } else if (code.equalsIgnoreCase("ERR")) {
                            String message = jsonObject.getString("data");
                            nonDismissErrorDialog("Message!!!", message);
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            nonDismissErrorDialog("Message!!!", "Something went wrong");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        nonDismissErrorDialog("Message!!!", "Something went wrong");
                    }

                } else {
                    pDialog.dismiss();
                    nonDismissErrorDialog("Message!!!", "Something went wrong");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                nonDismissErrorDialog("Message!!!", "Something went wrong");
            }
        });

    }








    public void nonDismissErrorDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.cancel)
                .setPositiveButton("OK", null)
                .show();
    }

    //---------------------fileupload End-----------------------------------
    //---------------------getmerchant state ---------------------------------
    /*private void getmerchantState() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingpayUserOnBoard.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        WebServiceInterface webServiceInterface = WebServiceInterface.retrofit2.create(WebServiceInterface.class);

        Call<JsonArray> call = webServiceInterface.getmerchantState();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                pDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonArray jsonArray = response.body();


                        // Extract the states from the JSON array response
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject stateObject = jsonArray.get(i).getAsJsonObject();
                            String stateName = stateObject.get("state").getAsString();
                            String stateId = stateObject.get("stateId").getAsString();

                            // Add state name to the list
                            statesList.add(stateName);
                            statesIdList.add(stateId);

                        }

                        // Show the states in a dialog
                        showStatesDialog(statesList, statesIdList);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FingpayUserOnBoard.this, "An error occurred while processing data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FingpayUserOnBoard.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                pDialog.dismiss();
                showErrorDialog();
            }
        });
    }


    private void showStatesDialog(List<String> statesList, List<String> statesIdList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FingpayUserOnBoard.this);
        builder.setTitle("Select State");

        // Create a CharSequence array from the list for the dialog
        CharSequence[] items = statesList.toArray(new CharSequence[0]);
        builder.setItems(items, (dialog, which) -> {
            // Retrieve the selected state name and ID
             selectedState = statesList.get(which);
             selectedStateId = statesIdList.get(which);

            // Display state name and ID in the EditText
            EditText etMerchantState = findViewById(R.id.et_merchantState);
            etMerchantState.setText("State: " + selectedState + " (ID: " + selectedStateId + ")");
        });

        builder.show();
    }*/



    private void getmerchantState() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingpayUserOnBoard.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        WebServiceInterface webServiceInterface = WebServiceInterface.retrofit2.create(WebServiceInterface.class);
        Call<JsonArray> call = webServiceInterface.getmerchantState();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                pDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonArray jsonArray = response.body();


                        // Extract the states from the JSON array response
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject stateObject = jsonArray.get(i).getAsJsonObject();
                            stateName = stateObject.get("state").getAsString();
                            stateId = stateObject.get("stateId").getAsString();

                            // Add state name to the list
                            statesList.add(stateName);
                            statesIdList.add(stateId);
                            Log.d("merchantPinCode:", "merchantPinCode" + selectedState + "\n" + merchantstate); //hi id hai iski
                        }

                        // Show the states in a dialog
                        showStatesDialog(statesList, statesIdList);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FingpayUserOnBoard.this, "An error occurred while processing data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FingpayUserOnBoard.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                pDialog.dismiss();
                showErrorDialog();
            }
        });
    }
    private void showErrorDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
                .setMessage("Please try after sometime.")
                .setTitle("Message!!!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> finish())
                .show();
    }
    private void showStatesDialog(List<String> statesList, List<String> statesIdList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FingpayUserOnBoard.this);
        builder.setTitle("Select State");

        // Create a CharSequence array from the list for the dialog
        CharSequence[] items = statesList.toArray(new CharSequence[0]);
        builder.setItems(items, (dialog, which) -> {
            selectedState = statesList.get(which);
          //  selectedStateId = statesIdList.get(which);
            merchantstate = statesIdList.get(which);

           // String selectedStateId = statesIdList.get(which);

            Log.d("State Selected", "State: " + selectedState + ", ID: " + merchantstate);

            // Display state name and ID in the EditText
           // et_merchantState.setText("State: " + selectedState + " (ID: " + merchantstate + ")");
            et_merchantState.setText(selectedState);
        });

        builder.show();
    }



    //---------------------getmerchant state ---------------------------------
    //--------------------getcompany type----------------------------------------
    private void et_merchantState() {
        final AlertDialog pDialog = new AlertDialog.Builder(FingpayUserOnBoard.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);

        // Show the dialog
        pDialog.show();

        WebServiceInterface webServiceInterface = WebServiceInterface.Retrofitcompnaytype.create(WebServiceInterface.class);
        Call<JsonObject> call = webServiceInterface.getCompnayType();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();

                    // Extract mccCode and mccDescription
                    JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                    StringBuilder messageBuilder = new StringBuilder();
                    List<String> mccDescriptions = new ArrayList<>(); // List to hold descriptions

                    for (int i = 0; i < dataArray.size(); i++) {
                        JsonObject item = dataArray.get(i).getAsJsonObject();
                        int mccCodeInt = item.get("mccCode").getAsInt();

                        // Convert mccCode to String
                        companyType = String.valueOf(mccCodeInt);   //mccCode


                        String mccDescription = item.get("mccDescription").getAsString();
                        messageBuilder.append("MCC Code: ").append(companyType)
                                .append("\nDescription: ").append(mccDescription)
                                .append("\n\n");

                        // Add the description to the list
                        mccDescriptions.add(mccDescription);
                    }

                    // Create a dialog to display descriptions
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FingpayUserOnBoard.this);
                    dialogBuilder.setTitle("MCC Codes");
                    dialogBuilder.setItems(mccDescriptions.toArray(new String[0]), (dialog, which) -> {
                        // Set the clicked description to AutoCompleteTextView
                        // et_companyType = findViewById(R.id.et_companyType);
                        et_companyType.setText(mccDescriptions.get(which));
                    });

                    dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    dialogBuilder.show();
                } else {
                    // Handle error
                    Toast.makeText(FingpayUserOnBoard.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss(); // Dismiss the progress dialog
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle failure
                Toast.makeText(FingpayUserOnBoard.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss(); // Dismiss the progress dialog
            }
        });



    }


    //-----------------------getcomapyhtype end------------------------------------

    //----------------------------variable yes no -----------------------------------

    private void setupDropdowns() {
        String[] options = {"true", "false"};

        // Create adapter with custom layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                options
        );

        List<AutoCompleteTextView> allDropdowns = Arrays.asList(
                certificateOfincorporation,
                physicalVerification,
                cancelChequeImages,
                termConditionsCheck,
                videoVerificationwithLatLong,
                tradeBusinessProof,
                shopImageTrueOrFalse
        );

        // Setup each dropdown
        for (AutoCompleteTextView dropdown : allDropdowns) {
            setupAutoComplete(dropdown, adapter);
        }
    }

    private void setupAutoComplete(AutoCompleteTextView autoCompleteTextView, ArrayAdapter<String> adapter) {
        // Set essential properties
        autoCompleteTextView.setInputType(InputType.TYPE_NULL); // Prevent keyboard
        autoCompleteTextView.setFocusable(false); // Prevent focus
        autoCompleteTextView.setClickable(true); // Enable clicking

        // Set adapter and initial state
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText("", false);

        // Set click listener
        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());

        // Set item selection listener
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = (String) parent.getItemAtPosition(position);
            autoCompleteTextView.setText(selectedOption, false);
        });
    }

    private Map<String, String> getAllValues() {
        Map<String, String> values = new HashMap<>();
        values.put("certificateOfincorporation", certificateOfincorporation.getText().toString());
        values.put("physicalVerification", physicalVerification.getText().toString());
        values.put("cancelChequeImages", cancelChequeImages.getText().toString());
        values.put("termConditionsCheck", termConditionsCheck.getText().toString());
        values.put("videoVerificationwithLatLong", videoVerificationwithLatLong.getText().toString());
        values.put("tradeBusinessProof", tradeBusinessProof.getText().toString());
        values.put("shopImageTrueOrFalse", shopImageTrueOrFalse.getText().toString());
        return values;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(FingpayUserOnBoard.this);
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
            doFingPayUserOnBoard();
        }
    };

    private void doFingPayUserOnBoard() {
        if (!validateImages()) {
            return;
        }
        final AlertDialog pDialog = new AlertDialog.Builder(FingpayUserOnBoard.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        merchantFirstname = et_firstname.getText().toString().trim();
        merchantLastName = et_lastname.getText().toString().trim();
        bankName = et_bankName.getText().toString().trim();
        accountNo = et_accountNo.getText().toString().trim();
        ifscCode = et_ifscCode.getText().toString().trim();


        boolean useDifferentAddress = cbDifferentAddress.isChecked(); // Checkbox for using a different address

        String finalAddress = useDifferentAddress ? etAddress.getText().toString().trim() : apiAddress;
        /*Call<JsonObject> call = RetrofitClient.getInstance().getApi().doFingPayUserOnBoard(
                AUTH_KEY, userId, deviceId, deviceInfo, lat, longi,
                userName, mobile, company, emailId, pinCode, city,
                district, address, panCard, aadharCard
        );*/
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doFingPayUserOnBoard(
                AUTH_KEY, userId, deviceId, deviceInfo, lat, longi,merchantFirstname,merchantLastName
                , mobile, company, emailId, pinCode,merchantstate, city,district,
                //address,
                finalAddress,
                panCard
                ,aadharCard,bankName,accountNo,ifscCode,companyType,
                "true","true","true","true","true","true","true",
                frontAadharImage,backAadharImage,PassbookImage,panCardUrl,ShopImage
        );
        Log.d("firstname::" , merchantFirstname +"\n final address : " + finalAddress );

        //yan aadharCard = aadharno;
        //yan panCardUrl = pancardImage
        // yan frontAadharImage =aadharFront
        // yan backAadharImage = aadharBack;
        // yan PassbookImage = passbook;
        //yan ShopImage = shopImage;
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("ResponseApi","Response"+response.body());
                if (response.isSuccessful()) {
                    try {
                        Log.d("firstname::" , merchantFirstname);
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("OTP")) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            primaryKey = dataObject.getString("primarykey");
                            encodeFpTxnId = dataObject.getString("encodefptxnid");

                            showOtpDialog();
                            pDialog.dismiss();

                        } else {
                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                        }



                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong\nPlease Try After Sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
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
                new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
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

    private boolean validateImages() {
        List<String> emptyImages = new ArrayList<>();

        if (frontAadharImage == null || frontAadharImage.trim().isEmpty()) {
            emptyImages.add("Aadhar Front");
        }
        if (backAadharImage == null || backAadharImage.trim().isEmpty()) {
            emptyImages.add("Aadhar Back");
        }
        if (PassbookImage == null || PassbookImage.trim().isEmpty()) {
            emptyImages.add("Passbook");
        }
        if (panCardUrl == null || panCardUrl.trim().isEmpty()) {
            emptyImages.add("PAN Card");
        }
        if (ShopImage == null || ShopImage.trim().isEmpty()) {
            emptyImages.add("Shop Image");
        }

        if (!emptyImages.isEmpty()) {
            String missingImages = TextUtils.join(", ", emptyImages);
            nonDismissErrorDialog("Message!!!", "Please select a file to upload \n"+missingImages);
            return false;
        }
        return true;
    }

    private void showOtpDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);
        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);

        imgClose.setOnClickListener(view -> {
            addSenderDialog.dismiss();
        });

        tvTitle.setText("OTP Verification");
        etOtp.setHint("OTP");

        btnCancel.setOnClickListener(view -> {
            addSenderDialog.dismiss();
        });

        btnSubmit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etOtp.getText())) {
                String otp = etOtp.getText().toString().trim();
                verifyOnboardOtp(otp);
                addSenderDialog.dismiss();
            } else {

                etOtp.setError("Required");
            }
        });

    }

    private void verifyOnboardOtp(String otp) {
        final AlertDialog pDialog = new AlertDialog.Builder(FingpayUserOnBoard.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyOnboardOtp(AUTH_KEY, userId, deviceId, deviceInfo, primaryKey, encodeFpTxnId,
                otp);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            startActivity(new Intent(FingpayUserOnBoard.this, FingPayUserBioMetricActivity.class));
                            finish();
                        } else {
                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong\nPlease Try After Sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong\nPlease Try After Sometime.")
                            .setCancelable(false)
                            .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(FingpayUserOnBoard.this)
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ok", (dialogInterface, i) -> finish()).show();
            }
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etMobile = findViewById(R.id.et_mobile);
        etCompany = findViewById(R.id.et_company);
        etEmailId = findViewById(R.id.et_email_id);
        etCity = findViewById(R.id.et_city);
        etAddress = findViewById(R.id.et_address);
        etPanCard = findViewById(R.id.et_pan_card);
        etAadharCard = findViewById(R.id.et_aadhar_number);
        etDistrict = findViewById(R.id.et_district);
        etPinCode = findViewById(R.id.et_pin_code);
        btnNext = findViewById(R.id.btn_next);
        //-----------------------------------------------------
        uploadPanCard = findViewById(R.id.tvUploadPan);
        imgPan = findViewById(R.id.imgPan);
        cardviewPan = findViewById(R.id.cardviewPan);
        //-----------------------------------------------------
        AadharFront = findViewById(R.id.tvAadharFront);
        imgAadharfrant = findViewById(R.id.imgAadharfrant);
        cardviewAadharFrant = findViewById(R.id.cardviewAadharFrant);
        //-----------------------------------------------------
        tvAadharBack = findViewById(R.id.tvAadharBack);
        cardviewAadharback = findViewById(R.id.cardviewAadharback);
        imgAadharback = findViewById(R.id.imgAadharback);
        //-----------------------------------------------------
        tvShopImage = findViewById(R.id.tvShopImage);
        cardviewshop = findViewById(R.id.cardviewshop);
        imgshop = findViewById(R.id.imgshop);
        //-------------------------------------------------------
        tvUploadPassbook = findViewById(R.id.tvUploadPassbook);
        cardviewPassbok = findViewById(R.id.cardviewPassbok);
        imgPassbook = findViewById(R.id.imgPassbook);
        //----------------------State------------------------
        et_merchantState = findViewById(R.id.et_merchantState);
        et_companyType = findViewById(R.id.et_companyType);
        //______________________variable yes ya no__________________________
        certificateOfincorporation = findViewById(R.id.certificateOfincorporation);
        physicalVerification = findViewById(R.id.physicalVerification);
        cancelChequeImages = findViewById(R.id.cancelChequeImages);
        termConditionsCheck = findViewById(R.id.termConditionsCheck);
        videoVerificationwithLatLong = findViewById(R.id.videoVerificationwithLatLong);
        tradeBusinessProof = findViewById(R.id.tradeBusinessProof);
        shopImageTrueOrFalse = findViewById(R.id.shopImageTrueOrFalse);
        //_____________________________________________________________________
        et_firstname = findViewById(R.id.et_firstname);
        et_lastname = findViewById(R.id.et_lastname);
        et_bankName = findViewById(R.id.et_bankName);
        et_accountNo = findViewById(R.id.et_accountNo);
        et_ifscCode = findViewById(R.id.et_ifscCode);

        cbDifferentAddress = findViewById(R.id.cbDifferentAddress);

    }

}