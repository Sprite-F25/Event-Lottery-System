package com.example.sprite.screens.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sprite.Controllers.QRCodeService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRCodePopup extends Fragment {

    private Event currentEvent;

    // Views from fragment_q_r_code_popup.xml
    private TextView popupTitleTextView;
    private TextView textView4;   // Event title
    private TextView textView5;   // Date
    private TextView textView7;   // Location
    private ImageView imageView9; // QR image
    private MaterialButton cancelButton;
    private MaterialButton displayButton;

    private QRCodeService qrCodeService;

    public QRCodePopup() {
        // Required empty public constructor
    }

    public static QRCodePopup newInstance(Event event) {
        QRCodePopup fragment = new QRCodePopup();
        Bundle args = new Bundle();
        args.putSerializable("selectedEvent", event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        qrCodeService = new QRCodeService();

        if (getArguments() != null) {
            Serializable serializable = getArguments().getSerializable("selectedEvent");
            if (serializable instanceof Event) {
                currentEvent = (Event) serializable;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_q_r_code_popup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Bind views
        popupTitleTextView = view.findViewById(R.id.popupTitleTextView);
        textView4 = view.findViewById(R.id.textView4);
        textView5 = view.findViewById(R.id.textView5);
        textView7 = view.findViewById(R.id.textView7);
        imageView9 = view.findViewById(R.id.imageView9);
        cancelButton = view.findViewById(R.id.createEventButton);
        displayButton = view.findViewById(R.id.createEventButton2);

        if (currentEvent == null) {
            Toast.makeText(requireContext(), "Event not received", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set text fields
        popupTitleTextView.setText("QR Code");
        textView4.setText(currentEvent.getTitle());
        textView7.setText(currentEvent.getLocation());

        Date date = currentEvent.getEventStartDate();
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            textView5.setText(sdf.format(date));
        } else {
            textView5.setText("No date");
        }

        // Cancel closes the popup
        cancelButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Display generates QR
        displayButton.setOnClickListener(v -> generateQRCode());
    }

    private void generateQRCode() {
        if (currentEvent == null) return;

        try {
            Bitmap qrBitmap = qrCodeService.generateEventQRCode(currentEvent, 800);
            imageView9.setImageBitmap(qrBitmap);
            Toast.makeText(requireContext(), "QR Code Generated", Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            Toast.makeText(requireContext(),
                    "Failed to generate QR code",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
