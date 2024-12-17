package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.adapters.DMTTransactionAdapter;
import wts.com.mitrsewa.adapters.MoneyReportAdapter;
import wts.com.mitrsewa.adapters.RecipientAdapter;
import wts.com.mitrsewa.models.MoneyReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class ShareDmtReportActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button btnShare,btnRaiseComplaint;
    int FILE_PERMISSION = 45;
    ImageView imgClose;
    TextView tvShopDetails;
    TextView tvAccountNumber, tvBeniname, ttvBankName, tvIfsc, tvDateTime;
    String accountNumber, beniName, bankName, ifsc, dateTime;
    RecyclerView recyclerView;
    boolean isMoneyReport;

    ArrayList<MoneyReportModel> moneyReportModelArrayList1=new ArrayList<>();
    String userId,deviceId,deviceInfo;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_dmt_report);

        inhitViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShareDmtReportActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileno", null);
        String role = sharedPreferences.getString("usertype", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        tvShopDetails.setText("Name " + ownerName + "(" + role + ")" + "\n" + "Contact No. " + mobile);

        accountNumber = getIntent().getStringExtra("accountNumber");
        beniName = getIntent().getStringExtra("beniName");
        bankName = getIntent().getStringExtra("bank");
        ifsc = getIntent().getStringExtra("ifsc");
        dateTime = getIntent().getStringExtra("date");
        isMoneyReport = getIntent().getBooleanExtra("isMoneyReport",false);

        tvAccountNumber.setText(accountNumber);
        tvBeniname.setText(beniName);
        ttvBankName.setText(bankName);
        tvIfsc.setText(ifsc);
        tvDateTime.setText(dateTime);

        if (isMoneyReport) {
            moneyReportModelArrayList1 = MoneyReportAdapter.moneyReportModelArrayList1;

        }
        else {
            moneyReportModelArrayList1 = RecipientAdapter.moneyReportModelArrayList;
            btnRaiseComplaint.setVisibility(View.GONE);
        }


        DMTTransactionAdapter dmtTransactionAdapter = new DMTTransactionAdapter(moneyReportModelArrayList1, ShareDmtReportActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShareDmtReportActivity.this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(dmtTransactionAdapter);

        imgClose.setOnClickListener(v -> finish());

        btnShare.setOnClickListener(v -> checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION));

        btnRaiseComplaint.setOnClickListener(v->
        {
            final android.app.AlertDialog complaintDialog = new android.app.AlertDialog.Builder(ShareDmtReportActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.complaint_dialog, null);
            complaintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            complaintDialog.setView(convertView);
            complaintDialog.setCancelable(false);
            complaintDialog.show();

            ImageView imgClose=convertView.findViewById(R.id.img_close);
            EditText etRemarks=convertView.findViewById(R.id.et_remarks);
            AppCompatButton btnMakeComplaint=convertView.findViewById(R.id.btn_make_complaint);

            imgClose.setOnClickListener(v1->
            {
                complaintDialog.dismiss();
            });

            btnMakeComplaint.setOnClickListener(v1->
            {
                if (!TextUtils.isEmpty(etRemarks.getText()))
                {
                    String remarks=etRemarks.getText().toString().trim();
                    raiseComplaint(remarks);
                    complaintDialog.dismiss();
                }
                else
                    etRemarks.setError("Required");
            });
        });
    }

    private void raiseComplaint(String remarks) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ShareDmtReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String uniqueId=moneyReportModelArrayList1.get(0).getTransactionId();
        String liveId=moneyReportModelArrayList1.get(0).getBankRRN();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().makeComplaint(AUTH_KEY,userId,deviceId,deviceInfo,uniqueId,remarks,
                "NA",liveId,"Money");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(ShareDmtReportActivity.this)
                                .setMessage(message)
                                .setTitle("Complain Status")
                                .setPositiveButton("ok",null)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Toast.makeText(ShareDmtReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    Toast.makeText(ShareDmtReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(ShareDmtReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ShareDmtReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(ShareDmtReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(ShareDmtReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            btnShare.setVisibility(View.GONE);
            Bitmap bitmap = getScreenBitmap();
            shareReceipt(bitmap);

        }
    }

    public Bitmap getScreenBitmap() {
        Bitmap b = null;
        try {
            ScrollView shareReportLayout = findViewById(R.id.share_report_layout);
            Bitmap bitmap = Bitmap.createBitmap(shareReportLayout.getWidth(),
                    shareReportLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareReportLayout.draw(canvas);
            btnShare.setVisibility(View.VISIBLE);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnShare.setVisibility(View.VISIBLE);
        return b;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title"+System.currentTimeMillis(), null);
            Uri imageUri = Uri.parse(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(share, "Share link!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FILE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareDmtReportActivity.this);
                permissionDialog.setTitle("Permission Required");
                permissionDialog.setMessage("You can set permission manually." + "\n" + "Settings-> App Permission -> Allow Storage permission.");
                permissionDialog.setCancelable(false);
                permissionDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                permissionDialog.show();

            }
        }
    }

    private void inhitViews() {

        tvShopDetails = findViewById(R.id.tv_shop_details);
        btnShare = findViewById(R.id.btn_share);
        btnRaiseComplaint = findViewById(R.id.btn_raise_complaint);
        imgClose = findViewById(R.id.img_close);
        tvAccountNumber = findViewById(R.id.tv_account_number);
        tvBeniname = findViewById(R.id.tv_beni_name);
        ttvBankName = findViewById(R.id.tv_bank_name);
        tvIfsc = findViewById(R.id.tv_ifsc);
        tvDateTime = findViewById(R.id.tv_all_report_date_time);
        recyclerView = findViewById(R.id.mini_statement_recycler);

    }
}