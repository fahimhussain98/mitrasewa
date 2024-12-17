package wts.com.mitrsewa.adapters;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import wts.com.mitrsewa.activities.ShareReportActivity;
import wts.com.mitrsewa.models.AllReportsModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class AllReportAdapter extends RecyclerView.Adapter<AllReportAdapter.ViewHolder> {

    ArrayList<AllReportsModel> allReportsModelArrayList;
    Context context;
    String userId;
    String deviceId,deviceInfo;
    SharedPreferences sharedPreferences;
    Activity activity;

    public AllReportAdapter(ArrayList<AllReportsModel> allReportsModelArrayList, Context context, String userId,Activity activity) {
        this.allReportsModelArrayList = allReportsModelArrayList;
        this.context=context;
        this.userId=userId;
        this.activity=activity;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge_report1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       String transactionId = allReportsModelArrayList.get(position).getTransactionId();
       String operatorName = allReportsModelArrayList.get(position).getOperatorName();
       String number = allReportsModelArrayList.get(position).getNumber();
       String amount = allReportsModelArrayList.get(position).getAmount();
       String commission = allReportsModelArrayList.get(position).getCommission();
       String cost = allReportsModelArrayList.get(position).getCost();
       String balance = allReportsModelArrayList.get(position).getBalance();
       String date = allReportsModelArrayList.get(position).getDate();
       String time = allReportsModelArrayList.get(position).getTime();
       String status = allReportsModelArrayList.get(position).getStatus();
       String stype = allReportsModelArrayList.get(position).getsType();
        String openingBalance = allReportsModelArrayList.get(position).getOpeningBalance();
        String closingBalance = allReportsModelArrayList.get(position).getClosingBalance();
        String uniqueId = allReportsModelArrayList.get(position).getUniqueId();
        String liveId = allReportsModelArrayList.get(position).getLiveId();
        String sur = allReportsModelArrayList.get(position).getSurcharge();
        String gst = allReportsModelArrayList.get(position).getGst();
        String tds = allReportsModelArrayList.get(position).getTds();

        holder.tvTransactionId.setText(transactionId);
        holder.tvDate.setText(date+","+time);
        holder.tvNumber.setText(number);
        holder.tvOpeningBalance.setText("₹ "+openingBalance);
        holder.tvClosingBalance.setText("₹ "+closingBalance);
        holder.tvComm.setText("₹ "+commission);
        holder.tvStatus.setText(status);
        holder.tvAmount.setText("₹ "+amount);

        if (status.equalsIgnoreCase("Pending")){
            holder.tvCheckStatus.setVisibility(View.VISIBLE);
        }


        holder.imgShare.setOnClickListener(v-> {

            /*final View view1 = LayoutInflater.from(context).inflate(R.layout.report_dialog_layout, null, false);
            final AlertDialog builder = new AlertDialog.Builder(context).create();
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            builder.setView(view1);
            builder.show();


            TextView tvTransactionId=view1.findViewById(R.id.tv_all_report_transaction_id);
            TextView tvOperatorName=view1.findViewById(R.id.tv_all_report_operator_name);
            TextView tvNumber=view1.findViewById(R.id.tv_all_report_number);
            TextView tvAmount=view1.findViewById(R.id.tv_all_report_amount);
            TextView tvCommission=view1.findViewById(R.id.tv_all_report_commission);
            TextView tvCost=view1.findViewById(R.id.tv_all_report_cost);
            TextView tvBalance=view1.findViewById(R.id.tv_all_report_balance);
            TextView tvDate=view1.findViewById(R.id.tv_all_report_date);
            TextView tvTime=view1.findViewById(R.id.tv_all_report_time);
            TextView tvStatus=view1.findViewById(R.id.tv_all_report_status);
            Button btnMakeComplaint=view1.findViewById(R.id.btn_make_complaint);
            ImageView imgShare=view1.findViewById(R.id.img_share);

            imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShareReportActivity.class);
                    intent.putExtra("number", allReportsModelArrayList.get(position).getNumber());
                    intent.putExtra("amount", "₹ " + allReportsModelArrayList.get(position).getAmount());
                    intent.putExtra("commission", "₹ " + allReportsModelArrayList.get(position).getCommission());
                    intent.putExtra("cost", "₹ " + allReportsModelArrayList.get(position).getCost());
                    intent.putExtra("balance", "₹ " + allReportsModelArrayList.get(position).getBalance());
                    intent.putExtra("dateTime", allReportsModelArrayList.get(position).getDate());
                    intent.putExtra("status", allReportsModelArrayList.get(position).getStatus());
                    intent.putExtra("operator", allReportsModelArrayList.get(position).getOperatorName());
                    context.startActivity(intent);
                }
            });

            btnMakeComplaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();

                    final View complaintView = LayoutInflater.from(context).inflate(R.layout.complaint_layout, null, false);
                    final AlertDialog builderMakeComplaint = new AlertDialog.Builder(context).create();
                    builderMakeComplaint.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    builderMakeComplaint.setCancelable(false);
                    builderMakeComplaint.setView(complaintView);
                    builderMakeComplaint.show();

                    final EditText etRemarks = complaintView.findViewById(R.id.et_remarks);
                    Button btnCancel = complaintView.findViewById(R.id.btn_cancel);
                    Button btnSubmit = complaintView.findViewById(R.id.btn_submit);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builderMakeComplaint.dismiss();
                        }
                    });

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(etRemarks.getText())) {
                                String remarks = etRemarks.getText().toString().trim();
                                makeComplaint(remarks, tokenKey);
                                builderMakeComplaint.dismiss();
                            } else {
                                Toast.makeText(context, "Remarks!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });


            transactionId = allReportsModelArrayList.get(position).getTransactionId();
            operatorName = allReportsModelArrayList.get(position).getOperatorName();
            number = allReportsModelArrayList.get(position).getNumber();
            amount = allReportsModelArrayList.get(position).getAmount();
            commission = allReportsModelArrayList.get(position).getCommission();
            cost = allReportsModelArrayList.get(position).getCost();
            balance = allReportsModelArrayList.get(position).getBalance();
            date = allReportsModelArrayList.get(position).getDate();
            time = allReportsModelArrayList.get(position).getTime();
            status = allReportsModelArrayList.get(position).getStatus();



            if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("TXN"))
            {
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            }
            else if (status.equalsIgnoreCase("Failure") || status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("ERR"))
            {
                tvStatus.setTextColor(Color.RED);
            }

            else if (status.equalsIgnoreCase("Reversal"))
            {
                tvStatus.setTextColor(Color.RED);
            }

            tvTransactionId.setText(transactionId);
            tvOperatorName.setText(operatorName);
            tvNumber.setText(number);
            tvAmount.setText("₹ "+amount);
            tvCommission.setText(commission);
            tvCost.setText(cost);
            tvBalance.setText(balance);
            tvDate.setText(date);
            tvTime.setText(time);
            tvStatus.setText(status);
*/

            Intent intent = new Intent(context, ShareReportActivity.class);
            intent.putExtra("number", number);
            intent.putExtra("amount", "₹ " + amount);
            intent.putExtra("commission", "₹ " + commission);
            intent.putExtra("cost", "₹ " + cost);
            intent.putExtra("balance", "₹ " + balance);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("status", status);
            intent.putExtra("operator", operatorName);
            intent.putExtra("uniqueId", uniqueId);
            intent.putExtra("liveId", liveId);
            context.startActivity(intent);

        });

        holder.tvCheckStatus.setOnClickListener(view -> {
            rechargeCheckStatus(uniqueId, commission, sur, tds, gst, amount);
        });

    }

     private void rechargeCheckStatus(String uniqueId, String comm, String sur, String tds, String gst, String amount) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        if (comm.equalsIgnoreCase("null")) {
            comm = "0";
        }
         if (sur.equalsIgnoreCase("null")) {
             sur = "0";
         }
         if (tds.equalsIgnoreCase("null")) {
             tds = "0";
         }
         if (gst.equalsIgnoreCase("null")) {
             gst = "0";
         }

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().rechargeCheckStatus(AUTH_KEY,userId,deviceId,deviceInfo,uniqueId,comm, tds,sur,gst, amount);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String transaction = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(context).
                                    setMessage(transaction)
                                    .show();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String transaction = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(context).
                                    setMessage(transaction)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context).
                                    setMessage("Something went wrong!!!")
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context).
                                setMessage("Something went wrong!!!")
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context).
                            setMessage("Something went wrong!!!")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context).
                        setMessage("Something went wrong!!!")
                        .show();
            }
        });

    }

   /* private void makeComplaint(String remarks, String tokenKey) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().makeComplaint(AUTH_KEY,deviceId,deviceInfo,userId, tokenKey, remarks);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("response_code");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String transaction = responseObject.getString("transactions");
                            pDialog.dismiss();
                            new AlertDialog.Builder(context).
                                    setMessage(transaction)
                                    .show();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String transaction = responseObject.getString("transactions");
                            pDialog.dismiss();
                            new AlertDialog.Builder(context).
                                    setMessage(transaction)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context).
                                    setMessage("Something went wrong!!!")
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context).
                                setMessage("Something went wrong!!!")
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context).
                            setMessage("Something went wrong!!!")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context).
                        setMessage("Something went wrong!!!")
                        .show();
            }
        });

    }*/

    @Override
    public int getItemCount() {
        return allReportsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvDate,tvNumber,tvAmount,tvOpeningBalance,tvComm,tvClosingBalance,tvStatus;
        ImageView imgOperator,imgShare;
        TextView tvCheckStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvDate=itemView.findViewById(R.id.tv_date_time);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvOpeningBalance = itemView.findViewById(R.id.tv_opening_balance);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvComm = itemView.findViewById(R.id.tv_commission);
            tvClosingBalance = itemView.findViewById(R.id.tv_closing_balance);
            tvStatus = itemView.findViewById(R.id.tv_status);
            imgShare = itemView.findViewById(R.id.img_share);
            imgOperator = itemView.findViewById(R.id.img_operator);
            tvCheckStatus = itemView.findViewById(R.id.tvCheckStatus);

        }
    }
}
