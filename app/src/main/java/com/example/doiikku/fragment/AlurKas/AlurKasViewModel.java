package com.example.doiikku.fragment.AlurKas;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.doiikku.database.DatabaseClient;
import com.example.doiikku.database.dao.DatabaseDao;
import com.example.doiikku.util.FunctionHelper;


public class AlurKasViewModel extends AndroidViewModel {

    private DatabaseDao databaseDao;

    // Tambahkan MutableLiveData untuk menyimpan bulan yang dipilih
    private MutableLiveData<String> selectedMonth = new MutableLiveData<>();

    public AlurKasViewModel(@NonNull Application application) {
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

    public LiveData<Integer> getTotalPengeluaran(String currentmont) {
        return databaseDao.getTotalPengeluaran(currentmont);
    }

    public LiveData<Integer> getTotalPemasukan(String currentmont) {
        return databaseDao.getTotalPemasukan(currentmont);
    }

    public LiveData<Integer> getTotalAlurKas(String currentmont) {
        return databaseDao.getTotalAlurKas(currentmont);
    }
}
