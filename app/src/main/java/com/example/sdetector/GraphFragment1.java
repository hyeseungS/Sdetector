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
import android.provider.Settings;
import android.util.LongSparseArray;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static String[] APPS;
    private static String[] TIME_NAME = new String[4]; // 앱 이름
    private static float[] TIME_DATA = new float[4]; // 앱 사용 시간 데이터
    private HorizontalBarChart barChart1;

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
                String ret[] = get_apps_name2();
                for (String s : ret){
                    System.out.println(s);
                }

                // 앱 이름(TIME_NAME), 시간(TIME_DATA) 불러오기
                APPS = get_apps_name();
                int index1 = 3, index2 = 3;
                for (int i = 0; i < APPS.length; i++) {
                    if (i % 2 == 0) TIME_NAME[index1--] = APPS[i];
                    else TIME_DATA[index2--] = Float.parseFloat(APPS[i]);
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
        axisLeft.setAxisMaximum(TIME_DATA[3] + 6f); // 최댓값
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
                return TIME_NAME[(int) value];
            }
        });
    }

    // BarChart에 BarData 생성
    private BarData createChartData() {

        // 1. [BarEntry] BarChart에 표시될 데이터 값 생성
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            float y = TIME_DATA[i];
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
                        + (String.valueOf((int) ((value - (int) value) * 100))) + "분";
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

    // 아래부터 예슬
    private class Pair {
        String name;
        long time;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String[] get_apps_name() {
        if (!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        String PackageName = "Nothing";
        long TimeInforground = 500;
        int minutes = 500, seconds = 500, hours = 500;
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getContext().getSystemService(USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        long cur_time = System.currentTimeMillis(), begin_time = cal.getTimeInMillis();
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, begin_time, cur_time);
        if (stats != null) {
            ArrayList<Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                TimeInforground = usageStats.getTotalTimeInForeground();
                PackageName = usageStats.getPackageName();
                Pair tmp = new Pair();
                tmp.name = PackageName;
                tmp.time = TimeInforground;
                list.add(tmp);
                //minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
                //seconds = (int) (TimeInforground / 1000) % 60;
                //hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);
                //System.out.println("PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
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
                minutes = (int) ((p.time / (1000 * 60)) % 60);
                seconds = (int) (p.time / 1000) % 60;
                hours = (int) ((p.time / (1000 * 60 * 60)) % 24);
                // System.out.println("PackageName is" + p.name + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
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
    public String[] get_apps_name2() {

        // UsageStatsManager 선언
        UsageStatsManager usageStatsManager = (UsageStatsManager) getContext().getSystemService(USAGE_STATS_SERVICE);

        long lastRunAppTimeStamp = 0L;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        final long end = System.currentTimeMillis();
        // 1 minute ago
        final long begin = cal.getTimeInMillis();

        //
        LongSparseArray packageNameMap = new LongSparseArray<>();

        // 수집한 이벤트들을 담기 위한 UsageEvents
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        // 이벤트가 여러개 있을 경우 (최소 존재는 해야 hasNextEvent가 null이 아니니까)
        while (usageEvents.hasNextEvent()) {

            // 현재 이벤트를 가져오기
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

            // 현재 이벤트가 포그라운드 상태라면 = 현재 화면에 보이는 앱이라면
//            if (isForeGroundEvent(event)) {
//                // 해당 앱 이름을 packageNameMap에 넣는다.
//                packageNameMap.put(event.getTimeStamp(), event.getPackageName());
//                // 가장 최근에 실행 된 이벤트에 대한 타임스탬프를 업데이트 해준다.
//                if (event.getTimeStamp() > lastRunAppTimeStamp) {
//                    lastRunAppTimeStamp = event.getTimeStamp();
//                }
//            }
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
