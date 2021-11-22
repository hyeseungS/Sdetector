package com.mbIT.sdetector.ui.report;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mbIT.sdetector.BuildConfig;
import com.mbIT.sdetector.R;
import com.mbIT.sdetector.databinding.FragmentReportBinding;
import com.mbIT.sdetector.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class ReportFragment extends Fragment {

    private static Context context;
    private ReportViewModel reportViewModel;
    private FragmentReportBinding binding;
    private static String TAG = "ReportFragment";
    private static String IP_ADDRESS = "52.78.165.117";   //매번 ip주소 바꿔줄 것

    public String emotion_name;

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

    private static String AppReport5 = "평균 스마트폰 사용시간보다 적게 사용하셨습니다. 이번주는 부지런히 건강한 하루를 보내셨군요!";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_GOOD = "num_good";
    private static final String TAG_NORMAL = "num_normal";
    private static final String TAG_BAD = "num_bad";

    ArrayList<HashMap<String, String>> mArrayList, mArrayList2;
    //ListView mlistView;
    String mJsonString;

    private String DiaryReport1, DiaryReport2, DiaryReport3, DiaryError;
    private String DiaryReport4 = "감정 분석을 위해 일기를 작성해주세요!";
    private int DiaryCount = 0;
    private int GoodEmotionNum = 0, NorEmotionNum = 0, BadEmotionNum = 0;

    private static double EmotionOfWeek_int;

    private String ManagementReport1;
    private String FinalReport1;

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

        // 앱 사용 분석 내용
        report_appText.setText(AppTimeReport());
        // 감정 일기 분석 내용
        report_diaryText.setText(DiaryReport());
        // 관리 방법 내용
        report_treatText.setText(ManagementReport());
        // 종합 의견 내용
        report_totalText.setText(FinalReport());
        // 이번주 당신의 감정-에 따라 바뀌게
        switch (EmotionOftheWeek()) {
            case 1:
                report_emotion.setImageResource(R.drawable.sad_emoticon);
                HomeFragment.EmotionImage_Main();
                break;
            case 2:
                report_emotion.setImageResource(R.drawable.normal_emoticon);
                HomeFragment.EmotionImage_Main();
                break;
            case 3:
                report_emotion.setImageResource(R.drawable.happy_emoticon);
                HomeFragment.EmotionImage_Main();
                break;
            default:
                report_emotion.setImageResource(R.drawable.pre_emotion);
                break;
        }
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

        if (AppTime_Week < 42 * 0.95) {
            AppReport5 = "평균 스마트폰 사용시간보다 적게 사용하셨습니다. 이번주는 부지런히 건강한 하루를 보내셨군요!";
        } else if (30 < AppTime_Week && AppTime_Week < 51) {
            AppReport5 = "평균 스마트폰 사용시간 범위입니다. 이번주도 수고하셨습니다:)";
        } else if (51 < AppTime_Week) {
            AppReport5 = "평균 스마트폰 사용시간보다 많이 사용하셨습니다. 눈의 피로를 풀어주세요!";
        }

        AppTimeReport = AppReport1 + Appreport2 + "\n" + AppReport3 + AppReport4 + "\n" + AppReport5;
        return AppTimeReport;

    }

    //DB로 부터 일기 분석 데이터 받아 최종 분석
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /*DiaryReport 함수 내로 보냄 ;doInBackground 리턴값 거기로 받음
            DiaryError = result;
            Log.d(TAG, "response  - " + result);

            if (result == null){
                DiaryError = errorString;
            }
            else {
                mJsonString = result;
                showResult();
            }*/
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "GetData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private ArrayList<HashMap<String, String>> showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String Ngood = item.getString(TAG_GOOD);
                String Nnor = item.getString(TAG_NORMAL);
                String Nbad = item.getString(TAG_BAD);

                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(TAG_GOOD, Ngood);
                hashMap.put(TAG_NORMAL, Nnor);
                hashMap.put(TAG_BAD, Nbad);

                mArrayList.add(hashMap);
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult Error : ", e);
        }

        return mArrayList;
    }

    // 일기 감정에 따른 결과 분석
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String DiaryReport() {

        mArrayList = new ArrayList<>();

        GetData task = new GetData();
        try {   //doInBackground 리턴값(String) 가져옴
            String result2 = task.execute("http://" + IP_ADDRESS + "/getjson.php").get();

            DiaryError = result2;
            Log.d(TAG, "response  - " + result2);

            if (result2 == null) {
                DiaryError = result2;
            } else {
                mJsonString = result2;
                mArrayList2 = showResult();

                //일기 작성 횟수
                DiaryCount = mArrayList2.size();
                //1~2
                if (DiaryCount <= 2 && DiaryCount > 0) {
                    DiaryReport4 = "분석 결과의 정확도가 낮아질 수 있어요!";
                }
                //3~5
                else if (DiaryCount > 2 && DiaryCount <= 5) {
                    DiaryReport4 = "더 정확한 분석을 위해 일기를 자주 작성해 주세요!";
                }
                //6~7
                else if (DiaryCount > 5) {
                    DiaryReport4 = "꾸준히 일기를 작성하셨네요. 부지런한 당신, 칭찬해요!";
                }

                for (int i = 0; i < mArrayList2.size(); i++) {
                    //GoodEmotionNum += Integer.parseInt(mArrayList2.get(i).get("num_good"));
                    //mysql에서 goodEmotion 갯수가 +1되어 나와 일단은 -1 해둔다.
                    GoodEmotionNum = GoodEmotionNum  + Integer.parseInt(mArrayList2.get(i).get("num_good")) -1;
                    NorEmotionNum += Integer.parseInt(mArrayList2.get(i).get("num_normal"));
                    BadEmotionNum += Integer.parseInt(mArrayList2.get(i).get("num_bad"));
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DiaryReport2 = "이번주 일기에서 '좋음' 감정 단어는 " + Integer.toString(GoodEmotionNum) + "번, '보통' 감정 단어는 " + Integer.toString(NorEmotionNum) + "번, '나쁨' 감정 단어는 " + Integer.toString(BadEmotionNum) + "번 나타났습니다.";
        DiaryReport3 = "이번주에는 일기를 " + DiaryCount + "회 작성하셨군요.";
        DiaryReport1 = DiaryReport2 + DiaryReport3 + DiaryReport4;
        return DiaryReport1;
    }

    // 최종 분석-이번주의 감정 분석
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String FinalReport() {
        // 저번주 앱 사용량이 존재할 때
        if (AppTime_LastWeek > 0) {
            if (AppTime_Week >= AppTime_LastWeek * 1.05) {
                //감정 나쁨 30%
                EmotionOfWeek_int = EmotionOfWeek_int - 3;
            } else if (AppTime_Week <= AppTime_LastWeek * 0.95) {
                //감정 좋음 30%
                EmotionOfWeek_int = EmotionOfWeek_int + 3;
            } else {
                //감정 보통 30%
                EmotionOfWeek_int = EmotionOfWeek_int + 0;
            }
        }
        //저번주 앱 사용량 없음 - 앱사용평균 42의 5% 위아래
        else {
            if (AppTime_Week >= 42 * 1.05) {
                //감정 나쁨 30%
                EmotionOfWeek_int = EmotionOfWeek_int - 3;
            } else if (AppTime_Week <= 42 * 0.95) {
                //감정 좋음 30%
                EmotionOfWeek_int = EmotionOfWeek_int + 3;
            } else {
                //감정 보통 30%
                EmotionOfWeek_int = EmotionOfWeek_int + 0;
            }
        }

        EmotionOfWeek_int = EmotionOfWeek_int + GoodEmotionNum * 0.7 - BadEmotionNum * 0.7;

        //이번주의 최종 감정 분석 [-1~-0.3 = 나쁨/0.3~1 = 좋음 /나머지 보통]
        if (EmotionOfWeek_int > 0.3) {
            FinalReport1 = "이번주 당신의 감정은 <좋음> 입니다. 행복한 한 주를 보내셨나요? 다음 주에도 좋은 날이 계속 되길 바랍니다.";
        } else if (EmotionOfWeek_int < -0.3) {
            FinalReport1 = "이번주 당신의 감정은 <나쁨> 입니다. 스트레스를 받아 힘든 한 주를 보내신 것 같아요. 고민이나 걱정들이 잘 해결되길 바랍니다.";
        } else {
            FinalReport1 = "이번주 당신의 감정은 <보통>입니다. 이번주도 평소와 같이 무사히 한 주를 보내셨군요. 평범한 하루가 모여 위대한 생이 된다고 합니다. 다음 주에도 소중한 일상을 보내길 바랍니다.";
        }

        return FinalReport1;
    }

    // 관리 분석
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String ManagementReport() {

        //좋은 기분
        if (EmotionOfWeek_int > 0.3) {
            Random ran = new Random();
            int random = ran.nextInt(3);
            switch (random) {
                case 0:
                    ManagementReport1 = "평소 좋아하는 음악을 들으며 한 주를 완벽하게 마무리해요.";
                    break;
                case 1:
                    ManagementReport1 = "오늘은 즐거운 기분으로 그동안 미루던 독서를 해볼까요?";
                    break;
                default:
                    ManagementReport1 = "오늘은 즐거운 기분으로 그동안 미루던 일을 해볼까요?";
                    break;
            }
        }
        //나쁜 기분
        else if (EmotionOfWeek_int < -0.3) {
            Random ran = new Random();
            int random = ran.nextInt(7);
            switch (random) {
                case 0:
                    ManagementReport1 = "한 주간 쌓인 스트레스는 가벼운 운동으로 날려봐요!";
                    break;
                case 1:
                    ManagementReport1 = "한 주간 쌓인 스트레스는 가벼운 러닝으로 날려봐요!";
                    break;
                case 2:
                    ManagementReport1 = "한 주간 쌓인 스트레스는 가벼운 스트레칭으로 날려봐요!";
                    break;
                case 3:
                    ManagementReport1 = "숙면을 취하는 것은 스트레스 해소에 좋은 방법입니다.";
                    break;
                case 4:
                    ManagementReport1 = "오늘은 달콤한 케이크가 당기는 날이네요.";
                    break;
                case 5:
                    ManagementReport1 = "오늘은 매콤한 떡볶이가 당기는 날이네요.";
                    break;
                default:
                    ManagementReport1 = "오늘은 든든한 고기가 당기는 날이네요.";
                    break;
            }
        }
        //보통 기분
        else {
            Random ran = new Random();
            int random = ran.nextInt(3);
            switch (random) {
                case 0:
                    ManagementReport1 = "따뜻한 차를 마시며 한 주 간 쌓였던 마음을 되돌아 봅시다.";
                    break;
                case 1:
                    ManagementReport1 = "다음에는 어디로 여행을 떠날지 구경해볼까요?";
                    break;
                default:
                    ManagementReport1 = "내일 아침에는 상쾌한 공기를 마시며 산책 해봐요.";
                    break;
            }
        }

        return ManagementReport1;
    }

    public static int EmotionOftheWeek() {
        if (EmotionOfWeek_int < -0.3) {
            return 1;
        } else if (EmotionOfWeek_int > 0.3) {
            return 3;
        } else if (EmotionOfWeek_int >= -0.3 && EmotionOfWeek_int <= 0.3) {
            return 2;
        } else {
            return 5;
        }
    }
}