package com.example.doiikku.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_doiku")
public class ModelDatabase implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id_doi;

    @ColumnInfo(name = "tipe")
    public String tipe;

    @ColumnInfo(name = "keterangan")
    public String keterangan;

    @ColumnInfo(name = "jml_uang")
    public int jmlUang;

    @ColumnInfo(name = "tanggal")
    public String tanggal;

    // Tambahkan konstruktor dengan parameter yang sesuai
    public ModelDatabase(int id_doi, String tipe, String keterangan, int jmlUang, String tanggal) {
        this.id_doi = id_doi;
        this.tipe = tipe;
        this.keterangan = keterangan;
        this.jmlUang = jmlUang;
        this.tanggal = tanggal;
    }

    // Konstruktor kosong
    public ModelDatabase() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_doi);
        dest.writeString(this.tipe);
        dest.writeString(this.keterangan);
        dest.writeInt(this.jmlUang);
        dest.writeString(this.tanggal);
    }

    protected ModelDatabase(Parcel in) {
        this.id_doi = in.readInt();
        this.tipe = in.readString();
        this.keterangan = in.readString();
        this.jmlUang = in.readInt();
        this.tanggal = in.readString();
    }

    public static final Creator<ModelDatabase> CREATOR = new Creator<ModelDatabase>() {
        @Override
        public ModelDatabase createFromParcel(Parcel source) {
            return new ModelDatabase(source);
        }

        @Override
        public ModelDatabase[] newArray(int size) {
            return new ModelDatabase[size];
        }
    };
}
