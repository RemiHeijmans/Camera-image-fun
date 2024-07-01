import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ImageGuessingGame {
    private static final int FRAME_WIDTH = 1500;
    private static final int FRAME_HEIGHT = 1000;
    private static final List<String> IMAGE_PATHS = List.of(




            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\download.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\download-betta.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\dog.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\doggo.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\Geograhic_V5_Territorial_Map.png",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\univeriteit maastricht\\Year1\\prodject 1-2\\Goat-peter.jpg",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\univeriteit maastricht\\Year1\\prodject 1-2\\bear-micheal.jpg",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\univeriteit maastricht\\Year1\\prodject 1-2\\manatee-samuel.jpg",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\univeriteit maastricht\\Year1\\prodject 1-2\\falcon-swedder.jpg",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\univeriteit maastricht\\Year1\\prodject 1-2\\pony-denisa.webp",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\univeriteit maastricht\\Year1\\prodject 1-2\\Neapolitan-Mastiff-laying-down-on-a-stoop-Nick.webp",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\niet school\\ART\\honnnnndd.PNG",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\niet school\\ART\\istockphoto-1212898099-1024x1024.jpg",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\niet school\\ART\\prachtiggg.png",
            "C:\\Users\\Admin\\OneDrive\\Documenten\\niet school\\ART\\schilderij nog lang niet af....png",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\shrimp.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\redpanda.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\eagle.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\hippo.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\kangaroejfif.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\specht.jpg",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\tree.jpeg",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\krok.jfif",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\kwal.jpg",
            "C:\\Users\\Admin\\OneDrive\\Afbeeldingen\\snek.jpg"

    );
    private static final Random RANDOM = new Random();
    private static Scanner scanner;
    private static ImagePanel panel;
    private static JFrame frame;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("Load successful");

        scanner = new Scanner(System.in);

        frame = new JFrame("Image Guessing Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);

        panel = new ImagePanel();
        frame.add(panel);

        while (true) {
            String imagePath = getRandomImagePath();
            processAndDisplayImage(imagePath);
            System.out.print("Type 'next' to load another image or 'stop' to end the game: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("stop")) {
                break;
            }
        }
    }

    private static String getRandomImagePath() {
        int index = RANDOM.nextInt(IMAGE_PATHS.size());
        return IMAGE_PATHS.get(index);
    }

    private static void processAndDisplayImage(String imagePath) {
        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            System.out.println("Error: Image not found or cannot be opened");
            return;
        }

        int originalWidth = image.cols();
        int originalHeight = image.rows();

        new Thread(() -> {
            try {
                for (int size = 1; size <= Math.max(originalWidth, originalHeight); size *= 2) {
                    Mat resizedImage = new Mat();
                    Imgproc.resize(image, resizedImage, new Size(size, size), 0, 0, Imgproc.INTER_AREA);
                    Mat displayImage = new Mat();
                    Imgproc.resize(resizedImage, displayImage, new Size(FRAME_WIDTH-245, FRAME_HEIGHT-375), 0, 0, Imgproc.INTER_AREA);
                    panel.setImage(displayImage);
                    panel.repaint();
                    Thread.sleep(1000); // Adjust the delay as needed
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    static class ImagePanel extends JPanel {
        private Mat mat;

        public void setImage(Mat mat) {
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
