package wts.com.mitrsewa.adapters;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import wts.com.mitrsewa.activities.ExpressDmtReportActivity;
import wts.com.mitrsewa.activities.ShareDmtReportActivity;
import wts.com.mitrsewa.activities.ShareExpressDmtActivity;
import wts.com.mitrsewa.models.MoneyReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class ExpressDmtReportAdapter extends RecyclerView.Adapter<ExpressDmtReportAdapter.Viewholder> {

    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    Context context;
    Activity activity;
    boolean isRefund;
    String userId, deviceId, deviceInfo;

    public ExpressDmtReportAdapter(ArrayList<MoneyReportModel> moneyReportModelArrayList, Context context,
                                   Activity activity, boolean isRefund, String userId, String deviceId, String deviceInfo) {
        this.moneyReportModelArrayList = moneyReportModelArrayList;
        this.context = context;
        this.activity = activity;
        this.isRefund = isRefund;
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_money_report_layout, parent, false);
        return new ExpressDmtReportAdapter.Viewholder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String amount = moneyReportModelArrayList.get(position).getAmount();
        final String accountNumber = moneyReportModelArrayList.get(position).getAccountNumber();
        final String beniName = moneyReportModelArrayList.get(position).getBeniName();
        final String bank = moneyReportModelArrayList.get(position).getBank();
        final String ifsc = moneyReportModelArrayList.get(position).getIfsc();
        String cost = moneyReportModelArrayList.get(position).getCost();
        String balance = moneyReportModelArrayList.get(position).getBalance();
        String date = moneyReportModelArrayList.get(position).getDate();
        final String transactionId = moneyReportModelArrayList.get(position).getTransactionId();
        String status = moneyReportModelArrayList.get(position).getStatus();


        if (status.equalsIgnoreCase("SUCCESS") || status.equalsIgnoreCase("Successful")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back_green));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        } else if (status.equalsIgnoreCase("pending")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.yellow_back));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        } else if (status.equalsIgnoreCase("failure") || status.equalsIgnoreCase("Failed")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.tvAmount.setText("₹ " + amount);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvBeniName.setText(beniName);
        holder.tvBank.setText(bank);
        holder.tvIfsc.setText(ifsc);
        holder.tvCost.setText("₹ " + cost);
        holder.tvBalance.setText("₹ " + balance);
        holder.tvDate.setText(date);
        holder.tvTransactionId.setText(transactionId);
        holder.tvStatus.setText(status);

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String transactionId = moneyReportModelArrayList.get(position).getTransactionId();
                String responseAmount = moneyReportModelArrayList.get(position).getAmount();
                String status = moneyReportModelArrayList.get(position).getStatus();
                String bankRefId = moneyReportModelArrayList.get(position).getBankRRN();
                String comm = moneyReportModelArrayList.get(position).getCommission();
                String surcharge = moneyReportModelArrayList.get(position).getSurcharge();

                MoneyReportModel moneyReportModel=new MoneyReportModel();

                moneyReportModel.setTransactionId(transactionId);
                moneyReportModel.setAmount(responseAmount);
                moneyReportModel.setStatus(status);
                moneyReportModel.setBankRRN(bankRefId);
                moneyReportModel.setCommission(comm);
                moneyReportModel.setSurcharge(surcharge);

                Intent intent = new Intent(context, ShareExpressDmtActivity.class);
                intent.putExtra("accountNumber", moneyReportModelArrayList.get(position).getAccountNumber());
                intent.putExtra("beniName", moneyReportModelArrayList.get(position).getBeniName());
                intent.putExtra("bank", moneyReportModelArrayList.get(position).getBank());
                intent.putExtra("ifsc", moneyReportModelArrayList.get(position).getIfsc());
                intent.putExtra("date", moneyReportModelArrayList.get(position).getDate());
                intent.putExtra("transactionId", moneyReportModelArrayList.get(position).getTransactionId());
                intent.putExtra("responseAmount", moneyReportModelArrayList.get(position).getAmount());
                intent.putExtra("status", moneyReportModelArrayList.get(position).getStatus());
                intent.putExtra("comm", moneyReportModelArrayList.get(position).getCommission());
                intent.putExtra("surcharge", moneyReportModelArrayList.get(position).getSurcharge() );
                intent.putExtra("uniqueId", moneyReportModelArrayList.get(position).getUniqueId());
                intent.putExtra("bankRefId", moneyReportModelArrayList.get(position).getBankRRN());
                intent.putExtra("isMoneyReport", true);
                context.startActivity(intent);
            }
        });


        if (!status.equalsIgnoreCase("Pending"))
        {
            holder.btnCheckStatus.setVisibility(View.GONE);
        }
        holder.btnCheckStatus.setOnClickListener(v->
        {
            String orderId=moneyReportModelArrayList.get(position).getUniqueId();
            String dateTime=moneyReportModelArrayList.get(position).getDate();
            checkStatus(orderId,dateTime);
        });


    }

    private void checkStatus(String orderId, String dateTime) {
        final AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().expressDmtCheckStatus(AUTH_KEY,userId,deviceId,deviceInfo,orderId,dateTime,
                "IMPS");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");

                        new androidx.appcompat.app.AlertDialog.Builder(context)
                                .setMessage(message)
                                .setTitle("Message")
                                .setPositiveButton("ok",null)
                                .show();
                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                }
                else
                {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return moneyReportModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        TextView tvAmount, tvAccountNumber, tvBeniName, tvBank, tvIfsc, tvCost, tvBalance, tvDate, tvTransactionId, tvStatus;
        ImageView imgShare;
        Button btnRefund,btnCheckStatus;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
            tvBeniName = itemView.findViewById(R.id.tv_beni_name);
            tvBank = itemView.findViewById(R.id.tv_bank_name);
            tvIfsc = itemView.findViewById(R.id.tv_ifsc);
            tvCost = itemView.findViewById(R.id.tv_all_report_cost);
            tvBalance = itemView.findViewById(R.id.tv_all_report_balance);
            tvDate = itemView.findViewById(R.id.tv_all_report_date_time);
            tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
            tvStatus = itemView.findViewById(R.id.tv_all_report_status);
            imgShare = itemView.findViewById(R.id.img_share);
            btnRefund = itemView.findViewById(R.id.btn_refund);
            btnCheckStatus = itemView.findViewById(R.id.btn_check_status);

        }
    }
}
