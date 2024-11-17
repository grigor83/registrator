package com.example.registar.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
            case 2: return new FragmentTwo();
            default: return new FragmentOne();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
}
