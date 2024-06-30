import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageRGBExtractor {

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

            // Resize the captured frame to 32x32 pixels
            Mat resizedFrame = new Mat();
            Imgproc.resize(frame, resizedFrame, new Size(32, 32));

            // Save the resized frame to an image file
            String filename = "captured_image_32x32.jpg";
            Imgcodecs.imwrite(filename, resizedFrame);
            System.out.println("Image saved as " + filename);

            // Extract and print RGB values
            for (int row = 0; row < resizedFrame.rows(); row++) {
                for (int col = 0; col < resizedFrame.cols(); col++) {
                    double[] rgb = resizedFrame.get(row, col);
                    System.out.println("Pixel (" + row + ", " + col + "): " +
                            "R=" + rgb[2] + ", G=" + rgb[1] + ", B=" + rgb[0]);
                }
            }
        } else {
            System.out.println("Failed to capture frame");
        }

        // Release the camera
        camera.release();
    }
}
