package com.example.sprite.Controllers;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sprite.Models.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Service class responsible for generating and decoding QR codes
 * for events in the Sprite app.
 */
public class QRCodeService {

    private static final String TAG = "QRCodeService";
    private static final int QR_SIZE = 512;

    private final QRCodeWriter qrCodeWriter;
    private final MultiFormatReader qrCodeReader;

    /**
     * Default constructor initializes QR code encoder and decoder.
     */
    public QRCodeService() {
        this.qrCodeWriter = new QRCodeWriter();
        this.qrCodeReader = new MultiFormatReader();
    }

    /**
     * Generates a QR code bitmap from arbitrary string data.
     *
     * @param data The text to encode in the QR code (e.g., eventId).
     * @return A Bitmap containing the QR code, or null if generation failed.
     */
    public Bitmap generateQRCode(@NonNull String data) {
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    QR_SIZE,
                    QR_SIZE
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;

        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code", e);
            return null;
        }
    }

    /**
     * Convenience helper to generate a QR code for an Event.
     * Currently encodes the event's ID string.
     *
     * @param event The event to encode.
     * @return A Bitmap QR code, or null if the event or eventId is invalid.
     */
    public Bitmap generateQRCodeForEvent(Event event) {
        if (event == null || event.getEventId() == null) {
            Log.w(TAG, "generateQRCodeForEvent: event or eventId is null");
            return null;
        }
        return generateQRCode(event.getEventId());
    }

    /**
     * Decodes a QR code image to extract its text payload.
     *
     * @param image The QR code image as a BinaryBitmap.
     * @return The decoded text (e.g., eventId).
     * @throws ChecksumException If the QR code checksum is invalid.
     * @throws NotFoundException If no QR code is found in the image.
     * @throws FormatException   If the QR code format is invalid.
     */
    public String decodeQRCode(BinaryBitmap image)
            throws ChecksumException, NotFoundException, FormatException {

        Result result = qrCodeReader.decode(image);
        return result.getText();
    }
}
