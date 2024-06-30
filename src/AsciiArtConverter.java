import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class AsciiArtConverter {

    private static final String ASCII_CHARS = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";

    public static void main(String[] args) {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("Load successful");

        // Create a VideoCapture object to access the camera
        VideoCapture camera = new VideoCapture(0);

        // Check if the camera is opened successfully
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not detected or cannot be opened");
            return;
        }

        // Capture a frame from the camera
        Mat frame = new Mat();
        if (camera.read(frame)) {
            System.out.println("Frame captured successfully");

            // Resize the captured frame to a smaller size for ASCII art
            Mat resizedFrame = new Mat();
            Imgproc.resize(frame, resizedFrame, new Size(100, 50)); // Adjust size as needed

            // Convert the frame to grayscale
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(resizedFrame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            // Adjust brightness using histogram equalization
            Mat equalizedFrame = new Mat();
            Imgproc.equalizeHist(grayFrame, equalizedFrame);

            // Convert equalized grayscale image to ASCII
            StringBuilder asciiArt = new StringBuilder();
            for (int row = 0; row < equalizedFrame.rows(); row++) {
                for (int col = 0; col < equalizedFrame.cols(); col++) {
                    double[] pixel = equalizedFrame.get(row, col);
                    double brightness = pixel[0];
                    int charIndex = (int) (brightness / 255.0 * (ASCII_CHARS.length() - 1));
                    asciiArt.append(ASCII_CHARS.charAt(charIndex));
                }
                asciiArt.append("\n");
            }

            // Print the ASCII art
            System.out.println(asciiArt.toString());

        } else {
            System.out.println("Failed to capture frame");
        }

        // Release the camera
        camera.release();
    }
}
