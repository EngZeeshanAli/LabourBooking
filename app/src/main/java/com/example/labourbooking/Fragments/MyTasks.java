package com.example.labourbooking.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.labourbooking.R;
import com.google.android.material.tabs.TabLayout;

public class MyTasks extends Fragment {
    ViewPager pager;
    TabLayout tabs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_tasks, container, false);
        initGui(v);
        return v;
    }

    private void initGui(View v) {
        tabs = v.findViewById(R.id.tabLayout);
        tabs.addTab(tabs.newTab().setText("My Works"));
        tabs.addTab(tabs.newTab().setText("Posted Works"));
        pager = v.findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), getActivity().getSupportFragmentManager(), tabs.getTabCount());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        Context c;
        int tabs;

        public ViewPagerAdapter(Context c, @NonNull FragmentManager fm, int tabs) {
            super(fm);
            this.c = c;
            this.tabs = tabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            MyWorks myWorks = new MyWorks();
            PostedWroks postedWroks = new PostedWroks();
            switch (position) {
                case 0:
                    return myWorks;
                case 1:
                    return postedWroks;
                default:
                    return myWorks;
            }
        }

        @Override
        public int getCount() {
            return tabs;
        }
    }

}
