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
 * Fragment that displays a QR code in a popup dialog.
 * 
 * <p>This fragment is used to show QR codes for events in a popup overlay.
 * Currently a placeholder for future QR code display functionality.</p>
 */
public class QRCodePopup extends Fragment {

    private QRCodePopupViewModel mViewModel;

    public static QRCodePopup newInstance() {
        return new QRCodePopup();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_q_r_code_popup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(QRCodePopupViewModel.class);
        // TODO: Use the ViewModel
    }

}