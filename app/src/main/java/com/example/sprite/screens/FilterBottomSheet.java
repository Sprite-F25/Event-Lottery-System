package com.example.sprite.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sprite.Models.FilterOptions;
import com.example.sprite.databinding.FilterPopupBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheet extends BottomSheetDialogFragment {


    public static final String REQUEST_KEY = "filter_request";
    public static final String RESULT_KEY  = "filter_result";

    private FilterPopupBinding binding;

    public static FilterBottomSheet newInstance() { return new FilterBottomSheet(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FilterPopupBinding.inflate(inflater, container, false);
        setupUI();
        return binding.getRoot();
    }

    private void setupUI() {

        RangeSlider price = binding.sliderPrice;
        TextView min = binding.tvPriceMin, max = binding.tvPriceMax;

        price.setLabelFormatter(value -> "$" + Math.round(value));

        List<Float> init = price.getValues();
        if (init.size() == 2) {
            min.setText("$" + Math.round(init.get(0)));
            max.setText("$" + Math.round(init.get(1)));
        }
        price.addOnChangeListener((slider, v, fromUser) -> {
            List<Float> vals = slider.getValues();
            min.setText("$" + Math.round(vals.get(0)));
            max.setText("$" + Math.round(vals.get(1)));
        });


        binding.etDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker().build();
            picker.addOnPositiveButtonClickListener(sel -> binding.etDate.setText(picker.getHeaderText()));
            picker.show(getParentFragmentManager(), "date");
        });


        binding.chipToday.setOnClickListener(v -> clearDateField());
        binding.chipTomorrow.setOnClickListener(v -> clearDateField());
        binding.chipThisWeek.setOnClickListener(v -> clearDateField());


        binding.btnReset.setOnClickListener(v -> resetAll());
        binding.btnApply.setOnClickListener(v -> {
            FilterOptions f = collectFilters();
            Bundle result = new Bundle();
            result.putSerializable(RESULT_KEY, f);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });
    }

    private void clearDateField() { binding.etDate.setText(""); }

    private void resetAll() {

        clearAllChecked(binding.chipGroupCategories);

        binding.chipGroupTime.clearCheck();

        binding.etDate.setText("");

        binding.etLocation.setText("");

        List<Float> def = new ArrayList<>();
        def.add(20f); def.add(120f);
        binding.sliderPrice.setValues(def);
        binding.tvPriceMin.setText("$20");
        binding.tvPriceMax.setText("$120");

        binding.switchFreeOnly.setChecked(false);
    }

    private void clearAllChecked(ChipGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View c = group.getChildAt(i);
            if (c instanceof Chip) ((Chip) c).setChecked(false);
        }

    }

    private FilterOptions collectFilters() {
        FilterOptions f = new FilterOptions();


        for (int i = 0; i < binding.chipGroupCategories.getChildCount(); i++) {
            View c = binding.chipGroupCategories.getChildAt(i);
            if (c instanceof Chip && ((Chip) c).isChecked()) {
                f.categories.add(((Chip) c).getText().toString());
            }
        }


        if (binding.chipToday.isChecked())        f.quickTime = "today";
        else if (binding.chipTomorrow.isChecked()) f.quickTime = "tomorrow";
        else if (binding.chipThisWeek.isChecked()) f.quickTime = "week";
        else                                       f.quickTime = "";


        f.location = binding.etLocation.getText() == null ? "" :
                binding.etLocation.getText().toString().trim();


        List<Float> vals = binding.sliderPrice.getValues();
        f.priceMin = vals.get(0);
        f.priceMax = vals.get(1);


        f.freeOnly = binding.switchFreeOnly.isChecked();

        return f;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
