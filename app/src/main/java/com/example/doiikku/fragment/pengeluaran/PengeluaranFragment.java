package com.example.doiikku.fragment.pengeluaran;

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
import com.example.doiikku.fragment.pengeluaran.add.AddPengeluaranActivity;
import com.example.doiikku.model.ModelDatabase;
import com.example.doiikku.util.FunctionHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PengeluaranFragment extends Fragment implements PengeluaranAdapter.PengeluaranAdapterCallback {

    private PengeluaranAdapter pengeluaranAdapter;
    private PengeluaranViewModel pengeluaranViewModel;
    private List<ModelDatabase> modelDatabase = new ArrayList<>();
    TextView tvTotal, tvNotFound;
    Button btnHapus;

    FloatingActionButton fabAdd;
    RecyclerView rvListData;

    public PengeluaranFragment() {
        // Required empty public constructor
    }

    //menginisialisasi tampilan pada fragment pengeluaran. fragment sendiri merupakan tata letak layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pengeluaran, container, false);
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

        pengeluaranViewModel = new ViewModelProvider(requireActivity()).get(PengeluaranViewModel.class);

        // Observasi nilai selectedMonth dari ViewModel
        pengeluaranViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), this::observeData);

        //menginisialisasi adapter yang akan digunakan dalam RecyclerView
        initAdapter();
        initAction();


    }


    private void initAction() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPengeluaranActivity.startActivity(requireActivity(), false, null);
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pengeluaranViewModel.deleteAllData();
                tvTotal.setText("0");
            }
        });

    }


    private void initAdapter() {
        pengeluaranAdapter = new PengeluaranAdapter(requireContext(), modelDatabase, this);
        rvListData.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvListData.setItemAnimator(new DefaultItemAnimator());
        rvListData.setAdapter(pengeluaranAdapter);
    }

    private void observeData(String month) {
        pengeluaranViewModel = new ViewModelProvider(requireActivity()).get(PengeluaranViewModel.class);

        pengeluaranViewModel.getPengeluaran(month).observe(getViewLifecycleOwner(), pengeluaran -> {
            showEmptyState(pengeluaran.isEmpty());
            pengeluaranAdapter.addData(pengeluaran);
        });

        pengeluaranViewModel.getTotalPengeluaran(month).observe(getViewLifecycleOwner(), total -> {
            int totalPrice = (total != null) ? total : 0;
            String formattedPrice = FunctionHelper.rupiahFormat(totalPrice);
            tvTotal.setText(formattedPrice);
        });

    }

    @Override
    public void onEdit(ModelDatabase modelDatabase) {
        AddPengeluaranActivity.startActivity(requireActivity(), true, modelDatabase);
    }

    @Override
    public void onDelete(ModelDatabase modelDatabase) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setMessage("Hapus dosa ini?");
        alertDialogBuilder.setPositiveButton("Ya, Hapus", (dialogInterface, i) -> {
            int uid = modelDatabase.id_doi;
            String sKeterangan = pengeluaranViewModel.deleteSingleData(uid);
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