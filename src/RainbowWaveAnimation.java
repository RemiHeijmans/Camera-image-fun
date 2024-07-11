import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RainbowWaveAnimation extends JPanel implements ActionListener {
    private double phase = 0;
    private Timer timer;

    public RainbowWaveAnimation() {
        timer = new Timer(6, this);  // Adjusted timer to 16ms
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        for (int x = 0; x < width; x += 2) {  // Increased step to reduce computational load
            for (int y = 0; y < height; y += 2) {
                int color = Color.HSBtoRGB((float) (Math.sin(x * 0.01 + phase) + Math.sin(y * 0.01 + phase)), 1.0f, 1.0f);
                g2d.setColor(new Color(color));
                g2d.fillRect(x, y, 2, 2);  // Use fillRect instead of drawLine for better performance
            }
        }

        updateWave();
    }

    private void updateWave() {
        phase += 0.1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Rainbow Wave Animation");
        RainbowWaveAnimation wave = new RainbowWaveAnimation();
        frame.add(wave);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
