package wts.com.mitrsewa.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.ShareCmsReportActivity;
import wts.com.mitrsewa.models.CMSReportModel;

public class CmsReportAdapter extends RecyclerView.Adapter<CmsReportAdapter.CmsViewHolder> {

    ArrayList<CMSReportModel> cmsReportModelArrayList;

    public CmsReportAdapter(ArrayList<CMSReportModel> cmsReportModelArrayList) {
        this.cmsReportModelArrayList = cmsReportModelArrayList;
    }

    @NonNull
    @Override
    public CmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cms_report,parent,false);
        return new CmsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CmsViewHolder holder, int position) {
        String transactionId=cmsReportModelArrayList.get(position).getTransactionId();
        String date=cmsReportModelArrayList.get(position).getDate();
        String time=cmsReportModelArrayList.get(position).getTime();
        String openingBalance=cmsReportModelArrayList.get(position).getOpeningBalance();
        String amount=cmsReportModelArrayList.get(position).getAmount();
        String commission=cmsReportModelArrayList.get(position).getCommission();
        String surcharge=cmsReportModelArrayList.get(position).getSurcharge();
        String closingBalance=cmsReportModelArrayList.get(position).getClosingBalance();
        String status=cmsReportModelArrayList.get(position).getStatus();
        String agentName=cmsReportModelArrayList.get(position).getAgentName();
        String billerName=cmsReportModelArrayList.get(position).getBillerName();
        String userId=cmsReportModelArrayList.get(position).getUserId();
        String ownerName=cmsReportModelArrayList.get(position).getOwnerName();


        holder.tvTransactionId.setText(": \t"+transactionId);
        holder.tvAgentName.setText(": \t"+agentName);
        holder.tvBillerName.setText(": \t"+billerName);
        holder.tvUserId.setText(": \t"+userId);
        holder.tvOwnerName.setText(": \t"+ownerName);
        holder.tvDate.setText(": \t"+date);
        holder.tvTime.setText(": \t"+time);
        holder.tvOpeningBalance.setText(": \t₹ "+openingBalance);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvCommission.setText(": \t₹ "+commission);
        holder.tvSurcharge.setText(": \t₹ "+surcharge);
        holder.tvClosingBalance.setText(": \t₹ "+closingBalance);
        holder.tvStatus.setText(status);

        if (status.equalsIgnoreCase("success") || status.equalsIgnoreCase("succession"))
        {
            holder.tvStatus.setBackgroundResource(R.drawable.button_back_green);
        } else if (status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("failure")) {
            holder.tvStatus.setBackgroundResource(R.drawable.button_back2);
        }
        else
        {
            holder.tvStatus.setBackgroundResource(R.drawable.yellow_back);
        }

        holder.imgShare.setOnClickListener(view ->
        {
            Intent in = new Intent(view.getContext(), ShareCmsReportActivity.class);
            in.putExtra("txnId", transactionId);
            in.putExtra("agent", agentName);
            in.putExtra("biller", billerName);
            in.putExtra("userId", userId);
            in.putExtra("userName", ownerName);
            in.putExtra("amount", amount);
            in.putExtra("closingBalance" , closingBalance);
            in.putExtra("commission" , commission);
            in.putExtra("cost" , amount);
            in.putExtra("status", status);
            in.putExtra("date", date);
            in.putExtra("time", time);
            view.getContext().startActivity(in);
        });

    }

    @Override
    public int getItemCount() {
        return cmsReportModelArrayList.size();
    }

    public static class CmsViewHolder extends RecyclerView.ViewHolder{
        ImageView imgShare;
        TextView tvTransactionId,tvAgentName, tvBillerName,tvUserId, tvOwnerName, tvDate,tvTime,tvOpeningBalance,tvAmount,tvCommission,tvSurcharge,tvClosingBalance,tvStatus;

        public CmsViewHolder(@NonNull View itemView) {
            super(itemView);

            imgShare = itemView.findViewById(R.id.imgShare);
            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvAgentName=itemView.findViewById(R.id.tv_agentName);
            tvBillerName=itemView.findViewById(R.id.tv_billerName);
            tvUserId=itemView.findViewById(R.id.tv_userId);
            tvOwnerName=itemView.findViewById(R.id.tv_ownerName);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvTime=itemView.findViewById(R.id.tv_time);
            tvOpeningBalance=itemView.findViewById(R.id.tv_opening_bal);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvCommission=itemView.findViewById(R.id.tv_commission);
            tvSurcharge=itemView.findViewById(R.id.tv_surcharge);
            tvClosingBalance=itemView.findViewById(R.id.tv_closing_bal);
            tvStatus=itemView.findViewById(R.id.tv_status);
        }
    }
}
