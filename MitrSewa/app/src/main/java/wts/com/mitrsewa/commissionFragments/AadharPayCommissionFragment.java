package wts.com.mitrsewa.commissionFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.MyCommissionActivity;
import wts.com.mitrsewa.adapters.CommissionAdapter;
import wts.com.mitrsewa.models.MyCommissionModel;

public class AadharPayCommissionFragment extends Fragment {

    ArrayList<MyCommissionModel> myCommissionModelArrayList;

    ListView listView;

    CommissionAdapter commissionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_aadhar_pay_commission, container, false);

        myCommissionModelArrayList = MyCommissionActivity.aadharPayCommissionList;
        listView = view.findViewById(R.id.gas_list);
        commissionAdapter = new CommissionAdapter(getContext(),getActivity(), myCommissionModelArrayList);

        listView.setAdapter(commissionAdapter);
        return view;
    }
}