package com.example.doiikku.fragment.pemasukan.add;

import static com.example.doiikku.util.FunctionHelper.rupiahFormat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.doiikku.R;
import com.example.doiikku.fragment.pengeluaran.add.AddPengeluaranActivity;
import com.example.doiikku.fragment.pengeluaran.add.AddPengeluaranViewModel;
import com.example.doiikku.model.ModelDatabase;
import com.example.doiikku.view.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddPemasukanActivity extends AppCompatActivity {

    private static String KEY_IS_EDIT = "key_is_edit";
    private static String KEY_DATA = "key_data";

    public static void startActivity(Context context, boolean isEdit, ModelDatabase pemasukan) {
        Intent intent = new Intent(context, AddPemasukanActivity.class);
        intent.putExtra(KEY_IS_EDIT, isEdit);
        intent.putExtra(KEY_DATA, pemasukan);
        context.startActivity(intent);
    }

    private AddPemasukanViewModel addPemasukanViewModel;

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

        addPemasukanViewModel = new ViewModelProvider(this).get(AddPemasukanViewModel.class);
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

        // Menambahkan TextWatcher untuk mengamati perubahan teks
        etJmlUang.addTextChangedListener(new TextWatcher() {

            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Tidak digunakan
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals(current)) {
                    etJmlUang.removeTextChangedListener(this);

                    // Menghapus simbol mata uang dan pemisah ribuan
                    String cleanString = charSequence.toString().replaceAll("[Rp,.\\s]", "");

                    if (!cleanString.equals("")) {
                        // Parse angka dan format ulang
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = formatRupiah(parsed);

                        current = formatted;
                        etJmlUang.setText(formatted);
                        etJmlUang.setSelection(formatted.length()); // Memastikan kursor berada di akhir
                    }

                    etJmlUang.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Tidak digunakan
            }
        });

        loadData();
        initAction();
    }

    private String formatRupiah(double number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return format.format(number).replaceAll(",00", ""); // Menghilangkan ,00 di belakang
    }


    // Fungsi format Rupiah dengan pemisah ribuan secara dinamis
    private String formatRupiah(long number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return "Rp " + formatter.format(number).replace(",", ".");
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

            new DatePickerDialog(AddPemasukanActivity.this, date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTipe = "pemasukan";
                String strKeterangan = etKeterangan.getText().toString();
                String strTanggal = etTanggal.getText().toString();
                String strJmlUang = etJmlUang.getText().toString();

                if (strKeterangan.isEmpty() || strJmlUang.isEmpty()) {
                    Toast.makeText(AddPemasukanActivity.this, "Ups, form tidak boleh kosong!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Menghapus format Rp dan tanda titik (pemformatan ribuan)
                    String cleanJmlUang = strJmlUang.replaceAll("[Rp,.\\s]", "");

                    // Lakukan pengecekan untuk memastikan jumlah uang tidak kosong setelah diformat
                    if (!cleanJmlUang.isEmpty()) {
                        int jumlahUang = Integer.parseInt(cleanJmlUang);  // Mengonversi string menjadi integer

                        if (mIsEdit) {
                            addPemasukanViewModel.updatePemasukan(strUid, strKeterangan, strTanggal, jumlahUang);
                        } else {
                            addPemasukanViewModel.addPemasukan(strTipe, strKeterangan, strTanggal, jumlahUang);
                        }

                        finish();
                    } else {
                        Toast.makeText(AddPemasukanActivity.this, "Jumlah uang tidak valid!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadData() {
        mIsEdit = getIntent().getBooleanExtra(KEY_IS_EDIT, false);
        if (mIsEdit) {
            ModelDatabase pemasukan = getIntent().getParcelableExtra(KEY_DATA);
            if (pemasukan != null) {
                strUid = pemasukan.id_doi;
                etKeterangan.setText(pemasukan.keterangan);
                etTanggal.setText(pemasukan.tanggal);
                etJmlUang.setText(String.valueOf(pemasukan.jmlUang));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(AddPemasukanActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
