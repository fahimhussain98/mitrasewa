package wts.com.mitrsewa.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wts.com.mitrsewa.R;


public class OperatorDialogAdapter extends ArrayAdapter
{
    ArrayList<String> operatorNameList;
    ArrayList<String> operatorImageList;
    ImageView imgOperator;
    TextView tvOperator;
    Context context;

    public OperatorDialogAdapter(@NonNull Context context, ArrayList<String> operatorNameList, ArrayList<String> operatorImageList) {
        super(context, 0,operatorNameList);

        this.operatorNameList =operatorNameList;
        this.operatorImageList =operatorImageList;
        this.context =context;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_operator_list,null,false);
        imgOperator=view.findViewById(R.id.img_operator);
        tvOperator=view.findViewById(R.id.tv_operator);


        tvOperator.setText(operatorNameList.get(position));
        Picasso.get().load(operatorImageList.get(position)).into(imgOperator);


        return view;
    }
}
