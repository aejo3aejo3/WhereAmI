package com.aaronlife.whereami;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();  // 初始化介面
        initPermission(); // 初始化權限
    }

    private WebView webView;
    private TextView txtInfo;

    // 首頁位置
    final static String MAP_URL = "file:///android_asset/www/index.html";

    private void initUI() {
        webView = (WebView) findViewById(R.id.webview);
        txtInfo = (TextView) findViewById(R.id.info);

        // 打開WebView的JavaScript功能
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(MAP_URL);

        // 將JavascriptInterface物件傳送給網頁
        webView.addJavascriptInterface(new JavaScriptInterface(this), "aaronho");

        webView.setWebViewClient(new WebViewClient() {
            // 網頁載入完成時被呼叫
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    final static int REQUEST_ACCESS_LOCATION = 0;

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_LOCATION);
        } else {
            // 開始定位服務
            setupLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 開始定位服務
                    setupLocationService();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    Location location;

    // 建立LocationListener來取得最新定位資訊
    LocationListener locationListener = new LocationListener() {
        // 位置變化時會觸發這個方法
        @Override
        public void onLocationChanged(Location location) {
            // 存下最新的定位資訊
            MainActivity.this.location = location;

            // 取得經緯度
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            // 更新資訊到TextView上
            float acc = location.getAccuracy(); // 精準度: 單位: 公尺
            float bea = location.getBearing();  // 方向: 單位: degree
            float spd = location.getSpeed();    // 速度: 小時/公尺
            float time = location.getTime();    // 該資訊的時間(1970年1月1日 道該時間經過得毫秒, UTC)

            txtInfo.setText("Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + acc + "\n" +
                    "Bearing: " + bea + "\n" +
                    "Speed: " + spd + "\n" +
                    "Time: " + time);


            // 呼叫JavaScript的moveTo()方法
            webView.loadUrl("javascript:moveTo(" + lat + ", " + lng + ");");

            Toast.makeText(MainActivity.this, "位置更新: " + lat + ", " + lng + "(" + location.getProvider() + ")", Toast.LENGTH_SHORT).show();
        }

        // 定位服務狀態變化時會被處發
        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Toast.makeText(MainActivity.this, provider + "服務中", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(MainActivity.this, provider + "沒有服務", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(MainActivity.this, provider + "暫時無法使用", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        // 定位功能開啟時會被呼叫
        @Override
        public void onProviderEnabled(String provider) // GPS Provider或Network Provider
        {
            Toast.makeText(MainActivity.this, provider + "被啟動了", Toast.LENGTH_SHORT).show();
        }

        // 定位功能關閉時會被呼叫
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, provider + "被關閉了", Toast.LENGTH_SHORT).show();
        }
    };

    // 設定LoacationListener到系統
    private void setupLocationService() {
        // 取得系統的LocationManager物件
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  // 要將傾聽器設頂給哪一個Provider
                1000,   // 定位間隔時間(毫秒)
                0,       // 位置移動多遠要更新
                locationListener); // 傾聽器

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  // 要將傾聽器設頂給哪一個Provider
                                                1000,   // 定位間隔時間(毫秒)
                                                0,       // 位置移動多遠要更新
                                                locationListener); // 傾聽器
    }

    public void getMe(View view)
    {
        if(location == null) return;

        // 取得經緯度
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        // 呼叫JavaScript的moveTo()方法
        webView.loadUrl("javascript:moveTo(" + lat + ", " + lng + ");");
    }

    // 從Javascript呼叫Java
    public class JavaScriptInterface
    {
        private Activity activity;

        public JavaScriptInterface(Activity activity)
        {
            this.activity = activity;
        }

        @android.webkit.JavascriptInterface
        public void showToast(String str)
        {
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
        }
    }
}
