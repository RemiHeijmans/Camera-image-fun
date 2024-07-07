import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants
import kotlin.concurrent.fixedRateTimer

fun main() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val camera = VideoCapture(0)
        if (!camera.isOpened) {
                println("Error: Camera not found or cannot be opened")
                return
        }

        val width = 80
        val height = 60
        val delay = 100L // milliseconds
        var useDarkToLight = true

        val frame = JFrame("Real-Time ASCII Art Video Feed")
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.setSize(800, 600)
        val imageLabel = JLabel()
        frame.add(imageLabel)
        frame.isVisible = true

        // Toggle the ASCII mapping when 't' is pressed
        frame.addKeyListener(object : java.awt.event.KeyAdapter() {
                override fun keyPressed(e: java.awt.event.KeyEvent) {
                        if (e.keyChar == 't') {
                                useDarkToLight = !useDarkToLight
                                println("Switched to ${if (useDarkToLight) "dark-to-light" else "light-to-dark"} mode")
                        }
                }
        })

        fixedRateTimer("cameraAsciiArtTimer", false, 0L, delay) {
                val matFrame = Mat()
                if (camera.read(matFrame)) {
                        val asciiArt = convertFrameToAscii(matFrame, width, height, useDarkToLight)
                        val asciiImage = asciiArtToBufferedImage(asciiArt, 10)
                        imageLabel.icon = ImageIcon(asciiImage)
                } else {
                        println("Error: Cannot read frame from camera")
                        camera.release()
                        cancel()
                }
        }
}

fun convertFrameToAscii(frame: Mat, width: Int, height: Int, useDarkToLight: Boolean): Array<String> {
        // Resize the frame
        val resizedFrame = Mat()
        Imgproc.resize(frame, resizedFrame, Size(width.toDouble(), height.toDouble()))

        // Convert to grayscale
        val grayFrame = Mat()
        Imgproc.cvtColor(resizedFrame, grayFrame, Imgproc.COLOR_BGR2GRAY)

        // Create ASCII characters
        val darkToLightChars = arrayOf(' ', '.', ',', ':', ';', '+', '*', '?', '%', 'S', '#', '@')
        val lightToDarkChars = darkToLightChars.reversedArray()

        val chars = if (useDarkToLight) darkToLightChars else lightToDarkChars

        // Convert each pixel to an ASCII character
        val asciiArt = Array(height) { "" }
        val data = ByteArray(width * height)
        grayFrame.get(0, 0, data)
        for (i in 0 until height) {
                val sb = StringBuilder()
                for (j in 0 until width) {
                        val pixelValue = data[i * width + j].toInt() and 0xFF
                        val charIndex = (pixelValue / (256 / chars.size)).coerceAtMost(chars.size - 1)
                        sb.append(chars[charIndex])
                }
                asciiArt[i] = sb.toString()
        }

        return asciiArt
}

fun asciiArtToBufferedImage(asciiArt: Array<String>, charWidth: Int): BufferedImage {
        val width = asciiArt[0].length * charWidth
        val height = asciiArt.size * charWidth
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = image.graphics

        for (y in asciiArt.indices) {
                for (x in asciiArt[y].indices) {
                        val c = asciiArt[y][x]
                        val gray = c.toGrayScale()
                        g.color = java.awt.Color(gray, gray, gray)
                        g.drawString(c.toString(), x * charWidth, (y + 1) * charWidth)
                }
        }
        g.dispose()
        return image
}

fun Char.toGrayScale(): Int {
        return when (this) {
                ' ' -> 255
                '.' -> 230
                ',' -> 200
                ':' -> 180
                ';' -> 150
                '+' -> 120
                '*' -> 100
                '?' -> 70
                '%' -> 50
                'S' -> 30
                '#' -> 10
                '@' -> 0
                else -> 255
        }
}
