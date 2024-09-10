package com.example.doiikku.view;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.doiikku.R;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    int[] tabIcons = {R.drawable.ic_pengeluaran, R.drawable.ic_pemasukan};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menampilkan appbar custom
        setupActionBar();
        // Memanggil layout TabLayout dan ViewPager
        setIdLayout();
        // Memanggil tampilan yang akan ditampilkan
        setInitLayout();
        // Memanggil fungsi untuk memfilter data berdasarkan bulan
        initMonthPicker();
    }

    private void setupActionBar() {
        // Mengatur latar belakang ActionBar menjadi gradient
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradientappbar, null);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) { // Cek null untuk menghindari NullPointerException
            actionBar.setBackgroundDrawable(drawable);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            View customView = LayoutInflater.from(this).inflate(R.layout.actionbar_custom_view, null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT
            );
            actionBar.setCustomView(customView, layoutParams);
            actionBar.setDisplayShowCustomEnabled(true);
        }
    }

    private void initMonthPicker() {
        findViewById(R.id.btnPickMonth).setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                // Tindakan berdasarkan bulan yang dipilih
                String selectedMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime());
                showToast("Bulan dipilih: " + selectedMonth);
            };

            // Mengatur tampilan DatePickerDialog hanya untuk bulan
            new DatePickerDialog(this, date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setIdLayout() {
        tabLayout = findViewById(R.id.tabsLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setInitLayout() {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        // Mengatur jumlah fragment yang ada di luar layar
        viewPager.setOffscreenPageLimit(1);
        // Menghubungkan TabLayout dengan ViewPager
        tabLayout.setupWithViewPager(viewPager);
        // Menampilkan icon pada tab pertama
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        // Menampilkan icon pada tab kedua
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
}
