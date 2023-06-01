package com.example.box.FragmentAndActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.box.FragmentAndActivity.AccountFragment;
import com.example.box.FragmentAndActivity.FavoriteFragment;
import com.example.box.FragmentAndActivity.HomeFragment;
import com.example.box.FragmentAndActivity.NotificationFragment;
import com.example.box.FragmentAndActivity.OrderFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new HomeFragment();
            case 1:
                return new FavoriteFragment();
            case 2:
                return new OrderFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new AccountFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
