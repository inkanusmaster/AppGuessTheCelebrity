package com.example.section5_appguessthecelebrity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String result, keyCelebrityName, value;
    Pattern p;
    Matcher m;
    Map<String, String> celebrityMap = new HashMap<>();
    GridLayout buttonGridLayout;
    Button button;
    String[] keys = new String[4];

    public void initializeVars() {
        buttonGridLayout = findViewById(R.id.buttonGridLayout);
    }


    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... httpUrl) {

            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(httpUrl[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed!";
            }

        }
    }


    public void downloadContent() {

        result = null;
        celebrityMap.clear();
        DownloadTask task = new DownloadTask();

        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            p = Pattern.compile("<img src=\"(.*?)\" alt=\"(.*?)\"/>");
            m = p.matcher(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            while (m.find()) {
                celebrityMap.put(m.group(2), m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(celebrityMap);

    }


    public void checkAnswer(View view) {

    }

    @SuppressLint("NewApi")
    public void randomCelebrities() {



        for (int i = 0; i < buttonGridLayout.getChildCount(); i++) {
            keyCelebrityName = (String) Objects.requireNonNull(celebrityMap.keySet().toArray())[new Random().nextInt(Objects.requireNonNull(celebrityMap.keySet().toArray()).length)];
            button = (Button) buttonGridLayout.getChildAt(i);
            button.setText(keyCelebrityName);
            keys[i] = keyCelebrityName;
        }

        Log.i("Keys", Arrays.toString(keys));

        int index = new Random().nextInt(keys.length);
        String randomKey = (keys[index]);
        Log.i("Random key", randomKey);
        value = celebrityMap.get(randomKey);
        Log.i("Random value",value);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVars();
        downloadContent();
        randomCelebrities();
    }

}