package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import wts.com.mitrsewa.R;

public class RechargeStatusActivity extends AppCompatActivity {

    String responseNumber, responseAmount, responseStatus, responseTransactionId, responseOperator;

    String outputDate;
    String outputTime;

    ImageView imgStatus;

    TextView tvStatus,tvAmount,tvTransactionId,tvNumber,tvDate,tvTime,tvOperator;
    Button btnDone;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_status);

        inhitViews();

        responseNumber=getIntent().getStringExtra("responseNumber");
        responseAmount=getIntent().getStringExtra("responseAmount");
        responseStatus=getIntent().getStringExtra("responseStatus");
        responseTransactionId=getIntent().getStringExtra("responseTransactionId");
        outputDate=getIntent().getStringExtra("outputDate");
        outputTime=getIntent().getStringExtra("outputTime");
        responseOperator=getIntent().getStringExtra("responseOperator");

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvStatus.setText(responseStatus);
        tvAmount.setText("₹ "+responseAmount);
        tvTransactionId.setText("Transaction Id :  "+responseTransactionId);
        tvNumber.setText("Number :  "+responseNumber);
        tvDate.setText("Date :  "+outputDate);
        tvTime.setText("Time :  "+outputTime);
        tvOperator.setText("Operator :  "+responseOperator);

        if (responseStatus.equalsIgnoreCase("FAILURE") || responseStatus.equalsIgnoreCase("FAILED"))
        {
            imgStatus.setImageResource(R.drawable.failureicon);
        }
        else if (responseStatus.equalsIgnoreCase("PENDING"))
        {
            imgStatus.setImageResource(R.drawable.pendingicon);
        }

    }

    private void inhitViews() {
        tvStatus=findViewById(R.id.tv_recharge_status);
        tvAmount=findViewById(R.id.tv_amount);
        tvTransactionId=findViewById(R.id.tv_transaction_id);
        tvNumber=findViewById(R.id.tv_mobile_number);
        tvDate=findViewById(R.id.tv_date);
        tvTime=findViewById(R.id.tv_time);
        tvOperator=findViewById(R.id.tv_operator_name);
        btnDone=findViewById(R.id.btn_done);
        imgStatus=findViewById(R.id.img_status);
    }

}