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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PemasukanFragment extends Fragment implements PemasukanAdapter.PemasukanAdapterCallback {

    private PemasukanAdapter pemasukanAdapter;
    private PemasukanViewModel pemasukanViewModel;
    private List<ModelDatabase> modelDatabase = new ArrayList<>();
    TextView tvTotal, tvNotFound;
    Button btnHapus;

    MaterialButton btnPickMonth;

    FloatingActionButton fabAdd;
    RecyclerView rvListData;

    public PemasukanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pemasukan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotal = view.findViewById(R.id.tvTotal);
        tvNotFound = view.findViewById(R.id.tvNotFound);
        btnHapus = view.findViewById(R.id.btnHapus);
        fabAdd = view.findViewById(R.id.fabAdd);
        rvListData = view.findViewById(R.id.rvListData);
        tvNotFound.setVisibility(View.GONE);

        initAdapter();
        observeData();
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
                pemasukanViewModel.deleteAllData();
                tvTotal.setText("0");
            }
        });
    }

    private void initAdapter() {
        pemasukanAdapter = new PemasukanAdapter(requireContext(), modelDatabase, this);
        rvListData.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvListData.setItemAnimator(new DefaultItemAnimator());
        rvListData.setAdapter(pemasukanAdapter);
    }

    private void observeData() {
        pemasukanViewModel = new ViewModelProvider(requireActivity()).get(PemasukanViewModel.class);
        pemasukanViewModel.getPemasukan().observe(requireActivity(), new Observer<List<ModelDatabase>>() {
            @Override
            public void onChanged(List<ModelDatabase> pemasukan) {
                showEmptyState(pemasukan.isEmpty());
                pemasukanAdapter.addData(pemasukan);
            }
        });

        pemasukanViewModel.getTotalPemasukan().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                int totalPrice = (integer != null) ? integer : 0;
                String initPrice = FunctionHelper.rupiahFormat(totalPrice);
                tvTotal.setText(initPrice);
            }
        });
    }

    @Override
    public void onEdit(ModelDatabase modelDatabase) {
        AddPemasukanActivity.startActivity(requireActivity(), true, modelDatabase);
    }

    @Override
    public void onDelete(ModelDatabase modelDatabase) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setMessage("Hapus pemasukan ini?");
        alertDialogBuilder.setPositiveButton("Ya, Hapus", (dialogInterface, i) -> {
            int uid = modelDatabase.id_doi;
            String sKeterangan = pemasukanViewModel.deleteSingleData(uid);
            if (sKeterangan.equals("OK")) {
                Toast.makeText(requireContext(), "Pemasukan yang Anda pilih sudah dihapus", Toast.LENGTH_SHORT).show();
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
