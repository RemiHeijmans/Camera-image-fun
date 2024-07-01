import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Scanner;

public class ImageProcessor {
    //private static final String ASCII_CHARS = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'...    ";
    private static final String ASCII_CHARS = "           .........-':_,^=;><+!rc*/z?sLTv)J7(|Fi{C}fI31tlu[neoZ5Yxjya]2ESwqkP6h9d4VpOGbUAKXHm8RD#$Bg0MNWQ%&@";
    private static final Color[] BASIC_COLORS = {
            Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY,
            Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK,
            Color.RED, Color.WHITE, Color.YELLOW,
            Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray,
            Color.green, Color.lightGray, Color.magenta, Color.orange, Color.pink,
            Color.red, Color.white, Color.yellow
    };
    private static final Color[] TRICOLORS = { Color.RED, Color.GREEN, Color.BLUE };

    private static String imagePath;
    private static int width;
    private static int height;
    private static String outputType;
    private static String colorFilter;
    private static int squareSize;

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

        if (outputType.equals("swing")) {
            System.out.print("Enter the size of the squares: ");
            squareSize = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        }

        if (!outputType.equals("ascii")) {
            System.out.print("Choose color filter (normal, black_white, low_col, tricol): ");
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
            case "low_col":
                result = reduceColors(image, BASIC_COLORS);
                break;
            case "tricol":
                result = reduceColors(image, TRICOLORS);
                break;
            default:
                result = image.clone();
        }
        return result;
    }

    private static Mat reduceColors(Mat image, Color[] palette) {
        Mat reducedImage = new Mat(image.size(), image.type());
        for (int row = 0; row < image.rows(); row++) {
            for (int col = 0; col < image.cols(); col++) {
                double[] pixel = image.get(row, col);
                Color color = new Color((int) pixel[2], (int) pixel[1], (int) pixel[0]);
                Color closestColor = findClosestColor(color, palette);
                reducedImage.put(row, col, new double[]{closestColor.getBlue(), closestColor.getGreen(), closestColor.getRed()});
            }
        }
        return reducedImage;
    }

    private static Color findClosestColor(Color color, Color[] palette) {
        Color closestColor = palette[0];
        double minDistance = Double.MAX_VALUE;
        for (Color basicColor : palette) {
            double distance = colorDistance(color, basicColor);
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = basicColor;
            }
        }
        return closestColor;
    }

    private static double colorDistance(Color c1, Color c2) {
        int redDiff = c1.getRed() - c2.getRed();
        int greenDiff = c1.getGreen() - c2.getGreen();
        int blueDiff = c1.getBlue() - c2.getBlue();
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
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

        ImagePanel panel = new ImagePanel(image, squareSize, colorFilter);
        frame.add(panel);
        frame.setSize(width * squareSize, height * squareSize);
        frame.setVisible(true);
    }

    static class ImagePanel extends JPanel {
        private final Mat mat;
        private final int squareSize;
        private final String colorFilter;

        public ImagePanel(Mat mat, int squareSize, String colorFilter) {
            this.mat = mat;
            this.squareSize = squareSize;
            this.colorFilter = colorFilter;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mat != null) {
                for (int row = 0; row < mat.rows(); row++) {
                    for (int col = 0; col < mat.cols(); col++) {
                        double[] pixel = mat.get(row, col);
                        Color color;
                        if (colorFilter.equals("black_white")) {
                            int gray = (int) pixel[0];
                            color = new Color(gray, gray, gray);
                        } else if (colorFilter.equals("tricol")) {
                            Color originalColor = new Color((int) pixel[2], (int) pixel[1], (int) pixel[0]);
                            color = findClosestColor(originalColor, TRICOLORS);
                        } else {
                            color = new Color((int) pixel[2], (int) pixel[1], (int) pixel[0]);
                        }
                        g.setColor(color);
                        g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
                    }
                }
            }
        }
    }
}
