package wts.com.mitrsewa.activities;

import static wts.com.mitrsewa.activities.SenderValidationActivity.isBeneCountZero;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.adapters.ViewPagerAdapter;
import wts.com.mitrsewa.dmtFragments.AddBeneFragment;
import wts.com.mitrsewa.dmtFragments.BeneficariesFragment;

public class NewMoneyTransferActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    public static ViewPager viewPager;
    TextView tvName,tvAvailableLimit,tvConsumedLimit,tvTotalLimit;
    public static String senderMobileNumber;
    static String senderName,availableLimit,totalLimit,consumedLimit;
    String deviceId,deviceInfo;
    String userId;
    SharedPreferences sharedPreferences;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_money_transfer);
        inhitViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(NewMoneyTransferActivity.this);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);
        userId=sharedPreferences.getString("userid",null);

        senderName=getIntent().getStringExtra("senderName");
        senderMobileNumber=getIntent().getStringExtra("senderMobileNumber");
        availableLimit=getIntent().getStringExtra("availableLimit");
        consumedLimit=getIntent().getStringExtra("consumedLimit");
        totalLimit=getIntent().getStringExtra("totalLimit");

        tvName.setText(senderName+" ("+senderMobileNumber+")");
        tvAvailableLimit.setText("Available Limit\n"+"₹"+availableLimit);
        tvConsumedLimit.setText("Consumed Limit\n"+"₹"+consumedLimit);
        tvTotalLimit.setText("Total Limit\n"+"₹"+totalLimit);

        setUpViewPager();
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (isBeneCountZero) {
            viewPagerAdapter.addFragments(new AddBeneFragment(), "Add");
            //viewPagerAdapter.addFragments(new BeneficariesFragment(), "Beneficiary");
            //viewPagerAdapter.addFragments(new TransactionsFragment(), "Transactions");
        }
        else

        {
            viewPagerAdapter.addFragments(new BeneficariesFragment(), "Beneficiary");
            //viewPagerAdapter.addFragments(new TransactionsFragment(), "Transactions");
            viewPagerAdapter.addFragments(new AddBeneFragment(), "Add");

        }


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        if (isBeneCountZero)
        {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.add_bene);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.beneficiary);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.list);
        }
        else
        {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.beneficiary);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.list);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.add_bene);
        }


    }

    private void inhitViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tablayout);
        tvName = findViewById(R.id.tv_name);
        tvAvailableLimit = findViewById(R.id.tv_available_limit);
        tvConsumedLimit = findViewById(R.id.tv_consumed_limit);
        tvTotalLimit = findViewById(R.id.tv_total_limit);
    }
}