package wts.com.mitrsewa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.models.MoneyReportModel;

public class DMTTransactionAdapter extends RecyclerView.Adapter<DMTTransactionAdapter.Viewholder> {

    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    Context context;


    public DMTTransactionAdapter(ArrayList<MoneyReportModel> moneyReportModelArrayList,Context context) {
        this.moneyReportModelArrayList = moneyReportModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_money_report,parent,false);

       return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String transactionId=moneyReportModelArrayList.get(position).getTransactionId();
        String amount=moneyReportModelArrayList.get(position).getAmount();
        String status=moneyReportModelArrayList.get(position).getStatus();
        String bankRRN=moneyReportModelArrayList.get(position).getBankRRN();
        String commission=moneyReportModelArrayList.get(position).getCommission();
        String surcharge=moneyReportModelArrayList.get(position).getSurcharge();

        if (status.equalsIgnoreCase("SUCCESS"))
        {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (status.equalsIgnoreCase("FAILED"))
        {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));

        }

        holder.tvTxnId.setText(transactionId);
        holder.tvAmount.setText(amount);
        holder.tvStatus.setText(status);
        holder.tvRRN.setText(bankRRN);
        holder.tvCommission.setText(commission);
        holder.tvSurcharge.setText(surcharge);

    }

    @Override
    public int getItemCount() {
        return moneyReportModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvTxnId,tvAmount,tvStatus,tvRRN,tvCommission,tvSurcharge;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvTxnId=itemView.findViewById(R.id.tv_txn_id);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvStatus=itemView.findViewById(R.id.tv_status);
            tvRRN=itemView.findViewById(R.id.tv_rrn);
            tvCommission=itemView.findViewById(R.id.tv_comm);
            tvSurcharge=itemView.findViewById(R.id.tv_charge);
        }
    }
}
