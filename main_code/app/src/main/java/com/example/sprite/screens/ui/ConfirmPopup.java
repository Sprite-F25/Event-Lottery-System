package com.example.sprite.screens.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sprite.R;

/**
 * Fragment that displays a confirmation dialog.
 * 
 * <p>This fragment is used to show confirmation dialogs for various actions
 * throughout the app, such as deleting events or canceling operations.</p>
 */
public class ConfirmPopup extends Fragment {

    private ConfirmPopupViewModel mViewModel;

    /**
     * Creates a new instance of ConfirmPopup.
     *
     * @return A new ConfirmPopup instance
     */
    public static ConfirmPopup newInstance() {
        return new ConfirmPopup();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_popup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ConfirmPopupViewModel.class);
        // TODO: Use the ViewModel
    }

}