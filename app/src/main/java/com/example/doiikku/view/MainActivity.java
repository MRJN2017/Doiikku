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

        //menampilkan appbar costum
        setupActionBar();
        //memanggil layout tablayout dan tab layout
        setIdLayout();
        //Memanggil tampilan yang akan ditampilkan
        setInitLayout();
        //memanggil fungsi untuk memffilter data berdasarkan bulan
        initMonthPicker();

    }

    private void setupActionBar() {
        // mengatur latar belakang actionbar. Disini saya membuat backgroundnya menjadi gradient
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradientappbar, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(drawable);

        //mengatur tampilan actionbar. Disini saya mengaturnya di tengah karna ada tombol back disebelah kirinya
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View customView = LayoutInflater.from(this).inflate(R.layout.actionbar_custom_view, null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
        );
        actionBar.setCustomView(customView, layoutParams);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void initMonthPicker() {
        findViewById(R.id.btnPickMonth).setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                // Tindakan yang ingin Anda lakukan berdasarkan bulan yang dipilih
                // Contoh: tampilkan pesan bulan yang dipilih
                String selectedMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime());
                showToast("Bulan dipilih: " + selectedMonth);
            };

            // Mengatur gaya spinner dan mode agar hanya menampilkan pemilihan bulan
            new DatePickerDialog(this, R.style.DatePickerDialogStyle, date,
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
        //mengatur jumlah fragment yang ada diluar layar . berfngsi ketika keluar dari aplikasi kita dapat kembali ke halaman yang sama tanpa memulai kembali dari awal
        viewPager.setOffscreenPageLimit(1);
        //menghubungkan tablayout dengan viewpager
        tabLayout.setupWithViewPager(viewPager);
        //menampilkan object tab pertama
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        //menampilkan object tab kedua
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
}