package com.mattpflance.hearthlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PagerFragment extends Fragment {

    public ViewPager viewPager;

    public PagerFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static PagerFragment newInstance() {
        PagerFragment fragment = new PagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);

        viewPager = (ViewPager) view;

        PagerAdapter pagerAdapter = new PagerAdapter(getFragmentManager(), getContext());
        viewPager.setAdapter(pagerAdapter);

        MainActivity.TabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
