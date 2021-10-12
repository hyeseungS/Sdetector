package com.example.sdetector.ui.report;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sdetector.BuildConfig;
import com.example.sdetector.R;
import com.example.sdetector.databinding.FragmentReportBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportFragment extends Fragment {

    private static Context context;
    private ReportViewModel reportViewModel;
    private FragmentReportBinding binding;
    private static String TAG = "ReportFragment";
    private static String IP_ADDRESS = "3.38.106.240";   //매번 ip주소 바꿔줄 것

    // 가져올 변수
    //private static final int MAX_X_VALUE = 4; // 보여줄 앱 개수
    private String[] APPS;
    private String[] TIME_NAME = new String[4]; // 앱 이름
    private float[] TIME_DATA = new float[4]; // 앱 사용 시간 데이터
    private String mostUseApp_Name;
    private float mostUseApp_Time;
    //저번주 데이터
    private String[] APPS_Lastweek;
    private String[] TIME_NAME_Lastweek = new String[4]; // 앱 이름
    private float[] TIME_DATA_Lastweek = new float[4]; // 앱 사용 시간 데이터

    String AppTimeReport;
    static float AppTime_Week;
    float AppTime_LastWeek;

    private static final String TAG_JSON="webnautes";
    private static final String TAG_GOOD = "num_good";
    private static final String TAG_NORMAL = "num_normal";
    private static final String TAG_BAD ="num_bad";

    ArrayList<HashMap<String, String>> mArrayList;
    //ListView mlistView;
    String mJsonString;
    
    String DiaryReport1, DiaryReport2, DiaryReport3="원본", DiaryError;

    String FinalReport1;

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
        report_diaryText.setText(DiaryReport());
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

    // 가져올 기간 정하기-현재부터 몇일전(term)까지의 사용결과를 분석할것인지
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String[] get_apps_name(int term) {
        if (!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        String[] ret = new String[8];
        String PackageName = "Nothing";
        long TimeInforground = 500;
        int minutes = 500, seconds = 500, hours = 500;
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getContext().getSystemService(USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, term);
        long cur_time = System.currentTimeMillis(), begin_time = cal.getTimeInMillis();
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begin_time, cur_time);
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

    public boolean checkPermission() {

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

        // 이번주 앱 사용시간과 비교
        APPS = get_apps_name(-6);
        int index1 = 3, index2 = 3;
        for (int i = 0; i < APPS.length; i++) {
            if (i % 2 == 0) TIME_NAME[index1--] = APPS[i];
            else TIME_DATA[index2--] = Float.parseFloat(APPS[i]);
        }
        AppTime_Week = TIME_DATA[0] + TIME_DATA[1] + TIME_DATA[2] + TIME_DATA[3];

        //이번주 가장 많이 사용한 앱
        for (int i = 0; i < 3; i++) {
            if (TIME_DATA[i] < TIME_DATA[i + 1]) {
                mostUseApp_Name = TIME_NAME[i + 1];
                mostUseApp_Time = TIME_DATA[i + 1];
            } else {
                mostUseApp_Time = TIME_DATA[i];
                mostUseApp_Name = TIME_NAME[i];
            }
        }
        String AppReport3 = "이번주에 가장 많이 사용한 앱은 " + mostUseApp_Name + "으로, 총 " + mostUseApp_Time + "시간 사용하였습니다.";


        // 저번주 앱 사용시간과 비교
        APPS_Lastweek = get_apps_name(-13);
        int index1_Lastweek = 3, index2_Lastweek = 3;
        for (int i = 0; i < APPS.length; i++) {
            if (i % 2 == 0) TIME_NAME_Lastweek[index1_Lastweek--] = APPS[i];
            else TIME_DATA_Lastweek[index2_Lastweek--] = Float.parseFloat(APPS[i]);
        }
        AppTime_LastWeek = TIME_DATA_Lastweek[0] + TIME_DATA_Lastweek[1] + TIME_DATA_Lastweek[2] + TIME_DATA_Lastweek[3] - AppTime_Week;
//이번주 가장 많이 사용한 앱
        for (int i = 0; i < 3; i++) {
            if (TIME_DATA[i] < TIME_DATA[i + 1]) {
                mostUseApp_Name = TIME_NAME[i + 1];
                mostUseApp_Time = TIME_DATA[i + 1];
            } else {
                mostUseApp_Time = TIME_DATA[i];
                mostUseApp_Name = TIME_NAME[i];
            }
        }
        String AppReport4 = "이번주에 가장 많이 사용한 앱은 " + mostUseApp_Name + "으로, 총 " + mostUseApp_Time + "시간 사용하였습니다.";


        String AppReport1 = "이번주 스마트폰 총 사용 시간은 " + AppTime_Week + "시간으로, ";

        String Appreport2 = " ";
        if (AppTime_Week >= AppTime_LastWeek) {
            float inter1 = AppTime_Week - AppTime_LastWeek;
            Appreport2 = "저번주 스마트폰 총 사용 시간인 " + AppTime_LastWeek + "보다 " + inter1 + "시간 증가하였습니다.";
        } else if (AppTime_Week < AppTime_LastWeek) {
            float inter2 = AppTime_LastWeek - AppTime_Week;
            Appreport2 = "저번주 스마트폰 총 사용 시간인 " + AppTime_LastWeek + "보다 " + inter2 + "시간 감소하였습니다.";
        }

        AppTimeReport = AppReport1 + Appreport2 + "\n" + AppReport3 + AppReport4;
        return AppTimeReport;

        /*String avgTime = " ";
        if (AppTime_Week < 30) {
            avgTime = "평균 스마트폰 사용시간보다 적게 사용하셨습니다. 이번주는 부지런히 건강한 하루를 보내셨군요!";
        }
        else if (30<AppTime_Week && AppTime_Week<51) {
            avgTime = "평균 스마트폰 사용시간 범위입니다. 이번주도 수고하셨습니다:)";
        }
        else if (51<AppTime_Week) {
            avgTime = "평균 스마트폰 사용시간보다 많이 사용하셨습니다. 눈의 피로를 풀어주세요!";
        }*/
    }

    //일기 분석 관련
    private class GetData extends AsyncTask<String, Void, String>{
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            DiaryError = result;
            Log.d(TAG, "response  - " + result);

            if (result == null){
                DiaryError = errorString;
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String Ngood = item.getString(TAG_GOOD);
                String Nnor = item.getString(TAG_NORMAL);
                String Nbad = item.getString(TAG_BAD);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_GOOD, Ngood);
                hashMap.put(TAG_NORMAL, Nnor);
                hashMap.put(TAG_BAD, Nbad);

                mArrayList.add(hashMap);
            }

            /*ListAdapter adapter = new SimpleAdapter(
                    ReportFragment.context, mArrayList, R.layout.item_list,
                    new String[]{TAG_GOOD,TAG_NORMAL, TAG_BAD},
                    new int[]{R.id.textView_list_id, R.id.textView_list_name, R.id.textView_list_address}
            );

            mlistView.setAdapter(adapter);*/

            for (int i=0; i<1; i++) {
                DiaryReport3 ="바뀐것 제발돼라";
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }

    // 일기 감정에 따른 결과 분석
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String DiaryReport() {

        mArrayList = new ArrayList<>();

        GetData task = new GetData();
        task.execute("http://"+IP_ADDRESS+"/getjson.php");

        DiaryReport2 = "이번주 일기에서 '좋음' 감정 단어는 ㅇ번, '보통' 감정 단어는 ㅇ번, '나쁨' 감정 단어는 ㅇ번 나타났습니다.";
        DiaryReport1 = DiaryReport3;
        return DiaryReport1;
    }


    // 최종 분석
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String FinalReport() {
        if (AppTime_LastWeek > 0) { // 저번주 앱 사용량이 존재할 때
            if (AppTime_Week >= AppTime_LastWeek * 1.05) {
                //감정 나쁨 30%
            } else if (AppTime_Week <= AppTime_LastWeek * 0.95) {
                //감정 좋음 30%
            } else {
                //감정 보통 30%
            }
        } else {
            //저번주 앱 사용량 없음
        }

        FinalReport1 = "";
        return FinalReport1;
    }
}