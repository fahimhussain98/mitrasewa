package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import wts.com.mitrsewa.R;
import wts.com.mitrsewa.adapters.MyPagerAdapter;
import wts.com.mitrsewa.reportsFragment.AllReportFragment;

public class AllReportsActivity extends AppCompatActivity {

    ImageView backButton;
    TextView activityTitle;
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);

        inhitViews();

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(AllReportsActivity.this, R.color.purple));
        //////CHANGE COLOR OF STATUS BAR
        activityTitle.setText("Reports");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        myPagerAdapter.addFragment(new AllReportFragment(),"Recharge Report");
        //myPagerAdapter.addFragment(new PrepaidReportFragment(),"Prepaid");
        //myPagerAdapter.addFragment(new PostpaidReportFragment(),"Postpaid");
        //myPagerAdapter.addFragment(new DthReportFragment(),"DTH");
        viewPager.setAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void inhitViews() {
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
    }
}