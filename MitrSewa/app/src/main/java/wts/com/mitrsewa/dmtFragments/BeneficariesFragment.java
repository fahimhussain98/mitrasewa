package wts.com.mitrsewa.dmtFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.activities.NewMoneyTransferActivity;
import wts.com.mitrsewa.activities.SenderValidationActivity;
import wts.com.mitrsewa.adapters.RecipientAdapter;
import wts.com.mitrsewa.models.RecipientModel;

public class BeneficariesFragment extends Fragment {

    ArrayList<RecipientModel> recipientModelArrayList;
    RecyclerView recipientRecycler;
    String  mobileNumber;
    String userId, userName;
    SharedPreferences sharedPreferences;
    String deviceId,deviceInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beneficaries, container, false);
        inhitViews(view);

        mobileNumber = NewMoneyTransferActivity.senderMobileNumber;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        userName = sharedPreferences.getString("userNameResponse", null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

        recipientModelArrayList = SenderValidationActivity.recipientModelArrayList;

        RecipientAdapter recipientAdapter = new RecipientAdapter(getContext(), getActivity(),
                recipientModelArrayList, mobileNumber,  userId, userName,deviceId,deviceInfo);
        recipientRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));
        recipientRecycler.setAdapter(recipientAdapter);

        return view;

    }

    private void inhitViews(View view) {
        recipientRecycler = view.findViewById(R.id.recipient_recycler_view);
    }
}