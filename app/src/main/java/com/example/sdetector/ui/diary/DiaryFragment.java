package com.example.sdetector.ui.diary;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sdetector.MainActivity;
import com.example.sdetector.R;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Calendar;

import android.widget.Toast;

public class DiaryFragment extends Fragment implements View.OnClickListener {

    //인터넷 서버 통신 코드
    private static String IP_ADDRESS = "3.34.139.172";   //매번 ip주소 바꿔줄 것
    private static String TAG = "DiaryFragment";
    private Object binding;

    Context mContext;

    //날짜 텍스트뷰
    TextView mDateTextView;

    //감정 상태 (나쁨1/보통2/좋음3)
    int emotionInt = 0;
    //날짜버튼
    Button mDatePickerBtn;
    //작성부분
    EditText mDiaryContent;
    //저장버튼
    Button msaveBtn;

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.saveBtn) {
            // DB로 전송

            //감정 넘버 emotionInt를 String으로 넘기기
            String emotionString = String.valueOf(emotionInt);

            //일기 내용
            String diaryContent = mDiaryContent.getText().toString();

            //데이터 넘검 - emotionString, diaryContent
            InsertData task = new InsertData();
            task.execute("http://" + IP_ADDRESS + "/insert.php", emotionString, diaryContent);
            Toast.makeText(getActivity().getApplicationContext(), "데이터 웹으로 넘김", Toast.LENGTH_SHORT).show();

            mDiaryContent.setText("");

            Log.i("TAG", "save 진행");
            FileOutputStream fos = null;

            /*try {
                fos = getActivity().openFileOutput("memo.txt", Context.MODE_PRIVATE);
                String out = mDiaryContent.getText().toString();
                fos.write(out.getBytes());
                Toast.makeText(getActivity().getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                MainActivity activity = (MainActivity) getActivity();
                activity.moveToDiaryList();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
        if (mDatePickerListener != null) {
            mDatePickerListener.DatePickerData(DiaryFragment.this.toString());
        }
    }

    public interface DatePickerListener {
        void DatePickerData(String data);
    }

    private DatePickerListener mDatePickerListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_diary, container, false);

        //저장버튼(saveBtn) 과 onClickListener 연결
        msaveBtn = (Button) myview.findViewById(R.id.saveBtn);
        msaveBtn.setOnClickListener(this);

        RadioGroup radioGroup = (RadioGroup) myview.findViewById(R.id.emotion_diary);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId) {
                    case R.id.bad_radioBtn:
                        emotionInt = 1;
                        Toast.makeText(getActivity().getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.normal_radioBtn:
                        emotionInt = 2;
                        Toast.makeText(getActivity().getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.good_radioBtn:
                        emotionInt = 3;
                        Toast.makeText(getActivity().getApplicationContext(), "3", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        return myview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mContext = mContext;
        if (getActivity() != null && getActivity() instanceof DatePickerListener) {
            mDatePickerListener = (DatePickerListener) getActivity();
        }

        // 날짜 텍스트_일단 오늘날짜 지정
        mDateTextView = getView().findViewById(R.id.DatetextView);
        Calendar cal = Calendar.getInstance();
        mDateTextView.setText(cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DATE) + "일");

        //날짜 선택 버튼
        mDatePickerBtn = getView().findViewById(R.id.DatePickerBtn);

        //내용 텍스트
        mDiaryContent = getView().findViewById(R.id.inputText);

        // 저장 버튼
        msaveBtn = getView().findViewById(R.id.saveBtn);
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(), "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String emotionContent =  (String) params[1];
            String diaryContent = (String) params[2];

            String serverURL = (String) params[0];
            String postParameters = "emotion=" + emotionContent + "&content=" + diaryContent;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                Toast.makeText(getActivity().getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                MainActivity activity = (MainActivity) getActivity();
                activity.moveToDiaryList();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onDetach() {
        super.onDetach();

        if (mContext != null) {
            mContext = null;
        }
    }
}