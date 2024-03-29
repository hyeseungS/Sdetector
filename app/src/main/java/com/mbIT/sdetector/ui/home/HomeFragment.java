package com.mbIT.sdetector.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.mbIT.sdetector.GraphFragment1;
import com.mbIT.sdetector.GraphFragment2;
import com.mbIT.sdetector.R;
import com.mbIT.sdetector.databinding.FragmentHomeBinding;
import com.mbIT.sdetector.ui.report.ReportFragment;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private String name;

    public String getName() {
        return name;
    }

    public static int N = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //String name = get_user_name();
        TextView home_text = (TextView) root.findViewById(R.id.home_text);
        //home_text.setText(name+"님, \n   스트레스 상태 ");
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    name = user.getKakaoAccount().getProfile().getNickname();
                    home_text.setText(name+"님,\n    스트레스 상태");
                }
                else {
                    home_text.setText("로그아웃 상태입니다");
                }
                return null;
            }
        });
        ImageView home_emotion = (ImageView) root.findViewById(R.id.home_emotion);
        home_emotion.setImageResource(R.drawable.pre_emotion);

        // 이번주 당신의 감정-에 따라 바뀌게
        if (N==1) {home_emotion.setImageResource(R.drawable.sad_emoticon);}
        else if (N==2) {home_emotion.setImageResource(R.drawable.normal_emoticon);}
        else if (N==3) {home_emotion.setImageResource(R.drawable.happy_emoticon);}
        else {home_emotion.setImageResource(R.drawable.pre_emotion);}


        ViewPager pager = root.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);

        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());

        GraphFragment1 fragment1 = new GraphFragment1();
        adapter.addItem(fragment1);

        GraphFragment2 fragment2 = new GraphFragment2();
        adapter.addItem(fragment2);

        pager.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        public Fragment getItem(int position) {
            return items.get(position);
        }

        public int getCount() {
            return items.size();
        }
    }

    public static void EmotionImage_Main() {
        if (1 <= ReportFragment.EmotionOftheWeek() && ReportFragment.EmotionOftheWeek() <= 5) {
            switch (ReportFragment.EmotionOftheWeek()) {
                case 1:
                    N = 1;
                    //home_emotion.setImageResource(R.drawable.sad_emoticon);
                    break;
                case 2:
                    N = 2;
                    //home_emotion.setImageResource(R.drawable.normal_emoticon);
                    break;
                case 3:
                    N = 3;
                    //home_emotion.setImageResource(R.drawable.happy_emoticon);
                    break;
                default:
                    //home_emotion.setImageResource(R.drawable.pre_emotion);
                    break;
            }
        } else {
            //N=0;
            //home_emotion.setImageResource(R.drawable.pre_emotion);
        }
    }
}
