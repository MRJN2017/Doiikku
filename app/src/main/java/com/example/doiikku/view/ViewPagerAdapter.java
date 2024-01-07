package com.example.doiikku.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.doiikku.fragment.pemasukan.PemasukanFragment;
import com.example.doiikku.fragment.pengeluaran.PengeluaranFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new PengeluaranFragment();
                break;
            case 1:
                fragment = new PemasukanFragment();
                break;
        }
        return fragment;    }

    @Override
    public int getCount() {
        return 1;
    }

    //masih belum sa tahu asal usulnya
    @Override
    public CharSequence getPageTitle(int position) {
        String strTitle = "";
        switch (position) {
            case 0:
                strTitle = "Expenses";
                break;
            case 1:
                strTitle = "Income";
                break;
        }
        return strTitle;
    }
}
