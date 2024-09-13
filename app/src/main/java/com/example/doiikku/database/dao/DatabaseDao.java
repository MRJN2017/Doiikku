package com.example.doiikku.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.doiikku.model.ModelDatabase;

import java.util.List;

@Dao
public interface DatabaseDao {
    @Query("SELECT * FROM tbl_doiku WHERE tanggal LIKE '%'||:currentmont||'%' and tipe = 'pengeluaran'")
    LiveData<List<ModelDatabase>> getAllPengeluaran(String currentmont);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPengeluaran(ModelDatabase... pengeluaran);

    @Query("DELETE FROM tbl_doiku WHERE tipe = 'pengeluaran'")
    void deleteAllPengeluaran();

    @Query("DELETE FROM tbl_doiku WHERE id_doi= :id_doi and tipe = 'pengeluaran'")
    void deleteSinglePengeluaran(int id_doi);

    @Query("SELECT SUM(jml_uang) FROM tbl_doiku WHERE tipe = 'pengeluaran' and tanggal LIKE '%'||:currentmont||'%'")
    LiveData<Integer> getTotalPengeluaran(String currentmont);

    @Query("UPDATE tbl_doiku SET keterangan = :keterangan, tanggal = :tgl, jml_uang = :harga WHERE id_doi = :id_doi and tipe = 'pengeluaran'")
    void updateDataPengeluaran(String keterangan, String tgl, int harga, int id_doi);

    //Data Pemasukan
    @Query("SELECT * FROM tbl_doiku WHERE tanggal LIKE '%'||:currentmont||'%' and tipe = 'pemasukan'")
    LiveData<List<ModelDatabase>> getAllPemasukan(String currentmont);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPemasukan(ModelDatabase... pemasukan);

    @Query("DELETE FROM tbl_doiku WHERE tipe = 'pemasukan'")
    void deleteAllPemasukan();

    @Query("DELETE FROM tbl_doiku WHERE id_doi= :id_doi and tipe = 'pemasukan'")
    void deleteSinglePemasukan(int id_doi);

    @Query("SELECT SUM(jml_uang) FROM tbl_doiku WHERE tanggal LIKE '%'||:currentmont||'%' and tipe = 'pemasukan'")
    LiveData<Integer> getTotalPemasukan(String currentmont);

    @Query("UPDATE tbl_doiku SET keterangan = :keterangan, tanggal = :tgl, jml_uang = :harga WHERE id_doi = :id_doi and tipe = 'pemasukan'")
    void updateDataPemasukan(String keterangan, String tgl, int harga, int id_doi);

    @Query("SELECT \n" +
            "    (SUM(CASE WHEN tipe = 'pemasukan' THEN jml_uang ELSE 0 END) - \n" +
            "     SUM(CASE WHEN tipe = 'pengeluaran' THEN jml_uang ELSE 0 END)) AS total_saldo\n" +
            "FROM tbl_doiku\n" +
            "WHERE tanggal LIKE '%' || :currentmont || '%'")
    LiveData<Integer> getTotalAlurKas(String currentmont);
}
