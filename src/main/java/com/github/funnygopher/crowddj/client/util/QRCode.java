package com.github.funnygopher.crowddj.client.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class QRCode {

    private BitMatrix bitMatrix;

    public QRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 1000, 1000);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public Image getImage() {
        try {
            //ByteOutputStream out = new ByteOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
            return new Image(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeToFile(File file) {
        try {
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
