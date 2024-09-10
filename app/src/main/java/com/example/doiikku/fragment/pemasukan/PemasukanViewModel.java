package com.example.doiikku.fragment.pemasukan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.doiikku.database.DatabaseClient;
import com.example.doiikku.database.dao.DatabaseDao;
import com.example.doiikku.model.ModelDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PemasukanViewModel extends AndroidViewModel {
    private LiveData<List<ModelDatabase>> mPemasukans;
    private DatabaseDao databaseDao;
    private LiveData<Integer> mTotalPrice;

    public PemasukanViewModel(@NonNull Application application) {
        super(application);
        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
        mPemasukans = databaseDao.getAllPemasukan();
        mTotalPrice = databaseDao.getTotalPemasukan();
    }

    public LiveData<List<ModelDatabase>> getPemasukan() {
        return mPemasukans;
    }

    public LiveData<Integer> getTotalPemasukan() {
        return mTotalPrice;
    }

    public void deleteAllData() {
        Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Throwable {
                        databaseDao.deleteAllPemasukan();
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public String deleteSingleData(final int uid) {
        String sKeterangan;
        try {
            Completable.fromAction(() -> databaseDao.deleteSinglePemasukan(uid))
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
