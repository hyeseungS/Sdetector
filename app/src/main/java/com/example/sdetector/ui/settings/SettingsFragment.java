package com.example.sdetector.ui.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sdetector.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Random;

public class SettingsFragment extends Fragment {
//    private static final int MAX_X_VALUE = 4;
//    private static final int MAX_Y_VALUE = 10;
//    private static final int MIN_Y_VALUE = 0;
//    private static final String SET_LABEL = " ";
//    private static final String[] APPS = { "인스타그램", "네이버", "카카오톡", "유튜브" };
//    private HorizontalBarChart chart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

//        chart = view.findViewById(R.id.chart);
//
//        BarData data = createChartData();
//        configureChartAppearance();
//        prepareChartData(data);

        return view;
    }

//    private void configureChartAppearance() {
//        chart.getDescription().setEnabled(false);
//        chart.setTouchEnabled(false);
//        chart.setDrawGridBackground(false);
//        chart.getLegend().setEnabled(false);
//
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(false);
//        xAxis.setGranularity(1f);
//        xAxis.setTextSize(15f);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        YAxis axisLeft = chart.getAxisLeft();
//        axisLeft.setDrawGridLines(false);
//        axisLeft.setDrawAxisLine(false);
//        axisLeft.setAxisMinimum(0f); // 최소값
//        axisLeft.setAxisMaximum(10f); // 최대값
//        axisLeft.setGranularity(5f); // 값 만큼 라인선 설정
//        axisLeft.setDrawLabels(false); // 값 셋팅 설정
//
//        YAxis axisRight = chart.getAxisRight();
//        axisRight.setTextSize(15f);
//        axisRight.setDrawLabels(false);
//        axisRight.setDrawGridLines(false);
//        axisRight.setDrawAxisLine(false);
//
//        xAxis.setValueFormatter(new ValueFormatter() {
//
//            @Override
//            public String getFormattedValue(float value) {
//                return APPS[(int)value];
//            }
//        });
//    }
//
//    private BarData createChartData() {
//        ArrayList<BarEntry> values = new ArrayList<>();
//        for (int i = 0; i < MAX_X_VALUE; i++) {
//            float x = i;
//            float y = new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE;
//            values.add(new BarEntry(x, y));
//        }
//        BarDataSet set1 = new BarDataSet(values, SET_LABEL);
//        set1.setDrawIcons(false);
//        set1.setDrawValues(true);
//        set1.setColor(Color.GRAY);
//
//        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//
//        BarData data = new BarData(dataSets);
//        data.setBarWidth(0.5f);
//
//        return data;
//    }
//
//    private void prepareChartData(BarData data) {
//        data.setValueTextSize(15);
//        chart.setData(data);
//        chart.invalidate();
//    }
}

