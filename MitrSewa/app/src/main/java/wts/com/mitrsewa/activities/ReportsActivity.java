package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import wts.com.mitrsewa.R;

public class ReportsActivity extends AppCompatActivity {

    ConstraintLayout rechargeReportsLayout,aepsReportsLayout,aepsLedgerLayout,payoutReportLayout,ledgerReportLayout,moneyTransferReportLayout
            ,creditReportLayout,debitReportLayout,matmReportLayout,cmsReportLayout,bbpsReportActivity,commissionLedgerLayout,dmtRefundReport,complaintReportLayout,
    upiReportLayout,expressDmtReport,walletPayReportLayout,pendingDmtReportLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        initViews();

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ReportsActivity.this, R.color.purple1));
        //////CHANGE COLOR OF STATUS BAR


        rechargeReportsLayout.setOnClickListener(view ->
                {
                    startActivity(new Intent(ReportsActivity.this,AllReportsActivity.class));
                }
                 );

        creditReportLayout.setOnClickListener(v->
        {
            Intent intent=new Intent(ReportsActivity.this,CreditDebitReportActivity.class);
            intent.putExtra("title","Credit Report");
            startActivity(intent);
        });

        debitReportLayout.setOnClickListener(v->
        {
            Intent intent=new Intent(ReportsActivity.this,CreditDebitReportActivity.class);
            intent.putExtra("title","Debit Report");
            startActivity(intent);
        });

        aepsReportsLayout.setOnClickListener(view -> startActivity(new Intent(ReportsActivity.this,AepsReportActivity.class)));

        aepsLedgerLayout.setOnClickListener(view -> {
            Intent intent=new Intent(ReportsActivity.this,LedgerReportActivity.class);
            intent.putExtra("title","Aeps Ledger Report");
            startActivity(intent);
        });

        commissionLedgerLayout.setOnClickListener(view -> {
            Intent intent=new Intent(ReportsActivity.this,LedgerReportActivity.class);
            intent.putExtra("title","Commission Ledger Report");
            startActivity(intent);
        });

        payoutReportLayout.setOnClickListener(view -> startActivity(new Intent(ReportsActivity.this,SettlementReportActivity.class)));

        ledgerReportLayout.setOnClickListener(view -> {
            Intent intent=new Intent(ReportsActivity.this,LedgerReportActivity.class);
            intent.putExtra("title","Ledger Report");
            startActivity(intent);
        });

        moneyTransferReportLayout.setOnClickListener(view -> {
            Intent intent=new Intent(ReportsActivity.this,DmtReportActivity.class);
            startActivity(intent);
        });

        matmReportLayout.setOnClickListener(view -> {
            startActivity(new Intent(ReportsActivity.this,MatmReportActivity.class));
        });

        cmsReportLayout.setOnClickListener(view ->
        {
            startActivity(new Intent(ReportsActivity.this,CmsReportActivity.class));
        });

        bbpsReportActivity.setOnClickListener(v->
        {
            startActivity(new Intent(ReportsActivity.this,BbpsReportActivity.class));
        });

        dmtRefundReport.setOnClickListener(v->
        {
            startActivity(new Intent(ReportsActivity.this,DmtRefundReportActivity.class));
        });

        complaintReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(ReportsActivity.this,ComplaintReportActivity.class));
        });

        upiReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(ReportsActivity.this,UpiReportActivity.class));
        });

        expressDmtReport.setOnClickListener(v->
        {
            Intent intent=new Intent(ReportsActivity.this,ExpressDmtReportActivity.class);
            startActivity(intent);
        });

        walletPayReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(ReportsActivity.this,WalletToWalletReportActivity.class));
        });

        pendingDmtReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(ReportsActivity.this,PendingDmtReportActivity.class));
        });

    }

    private void initViews() {
        rechargeReportsLayout=findViewById(R.id.recharge_reports_layout);
        aepsReportsLayout=findViewById(R.id.aeps_reports_layout);
        aepsLedgerLayout=findViewById(R.id.aeps_ledger_layout);
        payoutReportLayout=findViewById(R.id.settlement_report_layout);
        ledgerReportLayout=findViewById(R.id.ledger_report_layout);
        moneyTransferReportLayout=findViewById(R.id.money_report_layout);
        creditReportLayout=findViewById(R.id.credit_report_layout);
        debitReportLayout=findViewById(R.id.debit_report_layout);
        matmReportLayout=findViewById(R.id.matm_report_layout);
        cmsReportLayout=findViewById(R.id.cms_report_layout);
        bbpsReportActivity=findViewById(R.id.bbps_report_layout);
        commissionLedgerLayout=findViewById(R.id.commission_ledger_layout);
        dmtRefundReport=findViewById(R.id.money_report_refund_layout);
        complaintReportLayout=findViewById(R.id.complaint_layout);
        upiReportLayout=findViewById(R.id.upi_report_layout);
        expressDmtReport=findViewById(R.id.express_dmt_report_layout);
        walletPayReportLayout=findViewById(R.id.wallet_pay_report_layout);
        pendingDmtReportLayout=findViewById(R.id.money_report_pending_layout);
    }
}