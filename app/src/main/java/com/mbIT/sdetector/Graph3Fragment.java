package com.mbIT.sdetector;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.BuildConfig;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public class Graph3Fragment extends Fragment {

    public LineChart lineChart1;
    public LineChart lineChart2;
    public LineChart lineChart3;

    private static final String[][] LABEL = {{"", "", "", ""}, {"3주 전", "2주 전", "1주 전", "이번 주"}, {"3달 전", "2달 전", "1달 전", "이번 달"}};
    private static int[][] RANGE = {{8, 20, 40}, {2, 5, 10}};
    private static String[] APPS;
    private static String[] APPS_NAME = new String[4];
    private static float[][] APPS_DATA = new float[4][4];
    ViewGroup rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // 그래프 날짜 지정(일간)
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd");
        int i = 3;
        do {
            LABEL[0][i--] = formatter.format(cal.getTime());
            cal.add(Calendar.DATE, -1);
        } while (i >= 0);
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_graph3, container, false);

        Button button1 = (Button) rootView.findViewById(R.id.day_button);
        button1.setOnClickListener(this::onClick);
        Button button2 = (Button) rootView.findViewById(R.id.week_button);
        button2.setOnClickListener(this::onClick);
        Button button3 = (Button) rootView.findViewById(R.id.month_button);
        button3.setOnClickListener(this::onClick);

        lineChart1 = rootView.findViewById(R.id.day_time_chart);
        lineChart2 = rootView.findViewById(R.id.week_time_chart);
        lineChart3 = rootView.findViewById(R.id.month_time_chart);
        return rootView;
    }

    public void onClick(View view) {
        TextView time_text = (TextView) rootView.findViewById(R.id.detail_text);
        float max=0;

        switch (view.getId()) {
            case R.id.day_button:
                time_text.setText("- 일간 앱 사용 시간");
                // 앱 이름(TIME_NAME), 시간(TIME_DATA) 불러오기
                APPS = getAppsNameDaily(-1);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 == 0) APPS_NAME[i/2] = APPS[i];
                    else APPS_DATA[i/2][3] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameDaily(-2);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][2] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameDaily(-3);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][1] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameDaily(-4);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][0] = Float.parseFloat(APPS[i]);
                }
                for(int i=0; i< 4; i++) {
                    if(APPS_DATA[0][i]>max) {
                        max = APPS_DATA[0][i];
                    }
                }
                RANGE[0][0] = (int) (max + 3);
                RANGE[1][0] = RANGE[0][0]/4;
                LineData data1 = createChartData(0);
                configureChartAppearance(lineChart1, 0);
                prepareChartData(data1, lineChart1);
                MyMarkerView mv1 = new MyMarkerView(getContext(), R.layout.custom_marker_view);
                mv1.setChartView(lineChart1);
                lineChart1.setMarker(mv1);
                lineChart2.setVisibility(View.GONE);
                lineChart3.setVisibility(View.GONE);
                lineChart1.setVisibility(View.VISIBLE);
                break;
            case R.id.week_button:
                time_text.setText("- 주간 앱 사용 시간");
                // 앱 이름(TIME_NAME), 시간(TIME_DATA) 불러오기
                APPS = getAppsNameDaily(-7);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 == 0) APPS_NAME[i/2] = APPS[i];
                    else APPS_DATA[i/2][3] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameDaily(-14);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][2] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameDaily(-21);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][1] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameDaily(-28);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][0] = Float.parseFloat(APPS[i]);
                }
                for(int i=0; i< 4; i++) {
                    if(APPS_DATA[0][i]>max) {
                            max = APPS_DATA[0][i];
                    }
                }
                RANGE[0][1] = (int) (max + 3);
                RANGE[1][1] = RANGE[0][1]/4;

                LineData data2 = createChartData(1);
                configureChartAppearance(lineChart2, 1);
                prepareChartData(data2, lineChart2);
                MyMarkerView mv2 = new MyMarkerView(getContext(), R.layout.custom_marker_view);
                mv2.setChartView(lineChart2);
                lineChart2.setMarker(mv2);
                lineChart1.setVisibility(View.GONE);
                lineChart3.setVisibility(View.GONE);
                lineChart2.setVisibility(View.VISIBLE);
                break;
            case R.id.month_button:
                time_text.setText("- 월간 앱 사용 시간");
                APPS = getAppsNameMonthly(-1);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 == 0) APPS_NAME[i/2] = APPS[i];
                    else APPS_DATA[i/2][0] = Float.parseFloat(APPS[i]);
                }

                APPS = getAppsNameMonthly(-2);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][2] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameMonthly(-3);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][1] = Float.parseFloat(APPS[i]);
                }
                APPS = getAppsNameMonthly(-4);
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 != 0) APPS_DATA[i/2][0] = Float.parseFloat(APPS[i]);
                }
                for(int i=0; i < 4; i++) {
                    if(APPS_DATA[0][i]>max) {
                        max = APPS_DATA[0][i];
                    }
                }
                RANGE[0][2] = (int) (max + 3);
                RANGE[1][2] = RANGE[0][2]/4;
                LineData data3 = createChartData(2);
                configureChartAppearance(lineChart3, 2);
                prepareChartData(data3, lineChart3);
                MyMarkerView mv3 = new MyMarkerView(getContext(), R.layout.custom_marker_view);
                mv3.setChartView(lineChart3);
                lineChart3.setMarker(mv3);
                lineChart1.setVisibility(View.GONE);
                lineChart2.setVisibility(View.GONE);
                lineChart3.setVisibility(View.VISIBLE);
                break;
        }
    }

    // LineChart 기본 세팅
    private void configureChartAppearance(LineChart lineChart, int range) {

        lineChart.setExtraBottomOffset(15f); // 간격
        lineChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무

        // Legend는 차트의 범례
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(10);
        legend.setTextSize(13);
        legend.setTextColor(Color.parseColor("#A3A3A3"));
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setYEntrySpace(5);
        legend.setWordWrapEnabled(true);
        legend.setXOffset(80f);
        legend.setYOffset(20f);
        legend.getCalculatedLineSizes();

        // XAxis (아래쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 데이터 표시 위치
        xAxis.setGranularity(1f);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.rgb(118, 118, 118));
        xAxis.setSpaceMin(0.1f); // Chart 맨 왼쪽 간격 띄우기
        xAxis.setSpaceMax(0.1f); // Chart 맨 오른쪽 간격 띄우기

        // YAxis(Right) (왼쪽) - 선 유무, 데이터 최솟값/최댓값, 색상
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setTextSize(14f);
        yAxisLeft.setTextColor(Color.rgb(163, 163, 163));
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setAxisLineWidth(2);
        yAxisLeft.setAxisMinimum(0f); // 최솟값
        yAxisLeft.setAxisMaximum((float) RANGE[0][range]); // 최댓값
        yAxisLeft.setGranularity((float) RANGE[1][range]);

        // YAxis(Left) (오른쪽) - 선 유무, 데이터 최솟값/최댓값, 색상
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setDrawLabels(false); // label 삭제
        yAxis.setTextColor(Color.rgb(163, 163, 163));
        yAxis.setDrawAxisLine(false);
        yAxis.setAxisLineWidth(2);
        yAxis.setAxisMinimum(0f); // 최솟값
        yAxis.setAxisMaximum((float) RANGE[0][range]); // 최댓값
        yAxis.setGranularity((float) RANGE[1][range]);

        // XAxis에 원하는 String 설정하기 (날짜)
        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return LABEL[range][(int) value];
            }
        });
    }

    // 4개의 앱(entry) 데이터 생성
    private LineData createChartData(int range) {
        ArrayList<Entry> entry1 = new ArrayList<>(); // 앱1
        ArrayList<Entry> entry2 = new ArrayList<>(); // 앱2
        ArrayList<Entry> entry3 = new ArrayList<>(); // 앱3
        ArrayList<Entry> entry4 = new ArrayList<>(); // 앱4

        LineData chartData = new LineData();

        // 랜덤 데이터 추출 (추후 변경)
        for (int i = 0; i < 4; i++) {
            float val1 = APPS_DATA[0][i]; // 앱1 값
            float val2 = APPS_DATA[1][i]; // 앱2 값
            float val3 = APPS_DATA[2][i]; // 앱3 값
            float val4 = APPS_DATA[3][i]; // 앱4 값
            entry1.add(new Entry(i, val1));
            entry2.add(new Entry(i, val2));
            entry3.add(new Entry(i, val3));
            entry4.add(new Entry(i, val4));
        }

        // 4개 앱의 DataSet 추가 및 선 커스텀 (추후 앱 이름 변경)

        // 앱1
        LineDataSet lineDataSet1 = new LineDataSet(entry1, APPS_NAME[0]);
        chartData.addDataSet(lineDataSet1);

        lineDataSet1.setLineWidth(3);
        lineDataSet1.setCircleRadius(6);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setDrawCircleHole(true);
        lineDataSet1.setDrawCircles(true);
        lineDataSet1.setDrawHorizontalHighlightIndicator(false);
        lineDataSet1.setDrawHighlightIndicators(false);
        lineDataSet1.setColor(Color.rgb(255, 155, 155));
        lineDataSet1.setCircleColor(Color.rgb(255, 155, 155));

        // 앱2
        LineDataSet lineDataSet2 = new LineDataSet(entry2, APPS_NAME[1]);
        chartData.addDataSet(lineDataSet2);

        lineDataSet2.setLineWidth(3);
        lineDataSet2.setCircleRadius(6);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setColor(Color.rgb(178, 223, 138));
        lineDataSet2.setCircleColor(Color.rgb(178, 223, 138));

        // 앱3
        LineDataSet lineDataSet3 = new LineDataSet(entry3, APPS_NAME[2]);
        chartData.addDataSet(lineDataSet3);

        lineDataSet3.setLineWidth(3);
        lineDataSet3.setCircleRadius(6);
        lineDataSet3.setDrawValues(false);
        lineDataSet3.setDrawCircleHole(true);
        lineDataSet3.setDrawCircles(true);
        lineDataSet3.setDrawHorizontalHighlightIndicator(false);
        lineDataSet3.setDrawHighlightIndicators(false);
        lineDataSet3.setColor(Color.rgb(166, 208, 227));
        lineDataSet3.setCircleColor(Color.rgb(166, 208, 227));

        // 앱4
        LineDataSet lineDataSet4 = new LineDataSet(entry4, APPS_NAME[3]);
        chartData.addDataSet(lineDataSet4);

        lineDataSet4.setLineWidth(3);
        lineDataSet4.setCircleRadius(6);
        lineDataSet4.setDrawValues(false);
        lineDataSet4.setDrawCircleHole(true);
        lineDataSet4.setDrawCircles(true);
        lineDataSet4.setDrawHorizontalHighlightIndicator(false);
        lineDataSet4.setDrawHighlightIndicators(false);
        lineDataSet4.setColor(Color.rgb(31, 120, 180));
        lineDataSet4.setCircleColor(Color.rgb(31, 120, 180));

        chartData.setValueTextSize(15);
        return chartData;
    }

    // LineChart에 LineData 설정
    private void prepareChartData(LineData data, LineChart lineChart) {
        lineChart.setData(data); // LineData 전달
        lineChart.invalidate(); // LineChart 갱신해 데이터 표시
    }

    private class Pair {
        String name;
        long time;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isForeGroundEvent(UsageEvents.Event event) {

        if (event == null) return false;

        if (BuildConfig.VERSION_CODE >= 29)
            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;

        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }

    private boolean checkPermission() {

        boolean granted = false;

        AppOpsManager appOps = (AppOpsManager) getActivity().getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getActivity().getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (getActivity().getApplicationContext().checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String[] getAppsNameDaily(int begin) { // (begin은 항상 음수)
        if (!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(USAGE_STATS_SERVICE);
        Calendar cal_begin = new GregorianCalendar(Locale.KOREA); Calendar cal_end = new GregorianCalendar(Locale.KOREA);
        cal_begin.add(Calendar.DATE, begin);
        long begin_time = cal_begin.getTimeInMillis();
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begin_time, cal_end.getTimeInMillis());
        if (stats != null) {
            ArrayList<Graph3Fragment.Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                Graph3Fragment.Pair tmp = new Graph3Fragment.Pair();
                tmp.name = usageStats.getPackageName();
                tmp.time = usageStats.getTotalTimeInForeground();
                list.add(tmp);
            }

            Collections.sort(list, new Comparator<Graph3Fragment.Pair>() {
                @Override
                public int compare(Graph3Fragment.Pair a, Graph3Fragment.Pair b) {
                    if (a.time < b.time)
                        return 1;
                    else if (a.time == b.time)
                        return 0;
                    return -1;
                }
            });

            int i = 0;
            for (Graph3Fragment.Pair p : list) {
                if (i > 3)
                    break;
                int minutes = (int) ((p.time / (1000 * 60)) % 60);
                int hours = (int) ((p.time / (1000 * 60 * 60)) % 24);
                // System.out.println("PackageName is" + p.name + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
                String s = p.name;
                String[] s2 = s.split("\\.");
                ret[i * 2] = s2[s2.length - 1];
                ret[i * 2 + 1] = Integer.toString(hours) + "." + (((int)Math.log10(minutes) == 0)? String.format("%02d", minutes) : Integer.toString(minutes));
                ++i;
            }
        }
        return ret;
    }

    private String[] getAppsNameMonthly(int begin) { // end의 경우 현재 날짜이면 0
        if (!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(USAGE_STATS_SERVICE);
        Calendar cal_begin = new GregorianCalendar(Locale.KOREA); Calendar cal_end = new GregorianCalendar(Locale.KOREA);
        cal_begin.add(Calendar.MONTH, begin);
        long begin_time = cal_begin.getTimeInMillis();
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, begin_time, cal_end.getTimeInMillis());
        if (stats != null) {
            ArrayList<Graph3Fragment.Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                Graph3Fragment.Pair tmp = new Graph3Fragment.Pair();
                tmp.name = usageStats.getPackageName();
                tmp.time = usageStats.getTotalTimeInForeground();
                list.add(tmp);
            }

            Collections.sort(list, new Comparator<Graph3Fragment.Pair>() {
                @Override
                public int compare(Graph3Fragment.Pair a, Graph3Fragment.Pair b) {
                    if (a.time < b.time)
                        return 1;
                    else if (a.time == b.time)
                        return 0;
                    return -1;
                }
            });

            int i = 0;
            for (Graph3Fragment.Pair p : list) {
                if (i > 3)
                    break;
                int minutes = (int) ((p.time / (1000 * 60)) % 60);
                int hours = (int) ((p.time / (1000 * 60 * 60)) % 24);
                String s = p.name;
                String[] s2 = s.split("\\.");
                ret[i * 2] = s2[s2.length - 1];
                ret[i * 2 + 1] = Integer.toString(hours) + "." + Integer.toString(minutes);
                ++i;
            }
        }
        return ret;
    }

}