import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

public class RealTimeMotionDetection {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static Mat previousFrame;

    public static void main(String[] args) {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not found or cannot be opened");
            return;
        }

        JFrame frame = new JFrame("Real-Time Motion Detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JLabel imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);

        new Timer(33, e -> {
            Mat frameMat = new Mat();
            if (camera.read(frameMat)) {
                Mat motionFrame = detectMotion(frameMat);

                ImageIcon icon = new ImageIcon(matToBufferedImage(motionFrame));
                imageLabel.setIcon(icon);
            } else {
                System.out.println("Error: Cannot read frame from camera");
                ((Timer)e.getSource()).stop();
                camera.release();
            }
        }).start();
    }

    private static Mat detectMotion(Mat frame) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(21, 21), 0);

        if (previousFrame == null) {
            previousFrame = grayFrame;
            return frame;
        }

        Mat frameDelta = new Mat();
        Core.absdiff(previousFrame, grayFrame, frameDelta);
        Imgproc.threshold(frameDelta, frameDelta, 25, 255, Imgproc.THRESH_BINARY);
        Imgproc.dilate(frameDelta, frameDelta, new Mat(), new org.opencv.core.Point(-1, -1), 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(frameDelta, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
        }

        previousFrame = grayFrame;
        return frame;
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
