package com.example.doiikku.fragment.pengeluaran;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doiikku.database.DatabaseClient;
import com.example.doiikku.database.dao.DatabaseDao;
import com.example.doiikku.model.ModelDatabase;
import com.example.doiikku.util.FunctionHelper;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PengeluaranViewModel extends AndroidViewModel {
    private LiveData<List<ModelDatabase>> mPengeluarans;
    private DatabaseDao databaseDao;
    private LiveData<Integer> mTotalPrice;

    // Tambahkan MutableLiveData untuk menyimpan bulan yang dipilih
    private MutableLiveData<String> selectedMonth = new MutableLiveData<>();

    public PengeluaranViewModel(@NonNull Application application) {
        super(application);
        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
        selectedMonth.setValue(FunctionHelper.getCurrentMonth());
    }

    // Metode untuk mengatur bulan yang dipilih
    public void setSelectedMonth(String month) {
        selectedMonth.setValue(month);
    }

    // Metode untuk mendapatkan bulan yang dipilih
    public LiveData<String> getSelectedMonth() {
        return selectedMonth;
    }

    public LiveData<List<ModelDatabase>> getPengeluaran(String currentmont) {
        return databaseDao.getAllPengeluaran(currentmont);
    }

    public LiveData<Integer> getTotalPengeluaran(String currentmont) {
        return databaseDao.getTotalPengeluaran(currentmont);
    }

    public void deleteAllData() {
        Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Throwable {
                        databaseDao.deleteAllPengeluaran();
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public String deleteSingleData(final int uid) {
        String sKeterangan;
        try {
            Completable.fromAction(() -> databaseDao.deleteSinglePengeluaran(uid))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            sKeterangan = "OK";
        } catch (Exception e) {
            sKeterangan = "NO";
        }
        return sKeterangan;
    }
}
