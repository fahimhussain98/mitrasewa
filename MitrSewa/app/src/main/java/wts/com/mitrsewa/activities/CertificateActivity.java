package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import wts.com.mitrsewa.R;

public class CertificateActivity extends AppCompatActivity {

    String userName,firmName,emailId,address,dateOfBirth;
    SharedPreferences sharedPreferences;
    TextView tvCertificateNo,tvFirmName,tvOwnerName,tvIssueDate,tvEmailId,tvAddress;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        tvCertificateNo=findViewById(R.id.tv_certificate_no);
        tvFirmName=findViewById(R.id.tv_firm_name);
        tvOwnerName=findViewById(R.id.tv_owner_name);
        tvIssueDate=findViewById(R.id.tv_issue_date);
        tvEmailId=findViewById(R.id.tv_email_id);
        tvAddress=findViewById(R.id.tv_address);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(CertificateActivity.this);

        userName=sharedPreferences.getString("username","");
        firmName=sharedPreferences.getString("firmName","");
        emailId=sharedPreferences.getString("email","");
        address=sharedPreferences.getString("address","");
        dateOfBirth=sharedPreferences.getString("dob","");


        tvCertificateNo.setText("Certificate No: "+userName);
        tvFirmName.setText(firmName);
        tvOwnerName.setText(userName);
        tvEmailId.setText(emailId);
        tvAddress.setText(address);
        tvIssueDate.setText(dateOfBirth);

    }
}