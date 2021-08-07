package com.example.sdetector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.EventDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiarylistFragment extends Fragment {
    private ViewGroup rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_diarylist, container, false);

        List<EventDay> events = new ArrayList<>();

        // 저장된 일기 캘린더에 감정으로 표시
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2021, 6, 22);
        events.add(new EventDay(calendar1, R.drawable.happy_emoticon));

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2021, 7, 1);
        events.add(new EventDay(calendar2, R.drawable.normal_emoticon));

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(2021, 7, 5);
        events.add(new EventDay(calendar3, R.drawable.sad_emoticon));

        com.applandeo.materialcalendarview.CalendarView calendarView = (com.applandeo.materialcalendarview.CalendarView) rootView.findViewById(R.id.calendarView);
        calendarView.setEvents(events);

        return rootView;
    }
}
