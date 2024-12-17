package wts.com.mitrsewa.adapters;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import wts.com.mitrsewa.models.MoneyReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class PendingDmtReportAdapter extends RecyclerView.Adapter<PendingDmtReportAdapter.Viewholder> {

    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    Context context;
    Activity activity;
    String userId, deviceId, deviceInfo;

    public PendingDmtReportAdapter(ArrayList<MoneyReportModel> moneyReportModelArrayList, Context context,
                                   Activity activity, String userId, String deviceId, String deviceInfo) {
        this.moneyReportModelArrayList = moneyReportModelArrayList;
        this.context = context;
        this.activity = activity;
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_dmt_report, parent, false);
        return new PendingDmtReportAdapter.Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
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

        holder.btnCheckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uniqueId = moneyReportModelArrayList.get(position).getTransactionId();
                String commission = moneyReportModelArrayList.get(position).getCommission();
                String tds = moneyReportModelArrayList.get(position).getTds();
                String surcharge = moneyReportModelArrayList.get(position).getSurcharge();
                String gst = moneyReportModelArrayList.get(position).getGst();
                String amount = moneyReportModelArrayList.get(position).getAmount();

                checkStatus(uniqueId, commission, tds, surcharge, gst, amount);

            }
        });

    }

    private void checkStatus(String uniqueId, String commission, String tds, String surcharge, String gst, String amount) {
        final AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().pendingDmtCheckStatus(AUTH_KEY, userId, deviceId, deviceInfo,
                uniqueId, commission, tds, surcharge, gst, amount);
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
                                .setPositiveButton("OK", (dialogInterface, i) -> activity.finish())
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

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvAccountNumber, tvBeniName, tvBank, tvIfsc, tvCost, tvBalance, tvDate, tvTransactionId, tvStatus;
        Button btnCheckStatus;

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
            btnCheckStatus = itemView.findViewById(R.id.btn_check_status);
        }
    }
}
