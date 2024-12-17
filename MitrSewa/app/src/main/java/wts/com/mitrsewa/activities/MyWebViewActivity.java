package wts.com.mitrsewa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import wts.com.mitrsewa.R;

public class MyWebViewActivity extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;
    String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);

        url=getIntent().getStringExtra("url");

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new mywebview(MyWebViewActivity.this));
        webView.loadUrl(url);

        progressDialog = new ProgressDialog(MyWebViewActivity.this);
        // progressDialog.setMax(100);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Connect to internet");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class mywebview extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //Toast.makeText(MainActivity.this, "In On Page Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Toast.makeText(MainActivity.this, "In on page finished", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }

        public mywebview(MyWebViewActivity mainActivity) {
        }
    }

}