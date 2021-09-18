package com.example.sdetector.ui.report;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sdetector.BuildConfig;
import com.example.sdetector.R;
import com.example.sdetector.databinding.FragmentReportBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Context.USAGE_STATS_SERVICE;

public class ReportFragment extends Fragment {

    private ReportViewModel reportViewModel;
    private FragmentReportBinding binding;

    // 가져올 변수
    private static final int MAX_X_VALUE = 4; // 보여줄 앱 개수
    private static String[] APPS;
    private static String[] TIME_NAME = new String[4]; // 앱 이름
    private static float[] TIME_DATA = new float[4]; // 앱 사용 시간 데이터

    String AppTimeReport;
    float AppTime_Week;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reportViewModel =
                new ViewModelProvider(this).get(ReportViewModel.class);

        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 종합의견 String Format 설정
//        String name = "000님"; // 사용자 이름
//        Resources res = getResources();
//        String text = String.format(res.getString(R.string.report_total), name);
        ImageView report_emotion = (ImageView) root.findViewById(R.id.report_emotion);
        TextView report_appText = (TextView) root.findViewById(R.id.reportText_APP);
        TextView report_diaryText = (TextView) root.findViewById(R.id.report_text2);
        TextView report_treatText = (TextView) root.findViewById(R.id.report_text3);
        TextView report_totalText = (TextView) root.findViewById(R.id.report_text4);

        // 이번주 당신의 감정
        report_emotion.setImageResource(R.drawable.sad_emoticon);
        // 앱 사용 분석 내용
        report_appText.setText(AppTimeReport());
        // 감정 일기 분석 내용
        report_diaryText.setText(R.string.report_diary);
        // 관리 방법 내용
        report_treatText.setText(R.string.report_treat);
        // 종합 의견 내용
        report_totalText.setText(R.string.report_total);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 백엔드
    private class Pair {
        String name;
        long time;
    }

    // 가져올 기간 정하기
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
            ArrayList<ReportFragment.Pair> list = new ArrayList<>();

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                TimeInforground = usageStats.getTotalTimeInForeground();
                PackageName = usageStats.getPackageName();
                ReportFragment.Pair tmp = new ReportFragment.Pair();
                tmp.name = PackageName;
                tmp.time = TimeInforground;
                list.add(tmp);
                //minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
                //seconds = (int) (TimeInforground / 1000) % 60;
                //hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);
                //System.out.println("PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
            }

            Collections.sort(list, new Comparator<ReportFragment.Pair>() {
                @Override
                public int compare(ReportFragment.Pair a, ReportFragment.Pair b) {
                    if (a.time < b.time)
                        return 1;
                    else if (a.time == b.time)
                        return 0;
                    return -1;
                }
            });

            int i = 0;
            for (ReportFragment.Pair p : list) {
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
            if (isForeGroundEvent(event)) {
                // 해당 앱 이름을 packageNameMap에 넣는다.
                packageNameMap.put(event.getTimeStamp(), event.getPackageName());
                // 가장 최근에 실행 된 이벤트에 대한 타임스탬프를 업데이트 해준다.
                if (event.getTimeStamp() > lastRunAppTimeStamp) {
                    lastRunAppTimeStamp = event.getTimeStamp();
                }
            }
        }
        // 가장 마지막까지 있는 앱의 이름을 리턴해준다.
        return packageNameMap.get(lastRunAppTimeStamp, "").toString();
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



    // 앱 사용 시간에 따른 결과 분석
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String AppTimeReport() {

        // 앱 이름(TIME_NAME), 시간(TIME_DATA) 불러오기
        APPS = get_apps_name();
        int index1 = 3, index2 = 3;
        for (int i = 0; i < APPS.length; i++) {
            if (i % 2 == 0) TIME_NAME[index1--] = APPS[i];
            else TIME_DATA[index2--] = Float.parseFloat(APPS[i]);
        }

        // 일주일간 전체 앱 사용 시간 (30이하/31-50/51이상)
        // 통계결과에 따라 일평균 사용시간=6시간 > 주평균 사용시간=42시간
        AppTime_Week = TIME_DATA[0]+TIME_DATA[1]+TIME_DATA[2]+TIME_DATA[3];

        String allTime = "@사용자 가 이번주 사용한 앱 시간은 총 "+AppTime_Week+"시간 입니다. ";

        String avgTime = " ";
        if (AppTime_Week < 30) {
            avgTime = "평균 스마트폰 사용시간보다 적게 사용하셨습니다. 이번주는 부지런히 건강한 하루를 보내셨군요!";
        }
        else if (30<AppTime_Week && AppTime_Week<51) {
            avgTime = "평균 스마트폰 사용시간 범위입니다. 이번주도 수고하셨습니다:)";
        }
        else if (51<AppTime_Week) {
            avgTime = "평균 스마트폰 사용시간보다 많이 사용하셨습니다. 눈의 피로를 풀어주세요!";
        }

        // 일주일간 전체 앱 사용 횟수

        // 저번주 앱 사용시간과 비교


        AppTimeReport = allTime + avgTime;
        return AppTimeReport;
    }
}