package com.example.sdetector.ui.diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sdetector.MainActivity;
import com.example.sdetector.R;
import com.example.sdetector.databinding.FragmentDiaryBinding;
import com.google.android.material.tabs.TabLayout;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;

import android.widget.Toast;

public class DiaryFragment extends Fragment {

    private DiaryViewModel diaryViewModel;
    private FragmentDiaryBinding binding;

    private static String TAG = "DiaryFragment";

    public interface DatePickerListener{
        void DatePickerData(String data);
    }

    private DatePickerListener mDatePickerListener;

    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
            listener = null;
        }
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        return view;
    }

    Context context;

    View view;
    DatePickerDialog datedialog;
    TextView tv;
    Calendar cal;
    Date today = new Date();
    //날짜 포맷 설정
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 mm월 dd일");
    String result = dateFormat.format(today);

    //날짜버튼
    Button mDatePickerBtn;
    //작성부분
    EditText minputText;
    //저장버튼
    Button msaveBtn;

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        this.context = context;
        if (getActivity() != null && getActivity() instanceof DatePickerListener){
            mDatePickerListener = (DatePickerListener) getActivity();
        }


        // 날짜 텍스트_일단 오늘날짜 지정
        tv = getView().findViewById(R.id.DatetextView);
        Calendar cal = Calendar.getInstance();
        tv.setText(cal.get(Calendar.YEAR) +"-"+ (cal.get(Calendar.MONTH)+1) +"-"+ cal.get(Calendar.DATE));

        //날짜 선택 버튼
        mDatePickerBtn = getView().findViewById(R.id.DatePickerBtn);

        //내용 텍스트
        minputText = getView().findViewById(R.id.inputText);

        // 저장 버튼
        msaveBtn = getView().findViewById(R.id.saveBtn);
        msaveBtn.setOnClickListener(listener);
    }

    // 저장 버튼 누를때 listener
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.saveBtn) {
                Log.i("TAG", "save 진행");
                FileOutputStream fos = null;

                try {
                    fos = getActivity().openFileOutput("memo.txt", Context.MODE_PRIVATE);
                    String out = minputText.getText().toString();
                    fos.write(out.getBytes());
                    Toast.makeText(getActivity().getApplicationContext(), "save 완료", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        {
            if(mDatePickerListener != null){
                mDatePickerListener.DatePickerData(DiaryFragment.this.toString());
        }
        }

    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}