import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class VideoRecorder {

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

        // Define the codec and create VideoWriter object
        String filename = "output_video.avi";
        int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
        VideoWriter writer = new VideoWriter(filename, fourcc, 30,
                new org.opencv.core.Size((int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH),
                        (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT)));

        if (!writer.isOpened()) {
            System.out.println("Error: Cannot open VideoWriter");
            camera.release();
            return;
        }

        // Record video for a fixed duration (e.g., 10 seconds)
        long startTime = System.currentTimeMillis();
        long duration = 10000; // 10 seconds

        Mat frame = new Mat();
        while (System.currentTimeMillis() - startTime < duration) {
            if (camera.read(frame)) {
                writer.write(frame);

                // Show the frame in a window
                HighGui.imshow("Recording", frame);
                if (HighGui.waitKey(1) == 27) { // Press 'ESC' to exit early
                    break;
                }
            } else {
                System.out.println("Failed to capture frame");
            }
        }

        // Release resources
        writer.release();
        camera.release();
        HighGui.destroyAllWindows();
        System.out.println("Video recording complete. Saved as " + filename);
    }
}

