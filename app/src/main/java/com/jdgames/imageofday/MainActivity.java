package com.jdgames.imageofday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    boolean expanded = true;
    LinearLayout linearLayout;
    TextView textView, titleTextView, descTextView;
    MaterialSwitch switcher;
    String NASA_API = "voduojpgG4fQRyOQJ3BXa5AkyuaDatuyBL5lH89E";
    String hdURL = "";
    String normalURL = "";

    public void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.nasa.gov/planetary/apod?api_key=" + NASA_API, response -> {
            try {
                JSONArray jsonArray = new JSONArray("[" + response + "]");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    normalURL = jsonObject.getString("url");
                    hdURL = jsonObject.getString("hdurl");
                    String title = jsonObject.getString("title");
                    String desc = jsonObject.getString("explanation");
                    textView.setVisibility(View.GONE);
                    switcher.setVisibility(View.VISIBLE);
                    Glide.with(this).load(normalURL).into(imageView);
                    titleTextView.setText(title);
                    descTextView.setText(desc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        RequestQueue request = Volley.newRequestQueue(this);
        request.getCache().clear();
        request.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        linearLayout = findViewById(R.id.linearLayout);
        titleTextView = findViewById(R.id.titleTextView);
        descTextView = findViewById(R.id.descTextView);
        textView = findViewById(R.id.textView);
        switcher = findViewById(R.id.switcher);

        switcher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Glide.with(MainActivity.this).load(isChecked ? hdURL : normalURL).into(imageView);
        });

        Glide.with(this).load(R.drawable.loading).into(imageView);
        titleTextView.setText(getString(R.string.loading));
        descTextView.setText(getString(R.string.loading));

        imageView.setOnClickListener(v -> {
            linearLayout.animate().setDuration(300).translationY(expanded ? 1000 : 0).start();
            getWindow().getDecorView().setSystemUiVisibility(expanded ? View.SYSTEM_UI_FLAG_HIDE_NAVIGATION : View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            expanded = !expanded;
        });

        networkFunction();
    }

    public void networkFunction() {
        if (isConnectionAvailable(this)) {
            loadData();
        } else {
            textView.setText(getString(R.string.refresh));
            textView.setOnClickListener(v -> {
                textView.setText(getString(R.string.loading));
                new Handler().postDelayed(this::networkFunction, 1000);

            });
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }
}