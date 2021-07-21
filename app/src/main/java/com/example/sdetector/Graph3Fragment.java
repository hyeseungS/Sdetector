package com.example.sdetector;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class Graph3Fragment extends Fragment {

    public LineChart lineChart1;
    public LineChart lineChart2;
    public LineChart lineChart3;

    private static final String[][] LABEL = {{"7.17", "7.18", "7.19", "7.20"}, {"3주 전", "2주 전", "1주 전", "이번 주"}, {"3달 전", "2달 전", "1달 전", "이번 달"}};
    private static final int[][] RANGE = {{8, 20, 40}, {2, 5, 10}};
    ViewGroup rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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

        LineData data1 = createChartData(0);
        configureChartAppearance(lineChart1, 0);
        prepareChartData(data1, lineChart1);
        LineData data2 = createChartData(1);
        configureChartAppearance(lineChart2, 1);
        prepareChartData(data2, lineChart2);
        LineData data3 = createChartData(2);
        configureChartAppearance(lineChart3, 2);
        prepareChartData(data3, lineChart3);
        MyMarkerView mv1 = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv1.setChartView(lineChart1);
        MyMarkerView mv2 = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv2.setChartView(lineChart2);
        MyMarkerView mv3 = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv3.setChartView(lineChart3);
        lineChart1.setMarker(mv1);
        lineChart2.setMarker(mv2);
        lineChart3.setMarker(mv3);

        return rootView;
    }

    public void onClick(View view) {
        TextView time_text = (TextView) rootView.findViewById(R.id.detail_text);

        switch (view.getId()) {
            case R.id.day_button:
                time_text.setText("- 일간 앱 사용 시간");
                lineChart2.setVisibility(View.GONE);
                lineChart3.setVisibility(View.GONE);
                lineChart1.setVisibility(View.VISIBLE);
                break;
            case R.id.week_button:
                time_text.setText("- 주간 앱 사용 시간");
                lineChart1.setVisibility(View.GONE);
                lineChart3.setVisibility(View.GONE);
                lineChart2.setVisibility(View.VISIBLE);
                break;
            case R.id.month_button:
                time_text.setText("- 월간 앱 사용 시간");
                lineChart1.setVisibility(View.GONE);
                lineChart2.setVisibility(View.GONE);
                lineChart3.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void configureChartAppearance(LineChart lineChart, int range) {
        lineChart.setExtraBottomOffset(15f);
        lineChart.getDescription().setEnabled(false);
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

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.rgb(118, 118, 118));
        xAxis.setSpaceMin(0.1f);
        xAxis.setSpaceMax(0.1f);

        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setTextColor(Color.LTGRAY);

        yAxis.setGranularity((float) RANGE[1][range]);
        yAxis.setAxisMinimum(0f); // 최소값
        yAxis.setAxisLineWidth(2);

        yAxis.setAxisMaximum((float) RANGE[0][range]); // 최대값

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setTextSize(14f);
        yAxisLeft.setAxisLineWidth(2);
        yAxisLeft.setAxisMinimum(0f); // 최소값
        yAxisLeft.setAxisMaximum((float) RANGE[0][range]); // 최대값
        yAxisLeft.setGranularity((float) RANGE[1][range]);
        yAxisLeft.setTextColor(Color.rgb(163, 163, 163));

        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return LABEL[range][(int) value];
            }
        });
    }

    private LineData createChartData(int range) {
        ArrayList<Entry> entry1 = new ArrayList<>();
        ArrayList<Entry> entry2 = new ArrayList<>();
        ArrayList<Entry> entry3 = new ArrayList<>();
        ArrayList<Entry> entry4 = new ArrayList<>();

        LineData chartData = new LineData();

        for (int i = 0; i < 4; i++) {

            float val1 = (float) (Math.random() * RANGE[0][range]);
            float val2 = (float) (Math.random() * RANGE[0][range]);
            float val3 = (float) (Math.random() * RANGE[0][range]);
            float val4 = (float) (Math.random() * RANGE[0][range]);
            entry1.add(new Entry(i, val1));
            entry2.add(new Entry(i, val2));
            entry3.add(new Entry(i, val3));
            entry4.add(new Entry(i, val4));
        }

        LineDataSet lineDataSet1 = new LineDataSet(entry1, "인스타그램");
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

        LineDataSet lineDataSet2 = new LineDataSet(entry2, "네이버");
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

        LineDataSet lineDataSet3 = new LineDataSet(entry3, "카카오톡");
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

        LineDataSet lineDataSet4 = new LineDataSet(entry4, "유튜브");
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

        return chartData;
    }

    private void prepareChartData(LineData data, LineChart lineChart) {
        data.setValueTextSize(15);
        lineChart.setData(data);
        lineChart.invalidate();
    }

}