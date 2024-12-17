package wts.com.mitrsewa.adapters;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.DmtRefundReportActivity;
import wts.com.mitrsewa.models.UpiReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class UpiReportAdapter extends RecyclerView.Adapter<UpiReportAdapter.Viewholder>
{

    ArrayList<UpiReportModel> upiReportModelArrayList;
    String userId,deviceId,deviceInfo;
    Context context;
    Activity activity;

    public UpiReportAdapter(ArrayList<UpiReportModel> upiReportModelArrayList,String userId,String deviceId,
                            String deviceInfo,Context context,Activity activity) {
        this.upiReportModelArrayList = upiReportModelArrayList;
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.deviceInfo = deviceInfo;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upi_report,parent,false);

        return new UpiReportAdapter.Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String bankRrn=upiReportModelArrayList.get(position).getBankRrn();
        String amount=upiReportModelArrayList.get(position).getAmount();
        String status=upiReportModelArrayList.get(position).getStatus();
        String date=upiReportModelArrayList.get(position).getDate();
        String openingBal=upiReportModelArrayList.get(position).getOpeningBalance();
        String closingBal=upiReportModelArrayList.get(position).getClosingBalance();
        String uniqueTransactionId=upiReportModelArrayList.get(position).getUniqueTransactionId();

        holder.tvBankRrn.setText("Bank RRN "+bankRrn);
        holder.tvAmount.setText("Amount : ₹ "+amount);
        holder.tvStatus.setText("Status : "+status);
        holder.tvDate.setText("Date : "+date);
        holder.tvOpeningBalance.setText("Opening Balance : ₹ "+openingBal);
        holder.tvClosingBalance.setText("Closing Balance : ₹ "+closingBal);

        if (status.equalsIgnoreCase("PENDING"))
        {
            holder.btnCheckStatus.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.btnCheckStatus.setVisibility(View.GONE);
        }

        holder.btnCheckStatus.setOnClickListener(v->
        {
            checkStatus(uniqueTransactionId,status);
        });
    }

    private void checkStatus(String uniqueTransactionId, String status) {
        final AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApiWithoutAttribute().upiCheckStatus(AUTH_KEY,userId,deviceId,deviceInfo,uniqueTransactionId,status);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
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
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setMessage("Please try after sometime\n" + t.getMessage())
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
        return upiReportModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        TextView tvBankRrn,tvAmount,tvStatus,tvDate,tvOpeningBalance,tvClosingBalance;
        AppCompatButton btnCheckStatus;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvBankRrn=itemView.findViewById(R.id.tv_bank_rrn);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvStatus=itemView.findViewById(R.id.tv_status);
            tvDate=itemView.findViewById(R.id.tv_date);
            btnCheckStatus=itemView.findViewById(R.id.btn_check_status);
            tvOpeningBalance=itemView.findViewById(R.id.tv_opening_balance);
            tvClosingBalance=itemView.findViewById(R.id.tv_closing_balance);

        }
    }
}