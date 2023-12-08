package com.example.neurosight;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Visualisation extends AppCompatActivity {
    private static final String API_KEY = "BBFF-061a17f1ad45ac3edf8c0ef2e17c9d16c08";
    private static final String ECG_VARIABLE_ID = "648f6c9dea54f4000bd51d36";
    private static final String EEG_VARIABLE_ID = "648f2cf358ba49000ea5158b";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());

    private LineChart ecgChart;
    private LineChart eegChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation);

        ecgChart = findViewById(R.id.chartECG);
        eegChart = findViewById(R.id.chartEEG);

        handleUbidots();
    }

    private void handleUbidots() {
        initChart(ecgChart);
        initChart(eegChart);

        OkHttpClient client = new OkHttpClient();


        // Request for ECG Variable
        Request ecgRequest = new Request.Builder()
                .addHeader("X-Auth-Token", API_KEY)
                .url("https://industrial.ubidots.com/app/devices/648f6bd21545cb000ead4b63/648f6c9dea54f4000bd51d36/values")
                .build();

        client.newCall(ecgRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Chart", "Network error");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Log.d("Chart", body);

                List<Value> results = parseResponse(body);

                runOnUiThread(() -> onDataReady(results, ecgChart));
            }
        });

        // Request for EEG Variable
        // Request for EEG Variable
        Request eegRequest = new Request.Builder()
                .addHeader("X-Auth-Token", API_KEY)
                .url("https://industrial.ubidots.com/app/devices/648f6bd21545cb000ead4b63/648f2cf358ba49000ea5158b/values")
                .build();


        client.newCall(eegRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Chart", "Network error");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Log.d("Chart", body);

                List<Value> results = parseResponse(body);

                runOnUiThread(() -> onDataReady(results, eegChart));
            }
        });
    }

    private List<Value> parseResponse(String body) {
        List<Value> results = new ArrayList<>();

        try {
            JSONObject jObj = new JSONObject(body);
            JSONArray jRes = jObj.getJSONArray("results");
            for (int i = 0; i < jRes.length(); i++) {
                JSONObject obj = jRes.getJSONObject(i);
                Value val = new Value();
                val.timestamp = obj.getLong("timestamp");
                val.value = (float) obj.getDouble("value");
                results.add(val);
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }

        return results;
    }

    private void onDataReady(List<Value> results, LineChart chart) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            Entry entry = new Entry(results.get(i).value, i);
            entries.add(entry);

            Date date = new Date(results.get(i).timestamp);
            labels.add(sdf.format(date));
        }

        LineDataSet dataSet = new LineDataSet(entries, chart == ecgChart ? "ECG" : "EEG");
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setLineWidth(1f);
        dataSet.setCircleSize(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(Color.RED);

        List<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(labels, dataSets);

        chart.setData(lineData);
        chart.invalidate();
    }

    private void initChart(LineChart chart) {
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(true);
        chart.getAxisRight().setEnabled(false);
        chart.setDrawGridBackground(true);

        chart.getAxisLeft().setAxisLineWidth(2);
        chart.getAxisLeft().setDrawGridLines(true);

        chart.getXAxis().resetLabelsToSkip();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(true);
    }

    private static class Value {
        long timestamp;
        float value;
    }
}