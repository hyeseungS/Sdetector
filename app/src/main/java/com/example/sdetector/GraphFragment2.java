package com.example.sdetector;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Map;
import java.util.Random;

import static android.content.Context.USAGE_STATS_SERVICE;

public class GraphFragment2 extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final int MAX_X_VALUE = 4;
    private static final int MAX_Y_VALUE = 50;
    private static final int MIN_Y_VALUE = 0;
    private static final String SET_LABEL = " ";
    private static final String[] APPS = {"인스타그램", "네이버", "카카오톡", "유튜브"};
    private HorizontalBarChart barChart2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_graph2, container, false);
        Button graphButton2 = (Button) rootView.findViewById(R.id.app_count_button);

        // 주간 앱 사용 횟수(회색) 버튼 이벤트
        graphButton2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        axisLeft.setAxisMaximum(50f); // 최댓값
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
                return APPS[(int) value];
            }
        });
    }

    // BarChart에 BarData 생성
    private BarData createChartData() {

        // 1. [BarEntry] BarChart에 표시될 데이터 값 생성
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            float y = new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE;
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
    private String[] get_apps_name() {

        UsageStatsManager usageStatsManager = (UsageStatsManager) getContext().getSystemService(USAGE_STATS_SERVICE);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        final long end = System.currentTimeMillis();
        final long begin = cal.getTimeInMillis();

        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);
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