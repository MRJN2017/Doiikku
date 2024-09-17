package com.example.doiikku.fragment.pemasukan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doiikku.R;
import com.example.doiikku.fragment.pemasukan.add.AddPemasukanActivity;
import com.example.doiikku.model.ModelDatabase;
import com.example.doiikku.util.FunctionHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PemasukanFragment extends Fragment implements PemasukanAdapter.PemasukanAdapterCallback {

    private PemasukanAdapter pemasukanAdapter;
    private PemasukanViewModel pemasukanViewModel;
    private List<ModelDatabase> modelDatabase = new ArrayList<>();
    TextView tvTotal, tvNotFound;
    Button btnHapus;

    FloatingActionButton fabAdd;
    RecyclerView rvListData;

    public PemasukanFragment() {
        // Required empty public constructor
    }

    //menginisialisasi tampilan pada fragment pemasukan. fragment sendiri merupakan tata letak layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pemasukan, container, false);
    }

    //menginisisialisasi komponen dalam sebuah fragment setelah view dibuat. Merupakan tahap setup awal
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //menginisialisasi variabel tvTotal dalam view
        tvTotal = view.findViewById(R.id.tvTotal);
        //menginisialisasi tvNotFound
        tvNotFound = view.findViewById(R.id.tvNotFound);
        //mengisialisasi btnHapus
        btnHapus = view.findViewById(R.id.btnHapus);
        //menginisialisasi fabAdd
        fabAdd = view.findViewById(R.id.fabAdd);
        //menginisialisasi rvlistData
        rvListData = view.findViewById(R.id.rvListData);
        //mengatur visibilitas dari tvNotFounf menjadi gone atau hilang
        tvNotFound.setVisibility(View.GONE);

        pemasukanViewModel = new ViewModelProvider(requireActivity()).get(PemasukanViewModel.class);

        // Observasi nilai selectedMonth dari ViewModel
        pemasukanViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), this::observeData);

        //menginisialisasi adapter yang akan digunakan dalam RecyclerView
        initAdapter();
        initAction();
    }


    private void initAction() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPemasukanActivity.startActivity(requireActivity(), false, null);
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengambil bulan yang dipilih dari ViewModel
                pemasukanViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), selectedMonth -> {
                    if (selectedMonth != null) {
                        // Memanggil fungsi delete berdasarkan bulan yang dipilih
                        pemasukanViewModel.deleteDataByMonth(selectedMonth);
                        tvTotal.setText("0");
                    } else {
                        Toast.makeText(requireContext(), "Bulan belum dipilih", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    private void initAdapter() {
        pemasukanAdapter = new PemasukanAdapter(requireContext(), modelDatabase, this);
        rvListData.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvListData.setItemAnimator(new DefaultItemAnimator());
        rvListData.setAdapter(pemasukanAdapter);
    }

    private void observeData(String month) {
        pemasukanViewModel = new ViewModelProvider(requireActivity()).get(PemasukanViewModel.class);

        pemasukanViewModel.getPemasukan(month).observe(getViewLifecycleOwner(), pemasukan -> {
            showEmptyState(pemasukan.isEmpty());
            pemasukanAdapter.addData(pemasukan);
        });

        pemasukanViewModel.getTotalPemasukan(month).observe(getViewLifecycleOwner(), total -> {
            int totalPrice = (total != null) ? total : 0;
            String formattedPrice = FunctionHelper.rupiahFormat(totalPrice);
            tvTotal.setText(formattedPrice);
        });

    }

    @Override
    public void onEdit(ModelDatabase modelDatabase) {
        AddPemasukanActivity.startActivity(requireActivity(), true, modelDatabase);
    }

    @Override
    public void onDelete(ModelDatabase modelDatabase) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setMessage("Hapus dosa ini?");
        alertDialogBuilder.setPositiveButton("Ya, Hapus", (dialogInterface, i) -> {
            int uid = modelDatabase.id_doi;
            String sKeterangan = pemasukanViewModel.deleteSingleData(uid);
            if (sKeterangan.equals("OK")) {
                Toast.makeText(requireContext(), "Dosa yang Anda pilih sudah dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNegativeButton("Batal", (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showEmptyState(boolean isEmpty) {
        btnHapus.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvNotFound.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvListData.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}