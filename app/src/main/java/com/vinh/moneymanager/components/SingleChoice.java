package com.vinh.moneymanager.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.Fragment;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.databinding.SingleChoiceBinding;
import com.vinh.moneymanager.models.SingleChoiceModel;

public class SingleChoice extends Fragment {


    private SingleChoiceModel model;

    public SingleChoice(OnChoiceSelectedListener listener) {
        model = new SingleChoiceModel(listener);
        model.selectedIndex.set(2);
    }

    public SingleChoice(OnChoiceSelectedListener listener, int selectedIndex) {
        model = new SingleChoiceModel(listener);
        model.selectedIndex.set(selectedIndex);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SingleChoiceBinding binding = DataBindingUtil.inflate(inflater, R.layout.single_choice, container, false);
        View view = binding.getRoot();

        binding.setModel(model);

        return view;
    }

    public interface OnChoiceSelectedListener {
        void onChoiceClick(int index);
    }


}
