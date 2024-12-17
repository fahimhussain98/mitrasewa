package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import wts.com.mitrsewa.R;

public class ProfileActivity extends AppCompatActivity {


    TextView tvUserId, tvOwnername, tvUserName, tvRole, tvMobileNUmber, tvPanCard, tvAdharCard;
    String  ownerName, userName, role, mobileNumber, panCard, aadharCard, outletId;
    SharedPreferences sharedPreferences;

    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        inhitViews();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);

        ownerName = sharedPreferences.getString("username", null);
        userName = sharedPreferences.getString("username", null);
        role = sharedPreferences.getString("usertype", null);
        mobileNumber = sharedPreferences.getString("mobileno", null);
        panCard = sharedPreferences.getString("pancard", null);
        aadharCard = sharedPreferences.getString("adharcard", null);

        setViews();

    }
    private void setViews() {
        tvUserId.setText("*******");
        tvOwnername.setText(ownerName);
        tvUserName.setText(userName);
        tvRole.setText(role);
        tvMobileNUmber.setText(mobileNumber);
        tvPanCard.setText(panCard);
        tvAdharCard.setText(aadharCard);
    }

    private void inhitViews() {
        tvUserId =findViewById(R.id.tv_user_id);
        tvOwnername = findViewById(R.id.tv_owner_name);
        tvUserName = findViewById(R.id.tv_user_name);
        tvRole = findViewById(R.id.tv_role);
        tvMobileNUmber = findViewById(R.id.tv_mobile_number);
        tvPanCard = findViewById(R.id.tv_pan_card);
        tvAdharCard = findViewById(R.id.tv_adhar_card);
        imgBack=findViewById(R.id.img_back);
    }

}