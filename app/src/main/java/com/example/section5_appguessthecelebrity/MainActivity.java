package com.example.section5_appguessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

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

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    String result, keyCelebrityName, value, key;
    Pattern p;
    Matcher m;
    GridLayout buttonGridLayout;
    Button button;
    ImageView celebrityImageView;
    String[] randomKeys = new String[4];
    Map<String, String> celebrityHashMap = new HashMap<>();

    public void initializeVars() {
        buttonGridLayout = findViewById(R.id.buttonGridLayout);
        celebrityImageView = findViewById(R.id.celebrityImageView);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

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

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap celebrityDownloadedBitmap = BitmapFactory.decodeStream(in);
                return celebrityDownloadedBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void downloadSiteContent() {

        result = null;
        celebrityHashMap.clear();
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
                celebrityHashMap.put(m.group(2), m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(celebrityHashMap);
    }

    public void insertCelebrityImage() {
        ImageDownloader task = new ImageDownloader();
        Bitmap celebrityBitmap;

        try {
            celebrityBitmap = task.execute(value).get();
            celebrityImageView.setImageBitmap(celebrityBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkAnswer(View view) {
        Button b = (Button) view;
        if (b.getText().toString().equals(key)) {
            Toast.makeText(this, "Correct answer!", Toast.LENGTH_SHORT).show();
            randomCelebrities();
        } else {
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
        }
    }

    //ZRÓB LOSOWANIE KLUCZY DO BUTTONA BEZ POWTÓRZEN!!!!!!!!!!!!!
    public void randomCelebrities() {
        String alreadyRandomed = "";

        for (int i = 0; i < buttonGridLayout.getChildCount(); i++) {
            keyCelebrityName = (String) Objects.requireNonNull(celebrityHashMap.keySet().toArray())[new Random().nextInt(Objects.requireNonNull(celebrityHashMap.keySet().toArray()).length)];
            button = (Button) buttonGridLayout.getChildAt(i);
            button.setText(keyCelebrityName);
            randomKeys[i] = keyCelebrityName;
            alreadyRandomed = keyCelebrityName;
        }

        Log.i("Keys", Arrays.toString(randomKeys));
        int index = new Random().nextInt(randomKeys.length);
        key = (randomKeys[index]);
        Log.i("Random key", key);
        value = celebrityHashMap.get(key);
        Log.i("Random value", value);
        insertCelebrityImage();

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVars();
        downloadSiteContent();
        randomCelebrities();
    }

}