package com.example.sdetector;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Random;

public class GraphFragment1 extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final int MAX_X_VALUE = 4;
    private static final int MAX_Y_VALUE = 10;
    private static final int MIN_Y_VALUE = 0;
    private static final String SET_LABEL = " ";
    private static final String[] APPS = { "인스타그램", "네이버", "카카오톡", "유튜브" };
    private HorizontalBarChart chart1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_graph1, container, false);
        Button graphButton1 = (Button) rootView.findViewById(R.id.app_time_button);
        graphButton1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphButton1.setVisibility(View.GONE);
                chart1 = rootView.findViewById(R.id.chart1);

                BarData data = createChartData();
                configureChartAppearance();
                prepareChartData(data);
                chart1.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
        //xml 레이아웃이 인플레이트 되고 자바소스 코드와 연결이된다.
    }
    private void configureChartAppearance() {
        chart1.getDescription().setEnabled(false);
        chart1.setTouchEnabled(false);
        chart1.setDrawGridBackground(false);
        chart1.getLegend().setEnabled(false);

        XAxis xAxis = chart1.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis axisLeft = chart1.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0f); // 최소값
        axisLeft.setAxisMaximum(10f); // 최대값
        axisLeft.setGranularity(1f); // 값 만큼 라인선 설정
        axisLeft.setDrawLabels(false); // 값 셋팅 설정

        YAxis axisRight = chart1.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return APPS[(int)value];
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
        set1.setColor(Color.GRAY);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setBarWidth(0.5f);

        return data;
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(15);
        chart1.setData(data);
        chart1.invalidate();
    }
}
