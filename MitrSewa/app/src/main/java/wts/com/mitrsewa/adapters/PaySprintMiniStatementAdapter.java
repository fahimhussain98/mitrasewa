package wts.com.mitrsewa.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.models.MiniStatementModel;

public class PaySprintMiniStatementAdapter extends RecyclerView.Adapter<PaySprintMiniStatementAdapter.Viewholder> {

    ArrayList<MiniStatementModel> miniStatementModelArrayList;

    public PaySprintMiniStatementAdapter(ArrayList<MiniStatementModel> miniStatementModelArrayList) {
        this.miniStatementModelArrayList = miniStatementModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mini_statement,parent,false);
        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String date=miniStatementModelArrayList.get(position).getDate();
        String txnType=miniStatementModelArrayList.get(position).getTxnType();
        String amount=miniStatementModelArrayList.get(position).getAmount();
        String description=miniStatementModelArrayList.get(position).getDescription();

        holder.tvDate.setText(date);
        holder.tvTxnType.setText(txnType);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvDescription.setText(description);



    }

    @Override
    public int getItemCount() {
        return miniStatementModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvDate,tvTxnType,tvAmount,tvDescription;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvDate=itemView.findViewById(R.id.tv_date);
            tvTxnType=itemView.findViewById(R.id.tv_txn_type);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvDescription=itemView.findViewById(R.id.tv_description);
        }
    }
}
