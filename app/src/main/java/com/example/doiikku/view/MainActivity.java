package com.example.doiikku.view;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.doiikku.R;
import com.example.doiikku.fragment.AlurKas.AlurKasViewModel;
import com.example.doiikku.fragment.pemasukan.PemasukanViewModel;
import com.example.doiikku.fragment.pengeluaran.PengeluaranViewModel;
import com.example.doiikku.model.ModelDatabase;
import com.example.doiikku.util.FunctionHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    int[] tabIcons = {R.drawable.ic_pengeluaran, R.drawable.ic_pemasukan, R.drawable.ic_balance_24};
    PemasukanViewModel pemasukanViewModel;
    PengeluaranViewModel pengeluaranViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pemasukanViewModel = new ViewModelProvider(this).get(PemasukanViewModel.class);
        pengeluaranViewModel = new ViewModelProvider(this).get(PengeluaranViewModel.class);
        // Menampilkan appbar custom
        setupActionBar();
        // Memanggil layout TabLayout dan ViewPager
        setIdLayout();
        // Memanggil tampilan yang akan ditampilkan
        setInitLayout();
        // Memanggil fungsi untuk memfilter data berdasarkan bulan
        initMonthPicker();
        // Memanggil fungsi untuk menyimpan PDF
        initSavePDF();


    }

    private void initSavePDF() {
        findViewById(R.id.btnSavePDF).setOnClickListener(view -> {

            // Tampilkan dialog konfirmasi
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin mendownload laporan ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Validasi apakah bulan sudah dipilih
                        if (pemasukanViewModel.getSelectedMonth().getValue() == null || pengeluaranViewModel.getSelectedMonth().getValue() == null) {
                            Toast.makeText(this, "Pilih bulan terlebih dahulu!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Observasi pemasukan
                        pemasukanViewModel.getSelectedMonth().observe(this, selectedMonth -> {
                            if (selectedMonth != null) {
                                generatePDFpemasukan(selectedMonth);
                            } else {
                                Toast.makeText(this, "Data pemasukan tidak tersedia!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Observasi pengeluaran
                        pengeluaranViewModel.getSelectedMonth().observe(this, selectedMonth -> {
                            if (selectedMonth != null) {
                                generatePDFpengeluaran(selectedMonth);
                            } else {
                                Toast.makeText(this, "Data pengeluaran tidak tersedia!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Tidak", (dialog, which) -> {
                        // Tutup dialog jika pengguna memilih "Tidak"
                        dialog.dismiss();
                    })
                    .show();
        });
    }


    private void generatePDFpemasukan(String month) {
        pemasukanViewModel.getPemasukan(month).observe(this, pemasukanList -> {
            if (pemasukanList != null && !pemasukanList.isEmpty()) {
                // Membuat PdfDocument
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                Paint paint = new Paint();
                paint.setTextSize(12);
                paint.setColor(Color.BLACK);

                // Judul PDF
                canvas.drawText("Laporan Pemasukan - " + month, 10, 25, paint);

                int yPosition = 50; // Posisi Y untuk menulis data
                for (ModelDatabase pemasukan : pemasukanList) {
                    canvas.drawText(pemasukan.keterangan + " - " + FunctionHelper.rupiahFormat(pemasukan.jmlUang), 10, yPosition, paint);
                    yPosition += 15; // Jarak antar baris
                }

                // Menyelesaikan halaman
                pdfDocument.finishPage(page);

                // Simpan ke MediaStore
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "Pemasukan_" + month + ".pdf");
                contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
                if (uri != null) {
                    try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                        if (outputStream != null) {
                            pdfDocument.writeTo(outputStream);
                            Toast.makeText(this, "PDF berhasil disimpan di Documents", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Gagal membuka OutputStream", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show();
                    } finally {
                        pdfDocument.close();
                    }
                } else {
                    Toast.makeText(this, "Gagal membuat URI untuk PDF", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(this, "Tidak ada data untuk bulan ini", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePDFpengeluaran(String month) {
        pengeluaranViewModel.getPengeluaran(month).observe(this, pengeluaranList -> {
            if (pengeluaranList != null && !pengeluaranList.isEmpty()) {
                // Membuat PdfDocument
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                Paint paint = new Paint();
                paint.setTextSize(12);
                paint.setColor(Color.BLACK);

                // Judul PDF
                canvas.drawText("Laporan Pengeluaran - " + month, 10, 25, paint);

                int yPosition = 50; // Posisi Y untuk menulis data
                for (ModelDatabase pengeluaran : pengeluaranList) {
                    canvas.drawText(pengeluaran.keterangan + " - " + FunctionHelper.rupiahFormat(pengeluaran.jmlUang), 10, yPosition, paint);
                    yPosition += 15; // Jarak antar baris
                }

                // Menyelesaikan halaman
                pdfDocument.finishPage(page);

                // Simpan ke MediaStore
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "Pengeluaran_" + month + ".pdf");
                contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
                if (uri != null) {
                    try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                        if (outputStream != null) {
                            pdfDocument.writeTo(outputStream);
                            Toast.makeText(this, "PDF berhasil disimpan di Documents", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Gagal membuka OutputStream", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show();
                    } finally {
                        pdfDocument.close();
                    }
                } else {
                    Toast.makeText(this, "Gagal membuat URI untuk PDF", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(this, "Tidak ada data untuk bulan ini", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void closeDocument(Document document, String filePath) {
        document.close();
        Toast.makeText(this, "PDF disimpan di: " + filePath, Toast.LENGTH_LONG).show();
    }



    private void setupActionBar() {
        // Mengatur latar belakang ActionBar menjadi gradient
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradientviewcard, null);
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
                // Kirim nilai selectedMonth ke PengeluaranViewModel
                PengeluaranViewModel pengeluaranViewModel = new ViewModelProvider(this).get(PengeluaranViewModel.class);
                pengeluaranViewModel.setSelectedMonth(selectedMonth);

                PemasukanViewModel pemasukanViewModel = new ViewModelProvider(this).get(PemasukanViewModel.class);
                pemasukanViewModel.setSelectedMonth(selectedMonth);

                AlurKasViewModel alurkasViewModel = new ViewModelProvider(this).get(AlurKasViewModel.class);
                alurkasViewModel.setSelectedMonth(selectedMonth);
            };

            // Mengatur tampilan DatePickerDialog hanya untuk bulan
            new DatePickerDialog(this, date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


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
        // Menampilkan icon pada tab ketiga
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
}
