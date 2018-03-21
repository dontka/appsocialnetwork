package com.Zamblek.Sngine;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//import android.widget.Toast;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MyWb extends Activity
{
    WebView web;
    private AlertDialog.Builder Notify;
    URL c = new URL();
    private SwipeRefreshLayout swipeContainer;

//    ProgressBar progressBar;

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE=1;
    private final static int KITKAT_RESULTCODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if(requestCode==FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        web = (WebView) findViewById(R.id.activity_main_webview);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(c.url);
        web.setWebViewClient(new myWebClient());

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                web.reload();
                ( new Handler()).postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        swipeContainer.setRefreshing(false);

                    }
                }, 6000);


            }
        });

        if(CheckNetwork.isInternetAvailable(MyWb.this))
        {

        }
        else
        {
            refresh();
        }

       // web = new WebView(this);

        web.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                web.setVisibility(View.INVISIBLE);
            }
        });

        web.setWebChromeClient(new WebChromeClient()
        {

            public void openFileChooser(ValueCallback<Uri> uploadMsg) { /* Default code */ }

            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) { /* Default code */ }


            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("​*/*​");
                String[] mimetypes = {"image/*", "video/*", "audio/*"};
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

                MyWb.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MyWb.FILECHOOSER_RESULTCODE);

            }

            public void showPicker( ValueCallback<Uri> uploadMsg ){
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("​*/*​");
                String[] mimetypes = {"image/*", "video/*", "audio/*"};
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

                MyWb.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MyWb.FILECHOOSER_RESULTCODE);

            }

        });

//        setContentView(web);

    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            swipeContainer.setRefreshing(false);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    public void  refresh()
    {   createDialog();
        Notify.show();
    }

    public void createDialog()
    {
        Notify = new AlertDialog.Builder(this);
        Notify.setTitle("No Internet Connection");
        Notify.setPositiveButton("Refresh",
                new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, int which) {
                        if (isNetworkAvailable()) {
                            web.setVisibility(View.VISIBLE);
                            web.reload();
                            Thread timer = new Thread() {
                                public void run() {
                                    try {
                                        sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } finally {
                                        dialog.dismiss();
                                    }
                                }
                            };
                            timer.start();

                        } else {
                            refresh();
                        }
                    }

                });
    }

    @Override
public boolean onKeyDown(int keyCode, KeyEvent event)
{
    if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
        web.goBack();
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }

}