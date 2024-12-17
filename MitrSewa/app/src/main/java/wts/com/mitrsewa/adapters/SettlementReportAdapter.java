package wts.com.mitrsewa.adapters;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.SharePayoutReportActivity;
import wts.com.mitrsewa.models.SettlementReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class SettlementReportAdapter extends RecyclerView.Adapter<SettlementReportAdapter.ViewHolder> {

    ArrayList<SettlementReportModel> settlementReportModelArrayList;
    boolean isInitialReport;
    Context context;
    Activity activity;
    String userId, deviceId, deviceInfo;

    public SettlementReportAdapter(ArrayList<SettlementReportModel> settlementReportModelArrayList, boolean isInitialReport, Context context,
                                   Activity activity, String userId, String deviceId, String deviceInfo) {
        this.settlementReportModelArrayList = settlementReportModelArrayList;
        this.isInitialReport = isInitialReport;
        this.context = context;
        this.activity = activity;
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_settlement_report, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = settlementReportModelArrayList.get(position).getName();
        String surcharge = settlementReportModelArrayList.get(position).getSurcharge();
        String amount = settlementReportModelArrayList.get(position).getAmount();
        String paymentType = settlementReportModelArrayList.get(position).getPaymentType();
        String reqDate = settlementReportModelArrayList.get(position).getReqDate();
        String accountNumber = settlementReportModelArrayList.get(position).getAccountNo();
        String status = settlementReportModelArrayList.get(position).getStatus();


        holder.tvReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SharePayoutReportActivity.class);
                intent.putExtra("transactionId", settlementReportModelArrayList.get(position).getTransactionId());
                intent.putExtra("amount", settlementReportModelArrayList.get(position).getAmount());
                intent.putExtra("comm", settlementReportModelArrayList.get(position).getComm());
                intent.putExtra("balance", settlementReportModelArrayList.get(position).getNewBalance());
                intent.putExtra("dateTime", settlementReportModelArrayList.get(position).getReqDate());
                intent.putExtra("status", settlementReportModelArrayList.get(position).getStatus());
                intent.putExtra("transactionType", settlementReportModelArrayList.get(position).getPaymentType());
                intent.putExtra("oldBalance", settlementReportModelArrayList.get(position).getOldBalance());
                intent.putExtra("accountName", settlementReportModelArrayList.get(position).getName());
                intent.putExtra("accountNo", settlementReportModelArrayList.get(position).getAccountNo());
                intent.putExtra("bankName", settlementReportModelArrayList.get(position).getBankName());
                intent.putExtra("surcharge", settlementReportModelArrayList.get(position).getSurcharge());
                intent.putExtra("status", settlementReportModelArrayList.get(position).getStatus());
                intent.putExtra("banRRN", settlementReportModelArrayList.get(position).getBankRrn());
                intent.putExtra("isPayoutReport", true);
                intent.putExtra("serviceType", "payout");
                context.startActivity(intent);
            }
        });


        if (status.equalsIgnoreCase("FAILED")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back2));
        } else if (status.equalsIgnoreCase("PENDING")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back));
            holder.tvCheckStatus.setVisibility(View.VISIBLE);

            holder.tvCheckStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String orderId = settlementReportModelArrayList.get(position).getTransactionId();
                    String txnDate = settlementReportModelArrayList.get(position).getReqDate();
                    String txnType = settlementReportModelArrayList.get(position).getPaymentType();
                    String apiName = settlementReportModelArrayList.get(position).getApiName();

                    doCheckStatus(orderId, txnDate, txnType, apiName);
                }
            });

        } else {
            holder.tvCheckStatus.setVisibility(View.GONE);
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back1));
        }




        holder.tvName.setText(name);
        holder.tvSurcharge.setText("₹ " + surcharge);
        holder.tvAmount.setText("₹ " + amount);
        holder.tvPaymentType.setText(paymentType);
        holder.tvReqDate.setText(reqDate);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvStatus.setText(status);


    }

    private void doCheckStatus(String orderId, String txnDate, String txnType, String apiName) {
        final AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doSettlementCheckStatus(AUTH_KEY, userId, deviceId, deviceInfo, orderId, txnDate, txnType,apiName);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        pDialog.dismiss();

                        String message = responseObject.getString("data");
                        new androidx.appcompat.app.AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        activity.finish();
                                    }
                                })
                                .show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(context)
                                .setMessage("Please try after sometime")
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setMessage("Please try after sometime")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setMessage("Please try after sometime\n" + t.getMessage())
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return settlementReportModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSurcharge, tvAmount, tvPaymentType, tvReqDate, tvAccountNumber, tvStatus, tvReceipt, tvCheckStatus;
        ImageView imgReceipt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSurcharge = itemView.findViewById(R.id.tv_surcharge);
            tvAmount = itemView.findViewById(R.id.tv_all_report_amount);
            tvPaymentType = itemView.findViewById(R.id.tv_payment_type);
            tvReqDate = itemView.findViewById(R.id.tv_all_report_date_time);
            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
            tvStatus = itemView.findViewById(R.id.tv_all_report_status);
            imgReceipt = itemView.findViewById(R.id.img_receipt);
            tvReceipt = itemView.findViewById(R.id.tv_receipt);
            tvCheckStatus = itemView.findViewById(R.id.tv_check_status);
        }
    }
}
