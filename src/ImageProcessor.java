import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;
import org.opencv.core.MatOfInt;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ImageProcessor {

    private static final String ASCII_CHARS = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
    //private static final String ASCII_CHARS = "           .........-':_,^=;><+!rc*/z?sLTv)J7(|Fi{C}fI31tlu[neoZ5Yxjya]2ESwqkP6h9d4VpOGbUAKXHm8RD#$Bg0MNWQ%&@";

    private static String imagePath;
    private static int width;
    private static int height;
    private static String outputType;
    private static String colorFilter;

    public static void main(String[] args) {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("Load successful");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the path to the input image: ");
        imagePath = scanner.nextLine();

        System.out.print("Enter the width of the new image: ");
        width = scanner.nextInt();

        System.out.print("Enter the height of the new image: ");
        height = scanner.nextInt();

        scanner.nextLine(); // Consume newline

        System.out.print("Choose output type (picture, ascii, swing): ");
        outputType = scanner.nextLine().toLowerCase();

        if (!outputType.equals("ascii")) {
            System.out.print("Choose color filter (normal, black_white, no_red, no_green, no_blue, only_red, only_green, only_blue): ");
            colorFilter = scanner.nextLine().toLowerCase();
        } else {
            colorFilter = "normal";
        }

        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            System.out.println("Error: Image not found or cannot be opened");
            return;
        }

        // Apply the color filter
        Mat processedImage = applyColorFilter(image, colorFilter);

        // Resize the image
        Mat resizedImage = new Mat();
        Imgproc.resize(processedImage, resizedImage, new Size(width, height));

        if (outputType.equals("ascii")) {
            String asciiArt = convertToASCII(resizedImage);
            System.out.println(asciiArt);
        } else if (outputType.equals("swing")) {
            SwingUtilities.invokeLater(() -> createAndShowGUI(resizedImage));
        } else {
            String outputPath = "output_image.jpg";
            Imgcodecs.imwrite(outputPath, resizedImage);
            System.out.println("Image saved as " + outputPath);
        }
    }

    private static Mat applyColorFilter(Mat image, String filter) {
        Mat result = new Mat();
        switch (filter) {
            case "black_white":
                Imgproc.cvtColor(image, result, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(result, result, Imgproc.COLOR_GRAY2BGR);
                break;
            case "no_red":
                List<Mat> noRedChannels = new ArrayList<>();
                Core.split(image, noRedChannels);
                noRedChannels.set(2, Mat.zeros(image.size(), image.type()));
                Core.merge(noRedChannels, result);
                break;
            case "no_green":
                List<Mat> noGreenChannels = new ArrayList<>();
                Core.split(image, noGreenChannels);
                noGreenChannels.set(1, Mat.zeros(image.size(), image.type()));
                Core.merge(noGreenChannels, result);
                break;
            case "no_blue":
                List<Mat> noBlueChannels = new ArrayList<>();
                Core.split(image, noBlueChannels);
                noBlueChannels.set(0, Mat.zeros(image.size(), image.type()));
                Core.merge(noBlueChannels, result);
                break;
            case "only_red":
                List<Mat> onlyRedChannels = new ArrayList<>();
                Core.split(image, onlyRedChannels);
                onlyRedChannels.set(0, Mat.zeros(image.size(), image.type()));
                onlyRedChannels.set(1, Mat.zeros(image.size(), image.type()));
                Core.merge(onlyRedChannels, result);
                break;
            case "only_green":
                List<Mat> onlyGreenChannels = new ArrayList<>();
                Core.split(image, onlyGreenChannels);
                onlyGreenChannels.set(0, Mat.zeros(image.size(), image.type()));
                onlyGreenChannels.set(2, Mat.zeros(image.size(), image.type()));
                Core.merge(onlyGreenChannels, result);
                break;
            case "only_blue":
                List<Mat> onlyBlueChannels = new ArrayList<>();
                Core.split(image, onlyBlueChannels);
                onlyBlueChannels.set(1, Mat.zeros(image.size(), image.type()));
                onlyBlueChannels.set(2, Mat.zeros(image.size(), image.type()));
                Core.merge(onlyBlueChannels, result);
                break;
            default:
                result = image.clone();
        }
        return result;
    }

    private static String convertToASCII(Mat image) {
        StringBuilder asciiArt = new StringBuilder();
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat equalizedImage = new Mat();
        Imgproc.equalizeHist(grayImage, equalizedImage);

        for (int row = 0; row < equalizedImage.rows(); row++) {
            for (int col = 0; col < equalizedImage.cols(); col++) {
                double[] pixel = equalizedImage.get(row, col);
                double brightness = pixel[0];
                int charIndex = (int) (brightness / 255.0 * (ASCII_CHARS.length() - 1));
                asciiArt.append(ASCII_CHARS.charAt(charIndex));
            }
            asciiArt.append("\n");
        }
        return asciiArt.toString();
    }

    private static void createAndShowGUI(Mat image) {
        JFrame frame = new JFrame("Swing Image Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImagePanel panel = new ImagePanel(image);
        frame.add(panel);
        frame.setSize(width, height);
        frame.setVisible(true);
    }

    static class ImagePanel extends JPanel {
        private final Mat mat;

        public ImagePanel(Mat mat) {
            this.mat = mat;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mat != null) {
                for (int row = 0; row < mat.rows(); row++) {
                    for (int col = 0; col < mat.cols(); col++) {
                        double[] pixel = mat.get(row, col);
                        Color color = new Color((int) pixel[2], (int) pixel[1], (int) pixel[0]);
                        g.setColor(color);
                        g.fillRect(col, row, 1, 1);
                    }
                }
            }
        }
    }
}
