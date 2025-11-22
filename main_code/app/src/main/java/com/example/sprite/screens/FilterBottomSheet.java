package com.example.sprite.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sprite.Models.FilterOptions;
import com.example.sprite.R;
import com.example.sprite.databinding.FilterPopupBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

/**
 * Bottom sheet dialog fragment for filtering events.
 * 
 * <p>This fragment provides a bottom sheet interface for users to filter events
 * by categories, date, location, price range, and other criteria. The filter
 * options are collected and returned via fragment result.</p>
 */
public class FilterBottomSheet extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "filter_request";
    public static final String RESULT_KEY  = "filter_result";

    private FilterPopupBinding binding;
    private Long selectedDateTimestamp = null;

    public static FilterBottomSheet newInstance() {
        return new FilterBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FilterPopupBinding.inflate(inflater, container, false);
        setupUI(binding.getRoot());
        return binding.getRoot();
    }

    private void setupUI(@NonNull View root) {

        RangeSlider price = binding.sliderPrice;
        TextView min = binding.tvPriceMin;
        TextView max = binding.tvPriceMax;


        price.setValueFrom(0f);
        price.setValueTo(200f);
        price.setStepSize(5f);


        List<Float> current = price.getValues();
        if (current == null || current.size() != 2) {
            price.setValues(20f, 120f);
            min.setText("$20");
            max.setText("$120");
        } else {
            min.setText("$" + Math.round(current.get(0)));
            max.setText("$" + Math.round(current.get(1)));
        }

        price.setLabelFormatter(value -> "$" + Math.round(value));
        price.addOnChangeListener((slider, v, fromUser) -> {
            List<Float> vals = slider.getValues();
            if (vals.size() == 2) {
                min.setText("$" + Math.round(vals.get(0)));
                max.setText("$" + Math.round(vals.get(1)));
            }
        });


        binding.etDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker().build();
            picker.addOnPositiveButtonClickListener(sel -> {
                selectedDateTimestamp = sel;
                binding.etDate.setText(picker.getHeaderText());
            });
            picker.show(getParentFragmentManager(), "date");
        });


        binding.chipToday.setOnClickListener(v -> clearDateField());
        binding.chipTomorrow.setOnClickListener(v -> clearDateField());
        binding.chipThisWeek.setOnClickListener(v -> clearDateField());


        binding.btnReset.setOnClickListener(v -> resetAll());
        binding.btnApply.setOnClickListener(v -> {
            FilterOptions f = collectFilters();
            Bundle result = new Bundle();
            result.putSerializable(RESULT_KEY, f); // Ensure FilterOptions implements Serializable
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismissAllowingStateLoss();
        });
    }

    private void clearDateField() {
        binding.etDate.setText("");
    }

    private void resetAll() {
        // Categories
        clearAllChecked(binding.chipGroupCategories);

        // Quick time
        binding.chipGroupTime.clearCheck();
        // Manual date
        binding.etDate.setText("");
        selectedDateTimestamp = null;

        // Location
        binding.etLocation.setText("");

        // Price
        binding.sliderPrice.setValues(20f, 120f);
        binding.tvPriceMin.setText("$20");
        binding.tvPriceMax.setText("$120");

        // Free only
        binding.switchFreeOnly.setChecked(false);
    }

    private void clearAllChecked(@NonNull ChipGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View c = group.getChildAt(i);
            if (c instanceof Chip) {
                ((Chip) c).setChecked(false);
            }
        }
    }

    private FilterOptions collectFilters() {
        FilterOptions f = new FilterOptions();

        // Categories
        for (int i = 0; i < binding.chipGroupCategories.getChildCount(); i++) {
            View c = binding.chipGroupCategories.getChildAt(i);
            if (c instanceof Chip && ((Chip) c).isChecked()) {
                f.categories.add(((Chip) c).getText().toString());
            }
        }

        // Quick time
        if (binding.chipToday.isChecked()) {
            f.quickTime = "today";
            f.chosenDate = null; // Clear chosenDate if quick time is selected
        } else if (binding.chipTomorrow.isChecked()) {
            f.quickTime = "tomorrow";
            f.chosenDate = null; // Clear chosenDate if quick time is selected
        } else if (binding.chipThisWeek.isChecked()) {
            f.quickTime = "week";
            f.chosenDate = null; // Clear chosenDate if quick time is selected
        } else {
            f.quickTime = "";
            // Use chosenDate if a specific date was selected
            f.chosenDate = selectedDateTimestamp;
        }

        // Location
        f.location = binding.etLocation.getText() == null ? "" :
                binding.etLocation.getText().toString().trim();

        // Price
        List<Float> vals = binding.sliderPrice.getValues();
        if (vals.size() == 2) {
            f.priceMin = vals.get(0);
            f.priceMax = vals.get(1);
        }

        // Free only
        f.freeOnly = binding.switchFreeOnly.isChecked();

        return f;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
