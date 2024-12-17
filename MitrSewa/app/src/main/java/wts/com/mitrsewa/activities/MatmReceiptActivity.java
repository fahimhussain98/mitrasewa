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

public class MatmReceiptActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView tvShopDetails;
    AppCompatButton btnShare,btnRaiseComplaint;
    int FILE_PERMISSION = 45;
    ImageView imgClose;

    TextView tvCardNumber, tvCardType, tvAmount, tvMessage, tvBankBalance, tvBankRRN, tvType, tvBank, tvuniqueTransactionId, tvStatus, tvDate, tvTime,
    tvOpeningBalance,tvClosingBalance;
    String cardNumber, cardType, amount, message, bankBalance, bankRRN, transactionType, bank, uniqueTransactionId, status, date, time,
    openingBalance,closingBalance;
    boolean isMatmReport;
    LinearLayout messageContainer, bankBalanceContainer, cardTypeContainer, dateContainer, timeContainer, amountContainer,openingBalanceContainer,
    closingBalanceContainer;
    String userId,deviceId,deviceInfo;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_matm_receipt);

        inhitViews();

        cardNumber = getIntent().getStringExtra("cardNumber");
        cardType = getIntent().getStringExtra("cardType");
        amount = getIntent().getStringExtra("amount");
        openingBalance = getIntent().getStringExtra("openingBalance");
        closingBalance = getIntent().getStringExtra("closingBalance");
        message = getIntent().getStringExtra("message");
        bankBalance = getIntent().getStringExtra("bankBalance");
        bankRRN = getIntent().getStringExtra("bankRRN");
        transactionType = getIntent().getStringExtra("transactionType");
        bank = getIntent().getStringExtra("bank");
        uniqueTransactionId = getIntent().getStringExtra("uniqueTransactionId");
        status = getIntent().getStringExtra("status");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        isMatmReport = getIntent().getBooleanExtra("isMatmReport", false);

        if (isMatmReport) {
            bankBalanceContainer.setVisibility(View.GONE);
            messageContainer.setVisibility(View.GONE);
            cardTypeContainer.setVisibility(View.GONE);

        } else {
            dateContainer.setVisibility(View.GONE);
            timeContainer.setVisibility(View.GONE);
            openingBalanceContainer.setVisibility(View.GONE);
            closingBalanceContainer.setVisibility(View.GONE);
            btnRaiseComplaint.setVisibility(View.GONE);
        }

        if (!(transactionType.equalsIgnoreCase("cw") || transactionType.equalsIgnoreCase("Cash Withdraw") ||
                transactionType.equalsIgnoreCase("cd") || transactionType.equalsIgnoreCase("Cash Deposit"))) {
            amountContainer.setVisibility(View.GONE);
            openingBalanceContainer.setVisibility(View.GONE);
            closingBalanceContainer.setVisibility(View.GONE);
        }

        tvCardNumber.setText(cardNumber);
        tvCardType.setText(cardType);
        tvAmount.setText("₹ " + amount);
        tvOpeningBalance.setText("₹ " + openingBalance);
        tvClosingBalance.setText("₹ " + closingBalance);
        tvMessage.setText(message);
        tvBankBalance.setText("₹ " + bankBalance);
        tvBankRRN.setText(bankRRN);
        tvType.setText(transactionType);
        tvBank.setText(bank);
        tvuniqueTransactionId.setText(uniqueTransactionId);
        tvDate.setText(date);
        tvTime.setText(time);
        tvStatus.setText(status);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MatmReceiptActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileno", null);
        String role = sharedPreferences.getString("usertype", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        tvShopDetails.setText("Name  :  " + ownerName + "(" + role + ")" + "\n" + "Contact No.  :  " + mobile);

        if (!(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful"))) {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back2));
        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

            }
        });

        btnRaiseComplaint.setOnClickListener(v->
        {
            final android.app.AlertDialog complaintDialog = new android.app.AlertDialog.Builder(MatmReceiptActivity.this).create();
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
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(MatmReceiptActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().makeComplaint(AUTH_KEY,userId,deviceId,deviceInfo,uniqueTransactionId,remarks,
                "NA",bankRRN,"MATM");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(MatmReceiptActivity.this)
                                .setMessage(message)
                                .setTitle("Complain Status")
                                .setPositiveButton("ok",null)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Toast.makeText(MatmReceiptActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    Toast.makeText(MatmReceiptActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(MatmReceiptActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MatmReceiptActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(MatmReceiptActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MatmReceiptActivity.this, new String[]{writePermission, readPermission}, requestCode);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(MatmReceiptActivity.this);
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
        imgClose = findViewById(R.id.img_close);

        tvCardNumber = findViewById(R.id.tv_card_number);
        tvCardType = findViewById(R.id.tv_card_type);
        tvAmount = findViewById(R.id.tv_all_report_amount);
        tvOpeningBalance = findViewById(R.id.tv_opening_balance);
        tvClosingBalance = findViewById(R.id.tv_closing_balance);
        tvMessage = findViewById(R.id.tv_message);
        tvBankBalance = findViewById(R.id.tv_bank_balance);
        tvBankRRN = findViewById(R.id.tv_bank_rrn);
        tvType = findViewById(R.id.tv_transaction_type);
        tvBank = findViewById(R.id.tv_bank_name);
        tvuniqueTransactionId = findViewById(R.id.tv_unique_id);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvStatus = findViewById(R.id.tv_status);

        messageContainer = findViewById(R.id.message_container);
        bankBalanceContainer = findViewById(R.id.bank_balance_container);
        cardTypeContainer = findViewById(R.id.card_type_container);
        dateContainer = findViewById(R.id.date_container);
        timeContainer = findViewById(R.id.time_container);
        amountContainer = findViewById(R.id.amount_container);
        openingBalanceContainer = findViewById(R.id.opening_balance_container);
        closingBalanceContainer = findViewById(R.id.closing_balance_container);

        btnShare = findViewById(R.id.btn_share);
        btnRaiseComplaint = findViewById(R.id.btn_raise_complaint);



    }

}