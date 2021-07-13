package com.example.sdetector.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.sdetector.GraphFragment1;
import com.example.sdetector.GraphFragment2;
import com.example.sdetector.R;
import com.example.sdetector.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final LinearLayout LinearLayoutView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

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


}
