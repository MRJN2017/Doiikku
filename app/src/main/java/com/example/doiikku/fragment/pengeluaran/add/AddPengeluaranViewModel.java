package com.example.doiikku.fragment.pengeluaran.add;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.doiikku.database.DatabaseClient;
import com.example.doiikku.database.dao.DatabaseDao;
import com.example.doiikku.model.ModelDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddPengeluaranViewModel extends AndroidViewModel {

    private DatabaseDao databaseDao;

    public AddPengeluaranViewModel(@NonNull Application application) {
        super(application);

        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
    }

    public void addPengeluaran(final String type, final String note, final String date, final int price) {
        Completable.fromAction(() -> {
                    ModelDatabase pengeluaran = new ModelDatabase();
                    pengeluaran.tipe = type;
                    pengeluaran.keterangan = note;
                    pengeluaran.tanggal = date;
                    pengeluaran.jmlUang = price;
                    databaseDao.insertPengeluaran(pengeluaran);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void updatePengeluaran(final int uid, final String note, final String date, final int price) {
        Completable.fromAction(() -> {
                    databaseDao.updateDataPengeluaran(note, date, price, uid);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
