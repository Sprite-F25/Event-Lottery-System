package com.example.sprite.screens.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.Controllers.QRCodeService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.zxing.WriterException;

import java.io.Serializable;

/**
 * Fragment that shows the promotional QR code for a single {@link Event}.
 *
 * The fragment expects an Event to be passed in its arguments bundle under
 * the key "selectedEvent" (same key you’re already using for EventDetails).
 *
 * It generates a QR code that encodes the event id using QRCodeService and
 * displays it in the ImageView with id R.id.qr_image_view in
 * fragment_q_r_code_popup.xml.
 *
 * This covers the organizer side of:
 * US 02.01.01 – create event + generate unique promotional QR code
 * that links back to that event in-app.
 */
public class QRCodePopup extends Fragment {

    private QRCodePopupViewModel mViewModel;

    private ImageView qrImageView;
    private QRCodeService qrCodeService;
    private Event currentEvent;

    public QRCodePopup() {

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
        mViewModel = new ViewModelProvider(this).get(QRCodePopupViewModel.class);
        qrCodeService = new QRCodeService();


        Bundle args = getArguments();
        if (args != null) {
            Serializable serializable = args.getSerializable("selectedEvent");
            if (serializable instanceof Event) {
                currentEvent = (Event) serializable;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =
                inflater.inflate(R.layout.fragment_q_r_code_popup, container, false);
        qrImageView = root.findViewById(R.id.qr_image_view);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (currentEvent == null) {
            Toast.makeText(requireContext(),
                    "No event supplied for QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (qrImageView == null) {
            Toast.makeText(requireContext(),
                    "ImageView with id qr_image_view missing in layout",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            Bitmap bitmap = qrCodeService.generateEventQRCode(currentEvent, 800);
            qrImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(requireContext(),
                    "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
