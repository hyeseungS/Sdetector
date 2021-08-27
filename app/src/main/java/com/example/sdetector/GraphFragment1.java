package com.example.sdetector;

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
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Context.USAGE_STATS_SERVICE;

public class GraphFragment1 extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final int MAX_X_VALUE = 4; // 보여줄 앱 개수
    private static final String SET_LABEL = " ";
    private static String[] WEEK_APPS;
    private static String[] DAY_APPS;
    private static String[] MONTH_APPS;
    private static String[] WEEK_TIME_NAME = new String[4]; // 앱 이름
    private static float[] WEEK_TIME_DATA = new float[4]; // 앱 사용 시간 데이터
    private static String[] DAY_TIME_NAME = new String[4]; // 앱 이름
    private static float[] DAY_TIME_DATA = new float[4]; // 앱 사용 시간 데이터
    private static String[] MONTH_TIME_NAME = new String[4]; // 앱 이름
    private static float[] MONTH_TIME_DATA = new float[4]; // 앱 사용 시간 데이터
    private HorizontalBarChart barChart1;
    Graph3Fragment f;
    MoreDataList datalist;
    ArrayList<MoreData> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_graph1, container, false);
        Button graphButton1 = (Button) rootView.findViewById(R.id.app_time_button);

        // 주간 앱 사용 시간(회색) 버튼 이벤트
        graphButton1.setOnClickListener(new Button.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                // 아래 코드 디버깅용. 앱 이름, 시간 제대로 찍히는 거 확인!
                // (get_apps_name 사용 시 권한 허용 필요)
                String ret[] = get_apps_name_weekly();
                for (String s : ret){
                    System.out.println(s);
                }

                // 앱 이름(TIME_NAME), 시간(TIME_DATA) 불러오기
                WEEK_APPS = get_apps_name_weekly();
                int index1 = 3, index2 = 3;
                for (int i = 0; i < WEEK_APPS.length; i++) {
                    if (i % 2 == 0) WEEK_TIME_NAME[index1--] = WEEK_APPS[i];
                    else WEEK_TIME_DATA[index2--] = Float.parseFloat(WEEK_APPS[i]);
                }

                // 주간 앱 사용 시간 BarChart 보여주기
                graphButton1.setVisibility(View.GONE);
                barChart1 = rootView.findViewById(R.id.chart1);
                BarData data = createChartData();
                configureChartAppearance();
                prepareChartData(data);
                barChart1.setVisibility(View.VISIBLE);
            }
        });

        // 더보기 버튼 이벤트
        Button moreButton = (Button) rootView.findViewById(R.id.more_button);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) getActivity();
                activity.moveToDetail();
            }
        });

        return rootView;
    }

    // BarChart 기본 세팅
    private void configureChartAppearance() {
        barChart1.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart1.setTouchEnabled(false); // 터치 유무
        barChart1.getLegend().setEnabled(false); // Legend는 차트의 범례
        barChart1.setExtraOffsets(10f, 0f, 40f, 0f); // 간격

        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        XAxis xAxis = barChart1.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 데이터 표시 위치

        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
        YAxis axisLeft = barChart1.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0f); // 최솟값
        axisLeft.setAxisMaximum(WEEK_TIME_DATA[3] + 6f); // 최댓값
        axisLeft.setGranularity(1f); // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false); // label 삭제

        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        YAxis axisRight = barChart1.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false); // label 삭제
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        // XAxis에 원하는 String 설정하기 (앱 이름)
        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return WEEK_TIME_NAME[(int) value];
            }
        });
    }

    // BarChart에 BarData 생성
    private BarData createChartData() {

        // 1. [BarEntry] BarChart에 표시될 데이터 값 생성
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            float y = WEEK_TIME_DATA[i];
            values.add(new BarEntry(x, y));
        }

        // 2. [BarDataSet] 단순 데이터를 막대 모양으로 표시, BarChart의 막대 커스텀
        BarDataSet set1 = new BarDataSet(values, SET_LABEL);
        set1.setDrawIcons(false);
        set1.setDrawValues(true);
        set1.setColor(Color.parseColor("#66767676")); // 색상 설정

        // 데이터 값 원하는 String 포맷으로 설정하기 (ex. ~시간~분)
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return (((int) value != 0) ? (String.valueOf((int) value)) + "시간 " : "")
                        + ((String.valueOf((int) ((value - (int) value) * 100))) + "분");
            }
        });

        // 3. [BarData] 보여질 데이터 구성
        BarData data = new BarData(set1);
        data.setBarWidth(0.5f);
        data.setValueTextSize(15);

        return data;
    }

    // BarChart에 BarData 설정
    private void prepareChartData(BarData data) {
        barChart1.setData(data); // BarData 전달
        barChart1.invalidate(); // BarChart 갱신해 데이터 표시
    }

    private class Pair {
        String name;
        long time;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String[] get_apps_name_weekly() {
        if (!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
//        System.out.println("Calendar ::::   " + cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DAY_OF_MONTH));
        long cur_time = System.currentTimeMillis(), begin_time = cal.getTimeInMillis();
//        System.out.println(cur_time + " " + begin_time);
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, begin_time, cur_time);
        if (stats != null) {
            ArrayList<Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                Pair tmp = new Pair();
                tmp.name = usageStats.getPackageName();
                tmp.time = usageStats.getTotalTimeInForeground();
                list.add(tmp);
            }

            Collections.sort(list, new Comparator<Pair>() {
                @Override
                public int compare(Pair a, Pair b) {
                    if (a.time < b.time)
                        return 1;
                    else if (a.time == b.time)
                        return 0;
                    return -1;
                }
            });

            int i = 0;
            for (Pair p : list) {
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

    private String[] get_apps_name_daily() {
        if (!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
//        System.out.println("Calendar ::::   " + cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DAY_OF_MONTH));
        long cur_time = System.currentTimeMillis(), begin_time = cal.getTimeInMillis();
//        System.out.println(cur_time + " " + begin_time);
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begin_time, cur_time);
        if (stats != null) {
            ArrayList<Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                Pair tmp = new Pair();
                tmp.name = usageStats.getPackageName();
                tmp.time = usageStats.getTotalTimeInForeground();
                list.add(tmp);
            }

            Collections.sort(list, new Comparator<Pair>() {
                @Override
                public int compare(Pair a, Pair b) {
                    if (a.time < b.time)
                        return 1;
                    else if (a.time == b.time)
                        return 0;
                    return -1;
                }
            });

            int i = 0;
            for (Pair p : list) {
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

    private String[] get_apps_name_monthly() {
        if (!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
//        System.out.println("Calendar ::::   " + cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DAY_OF_MONTH));
        long cur_time = System.currentTimeMillis(), begin_time = cal.getTimeInMillis();
//        System.out.println(cur_time + " " + begin_time);
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, begin_time, cur_time);
        if (stats != null) {
            ArrayList<Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                Pair tmp = new Pair();
                tmp.name = usageStats.getPackageName();
                tmp.time = usageStats.getTotalTimeInForeground();
                list.add(tmp);
            }

            Collections.sort(list, new Comparator<Pair>() {
                @Override
                public int compare(Pair a, Pair b) {
                    if (a.time < b.time)
                        return 1;
                    else if (a.time == b.time)
                        return 0;
                    return -1;
                }
            });

            int i = 0;
            for (Pair p : list) {
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


}
