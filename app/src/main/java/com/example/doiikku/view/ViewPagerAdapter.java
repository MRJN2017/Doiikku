package com.example.doiikku.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.doiikku.fragment.AlurKas.AlurKasFragment;
import com.example.doiikku.fragment.pemasukan.PemasukanFragment;
import com.example.doiikku.fragment.pengeluaran.PengeluaranFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles = new String[]{"Expenses", "Income", "Cash"};

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);  // Menggunakan fragment behavior terbaru
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PengeluaranFragment();
            case 1:
                return new PemasukanFragment();
            case 2:
                return new AlurKasFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Menentukan jumlah tab yang ditampilkan
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Mengembalikan judul tab berdasarkan posisi
        return tabTitles[position];
    }
}
