package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import wts.com.mitrsewa.R;

public class MoreServicesActivity extends AppCompatActivity {

    LinearLayout waterLayout,loanLayout,insuranceLayout,landlineLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_services);
        initViews();

        waterLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MoreServicesActivity.this, ElectricityActivity.class);
            intent.putExtra("service", "WATER");
            intent.putExtra("serviceId", "16");
            intent.putExtra("operatorServiceId", "16");

            startActivity(intent);
        });

        loanLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MoreServicesActivity.this, ElectricityActivity.class);
            intent.putExtra("service", "Loan Repayment");
            intent.putExtra("serviceId", "9");
            intent.putExtra("operatorServiceId", "9");

            startActivity(intent);
        });

        insuranceLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MoreServicesActivity.this, ElectricityActivity.class);
            intent.putExtra("service", "INSURANCE");
            intent.putExtra("serviceId", "13");
            intent.putExtra("operatorServiceId", "13");

            startActivity(intent);
        });

        landlineLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MoreServicesActivity.this, ElectricityActivity.class);
            intent.putExtra("service", "LANDLINE");
            intent.putExtra("serviceId", "15");
            intent.putExtra("operatorServiceId", "15");

            startActivity(intent);
        });

    }

    private void initViews() {
        waterLayout=findViewById(R.id.water_layout);
        loanLayout=findViewById(R.id.loan_layout);
        insuranceLayout=findViewById(R.id.insurance_layout);
        landlineLayout=findViewById(R.id.landline_layout);
    }
}