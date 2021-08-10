package com.example.sdetector;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Context.USAGE_STATS_SERVICE;

public class GraphFragment1 extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final int MAX_X_VALUE = 4;
    private static final int MAX_Y_VALUE = 10;
    private static final int MIN_Y_VALUE = 0;
    private static final String SET_LABEL = " ";
    private static final String[] APPS = {"인스타그램", "네이버", "카카오톡", "유튜브"};
    private HorizontalBarChart barChart1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_graph1, container, false);
        Button graphButton1 = (Button) rootView.findViewById(R.id.app_time_button);
        graphButton1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 아래 코드 디버깅용. 앱 이름, 시간 제대로 찍히는 거 확인! 권한 허용 해줘야 함.
                String ret[] = get_apps_name();
                for (String s : ret){
                    System.out.println(s);
                }

                graphButton1.setVisibility(View.GONE);
                barChart1 = rootView.findViewById(R.id.chart1);

                BarData data = createChartData();
                configureChartAppearance();
                prepareChartData(data);
                barChart1.setVisibility(View.VISIBLE);
            }
        });

        Button moreButton = (Button) rootView.findViewById(R.id.more_button);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.moveToDetail();


            }
        });

        return rootView;
        //xml 레이아웃이 인플레이트 되고 자바소스 코드와 연결이된다.
    }

    private void configureChartAppearance() {
        barChart1.getDescription().setEnabled(false);
        barChart1.setTouchEnabled(false);
        barChart1.getLegend().setEnabled(false);
        barChart1.setExtraOffsets(10f, 0f, 40f, 0f);
        XAxis xAxis = barChart1.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis axisLeft = barChart1.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0f); // 최소값
        axisLeft.setAxisMaximum(10f); // 최대값
        axisLeft.setGranularity(1f); // 값 만큼 라인선 설정
        axisLeft.setDrawLabels(false); // 값 셋팅 설정

        YAxis axisRight = barChart1.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return APPS[(int) value];
            }
        });
    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            float y = new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE;
            values.add(new BarEntry(x, y));
        }
        BarDataSet set1 = new BarDataSet(values, SET_LABEL);
        set1.setDrawIcons(false);
        set1.setDrawValues(true);
        set1.setColor(Color.parseColor("#66767676"));
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String.valueOf((int) value)) + "시간";
            }
        });

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setBarWidth(0.5f);

        return data;
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(15);
        barChart1.setData(data);
        barChart1.invalidate();
    }

    // 아래부터 예슬
    private class Pair{
        String name;
        long time;
    }

    private String[] get_apps_name() {
        if(!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        String PackageName = "Nothing";
        long TimeInforground = 500;
        int minutes = 500, seconds = 500, hours = 500;
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getContext().getSystemService(USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 1000 * 10, time);
        if (stats != null) {
            ArrayList<Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                TimeInforground = usageStats.getTotalTimeInForeground();
                PackageName = usageStats.getPackageName();
                Pair tmp = new Pair(); tmp.name = PackageName; tmp.time = TimeInforground;
                list.add(tmp);
                //minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
                //seconds = (int) (TimeInforground / 1000) % 60;
                //hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);
                //System.out.println("PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
            }

            list.sort(new Comparator<Pair>() {
                @Override
                public int compare(Pair a, Pair b){
                    if (a.time < b.time)
                        return 1;
                    else if (a.time == b.time)
                        return 0;
                    return -1;
                }
            });

            int i = 0;
            for (Pair p : list){
                if (i > 3)
                    break;
                minutes = (int) ((p.time / (1000 * 60)) % 60);
                seconds = (int) (p.time / 1000) % 60;
                hours = (int) ((p.time / (1000 * 60 * 60)) % 24);
                // System.out.println("PackageName is" + p.name + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
                String s = p.name;
                String[] s2 = s.split("\\.");
                ret[i * 2] = s2[s2.length - 1];
                ret[i * 2 + 1] = Integer.toString(hours) + "시간" + Integer.toString(minutes) + "분";
                ++i;
            }
        }
        return ret;
    }

    public static String getPackageName(@NonNull Context context) {

        // UsageStatsManager 선언
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        long lastRunAppTimeStamp = 0L;

        // 얼마만큼의 시간동안 수집한 앱의 이름을 가져오는지 정하기 (begin ~ end 까지의 앱 이름을 수집한다)
        final long INTERVAL = 10000;
        final long end = System.currentTimeMillis();
        // 1 minute ago
        final long begin = end - INTERVAL;

        //
        LongSparseArray packageNameMap = new LongSparseArray<>();

        // 수집한 이벤트들을 담기 위한 UsageEvents
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);

        // 이벤트가 여러개 있을 경우 (최소 존재는 해야 hasNextEvent가 null이 아니니까)
        while (usageEvents.hasNextEvent()) {

            // 현재 이벤트를 가져오기
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            // 현재 이벤트가 포그라운드 상태라면 = 현재 화면에 보이는 앱이라면
            if(isForeGroundEvent(event)) {
                // 해당 앱 이름을 packageNameMap에 넣는다.
                packageNameMap.put(event.getTimeStamp(), event.getPackageName());
                // 가장 최근에 실행 된 이벤트에 대한 타임스탬프를 업데이트 해준다.
                if(event.getTimeStamp() > lastRunAppTimeStamp) {
                    lastRunAppTimeStamp = event.getTimeStamp();
                }
            }
        }
        // 가장 마지막까지 있는 앱의 이름을 리턴해준다.
        return packageNameMap.get(lastRunAppTimeStamp, "").toString();
    }


    private static boolean isForeGroundEvent(UsageEvents.Event event) {

        if(event == null) return false;

        if(BuildConfig.VERSION_CODE >= 29)
            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;

        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }
    private boolean checkPermission(){

        boolean granted = false;

        AppOpsManager appOps = (AppOpsManager) getActivity().getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getActivity().getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (getActivity().getApplicationContext().checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        }
        else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }
}
