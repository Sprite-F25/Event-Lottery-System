package com.example.sprite.screens.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sprite.Controllers.QRCodeService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Dialog fragment that shows an event's QR code and lets the organizer
 * download it to the device gallery.
 */
public class QRCodePopup extends DialogFragment {

    private ImageView qrImageView;
    private TextView popupTitleTextView;
    private TextView eventTitleTextView;
    private MaterialButton closeButton;
    private MaterialButton downloadButton;

    private Event event;
    private final QRCodeService qrCodeService = new QRCodeService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_q_r_code_popup, container, false);


        popupTitleTextView = view.findViewById(R.id.popupTitleTextView);
        eventTitleTextView = view.findViewById(R.id.textView4);
        qrImageView = view.findViewById(R.id.qr_image_view);
        closeButton = view.findViewById(R.id.createEventButton);
        downloadButton = view.findViewById(R.id.createEventButton2);

        popupTitleTextView.setText("Event QR Code");


        Bundle args = getArguments();
        if (args != null) {
            Object obj = args.getSerializable("selectedEvent");
            if (obj instanceof Event) {
                event = (Event) obj;
            }
        }

        if (event != null) {
            eventTitleTextView.setText(event.getTitle());


            Bitmap qrBitmap = qrCodeService.generateQRCode(event.getEventId());
            if (qrBitmap != null) {
                qrImageView.setImageBitmap(qrBitmap);
            }
        }


        closeButton.setOnClickListener(v -> dismiss());


        downloadButton.setOnClickListener(v -> downloadQrImage());

        return view;
    }

    /**
     * Retrieves the bitmap from the ImageView and saves it to the gallery.
     */
    private void downloadQrImage() {
        if (qrImageView.getDrawable() == null
                || !(qrImageView.getDrawable() instanceof BitmapDrawable)) {
            Toast.makeText(requireContext(), "QR code not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) qrImageView.getDrawable()).getBitmap();
        if (bitmap == null) {
            Toast.makeText(requireContext(), "QR code not available", Toast.LENGTH_SHORT).show();
            return;
        }

        saveBitmapToGallery(bitmap);
    }

    /**
     * Saves the given bitmap to the device's Pictures/SpriteQR folder using MediaStore.
     * This works on Android Q+ without needing WRITE_EXTERNAL_STORAGE.
     */
    private void saveBitmapToGallery(@NonNull Bitmap bitmap) {
        ContentResolver resolver = requireContext().getContentResolver();

        String fileName = "sprite_qr_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                        .format(new Date()) + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/SpriteQR");

        Uri uri = null;
        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(requireContext(),
                        "Could not create file to save QR code",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try (OutputStream out = resolver.openOutputStream(uri)) {
                if (out == null ||
                        !bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    Toast.makeText(requireContext(),
                            "Failed to save QR code",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(requireContext(),
                    "QR code saved to gallery",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            if (uri != null) {
                resolver.delete(uri, null, null);
            }
            Toast.makeText(requireContext(),
                    "Error saving QR code",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
