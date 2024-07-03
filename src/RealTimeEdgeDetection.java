import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class RealTimeEdgeDetection {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not found or cannot be opened");
            return;
        }

        JFrame frame = new JFrame("Real-Time Edge Detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JLabel imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);

        new Timer(33, e -> {
            Mat frameMat = new Mat();
            if (camera.read(frameMat)) {
                Mat edges = new Mat();
                Imgproc.cvtColor(frameMat, edges, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(edges, edges, new Size(3, 3), 0);
                Imgproc.Canny(edges, edges, 50, 150);

                ImageIcon icon = new ImageIcon(matToBufferedImage(edges));
                imageLabel.setIcon(icon);
            } else {
                System.out.println("Error: Cannot read frame from camera");
                ((Timer)e.getSource()).stop();
                camera.release();
            }
        }).start();
    }

    private static BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            Mat bgr = new Mat();
            Imgproc.cvtColor(matrix, bgr, Imgproc.COLOR_GRAY2BGR);
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
