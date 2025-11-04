package com.example.sprite.Controllers;

import android.graphics.Bitmap;

import com.example.sprite.Models.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Map;

/**
 * This class handles qrCode functions, including encoding and decoding QRCodes
 * This class also handles updating the qr code data in firebase
 */
public class QRCodeService{
    private DatabaseService databaseService;
    private QRCodeWriter qrCodeWriter;
    private QRCodeReader qrCodeReader;
    public QRCodeService(){
        databaseService = new DatabaseService();
        qrCodeWriter = new QRCodeWriter();
        qrCodeReader = new QRCodeReader();
    }

    /**
     * Encodes the QRCode given an Event
     * @return
     * Returns a bitmap image of the QRCode of the event
     */
    public Bitmap encodeQRCode(Event event)
    {
        try {
            BitMatrix qrCode = qrCodeWriter.encode("String", BarcodeFormat.QR_CODE,30, 30);
            return null;
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decodes a QRCode and returns...
     * @param image
     *      The QR Code Image
     * @throws ChecksumException
     * @throws NotFoundException
     * @throws FormatException
     */
    public void decodeQRCode(BinaryBitmap image) throws ChecksumException, NotFoundException, FormatException {
        Result result = qrCodeReader.decode(image);
    }
}