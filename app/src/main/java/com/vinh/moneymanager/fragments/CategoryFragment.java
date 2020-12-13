package com.vinh.moneymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.GridCategoryAdapter;
import com.vinh.moneymanager.components.ExpandableHeightGridView;
import com.vinh.moneymanager.room.entities.Category;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment implements GridCategoryAdapter.OnGridItemClickListener {

    private ExpandableHeightGridView gridViewCategories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        gridViewCategories = view.findViewById(R.id.grid_view_category);

        gridViewCategories.setAdapter(new GridCategoryAdapter(this.getContext(), getListCategory(), this));
        gridViewCategories.setExpanded(true);

        gridViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) gridViewCategories.getItemAtPosition(position);
                System.out.println(category.getName());
            }
        });

        return view;
    }



    private List<Category> getListCategory(){
        List<Category> list = new ArrayList<>();
        list.add(new Category("AAA", 1, "Ahihi"));
        list.add(new Category("BBB", 1, "Ahihi"));
        list.add(new Category("CCC", 1, "Ahihi"));
        list.add(new Category("DDD", 1, "Ahihi"));
        list.add(new Category("BBB", 1, "Ahihi"));
        list.add(new Category("CCC", 1, "Ahihi"));
        list.add(new Category("DDD", 1, "Ahihi"));

        return list;
    }

    @Override
    public void onGridItemClick(int position) {

    }
}