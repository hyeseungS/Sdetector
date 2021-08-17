package com.example.sdetector.ui.report;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sdetector.MainActivity;
import com.example.sdetector.R;
import com.example.sdetector.databinding.FragmentReportBinding;
import com.example.sdetector.userInfo;

public class ReportFragment extends Fragment {

    private ReportViewModel reportViewModel;
    private FragmentReportBinding binding;

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
        TextView report_appText = (TextView) root.findViewById(R.id.report_text1);
        TextView report_diaryText = (TextView) root.findViewById(R.id.report_text2);
        TextView report_treatText = (TextView) root.findViewById(R.id.report_text3);
        TextView report_totalText = (TextView) root.findViewById(R.id.report_text4);

        // 이번주 당신의 감정
        report_emotion.setImageResource(R.drawable.sad_emoticon);
        // 앱 사용 분석 내용
        report_appText.setText(R.string.report_app);
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
}