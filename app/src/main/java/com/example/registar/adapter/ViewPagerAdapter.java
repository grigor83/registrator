package com.example.registar.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.registar.fragment.FragmentFour;
import com.example.registar.fragment.FragmentOne;
import com.example.registar.fragment.FragmentThree;
import com.example.registar.fragment.FragmentTwo;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new FragmentOne();
            case 1: return new FragmentTwo();
            case 2: return new FragmentThree();
            case 3: return new FragmentFour();
            default: return new FragmentOne();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}
