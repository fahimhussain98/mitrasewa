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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class SharePayoutReportActivity extends AppCompatActivity {

    TextView tvTransactionId, tvTransactionType, tvOldBalance, tvNewBalance, tvAccountName, tvAccountNo, tvBankName, tvCommission, tvSurcharge, tvAmount,
            tvShopDetails, tvTransactionAmount, tvStatus;
    AppCompatButton btnShare,btnRaiseComplaint;
    String transactionId, transactionType, oldBalance, newBalance, name, accountNo, bankName, commission, surcharge, amount, cost, status,bankRRN;
    SharedPreferences sharedPreferences;
    ImageView imgClose;
    int FILE_PERMISSION = 45;

    String serviceType;
    LinearLayout transactionTypeContainer, oldBalanceContainer, transactionAmountContainer;

    String userId,deviceId,deviceInfo;
    boolean isSettlementReport;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_payout_report);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SharePayoutReportActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileno", null);
        String role = sharedPreferences.getString("usertype", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        tvShopDetails.setText("Name  :  " + ownerName + "(" + role + ")" + "\n" + "Contact No.  :  " + mobile);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceType = getIntent().getStringExtra("serviceType");

        if (serviceType.equalsIgnoreCase("Money")) {
            transactionTypeContainer.setVisibility(View.GONE);
            oldBalanceContainer.setVisibility(View.GONE);
        } else {
            transactionAmountContainer.setVisibility(View.GONE);
        }
        transactionId = getIntent().getStringExtra("transactionId");
        transactionType = getIntent().getStringExtra("transactionType");
        oldBalance = getIntent().getStringExtra("oldBalance");
        newBalance = getIntent().getStringExtra("balance");
        name = getIntent().getStringExtra("accountName");
        accountNo = getIntent().getStringExtra("accountNo");
        bankName = getIntent().getStringExtra("bankName");
        commission = getIntent().getStringExtra("comm");
        surcharge = getIntent().getStringExtra("surcharge");
        amount = getIntent().getStringExtra("amount");
        cost = getIntent().getStringExtra("cost");
        bankRRN = getIntent().getStringExtra("banRRN");
        status = getIntent().getStringExtra("status");
        isSettlementReport = getIntent().getBooleanExtra("isPayoutReport",false);

        if (!isSettlementReport)
            btnRaiseComplaint.setVisibility(View.GONE);

        setViews();
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

            }
        });

        btnRaiseComplaint.setOnClickListener(v->
        {
            final android.app.AlertDialog complaintDialog = new android.app.AlertDialog.Builder(SharePayoutReportActivity.this).create();
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
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SharePayoutReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().makeComplaint(AUTH_KEY,userId,deviceId,deviceInfo,transactionId,remarks,
                "NA",bankRRN,"Settlement");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(SharePayoutReportActivity.this)
                                .setMessage(message)
                                .setTitle("Complain Status")
                                .setPositiveButton("ok",null)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Toast.makeText(SharePayoutReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    Toast.makeText(SharePayoutReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(SharePayoutReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(SharePayoutReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(SharePayoutReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(SharePayoutReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(SharePayoutReportActivity.this);
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

    @SuppressLint("SetTextI18n")
    private void setViews() {
        if (transactionType.equalsIgnoreCase("SAP")) {
            tvTransactionType.setText("Mini Statement");
        } else if (transactionType.equalsIgnoreCase("WAP")) {
            tvTransactionType.setText("Cash withdrawal");
        } else if (transactionType.equalsIgnoreCase("BAP")) {
            tvTransactionType.setText("Balance Enquiry");
        } else if (transactionType.equalsIgnoreCase("MZZ")) {
            tvTransactionType.setText("Aadhar Pay");
        } else {
            tvTransactionType.setText(transactionType);
        }
        tvTransactionId.setText(transactionId);
        tvOldBalance.setText("₹ " + oldBalance);
        tvNewBalance.setText("₹ " + newBalance);
        tvAccountName.setText(name);
        tvAccountNo.setText(accountNo);
        tvBankName.setText(bankName);
        tvCommission.setText("₹ " + commission);
        tvSurcharge.setText("₹ " + surcharge);
        if (serviceType.equalsIgnoreCase("Money")) {
            tvTransactionAmount.setText("₹ " + amount);
            tvAmount.setText("₹ " + cost);
        } else {
            tvAmount.setText("₹ " + amount);
        }

        tvStatus.setText(status);
        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful")) {
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvStatus.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void initViews() {
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvTransactionType = findViewById(R.id.tv_transaction_type);
        tvOldBalance = findViewById(R.id.tv_old_balance);
        tvNewBalance = findViewById(R.id.tv_new_balance);
        tvAccountName = findViewById(R.id.tv_account_name);
        tvAccountNo = findViewById(R.id.tv_account_no);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvCommission = findViewById(R.id.tv_all_report_commission);
        tvSurcharge = findViewById(R.id.tv_surcharge);
        tvAmount = findViewById(R.id.tv_amount);
        tvShopDetails = findViewById(R.id.tv_shop_details);
        tvTransactionAmount = findViewById(R.id.tv_transaction_amount);
        tvStatus = findViewById(R.id.tv_status);

        transactionTypeContainer = findViewById(R.id.transaction_type_container);
        oldBalanceContainer = findViewById(R.id.old_balance_container);
        transactionAmountContainer = findViewById(R.id.transaction_amount_container);

        btnShare = findViewById(R.id.btn_share);
        btnRaiseComplaint = findViewById(R.id.btn_raise_complaint);
        imgClose = findViewById(R.id.img_close);

    }
}