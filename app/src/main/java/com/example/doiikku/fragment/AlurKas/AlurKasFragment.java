package com.example.doiikku.fragment.AlurKas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.doiikku.R;
import com.example.doiikku.util.FunctionHelper;

public class AlurKasFragment extends Fragment  {


    private AlurKasViewModel alurKasViewModel;
    TextView tvTotal, tvPemasukanTotal, tvPengeluaranTotal;

    public AlurKasFragment() {
        // Required empty public constructor
    }

    //menginisialisasi tampilan pada fragment pemasukan. fragment sendiri merupakan tata letak layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alurkas, container, false);
    }

    //menginisisialisasi komponen dalam sebuah fragment setelah view dibuat. Merupakan tahap setup awal
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi TextView untuk total alur kas, pemasukan, dan pengeluaran
        tvTotal = view.findViewById(R.id.tvTotal);
        tvPemasukanTotal = view.findViewById(R.id.tvPemasukanTotal);
        tvPengeluaranTotal = view.findViewById(R.id.tvPengeluaranTotal);

        alurKasViewModel = new ViewModelProvider(requireActivity()).get(AlurKasViewModel.class);

        // Observasi nilai selectedMonth dari ViewModel
        alurKasViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), this::observeData);
    }

    private void observeData(String month) {
        alurKasViewModel = new ViewModelProvider(requireActivity()).get(AlurKasViewModel.class);

        alurKasViewModel.getTotalPemasukan(month).observe(getViewLifecycleOwner(), total -> {
            int totalPrice = (total != null) ? total : 0;
            String formattedPrice = FunctionHelper.rupiahFormat(totalPrice);
            tvPemasukanTotal.setText(formattedPrice);
        });

        alurKasViewModel.getTotalPengeluaran(month).observe(getViewLifecycleOwner(), total -> {
            int totalPrice = (total != null) ? total : 0;
            String formattedPrice = FunctionHelper.rupiahFormat(totalPrice);
            tvPengeluaranTotal.setText(formattedPrice);
        });

        alurKasViewModel.getTotalAlurKas(month).observe(getViewLifecycleOwner(), total -> {
            int totalPrice = (total != null) ? total : 0;
            String formattedPrice = FunctionHelper.rupiahFormat(totalPrice);
            tvTotal.setText(formattedPrice);
        });

    }



}