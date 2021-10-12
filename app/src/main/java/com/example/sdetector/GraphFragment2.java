package com.example.sdetector;

import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.USAGE_STATS_SERVICE;

public class GraphFragment2 extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final int MAX_X_VALUE = 4;
    private static final String SET_LABEL = " ";
    private static String[] APPS;
    private static String[] FREQ_NAME = new String[4]; // 앱 이름
    private static float[] FREQ_DATA = new float[4]; // 앱 사용 빈도수 데이터
    private HorizontalBarChart barChart2;
    private static String IP_ADDRESS = "3.38.106.240";   //매번 ip주소 바꿔줄 것
    private static String TAG = "GraphFragment2";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_graph2, container, false);
        Button graphButton2 = (Button) rootView.findViewById(R.id.app_count_button);

        // 주간 앱 사용 횟수(회색) 버튼 이벤트
        graphButton2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ret[] = getAppsName(-7);
                for (String s : ret){
                    System.out.println(s);
                }

                // 앱 이름(TIME_NAME), 빈도수(FREQ_DATA) 불러오기
                APPS = getAppsName(-7);
                int index1 = 3, index2 = 3;
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 == 0) FREQ_NAME[index1--] = APPS[i];
                    else FREQ_DATA[index2--] = Float.parseFloat(APPS[i]);
                }

                //데이터 넘김 - WEEK_FREQ_APP, WEEK_FREQ_DATA
                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insertfrequency.php", FREQ_NAME[3], Float.toString(FREQ_DATA[3]), FREQ_NAME[2], Float.toString(FREQ_DATA[2]), FREQ_NAME[1], Float.toString(FREQ_DATA[1]),FREQ_NAME[0], Float.toString(FREQ_DATA[0]));

                // 주간 앱 사용 횟수 BarChart 보여주기
                graphButton2.setVisibility(View.GONE);
                barChart2 = rootView.findViewById(R.id.chart2);

                BarData data = createChartData();
                configureChartAppearance();
                prepareChartData(data);
                barChart2.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(), "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String app1 =  (String) params[1];
            String frequency1 = (String) params[2];
            String app2 =  (String) params[3];
            String frequency2 = (String) params[4];
            String app3 =  (String) params[5];
            String frequency3 = (String) params[6];
            String app4 =  (String) params[7];
            String frequency4 = (String) params[8];
            String serverURL = (String) params[0];
            String postParameters = "app1=" + app1 + "&frequency1=" + frequency1
                    + "&app2=" + app2 + "&frequency2=" + frequency2
                    + "&app3=" + app3 + "&frequency3=" + frequency3
                    + "&app4=" + app4 + "&frequency4=" + frequency4;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                Toast.makeText(getActivity().getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                MainActivity activity = (MainActivity) getActivity();
                activity.moveToDiaryList();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
    }

    // BarChart 기본 세팅
    private void configureChartAppearance() {

        barChart2.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart2.setTouchEnabled(false); // 터치 유무
        barChart2.getLegend().setEnabled(false); // Legend는 차트의 범례
        barChart2.setExtraOffsets(10f, 0f, 40f, 0f);

        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        XAxis xAxis = barChart2.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X 축 데이터 표시 위치

        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
        YAxis axisLeft = barChart2.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0f); // 최솟값
        axisLeft.setAxisMaximum(FREQ_DATA[3]+6f); // 최댓값
        axisLeft.setGranularity(1f); // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false); // label 삭제

        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        YAxis axisRight = barChart2.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false); // label 삭제
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        // XAxis에 원하는 String 설정하기 (앱 이름)
        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return FREQ_NAME[(int) value];
            }
        });
    }

    // BarChart에 BarData 생성
    private BarData createChartData() {

        // 1. [BarEntry] BarChart에 표시될 데이터 값 생성
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            float y = FREQ_DATA[i];
            values.add(new BarEntry(x, y));
        }

        // 2. [BarDataSet] 단순 데이터를 막대 모양으로 표시, BarChart의 막대 커스텀
        BarDataSet set2 = new BarDataSet(values, SET_LABEL);
        set2.setDrawIcons(false);
        set2.setDrawValues(true);
        set2.setColor(Color.parseColor("#66767676")); // 색상 설정
        // 데이터 값 원하는 String 포맷으로 설정하기 (ex. ~회)
        set2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String.valueOf((int) value)) + "회";
            }
        });

        // 3. [BarData] 보여질 데이터 구성
        BarData data = new BarData(set2);
        data.setBarWidth(0.5f);
        data.setValueTextSize(15);

        return data;
    }

    // BarChart에 BarData 설정
    private void prepareChartData(BarData data) {
        barChart2.setData(data); // BarData 전달
        barChart2.invalidate(); // BarChart 갱신해 데이터 표시
    }



    private class Pair {
        String name;
        long time;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String[] getAppsName(int begin) {

        UsageStatsManager usageStatsManager = (UsageStatsManager) getContext().getSystemService(USAGE_STATS_SERVICE);

        Calendar cal_begin = new GregorianCalendar(Locale.KOREA); Calendar cal_end = new GregorianCalendar(Locale.KOREA);
        cal_begin.add(Calendar.DATE, begin);
        long begin_time = cal_begin.getTimeInMillis();
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin_time, cal_end.getTimeInMillis());
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        while (usageEvents.hasNextEvent()) {

            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            int eventType = event.getEventType();
            if (eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                String name = event.getPackageName();
                if (map.get(name) == null)
                    map.put(name, 1);
                else
                    map.put(name, map.get(name) + 1);
            }
        }

        ArrayList<Pair> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Pair tmp = new Pair();
            tmp.name = entry.getKey();
            tmp.time = entry.getValue();
            result.add(tmp);
        }

        Collections.sort(result, new Comparator<Pair>() {
            @Override
            public int compare(Pair a, Pair b) {
                if (a.time < b.time)
                    return 1;
                else if (a.time == b.time)
                    return 0;
                return -1;
            }
        });

        String[] ret = new String[8];

        int i = 0;
        for (Pair p : result) {
            if (i > 3)
                break;
            String s = p.name;
            String[] s2 = s.split("\\.");
            ret[i * 2] = s2[s2.length - 1];
            ret[i * 2 + 1] = Integer.toString((int)p.time);
            ++i;
        }

        return ret;
    }

}