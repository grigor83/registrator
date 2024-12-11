package com.example.registar.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.registar.fragment.FifthFragment;
import com.example.registar.fragment.FirstFragment;
import com.example.registar.fragment.FourthFragment;
import com.example.registar.fragment.ThirdFragment;
import com.example.registar.fragment.SecondFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new FirstFragment();
            case 1: return new SecondFragment();
            case 2: return new ThirdFragment();
            case 3: return new FourthFragment();
            case 4: return new FifthFragment();
            default: return new FirstFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Number of tabs
    }
}
