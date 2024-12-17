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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import wts.com.mitrsewa.activities.ShareDmtReportActivity;
import wts.com.mitrsewa.models.MoneyReportModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class MoneyReportAdapter extends RecyclerView.Adapter<MoneyReportAdapter.ViewHolder> {

    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    Context context;
    Activity activity;
    boolean isRefund;
    String userId, deviceId, deviceInfo;

    public static ArrayList<MoneyReportModel> moneyReportModelArrayList1;

    public MoneyReportAdapter(ArrayList<MoneyReportModel> moneyReportModelArrayList, Context context, Activity activity, boolean isRefund,
                              String userId, String deviceId, String deviceInfo) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_money_report_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
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
        String tds = moneyReportModelArrayList.get(position).getTds();


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

        if (isRefund) {
            holder.imgShare.setVisibility(View.GONE);
            holder.btnRefund.setVisibility(View.VISIBLE);
        }


        holder.btnRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String transactionId = moneyReportModelArrayList.get(position).getTransactionId();
                String uniqueId = moneyReportModelArrayList.get(position).getUniqueId();
                String surcharge = moneyReportModelArrayList.get(position).getSurcharge();
                String gst = moneyReportModelArrayList.get(position).getGst();
                String amount = moneyReportModelArrayList.get(position).getAmount();

                doRefund(transactionId,uniqueId,surcharge,gst,amount,tds);//uniqueId as RefrenceId & transactionId as Acc
            }
        });

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, ShareDmtReportActivity.class);
                intent.putExtra("amount", moneyReportModelArrayList.get(position).getAmount());
                intent.putExtra("accountNumber", moneyReportModelArrayList.get(position).getAccountNumber());
                intent.putExtra("beniName", moneyReportModelArrayList.get(position).getBeniName());
                intent.putExtra("bank", moneyReportModelArrayList.get(position).getBank());
                intent.putExtra("ifsc", moneyReportModelArrayList.get(position).getIfsc());
                intent.putExtra("date", moneyReportModelArrayList.get(position).getDate());
                intent.putExtra("transactionId", moneyReportModelArrayList.get(position).getTransactionId());
                intent.putExtra("status", moneyReportModelArrayList.get(position).getStatus());

                context.startActivity(intent);*/

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

                 moneyReportModelArrayList1=new ArrayList<>();

                moneyReportModelArrayList1.add(moneyReportModel);

                Intent intent = new Intent(context, ShareDmtReportActivity.class);
                intent.putExtra("amount", moneyReportModelArrayList.get(position).getAmount());
                intent.putExtra("accountNumber", moneyReportModelArrayList.get(position).getAccountNumber());
                intent.putExtra("beniName", moneyReportModelArrayList.get(position).getBeniName());
                intent.putExtra("bank", moneyReportModelArrayList.get(position).getBank());
                intent.putExtra("ifsc", moneyReportModelArrayList.get(position).getIfsc());
                intent.putExtra("date", moneyReportModelArrayList.get(position).getDate());
                intent.putExtra("isMoneyReport", true);
                context.startActivity(intent);
                //checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
            }
        });

        holder.btnCheckStatus.setVisibility(View.GONE);


    }

    private void doRefund(String acknowledgementNo, String refrenceId, String surcharge, String gst,String amount,String tds) {
        final AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doDmtRefund(AUTH_KEY, userId, deviceId, deviceInfo,
                refrenceId, acknowledgementNo,tds);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = responseObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            showOtpDialog(surcharge, gst,tds,refrenceId,acknowledgementNo,amount);
                        } else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(context)
                                    .setMessage("Please try after sometime")
                                    .show();
                        }


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

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(String surcharge, String gst,String tds,String refrenceId,String acknowledgementId,String amount) {
        View addSenderDialogView = activity.getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        final EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setVisibility(View.GONE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        tvTitle.setText("OTP Verification");
        etOtp.setHint("OTP");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etOtp.getText())) {
                    addSenderDialog.dismiss();
                    String userOtp = etOtp.getText().toString().trim();
                    validateOtp(userOtp, surcharge, gst,tds,refrenceId,acknowledgementId,amount);
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    private void validateOtp(String userOtp, String surcharge, String gst,String tds,String refrenceId,String acknowledgementId,String amount) {
        final AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().validateRefundOtp(AUTH_KEY, userId, deviceId, deviceInfo,refrenceId,acknowledgementId,
                userOtp,amount,surcharge,gst,tds);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        pDialog.dismiss();

                        String message=responseObject.getString("data");
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvAccountNumber, tvBeniName, tvBank, tvIfsc, tvCost, tvBalance, tvDate, tvTransactionId, tvStatus;
        ImageView imgShare;
        Button btnRefund,btnCheckStatus;


        public ViewHolder(@NonNull View itemView) {
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
