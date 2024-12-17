package wts.com.mitrsewa.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.models.AffiliateModel;


public class AffiliateItemAdapter extends RecyclerView.Adapter<AffiliateItemAdapter.MyViewHolder> {

    ArrayList<AffiliateModel> arrayList;
    Context context;

    public AffiliateItemAdapter(ArrayList<AffiliateModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.affiliate_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String title = arrayList.get(position).getTitle();
        String icon = arrayList.get(position).getImageUrl();
        String link = arrayList.get(position).getLink();

        holder.tvTitle.setText(title);
        Picasso.get().load(icon).error(R.drawable.logo).into(holder.imgIcon);

        holder.itemView.setOnClickListener(view ->
        {
            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(in);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgIcon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }

}
