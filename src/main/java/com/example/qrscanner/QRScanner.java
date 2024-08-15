package com.example.qrscanner;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class QRScanner extends JFrame {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public QRScanner() {
        setTitle("QR Code Scanner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("QR Code Scanner", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JButton scanButton = new JButton("Scan QR Code");
        add(scanButton, BorderLayout.SOUTH);

        scanButton.addActionListener(e -> {
            try {
                BufferedImage qrImage = captureImage();
                if (qrImage != null) {
                    showImage(qrImage); // Show the captured image
                    BufferedImage processedImage = preprocessImage(qrImage); // Preprocess the image
                    String qrText = decodeQR(processedImage);
                    if (qrText != null) {
//                        DatabaseConnector.sendToDatabase(qrText);
                        JOptionPane.showMessageDialog(this, "QR Code found: " + qrText);
                    } else {
                        JOptionPane.showMessageDialog(this, "QR Code not found. Please try again.");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }

    private BufferedImage captureImage() {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Error: Camera is not available.");
            return null;
        }

        Mat frame = new Mat();
        camera.read(frame);
        camera.release();

        if (!frame.empty()) {
            return matToBufferedImage(frame);
        } else {
            JOptionPane.showMessageDialog(this, "Error: Unable to capture image.");
            return null;
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = (mat.channels() == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        DataBufferByte buffer = (DataBufferByte) image.getRaster().getDataBuffer();
        byte[] data = buffer.getData();
        mat.get(0, 0, data);
        return image;
    }

    private BufferedImage preprocessImage(BufferedImage image) {
        Mat mat = bufferedImageToMat(image);
        Mat gray = new Mat();
        Mat blurred = new Mat();
        Mat binary = new Mat();

        // Convert to grayscale
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // Apply adaptive thresholding to convert to binary image
        Imgproc.adaptiveThreshold(blurred, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        return matToBufferedImage(binary);
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    private String decodeQR(BufferedImage qrImage) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(qrImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.err.println("QR Code not found: " + e.getMessage());
            return null;
        }
    }

    private void showImage(BufferedImage image) {
        JFrame frame = new JFrame("Captured Image");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        JLabel label = new JLabel(new ImageIcon(image));
        frame.add(label);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QRScanner::new);
    }
}

