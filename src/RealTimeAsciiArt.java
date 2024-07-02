import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class RealTimeAsciiArt {
    // ASCII characters from dark to light
    private static final String ASCII_CHARS = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'........        ";
    private static final int SCALE = 8; // Adjust this to change the resolution of ASCII art

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not found or cannot be opened");
            return;
        }

        JFrame frame = new JFrame("Real-Time ASCII Art");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea));

        // Adjust size and update text area when window is resized
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = frame.getSize();
                textArea.setColumns(size.width / 7);
                textArea.setRows(size.height / 12);
            }
        });

        frame.setVisible(true);

        new Timer(33, e -> {
            Mat frameMat = new Mat();
            if (camera.read(frameMat)) {
                String asciiArt = convertToASCII(frameMat, textArea.getColumns(), textArea.getRows());
                textArea.setText(asciiArt);
            } else {
                System.out.println("Error: Cannot read frame from camera");
                ((Timer)e.getSource()).stop();
                camera.release();
            }
        }).start();
    }

    private static String convertToASCII(Mat frame, int textWidth, int textHeight) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        // Resize frame to fit text area dimensions
        Size newSize = new Size(textWidth, textHeight);
        Mat resizedFrame = new Mat();
        Imgproc.resize(grayFrame, resizedFrame, newSize);

        StringBuilder asciiArt = new StringBuilder();
        for (int row = 0; row < resizedFrame.rows(); row++) {
            for (int col = 0; col < resizedFrame.cols(); col++) {
                double[] pixel = resizedFrame.get(row, col);
                double brightness = pixel[0];
                int charIndex = (int) (brightness / 255.0 * (ASCII_CHARS.length() - 1));
                asciiArt.append(ASCII_CHARS.charAt(charIndex));
            }
            asciiArt.append("\n");
        }
        return asciiArt.toString();
    }
}
