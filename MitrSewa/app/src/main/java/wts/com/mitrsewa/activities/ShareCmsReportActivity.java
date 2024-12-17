package wts.com.mitrsewa.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import wts.com.mitrsewa.R;

public class ShareCmsReportActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView tvTxnId, tvAgent, tvBiller, tvuserId, tvUserName, tvAmount, tvCommission, tvCost, tvBalance, tvDateTime, tvStatus, tvShopDetails,tvTxnStatus,tvTime;
    AppCompatButton btnShare;
    int FILE_PERMISSION = 45;
    ImageView imgClose;

    String userId,deviceId,deviceInfo;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_cms_report);

        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShareCmsReportActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileno", null);
        String role = sharedPreferences.getString("usertype", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        String txnId = getIntent().getStringExtra("txnId");
        String agent = getIntent().getStringExtra("agent");
        String biller = getIntent().getStringExtra("biller");
        String userId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
       String amount = getIntent().getStringExtra("amount");
       String commission = getIntent().getStringExtra("commission");
      String  cost = getIntent().getStringExtra("cost");
       String balance = getIntent().getStringExtra("closingBalance");
       String date = getIntent().getStringExtra("date");
       String status = getIntent().getStringExtra("status");
       String time = getIntent().getStringExtra("time");

       tvTxnId.setText(txnId);
       tvAgent.setText(agent);
       tvBiller.setText(biller);
       tvuserId.setText(userId);
       tvUserName.setText(userName);

        tvAmount.setText("\u20b9 "+amount);
        tvCommission.setText(commission);
        tvCost.setText(cost);
        tvBalance.setText("\u20b9 "+balance);
        tvDateTime.setText(date);
        tvStatus.setText(status);
        tvTxnStatus.setText(status);
        tvTime.setText(time);

        if(!(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful")))
        {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back2));
        }

        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful"))
        {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back_green));
        } else if (status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("failure")) {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back2));
        }
        else
        {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.yellow_back));
        }


        tvShopDetails.setText( ownerName + "(" + role + ")" + "\n" +  mobile);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES, FILE_PERMISSION);

            }
        });


    }

    public void checkPermission(String writePermission, String readPermission,String mediaFilePermission, int requestCode) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {
            if (ContextCompat.checkSelfPermission(ShareCmsReportActivity.this, mediaFilePermission) == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(ShareCmsReportActivity.this, new String[]{mediaFilePermission}, requestCode);
            }
            else {

                Bitmap bitmap = getScreenBitmap();
                shareReceipt(bitmap);
            }
        }
        else
        {
            if (ContextCompat.checkSelfPermission(ShareCmsReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(ShareCmsReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
                // Requesting the permission
                ActivityCompat.requestPermissions(ShareCmsReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
            } else {

                Bitmap bitmap = getScreenBitmap();
                shareReceipt(bitmap);
            }
        }

    }

    public Bitmap getScreenBitmap() {
        Bitmap b = null;
        try {
            ScrollView shareReportLayout = findViewById(R.id.scrollView);

            Bitmap bitmap = Bitmap.createBitmap(shareReportLayout.getWidth(), shareReportLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareReportLayout.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title" + System.currentTimeMillis(), null);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FILE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareCmsReportActivity.this);
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


    private void initViews() {
        tvTxnId = findViewById(R.id.tv_txnId);
        tvAgent = findViewById(R.id.tv_agent);
        tvBiller = findViewById(R.id.tv_biller);
        tvuserId = findViewById(R.id.tv_userId);
        tvUserName = findViewById(R.id.tv_userName);
        tvAmount = findViewById(R.id.tv_all_report_amount);
        tvCommission = findViewById(R.id.tv_all_report_commission);
        tvCost = findViewById(R.id.tv_all_report_cost);
        tvBalance = findViewById(R.id.tv_all_report_balance);
        tvDateTime = findViewById(R.id.tv_all_report_date_time);
        tvTime = findViewById(R.id.tv_time);
        tvStatus = findViewById(R.id.tv_status);

        tvShopDetails = findViewById(R.id.tv_shop_details);
        btnShare = findViewById(R.id.btn_share);
        imgClose = findViewById(R.id.img_close);
        tvTxnStatus = findViewById(R.id.tv_txn_status);
    }

}