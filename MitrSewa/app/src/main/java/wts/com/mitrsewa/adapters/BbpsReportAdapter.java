package wts.com.mitrsewa.adapters;

import static wts.com.mitrsewa.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.SharePayoutReportActivity;
import wts.com.mitrsewa.models.AllReportsModel;
import wts.com.mitrsewa.retrofit.RetrofitClient;

public class BbpsReportAdapter extends RecyclerView.Adapter<BbpsReportAdapter.Viewholder> {

    ArrayList<AllReportsModel> allReportsModelArrayList;
    String transactionId, operatorName, number, amount, commission, cost, balance, date, time, status, stype, imgUrl, brId, tDateTime;
    Context context;
    String userId, userName, deviveId, deviceInfo;
    boolean isInitialReport;
    int FILE_PERMISSION = 45;
    View rechargeDialogView;
    Activity activity;


    public BbpsReportAdapter(ArrayList<AllReportsModel> allReportsModelArrayList, Context context, Activity activity, String userId, String userName,
                             String deviveId, String deviceInfo, boolean isInitialReport) {
        this.allReportsModelArrayList = allReportsModelArrayList;
        this.context = context;
        this.activity = activity;
        this.userId = userId;
        this.userName = userName;
        this.deviceInfo = deviceInfo;
        this.deviveId = deviveId;
        this.isInitialReport = isInitialReport;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge_report, parent, false);
        return new Viewholder(view);
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
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
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
        stype = allReportsModelArrayList.get(position).getsType();
        imgUrl = allReportsModelArrayList.get(position).getImageUrl();

        holder.setData(transactionId, operatorName, number, amount, commission, cost, balance, date, time, status, stype, imgUrl);

        holder.btnViewMore.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                rechargeDialogView = LayoutInflater.from(context).inflate(R.layout.report_dialog_layout, null, false);
                final AlertDialog builder = new AlertDialog.Builder(context).create();
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setView(rechargeDialogView);
                builder.show();


                TextView tvTransactionId = rechargeDialogView.findViewById(R.id.tv_all_report_transaction_id);
                TextView tvOperatorName = rechargeDialogView.findViewById(R.id.tv_all_report_operator_name);
                TextView tvNumber = rechargeDialogView.findViewById(R.id.tv_all_report_number);
                TextView tvAmount = rechargeDialogView.findViewById(R.id.tv_all_report_amount);
                TextView tvCommission = rechargeDialogView.findViewById(R.id.tv_all_report_commission);
                TextView tvCost = rechargeDialogView.findViewById(R.id.tv_all_report_cost);
                TextView tvBalance = rechargeDialogView.findViewById(R.id.tv_all_report_balance);
                TextView tvDate = rechargeDialogView.findViewById(R.id.tv_all_report_date);
                TextView tvTime = rechargeDialogView.findViewById(R.id.tv_all_report_time);
                TextView tvStatus = rechargeDialogView.findViewById(R.id.tv_all_report_status);
                ImageView imShare = rechargeDialogView.findViewById(R.id.img_share);
                Button btnMakeComplaint = rechargeDialogView.findViewById(R.id.btn_make_complaint);


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
                imgUrl = allReportsModelArrayList.get(position).getImageUrl();


                if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("TXN")) {
                    tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                } else if (status.equalsIgnoreCase("Failure") || status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("ERR")
                        || status.equalsIgnoreCase("Falied")) {
                    tvStatus.setTextColor(Color.RED);
                } else if (status.equalsIgnoreCase("Reversal")) {
                    tvStatus.setTextColor(Color.RED);
                }

                //Picasso.get().load(imgUrl).into(imgOperator);

                tvTransactionId.setText(transactionId);
                tvOperatorName.setText(operatorName);
                tvNumber.setText(number);
                tvAmount.setText("₹ " + amount);
                tvCommission.setText("₹ " + commission);
                tvCost.setText("₹ " + cost);
                tvBalance.setText("₹ " + balance);
                tvDate.setText(date);
                tvTime.setText(time);
                tvStatus.setText(status);

                btnMakeComplaint.setOnClickListener(v1->
                {
                    final android.app.AlertDialog complaintDialog = new android.app.AlertDialog.Builder(context).create();
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.complaint_dialog, null);
                    complaintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    complaintDialog.setView(convertView);
                    complaintDialog.setCancelable(false);
                    complaintDialog.show();

                    ImageView imgClose=convertView.findViewById(R.id.img_close);
                    EditText etRemarks=convertView.findViewById(R.id.et_remarks);
                    AppCompatButton btnMakeComplaint1=convertView.findViewById(R.id.btn_make_complaint);

                    imgClose.setOnClickListener(v2->
                    {
                        complaintDialog.dismiss();
                    });

                    btnMakeComplaint1.setOnClickListener(v2->
                    {
                        if (!TextUtils.isEmpty(etRemarks.getText()))
                        {
                            String remarks=etRemarks.getText().toString().trim();
                            raiseComplaint(remarks,transactionId);
                            complaintDialog.dismiss();
                        }
                        else
                            etRemarks.setError("Required");
                    });
                });


                imShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

                    }
                });

            }
        });

    }

    private void raiseComplaint(String remarks,String transactionId) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().makeComplaint(AUTH_KEY,userId,deviveId,deviceInfo,transactionId,remarks,
                "NA",transactionId,"Settlement");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setMessage(message)
                                .setTitle("Complain Status")
                                .setPositiveButton("ok",null)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Toast.makeText(context, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    Toast.makeText(context, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(context, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(context, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(activity, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            Bitmap bitmap = getScreenBitmap();
            shareReceipt(bitmap);

        }
    }

    public Bitmap getScreenBitmap() {
        Bitmap b = null;
        try {
            Bitmap bitmap = Bitmap.createBitmap(rechargeDialogView.getWidth(),
                    rechargeDialogView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rechargeDialogView.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            context.startActivity(Intent.createChooser(share, "Share link!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (isInitialReport) {
            if (allReportsModelArrayList.size() <= 10)
                return allReportsModelArrayList.size();
            else {
                return 10;
            }
        } else {
            return allReportsModelArrayList.size();
        }
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvTime, tvDate, tvNumber, tvAmount;
        TextView btnViewMore;
        ImageView imgStatus, imgService;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tv_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            imgStatus = itemView.findViewById(R.id.img_status);
            btnViewMore = itemView.findViewById(R.id.btn_view_more);
            imgService = itemView.findViewById(R.id.img_operator);
        }

        @SuppressLint("SetTextI18n")
        public void setData(String transactionId, String operatorName, String number, String amount, String commission
                , String cost, String balance, String date, String time, String status, String stype, String imgUrl) {

            if (status.equalsIgnoreCase("Failure") || status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("ERR")
                    || status.equalsIgnoreCase("Falied")) {
                imgStatus.setImageResource(R.drawable.failed);
            } else if (status.equalsIgnoreCase("Success")) {
                imgStatus.setImageResource(R.drawable.success);
            } else if (status.equalsIgnoreCase("Reversal")) {
                imgStatus.setImageResource(R.drawable.reversal);
            } else if (status.equalsIgnoreCase("Process")) {
                imgStatus.setImageResource(R.drawable.process);
            } else if (status.equalsIgnoreCase("Pending")) {
                imgStatus.setImageResource(R.drawable.pending);
            }


            imgService.setImageResource(R.drawable.bbps_logo);


            tvNumber.setText(number);
            tvAmount.setText("₹ " + amount);
            tvDate.setText(date);
            tvTime.setText(time);
        }
    }
}
