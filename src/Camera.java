import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class Camera {

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

            // Save the captured frame to an image file
            Imgcodecs.imwrite("captured_image.jpg", frame);
            System.out.println("Image saved as captured_image.jpg");
        } else {
            System.out.println("Failed to capture frame");
        }

        // Release the camera
        camera.release();
    }
}
