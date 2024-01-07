package com.example.doiikku.fragment.pengeluaran.add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.doiikku.R;
import com.example.doiikku.model.ModelDatabase;
import com.example.doiikku.view.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddPengeluaranActivity extends AppCompatActivity {

    private static String KEY_IS_EDIT = "key_is_edit";
    private static String KEY_DATA = "key_data";

    public static void startActivity(Context context, boolean isEdit, ModelDatabase pengeluaran) {
        Intent intent = new Intent(context, AddPengeluaranActivity.class);
        intent.putExtra(KEY_IS_EDIT, isEdit);
        intent.putExtra(KEY_DATA, pengeluaran);
        context.startActivity(intent);
    }


    private AddPengeluaranViewModel addPengeluaranViewModel;

    private boolean mIsEdit = false;
    private int strUid = 0;

    Toolbar toolbar;
    TextInputEditText etKeterangan, etTanggal, etJmlUang;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);

        etKeterangan = findViewById(R.id.etKeterangan);
        etTanggal = findViewById(R.id.etTanggal);
        etJmlUang = findViewById(R.id.etJmlUang);
        btnSimpan = findViewById(R.id.btnSimpan);

        addPengeluaranViewModel = new ViewModelProvider(this).get(AddPengeluaranViewModel.class);
        // mengatur latar belakang actionbar. Disini saya membuat backgroundnya menjadi gradient
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradientappbar, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(drawable);

        //mengatur tampilan actionbar. Disini saya mengaturnya di tengah karna ada tombol back disebelah kirinya
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View customView = LayoutInflater.from(this).inflate(R.layout.actionbar_tambah_data, null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
        );
        actionBar.setCustomView(customView, layoutParams);
        actionBar.setDisplayShowCustomEnabled(true);

        // menambahkan tombol back di actionbar
        ImageButton backButton = customView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadData();
        initAction();
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void initAction() {
        etTanggal.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String strFormatDefault = "d MMMM yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormatDefault, Locale.getDefault());
                etTanggal.setText(simpleDateFormat.format(calendar.getTime()));
            };

            new DatePickerDialog(AddPengeluaranActivity.this, date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTipe = "pengeluaran";
                String strKeterangan = etKeterangan.getText().toString();
                String strTanggal = etTanggal.getText().toString();
                String strJmlUang = etJmlUang.getText().toString();

                if (strKeterangan.isEmpty() || strJmlUang.isEmpty()) {
                    Toast.makeText(AddPengeluaranActivity.this, "Ups, form tidak boleh kosong!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mIsEdit) {
                        addPengeluaranViewModel.updatePengeluaran(strUid, strKeterangan,
                                strTanggal, Integer.parseInt(strJmlUang));
                    } else {
                        addPengeluaranViewModel.addPengeluaran(strTipe, strKeterangan,
                                strTanggal, Integer.parseInt(strJmlUang));
                    }
                    finish();
                }
            }
        });
    }

    private void loadData() {
        mIsEdit = getIntent().getBooleanExtra(KEY_IS_EDIT, false);
        if (mIsEdit) {
            ModelDatabase pengeluaran = getIntent().getParcelableExtra(KEY_DATA);
            if (pengeluaran != null) {
                strUid = pengeluaran.id_doi;
                String keterangan = pengeluaran.keterangan;
                String tanggal = pengeluaran.tanggal;
                int uang = pengeluaran.jmlUang;

                etKeterangan.setText(keterangan);
                etTanggal.setText(tanggal);
                etJmlUang.setText(String.valueOf(uang));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(AddPengeluaranActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}