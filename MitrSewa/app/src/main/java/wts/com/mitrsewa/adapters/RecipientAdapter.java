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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.SenderValidationActivity;
import wts.com.mitrsewa.activities.ShareDmtReportActivity;
import wts.com.mitrsewa.activities.ShareExpressDmtActivity;
import wts.com.mitrsewa.models.MoneyReportModel;
import wts.com.mitrsewa.models.RecipientModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.Viewholder> {

    Context context;
    ArrayList<RecipientModel> recipientModelArrayList;
    String mobileNumber, userId, userName;
    ProgressDialog pDialog;
    Activity activity;
    String beniName, accountNumber, responseBankName, responseIfsc, date;
    String deviceId, deviceInfo;
    String selectedTransactionMode = "ifs";

    public static ArrayList<MoneyReportModel> moneyReportModelArrayList;

    public RecipientAdapter(Context context, Activity activity, ArrayList<RecipientModel> recipientModelArrayList,
                            String mobileNumber, String userId, String userName, String deviceId, String deviceInfo) {
        this.context = context;
        this.activity = activity;
        this.recipientModelArrayList = recipientModelArrayList;
        this.mobileNumber = mobileNumber;
        this.userId = userId;
        this.userName = userName;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_recipient_list, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {

        final String recipientName = recipientModelArrayList.get(position).getRecipientName();
        String accountNumber = recipientModelArrayList.get(position).getBankAccountNumber();
        final String bankName = recipientModelArrayList.get(position).getBankName();
        final String recipientId = recipientModelArrayList.get(position).getRecipientId();
        final String bankAccountNumber = recipientModelArrayList.get(position).getBankAccountNumber();
        final String ifsc = recipientModelArrayList.get(position).getIfsc();
        final String benificaryName = recipientModelArrayList.get(position).getRecipientName();
        //final String benificaryMobileNumber = recipientModelArrayList.get(position).getMobileNumber();


        holder.tvRecipientName.setText(recipientName);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvBankName.setText(bankName);
        holder.tvIfsc.setText(ifsc);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("Confirmation")
                        .setMessage("Do you want to delete this beneficiary ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBene(recipientId);
                            }
                        }).setNegativeButton("Cancel", null)
                        .show();
            }
        });

        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_benificary_layout,
                        null, false);
                final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
                addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addSenderOTPDialog.setCancelable(false);
                addSenderOTPDialog.setView(addSenderOTPDialogView);
                addSenderOTPDialog.show();

                final EditText etAmount = addSenderOTPDialog.findViewById(R.id.et_amount);
                final EditText etTpin = addSenderOTPDialog.findViewById(R.id.et_tpin);
                Button btnProceed = addSenderOTPDialog.findViewById(R.id.btn_proceed);
                Button btnCancel = addSenderOTPDialog.findViewById(R.id.btn_cancel);
                TextView tvBeniName = addSenderOTPDialog.findViewById(R.id.tv_beni_name);
                TextView tvBankName = addSenderOTPDialog.findViewById(R.id.tv_bank_name);
                TextView tvAccountNumber = addSenderOTPDialog.findViewById(R.id.tv_account_number);
                TextView tvIfsc = addSenderOTPDialog.findViewById(R.id.tv_ifsc);
                RadioButton rdImps = addSenderOTPDialog.findViewById(R.id.rd_imps);
                RadioButton rdNeft = addSenderOTPDialog.findViewById(R.id.rd_neft);

                tvBeniName.setText(benificaryName);
                tvBankName.setText(bankName);
                tvAccountNumber.setText(bankAccountNumber);
                tvIfsc.setText(ifsc);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSenderOTPDialog.dismiss();
                    }
                });

                btnProceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(etAmount.getText())) {
                            if (!TextUtils.isEmpty(etTpin.getText())) {

                                addSenderOTPDialog.dismiss();
                                String amount = etAmount.getText().toString().trim();
                                String tpin = etTpin.getText().toString().trim();
                                if (rdImps.isChecked()) {
                                    selectedTransactionMode = "IMPS";
                                    checkTpin(tpin, amount, recipientId, bankAccountNumber, bankName, ifsc, benificaryName, mobileNumber);

                                } else if (rdNeft.isChecked()) {
                                    selectedTransactionMode = "NEFT";
                                    checkTpin(tpin, amount, recipientId, bankAccountNumber, bankName, ifsc, benificaryName, mobileNumber);

                                } else {
                                    new AlertDialog.Builder(context)
                                            .setMessage("Please select transaction mode.")
                                            .setPositiveButton("Ok", null)
                                            .show();
                                }

                                //payBenificary(recipientId, amount, bankAccountNumber, bankName, ifsc, benificaryName,benificaryMobileNumber);
                            } else {
                                etTpin.setError("Required");
                            }


                        } else {
                            etAmount.setError("Enter Amount");
                        }
                    }
                });
            }
        });
    }
    private void checkTpin(String tpin, String amount, String recipientId, String bankAccountNumber, String bankName,
                           String ifsc, String benificaryName, String benificaryMobileNumber) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkMpinOrTPIN(AUTH_KEY, userId, deviceId, deviceInfo, "tpin", tpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            if (SenderValidationActivity.isExpressDmt) {
                                payBenificaryExpress(recipientId, amount, bankAccountNumber, bankName, ifsc, benificaryName, benificaryMobileNumber);
                            } else {
                                payBenificary(recipientId, amount, bankAccountNumber, bankName, ifsc, benificaryName, benificaryMobileNumber);
                            }
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String transaction = responseObject.getString("status");
                            new AlertDialog.Builder(context)
                                    .setMessage(transaction)
                                    .show();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setMessage("Something went wrong.Please try after sometime.")
                                    .show();
                        }


                    } catch (Exception e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setMessage("Something went wrong.Please try after sometime.")
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context)
                            .setMessage("Something went wrong.Please try after sometime.")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context)
                        .setMessage("Something went wrong.Please try after sometime.")
                        .show();
            }
        });
    }

    private void payBenificaryExpress(String recipientId, String amount, String bankAccountNumber, String bankName, String ifsc, String benificaryName, String benificaryMobileNumber) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

//        if (selectedTransactionMode.equalsIgnoreCase("IMPS"))
//            selectedTransactionMode = "2";
//        else if (selectedTransactionMode.equalsIgnoreCase("RTGS"))
//            selectedTransactionMode = "4";
//        else if (selectedTransactionMode.equalsIgnoreCase("NEFT"))
//            selectedTransactionMode = "3";
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().payBeneficiaryExpress(AUTH_KEY, userId, deviceId, deviceInfo, amount, recipientId,
                SenderValidationActivity.sendername, SenderValidationActivity.senderMobileNumber, selectedTransactionMode);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String respoonseCode = jsonObject.getString("statuscode");

                        if (respoonseCode.equalsIgnoreCase("TXN")) {

                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            String accountNumber = dataObject.getString("AccountNo");
                            String beniName = dataObject.getString("BeneficiaryName");
                            String responseBankName = dataObject.getString("BankName");
                            String responseIfsc = dataObject.getString("IfscCode");
                            String date = dataObject.getString("CreatedOn");
                            String transactionId = dataObject.getString("TransactionId");
                            String uniqueId = dataObject.getString("UniqueId");
                            String responseAmount = dataObject.getString("Amount");
                            String status = dataObject.getString("Status");
                            String bankRefId = dataObject.getString("BankRrnNo");
                            String comm = dataObject.getString("Commission");
                            String surcharge = dataObject.getString("Surcharge");

                            Intent intent = new Intent(context, ShareExpressDmtActivity.class);
                            intent.putExtra("amount", amount);
                            intent.putExtra("accountNumber", accountNumber);
                            intent.putExtra("beniName", beniName);
                            intent.putExtra("bank", responseBankName);
                            intent.putExtra("ifsc", responseIfsc);
                            intent.putExtra("date", date);
                            intent.putExtra("transactionId", transactionId);
                            intent.putExtra("uniqueId", uniqueId);
                            intent.putExtra("bankRefId", bankRefId);
                            intent.putExtra("responseAmount", responseAmount);
                            intent.putExtra("status", status);
                            intent.putExtra("comm", comm);
                            intent.putExtra("surcharge", surcharge);
                            intent.putExtra("isMoneyReport", false);

                            context.startActivity(intent);

                            /*final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_bene_status_dialog_layout, null, false);
                            final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
                            addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addSenderOTPDialog.setCancelable(false);
                            addSenderOTPDialog.setView(addSenderOTPDialogView);
                            addSenderOTPDialog.show();

                            TextView tvbeniName, tvAccountNumber, tvBankName, tvIfsc, tvBankRefId, tvTransactionId, tvAmount, tvSurcharge,
                                    tvCommission, tvDate, tvStatus;
                            Button btnOk;

                            tvbeniName = addSenderOTPDialogView.findViewById(R.id.tv_beni_name);
                            tvAccountNumber = addSenderOTPDialogView.findViewById(R.id.tv_account_number);
                            tvBankName = addSenderOTPDialogView.findViewById(R.id.tv_bank_name);
                            tvIfsc = addSenderOTPDialogView.findViewById(R.id.tv_ifsc);
                            tvBankRefId = addSenderOTPDialogView.findViewById(R.id.tv_bank_ref_id);
                            tvTransactionId = addSenderOTPDialogView.findViewById(R.id.tv_transaction_id);
                            tvAmount = addSenderOTPDialogView.findViewById(R.id.tv_amount);
                            tvSurcharge = addSenderOTPDialogView.findViewById(R.id.tv_surcharge);
                            tvCommission = addSenderOTPDialogView.findViewById(R.id.tv_commission);
                            tvDate = addSenderOTPDialogView.findViewById(R.id.tv_date);
                            tvStatus = addSenderOTPDialogView.findViewById(R.id.tv_status);
                            btnOk = addSenderOTPDialogView.findViewById(R.id.btn_ok);

                            tvbeniName.setText("Beneficiary Name : " + beniName);
                            tvAccountNumber.setText("Account Number : " + accountNumber);
                            tvBankName.setText("Bank Name : " + responseBankName);
                            tvIfsc.setText("Ifsc : " + responseIfsc);
                            tvBankRefId.setText("Bank Ref. ID : " + bankRefId);
                            tvTransactionId.setText("Transaction Id : " + transactionId);
                            tvAmount.setText("Amount : ₹ " + responseAmount);
                            tvDate.setText("Date and Time : " + date);
                            tvStatus.setText("Status : " + status);
                            tvCommission.setText("Comm. : ₹ " + comm);
                            tvSurcharge.setText("Surcharge : ₹ " + surcharge);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addSenderOTPDialog.dismiss();
                                    Intent intent = new Intent(context, ShareDmtReportActivity.class);
                                    intent.putExtra("amount", amount);
                                    intent.putExtra("accountNumber", accountNumber);
                                    intent.putExtra("beniName", benificaryName);
                                    intent.putExtra("bank", bankName);
                                    intent.putExtra("ifsc", ifsc);
                                    intent.putExtra("date", date);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("status", status);
                                    context.startActivity(intent);
                                }
                            });*/

                            pDialog.dismiss();


                        } else {
                            pDialog.dismiss();
                            String message = jsonObject.getString("data");
                            new AlertDialog.Builder(context).setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context).setTitle("Message")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context).setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context).setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void payBenificary(String recipientId, final String amount, String bankAccountNumber, final String bankName, final String ifsc,
                               final String benificaryName, String beneMobileNumber) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().payBeneficiary(AUTH_KEY, userId, deviceId, deviceInfo, amount, recipientId, "NA",
                SenderValidationActivity.sendername, SenderValidationActivity.senderMobileNumber, bankAccountNumber, benificaryName, bankName, ifsc,
                beneMobileNumber, selectedTransactionMode, "NA");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String respoonseCode = jsonObject.getString("statuscode");

                        if (respoonseCode.equalsIgnoreCase("TXN")) {

                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            moneyReportModelArrayList = new ArrayList<>();

                            beniName = dataObject.getString("BenificiaryName");
                            accountNumber = dataObject.getString("AccountNo");
                            responseBankName = dataObject.getString("BankName");
                            responseIfsc = dataObject.getString("IfscCode");
                            date = dataObject.getString("UpdateDate");

                            for (int i = 0; i < dataArray.length(); i++) {
                                MoneyReportModel moneyReportModel = new MoneyReportModel();

                                JSONObject dataObjectNew = dataArray.getJSONObject(i);

                                String transactionId = dataObjectNew.getString("TransactionID");
                                String responseAmount = dataObjectNew.getString("Amount");
                                String status = dataObjectNew.getString("Status");
                                String bankRefId = dataObjectNew.getString("BankRrnNo");
                                String comm = dataObjectNew.getString("Commission");
                                String surcharge = dataObjectNew.getString("Surcharge");

                                moneyReportModel.setTransactionId(transactionId);
                                moneyReportModel.setAmount(responseAmount);
                                moneyReportModel.setStatus(status);
                                moneyReportModel.setBankRRN(bankRefId);
                                moneyReportModel.setCommission(comm);
                                moneyReportModel.setSurcharge(surcharge);

                                moneyReportModelArrayList.add(moneyReportModel);
                            }

                            Intent intent = new Intent(context, ShareDmtReportActivity.class);
                            intent.putExtra("amount", amount);
                            intent.putExtra("accountNumber", accountNumber);
                            intent.putExtra("beniName", benificaryName);
                            intent.putExtra("bank", bankName);
                            intent.putExtra("ifsc", ifsc);
                            intent.putExtra("date", date);
                            intent.putExtra("isMoneyReport", false);

                            context.startActivity(intent);

                            /*final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_bene_status_dialog_layout, null, false);
                            final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
                            addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addSenderOTPDialog.setCancelable(false);
                            addSenderOTPDialog.setView(addSenderOTPDialogView);
                            addSenderOTPDialog.show();

                            TextView tvbeniName, tvAccountNumber, tvBankName, tvIfsc, tvBankRefId, tvTransactionId, tvAmount, tvSurcharge,
                                    tvCommission, tvDate, tvStatus;
                            Button btnOk;

                            tvbeniName = addSenderOTPDialogView.findViewById(R.id.tv_beni_name);
                            tvAccountNumber = addSenderOTPDialogView.findViewById(R.id.tv_account_number);
                            tvBankName = addSenderOTPDialogView.findViewById(R.id.tv_bank_name);
                            tvIfsc = addSenderOTPDialogView.findViewById(R.id.tv_ifsc);
                            tvBankRefId = addSenderOTPDialogView.findViewById(R.id.tv_bank_ref_id);
                            tvTransactionId = addSenderOTPDialogView.findViewById(R.id.tv_transaction_id);
                            tvAmount = addSenderOTPDialogView.findViewById(R.id.tv_amount);
                            tvSurcharge = addSenderOTPDialogView.findViewById(R.id.tv_surcharge);
                            tvCommission = addSenderOTPDialogView.findViewById(R.id.tv_commission);
                            tvDate = addSenderOTPDialogView.findViewById(R.id.tv_date);
                            tvStatus = addSenderOTPDialogView.findViewById(R.id.tv_status);
                            btnOk = addSenderOTPDialogView.findViewById(R.id.btn_ok);


                            tvbeniName.setText("Beneficiary Name : " + beniName);
                            tvAccountNumber.setText("Account Number : " + accountNumber);
                            tvBankName.setText("Bank Name : " + responseBankName);
                            tvIfsc.setText("Ifsc : " + responseIfsc);
                            tvBankRefId.setText("Bank Ref. ID : " + bankRefId);
                            tvTransactionId.setText("Transaction Id : " + transactionId);
                            tvAmount.setText("Amount : ₹ " + responseAmount);
                            tvDate.setText("Date and Time : " + date);
                            tvStatus.setText("Status : " + status);
                            tvCommission.setText("Comm. : ₹ " + comm);
                            tvSurcharge.setText("Surcharge : ₹ " + surcharge);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addSenderOTPDialog.dismiss();
                                    Intent intent = new Intent(context, ShareDmtReportActivity.class);
                                    intent.putExtra("amount", amount);
                                    intent.putExtra("accountNumber", accountNumber);
                                    intent.putExtra("beniName", benificaryName);
                                    intent.putExtra("bank", bankName);
                                    intent.putExtra("ifsc", ifsc);
                                    intent.putExtra("date", date);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("status", status);
                                    context.startActivity(intent);
                                }
                            });*/

                            pDialog.dismiss();


                        } else {
                            pDialog.dismiss();
                            String message = jsonObject.getString("data");
                            new AlertDialog.Builder(context).setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context).setTitle("Message")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context).setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context).setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void deleteBene(String recipientId) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = null;

        if (SenderValidationActivity.isExpressDmt) {
            call = RetrofitClient.getInstance().getApi().deleteBeneficiaryExpress(AUTH_KEY, deviceId, deviceInfo, userId, recipientId);
        } else {
            call = RetrofitClient.getInstance().getApi().deleteBeneficiary(AUTH_KEY, deviceId, deviceInfo, userId, recipientId, mobileNumber,
                    SenderValidationActivity.senderId);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();

                            String transaction = jsonObject.getString("data");

                            new AlertDialog.Builder(context)
                                    .setTitle("Status")
                                    .setMessage(transaction)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            activity.finish();
                                        }
                                    })
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
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
        return recipientModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvRecipientName, tvAccountNumber, tvBankName, tvIfsc;
        Button btnPay, btnDelete;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvRecipientName = itemView.findViewById(R.id.tv_recipient_name);
            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
            tvBankName = itemView.findViewById(R.id.tv_bank_name);
            btnPay = itemView.findViewById(R.id.btn_pay);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvIfsc = itemView.findViewById(R.id.tv_ifsc);
        }
    }
}
