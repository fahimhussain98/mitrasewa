package wts.com.mitrsewa.adapters;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.LoginActivity;
import wts.com.mitrsewa.activities.MatmReceiptActivity;
import wts.com.mitrsewa.models.MatmReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class MatmReportAdapter extends RecyclerView.Adapter<MatmReportAdapter.Viewholder> {

    ArrayList<MatmReportModel> matmReportModelArrayList;
    boolean isInitialReport;
    Context context;
    Activity activity;
    String userId,deviceId,deviceInfo;

    public MatmReportAdapter(ArrayList<MatmReportModel> matmReportModelArrayList,
                             boolean isInitialReport, Context context, Activity activity, String userId, String deviceId, String deviceInfo) {
        this.matmReportModelArrayList = matmReportModelArrayList;
        this.isInitialReport = isInitialReport;
        this.context = context;
        this.activity = activity;
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_matm_report, parent, false);

        return new Viewholder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String cardNo = matmReportModelArrayList.get(position).getCardNumber();
        String cardType = matmReportModelArrayList.get(position).getCardType();
        String amount = matmReportModelArrayList.get(position).getAmount();
        String paymentType = matmReportModelArrayList.get(position).getTransactionType();
        String date = matmReportModelArrayList.get(position).getDate();
        String time = matmReportModelArrayList.get(position).getTime();
        String status = matmReportModelArrayList.get(position).getStatus();

        holder.tvCardNo.setText(cardNo);
        holder.tvAmount.setText("₹ " + amount);
        holder.tvPaymentType.setText(paymentType);
        holder.tvDate.setText(date);
        holder.tvTime.setText(time);
        holder.tvStatus.setText(status);

        if (status.equalsIgnoreCase("Success")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back3));
            holder.tvCheckStatus.setVisibility(View.GONE);

        } else if (status.equalsIgnoreCase("FAILED")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back2));
            holder.tvCheckStatus.setVisibility(View.GONE);

        } else {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back2));
            holder.tvCheckStatus.setVisibility(View.VISIBLE);
        }


        if (!(paymentType.equalsIgnoreCase("cw") || paymentType.equalsIgnoreCase("Cash Withdraw") ||
                paymentType.equalsIgnoreCase("cd") || paymentType.equalsIgnoreCase("Cash Deposit"))) {
            holder.amountContainer.setVisibility(View.GONE);
        }

        holder.tvCheckStatus.setOnClickListener(v->
        {
            String amount1=matmReportModelArrayList.get(position).getAmount();
            String txnId=matmReportModelArrayList.get(position).getUniqueTransactionId();
            String cardType1=matmReportModelArrayList.get(position).getCardType();
            String transType=matmReportModelArrayList.get(position).getTransactionType();

            if (transType.equalsIgnoreCase("Cash Withdraw"))
                transType="cw";
            else
                transType="be";

            checkMatmStatus(amount1,txnId,cardType1,transType);

        });


        holder.imgReceipt.setOnClickListener(view -> {
            String message = matmReportModelArrayList.get(position).getMessage();
            String balAmount = matmReportModelArrayList.get(position).getBankBalance();
            String bankRrn = matmReportModelArrayList.get(position).getBankRRN();
            String bank = matmReportModelArrayList.get(position).getBankName();
            String uniqueTransactionId = matmReportModelArrayList.get(position).getUniqueTransactionId();
            String openingBalance = matmReportModelArrayList.get(position).getOpeningBalance();
            String closingBalance = matmReportModelArrayList.get(position).getClosingBalance();

            Intent intent = new Intent(context, MatmReceiptActivity.class);
            intent.putExtra("cardNumber", cardNo);
            intent.putExtra("cardType", cardType);
            intent.putExtra("amount", amount);
            intent.putExtra("message", message);
            intent.putExtra("bankBalance", balAmount + "");
            intent.putExtra("bankRRN", bankRrn);
            intent.putExtra("transactionType", paymentType);
            intent.putExtra("bank", bank);
            intent.putExtra("uniqueTransactionId", uniqueTransactionId);
            intent.putExtra("status", status);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("openingBalance", openingBalance);
            intent.putExtra("closingBalance", closingBalance);
            intent.putExtra("isMatmReport", true);

            context.startActivity(intent);

        });
    }

    private void checkMatmStatus(String amount1, String txnId, String cardType1, String transType) {
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().checkMatmStatus(AUTH_KEY,userId,deviceId,deviceInfo,amount1,txnId
                ,"na",transType);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");

                        progressDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setMessage(message)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        activity.finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setMessage("Please try after sometime.")
                                .setPositiveButton("ok",null)
                                .show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(context)
                            .setMessage("Please try after sometime.")
                            .setPositiveButton("ok",null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(context)
                        .setMessage(t.getMessage())
                        .setPositiveButton("ok",null)
                        .show();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return matmReportModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvCardNo, tvAmount, tvPaymentType, tvDate, tvTime, tvStatus, tvCheckStatus;
        ImageView imgReceipt;
        LinearLayout amountContainer;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvCardNo = itemView.findViewById(R.id.tv_card_number);
            tvAmount = itemView.findViewById(R.id.tv_all_report_amount);
            tvPaymentType = itemView.findViewById(R.id.tv_payment_type);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_all_report_status);

            imgReceipt = itemView.findViewById(R.id.img_receipt);
            tvCheckStatus = itemView.findViewById(R.id.tv_check_status);

            amountContainer = itemView.findViewById(R.id.amount_container);
        }
    }
}
