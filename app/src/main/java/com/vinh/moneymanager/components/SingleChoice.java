package com.vinh.moneymanager.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.databinding.SingleChoiceBinding;
import com.vinh.moneymanager.viewmodels.SingleChoiceViewModel;

public class SingleChoice extends Fragment {


    private final SingleChoiceViewModel viewModel;

    public SingleChoice(OnChoiceSelectedListener listener) {
        viewModel = new SingleChoiceViewModel(listener);
        viewModel.selectedIndex.set(2);
    }

    public SingleChoice(OnChoiceSelectedListener listener, int selectedIndex) {
        viewModel = new SingleChoiceViewModel(listener);
        viewModel.setSelectedIndex(selectedIndex);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SingleChoiceBinding binding = DataBindingUtil.inflate(inflater, R.layout.single_choice, container, false);
        View view = binding.getRoot();

        binding.setViewModel(viewModel);

        return view;
    }

    public interface OnChoiceSelectedListener {
        void onChoiceClick(int index);
    }


}
