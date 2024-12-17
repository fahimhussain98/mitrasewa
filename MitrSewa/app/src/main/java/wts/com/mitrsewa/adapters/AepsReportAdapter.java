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
import wts.com.mitrsewa.activities.ShareAepsReportActivity;
import wts.com.mitrsewa.models.AepsModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;


public class AepsReportAdapter extends RecyclerView.Adapter<AepsReportAdapter.ViewHolder>
{
    ArrayList<AepsModel> aepsModelArrayList;
    boolean isInitialReport;
    Context context;

    Activity activity;
    String userId, deviceId, deviceInfo;

    public AepsReportAdapter(ArrayList<AepsModel> aepsModelArrayList, boolean isInitialReport, Context context, Activity activity, String userId, String deviceId, String deviceInfo) {
        this.aepsModelArrayList = aepsModelArrayList;
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
        View view= LayoutInflater.from(context).inflate(R.layout.row_aeps_report,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String transactionId=aepsModelArrayList.get(position).getTransactionId();
        String amount=aepsModelArrayList.get(position).getAmount();
        String comm=aepsModelArrayList.get(position).getComm();
        String balance=aepsModelArrayList.get(position).getNewbalance();
        String dateTime=aepsModelArrayList.get(position).getTimestamp();
        String status=aepsModelArrayList.get(position).getTxnStatus();
        String transactionType=aepsModelArrayList.get(position).getTransactionType();
        String oldBalance=aepsModelArrayList.get(position).getOldBalance();
        String aadharNo=aepsModelArrayList.get(position).getAadharNo();
        String panNo=aepsModelArrayList.get(position).getPanNo();
        String bankName=aepsModelArrayList.get(position).getBankName();
        String surcharge=aepsModelArrayList.get(position).getSurcharge();


        holder.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent=new Intent(context, ShareAepsReportActivity.class);
                intent.putExtra("transactionId",aepsModelArrayList.get(position).getTransactionId());
                intent.putExtra("amount",aepsModelArrayList.get(position).getAmount());
                intent.putExtra("comm",aepsModelArrayList.get(position).getComm());
                intent.putExtra("balance",aepsModelArrayList.get(position).getNewbalance());
                intent.putExtra("dateTime",aepsModelArrayList.get(position).getTimestamp());
                intent.putExtra("status",aepsModelArrayList.get(position).getTxnStatus());
                intent.putExtra("transactionType",aepsModelArrayList.get(position).getTransactionType());
                intent.putExtra("oldBalance",aepsModelArrayList.get(position).getOldBalance());
                intent.putExtra("aadharNo",aepsModelArrayList.get(position).getAadharNo());
                intent.putExtra("panNo",aepsModelArrayList.get(position).getPanNo());
                intent.putExtra("bankName",aepsModelArrayList.get(position).getBankName());
                intent.putExtra("surcharge",aepsModelArrayList.get(position).getSurcharge());
                context.startActivity(intent);*/
            }
        });

        holder.imgReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent intent=new Intent(context, ShareAepsReportActivity.class);
                intent.putExtra("transactionId",aepsModelArrayList.get(position).getTransactionId());
                intent.putExtra("amount",aepsModelArrayList.get(position).getAmount());
                intent.putExtra("comm",aepsModelArrayList.get(position).getComm());
                intent.putExtra("balance",aepsModelArrayList.get(position).getNewbalance());
                intent.putExtra("dateTime",aepsModelArrayList.get(position).getTimestamp());
                intent.putExtra("status",aepsModelArrayList.get(position).getTxnStatus());
                intent.putExtra("transactionType",aepsModelArrayList.get(position).getTransactionType());
                intent.putExtra("oldBalance",aepsModelArrayList.get(position).getOldBalance());
                intent.putExtra("aadharNo",aepsModelArrayList.get(position).getAadharNo());
                intent.putExtra("panNo",aepsModelArrayList.get(position).getPanNo());
                intent.putExtra("bankName",aepsModelArrayList.get(position).getBankName());
                intent.putExtra("surcharge",aepsModelArrayList.get(position).getSurcharge());
                context.startActivity(intent);*/
            }
        });

        holder.tvReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ShareAepsReportActivity.class);
                intent.putExtra("transactionId",aepsModelArrayList.get(position).getTransactionId());
                intent.putExtra("amount",aepsModelArrayList.get(position).getAmount());
                intent.putExtra("comm",aepsModelArrayList.get(position).getComm());
                intent.putExtra("balance",aepsModelArrayList.get(position).getNewbalance());
                intent.putExtra("dateTime",aepsModelArrayList.get(position).getTimestamp());
                intent.putExtra("status",aepsModelArrayList.get(position).getTxnStatus());
                intent.putExtra("transactionType",aepsModelArrayList.get(position).getTransactionType());
                intent.putExtra("oldBalance",aepsModelArrayList.get(position).getOldBalance());
                intent.putExtra("aadharNo",aepsModelArrayList.get(position).getAadharNo());
                intent.putExtra("panNo",aepsModelArrayList.get(position).getPanNo());
                intent.putExtra("bankName",aepsModelArrayList.get(position).getBankName());
                intent.putExtra("surcharge",aepsModelArrayList.get(position).getSurcharge());
                intent.putExtra("status",aepsModelArrayList.get(position).getTxnStatus());
                intent.putExtra("uniqueId",aepsModelArrayList.get(position).getUniqueTransactionId());
                intent.putExtra("liveId",aepsModelArrayList.get(position).getBankRrn());
                context.startActivity(intent);
            }
        });

        if (status.equalsIgnoreCase("Failed")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back2));
        }
        else if (status.equalsIgnoreCase("PENDING")) {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back));
            holder.tvCheckStatus.setVisibility(View.VISIBLE);

            holder.tvCheckStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String orderId = aepsModelArrayList.get(position).getUniqueTransactionId();

                    doCheckStatus(orderId);
                }
            });

        }
        else {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back_green));
        }


        holder.tvTransactionId.setText(transactionId);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvComm.setText("₹ "+comm);
        holder.tvBalance.setText("₹ "+balance);
        holder.tvDateTime.setText(dateTime);
        holder.tvStatus.setText(status);
        if (transactionType.equalsIgnoreCase("SAP"))
        {
            holder.tvTransactionType.setText("Mini Statement");
        }
        else if (transactionType.equalsIgnoreCase("WAP"))
        {
            holder.tvTransactionType.setText("Cash withdrawal");
        }
        else if (transactionType.equalsIgnoreCase("BAP"))
        {
            holder.tvTransactionType.setText("Balance Enquiry");
        }
        else if (transactionType.equalsIgnoreCase("MZZ"))
        {
            holder.tvTransactionType.setText("Aadhar Pay");
        }
        else
        {
            holder.tvTransactionType.setText(transactionType);
        }

    }

    private void doCheckStatus(String orderId) {
        final AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doAepsCheckStatus(AUTH_KEY, userId, deviceId, deviceInfo, orderId, "AEPS");
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
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setMessage("Please try after sometime\n" + t.getMessage())
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return aepsModelArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvAmount,tvComm,tvTransactionType,tvBalance,tvDateTime,tvStatus,tvReceipt,tvCheckStatus;
        ImageView imgReceipt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_all_report_transaction_id);
            tvAmount=itemView.findViewById(R.id.tv_all_report_amount);
            tvComm=itemView.findViewById(R.id.tv_all_report_commission);
            tvBalance=itemView.findViewById(R.id.tv_new_balance);
            tvDateTime=itemView.findViewById(R.id.tv_all_report_date_time);
            tvStatus=itemView.findViewById(R.id.tv_all_report_status);
            tvTransactionType=itemView.findViewById(R.id.tv_transaction_type);
            imgReceipt=itemView.findViewById(R.id.img_receipt);
            tvReceipt=itemView.findViewById(R.id.tv_receipt);
            tvCheckStatus = itemView.findViewById(R.id.tv_check_status);
        }
    }
}
