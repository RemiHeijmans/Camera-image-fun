import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageToASCIIConverterallpixels {

    //private static final String ASCII_CHARS = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
    private static final String ASCII_CHARS = "           .........-':_,^=;><+!rc*/z?sLTv)J7(|Fi{C}fI31tlu[neoZ5Yxjya]2ESwqkP6h9d4VpOGbUAKXHm8RD#$Bg0MNWQ%&@";

    public static void main(String[] args) {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("Load successful");

        // Path to the input image file
        String imagePath = "C:\\Users\\Admin\\OneDrive\\Documenten\\univeriteit maastricht\\Year1\\prodject 1-2\\falcon-swedder.jpg"; // Replace with the actual path to your image

        // Read the image
        Mat image = Imgcodecs.imread(imagePath);

        // Check if the image is loaded successfully
        if (image.empty()) {
            System.out.println("Error: Image not found or cannot be opened");
            return;
        }

        // Convert the image to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Adjust brightness using histogram equalization
        Mat equalizedImage = new Mat();
        Imgproc.equalizeHist(grayImage, equalizedImage);

        // Convert equalized grayscale image to ASCII
        StringBuilder asciiArt = new StringBuilder();
        for (int row = 0; row < equalizedImage.rows(); row++) {
            for (int col = 0; col < equalizedImage.cols(); col++) {
                double[] pixel = equalizedImage.get(row, col);
                double brightness = pixel[0];
                int charIndex = (int) (brightness / 255.0 * (ASCII_CHARS.length() - 1));
                asciiArt.append(ASCII_CHARS.charAt(charIndex));
            }
            asciiArt.append("\n");
        }

        // Print the ASCII art
        System.out.println(asciiArt.toString());
    }
}
