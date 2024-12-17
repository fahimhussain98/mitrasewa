package wts.com.mitrsewa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.models.WalletToWalletReportModel;

public class WalletToWalletAdapter extends RecyclerView.Adapter<WalletToWalletAdapter.MyViewHolder> {

    ArrayList<WalletToWalletReportModel> walletToWalletReportModelArrayList;

    public WalletToWalletAdapter(ArrayList<WalletToWalletReportModel> walletToWalletReportModelArrayList) {
        this.walletToWalletReportModelArrayList = walletToWalletReportModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wallet_pay_report,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String dateTime=walletToWalletReportModelArrayList.get(position).getTxnDate();
        String referenceNo=walletToWalletReportModelArrayList.get(position).getReferenceNo();
        String paidBy=walletToWalletReportModelArrayList.get(position).getPaidBy();
        String receivedBy=walletToWalletReportModelArrayList.get(position).getReceivedBy();
        String amount=walletToWalletReportModelArrayList.get(position).getAmount();

        holder.tvDateTime.setText(dateTime);
        holder.tvReferenceNo.setText(referenceNo);
        holder.tvPaidBy.setText(paidBy);
        holder.tvReceivedBy.setText(receivedBy);
        holder.tvAmount.setText(amount);
    }

    @Override
    public int getItemCount() {
        return walletToWalletReportModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvDateTime,tvReferenceNo,tvPaidBy,tvReceivedBy,tvAmount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime=itemView.findViewById(R.id.tv_date);
            tvReferenceNo=itemView.findViewById(R.id.tv_reference_no);
            tvPaidBy=itemView.findViewById(R.id.tv_paid_by);
            tvReceivedBy=itemView.findViewById(R.id.tv_received_by);
            tvAmount=itemView.findViewById(R.id.tv_amount);
        }
    }
}
