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
        graphButton2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphButton2.setVisibility(View.GONE);
                barChart2 = rootView.findViewById(R.id.chart2);

                BarData data = createChartData();
                configureChartAppearance();
                prepareChartData(data);
                barChart2.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
        //xml 레이아웃이 인플레이트 되고 자바소스 코드와 연결이된다.
    }

    private void configureChartAppearance() {
        barChart2.getDescription().setEnabled(false);
        barChart2.setTouchEnabled(false);
        barChart2.getLegend().setEnabled(false);
        barChart2.setExtraOffsets(10f, 0f, 40f, 0f);

        XAxis xAxis = barChart2.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis axisLeft = barChart2.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0f); // 최소값
        axisLeft.setAxisMaximum(50f); // 최대값
        axisLeft.setGranularity(1f); // 값 만큼 라인선 설정
        axisLeft.setDrawLabels(false); // 값 셋팅 설정

        YAxis axisRight = barChart2.getAxisRight();
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
        BarDataSet set2 = new BarDataSet(values, SET_LABEL);
        set2.setDrawIcons(false);
        set2.setDrawValues(true);
        set2.setColor(Color.parseColor("#66767676"));
        set2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String.valueOf((int) value)) + "회";
            }
        });

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set2);

        BarData data = new BarData(dataSets);
        data.setBarWidth(0.5f);

        return data;
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(15);
        barChart2.setData(data);
        barChart2.invalidate();
    }
}