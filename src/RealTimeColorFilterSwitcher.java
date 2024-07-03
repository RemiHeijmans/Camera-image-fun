import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class RealTimeColorFilterSwitcher {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static String currentFilter = "normal";
    private static int pixelSize = 1;
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = 600;
    private static boolean normalColorsMode = true;

    public static void main(String[] args) {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not found or cannot be opened");
            return;
        }

        // Set initial resolution
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, DISPLAY_WIDTH);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, DISPLAY_HEIGHT);

        JFrame frame = new JFrame("Real-Time Color Filter Switcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        JLabel imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case '1' -> pixelSize = 1;
                    case '2' -> pixelSize = 4;
                    case '3' -> pixelSize = 16;
                    case '4' -> pixelSize = 32;
                    case '5' -> pixelSize = 64;
                    case '6' -> pixelSize = 128;
                    case '7' -> pixelSize = DISPLAY_WIDTH;  // Set to full frame size
                    case 'q' -> {
                        normalColorsMode = false;
                        currentFilter = "sepia";
                    }
                    case 'u' -> {
                        normalColorsMode = false;
                        currentFilter = "negative";
                    }
                    case 'e' -> {
                        normalColorsMode = false;
                        currentFilter = "grayscale";
                    }
                    case 'r' -> {
                        normalColorsMode = false;
                        currentFilter = "weird";
                    }
                    case 't' -> {
                        normalColorsMode = false;
                        currentFilter = "anotherFilter"; // Placeholder for any additional filter
                    }
                    case 'y' -> {
                        normalColorsMode = false;
                        currentFilter = "yetAnotherFilter"; // Placeholder for any additional filter
                    }
                    case KeyEvent.VK_SPACE -> {
                        normalColorsMode = true;
                        currentFilter = "normal";
                    }
                }
                System.out.println("Current filter: " + currentFilter + ", Pixel size: " + pixelSize + ", Normal Colors Mode: " + normalColorsMode);
            }
        });

        new Timer(33, e -> {
            Mat frameMat = new Mat();
            if (camera.read(frameMat)) {
                Mat processedMat;

                if (normalColorsMode) {
                    processedMat = frameMat;
                } else {
                    processedMat = applyFilter(frameMat, currentFilter);
                }

                Mat resizedMat = resizeToPixelSize(processedMat, pixelSize);

                ImageIcon icon = new ImageIcon(matToBufferedImage(resizedMat));
                imageLabel.setIcon(icon);
            } else {
                System.out.println("Error: Cannot read frame from camera");
                ((Timer)e.getSource()).stop();
                camera.release();
            }
        }).start();
    }

    private static Mat resizeToPixelSize(Mat frame, int pixelSize) {
        Mat smallMat = new Mat();
        int newWidth = Math.max(1, frame.width() / pixelSize);
        int newHeight = Math.max(1, frame.height() / pixelSize);
        Imgproc.resize(frame, smallMat, new Size(newWidth, newHeight));
        Mat largeMat = new Mat();
        Imgproc.resize(smallMat, largeMat, new Size(DISPLAY_WIDTH, DISPLAY_HEIGHT), 0, 0, Imgproc.INTER_NEAREST);
        return largeMat;
    }

    private static Mat applyFilter(Mat frame, String filter) {
        Mat result = new Mat();
        switch (filter) {
            case "sepia" -> {
                Mat kernel = new Mat(4, 4, CvType.CV_32F);
                float[] sepiaKernel = {
                        0.272f, 0.534f, 0.131f, 0f,
                        0.349f, 0.686f, 0.168f, 0f,
                        0.393f, 0.769f, 0.189f, 0f,
                        0f, 0f, 0f, 1f
                };
                kernel.put(0, 0, sepiaKernel);
                Core.transform(frame, result, kernel);
            }
            case "negative" -> Core.bitwise_not(frame, result);
            case "grayscale" -> Imgproc.cvtColor(frame, result, Imgproc.COLOR_BGR2GRAY);
            case "weird" -> {
                Mat weirdKernel = new Mat(3, 3, CvType.CV_32F);
                float[] weirdColors = {
                        1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f,
                        1.0f, 0.0f, 1.0f
                };
                weirdKernel.put(0, 0, weirdColors);
                Core.transform(frame, result, weirdKernel);
            }
            case "anotherFilter" -> {
                // Define another filter kernel or processing here
                // Example: increase brightness
                frame.convertTo(result, -1, 1, 50);  // Increase brightness by 50
            }
            case "yetAnotherFilter" -> {
                // Define yet another filter kernel or processing here
                // Example: apply a custom color transformation
                Mat customKernel = new Mat(3, 3, CvType.CV_32F);
                float[] customColors = {
                        0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f
                };
                customKernel.put(0, 0, customColors);
                Core.transform(frame, result, customKernel);
            }
            default -> result = frame;
        }
        return result;
    }

    private static BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            Mat bgr = new Mat();
            Imgproc.cvtColor(matrix, bgr, Imgproc.COLOR_BGR2RGB);
            matrix = bgr;
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage image = new BufferedImage(matrix.width(), matrix.height(), type);
        DataBufferByte buffer = (DataBufferByte) image.getRaster().getDataBuffer();
        byte[] data = buffer.getData();
        matrix.get(0, 0, data);
        return image;
    }
}
