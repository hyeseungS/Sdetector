package com.example.sdetector.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sdetector.R;
import com.example.sdetector.databinding.FragmentDiaryBinding;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.zip.Inflater;

import android.widget.Toast;

public class DiaryFragment extends Fragment implements View.OnClickListener {

    private DiaryViewModel diaryViewModel;
    private FragmentDiaryBinding binding;

    private static String TAG = "DiaryFragment";

    private Context context;

    View view;
    DatePickerDialog datedialog;
    TextView tv;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        diaryViewModel =
                new ViewModelProvider(this).get(DiaryViewModel.class);

        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //여기부터 추가코드
        view = inflater.inflate(R.layout.fragment_diary, container,false);
        context = container.getContext();
        tv = (TextView)view.findViewById(R.id.DatetextView);
        tv.setText(getArguments().getString("date"));
        Calendar cal = Calendar.getInstance();
        //tv.setText(cal.get(Calendar.YEAR) +"-"+ (cal.get(Calendar.MONTH)+1) +"-"+ cal.get(Calendar.DATE));

        datedialog = new DatePickerDialog(getContext(),
                (DatePickerDialog.OnDateSetListener) this,
                cal.get(Calendar.YEAR),
                (cal.get(Calendar.MONTH)+1),
                cal.get(Calendar.DATE));

        //날짜 선택 버튼
        mDatePickerBtn = (Button)view.findViewById(R.id.DatePickerBtn);
        mDatePickerBtn.setText((CharSequence) today);
        mDatePickerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDatePicker();
            }
        });

        //내용 부분
        minputText = (EditText)view.findViewById(R.id.inputText);

        //저장 버튼
        msaveBtn = (Button)view.findViewById(R.id.saveBtn);
        msaveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (view.getId() == R.id.saveBtn) {
                    Log.i("TAG", "save 진행");
                    FileOutputStream fos = null;

                    try {
                        fos = context.openFileOutput("memo.txt", Context.MODE_PRIVATE);
                        String out = minputText.getText().toString();
                        fos.write(out.getBytes());
                        Toast.makeText(getContext(), "save 완료", Toast.LENGTH_SHORT).show();
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
        });

        return root;
        //return view;
    }

    DatePickerDialog.OnDateSetListener mDateSetListener = (datePicker, yy, mm, dd) -> {
        // Date Picker에서 선택한 날짜를 TextView에 설정
        tv.setText(String.format("%d-%d-%d", yy,mm+1,dd));
    };

    public void showDatePicker() {
        // DATE Picker가 처음 떴을 때, 오늘 날짜가 보이도록 설정.
        Calendar cal = Calendar.getInstance();
        try {
            today = dateFormat.parse(mDatePickerBtn.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cal.setTime(today);

        //int todayYear = cal.get(Calendar.YEAR);
        //int todayMonth = cal.get(Calendar.MONTH);
        //int todayDay = cal.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), mDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveBtn) {
            Log.i("TAG", "save 진행");
            FileOutputStream fos = null;

            try {
                /*fos = openFileOutput("memo.txt", Context.MODE_PRIVATE);
                String out = minputText.getText().toString();
                fos.write(out.getBytes());*/
                Toast.makeText(getContext(), "save 완료", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}