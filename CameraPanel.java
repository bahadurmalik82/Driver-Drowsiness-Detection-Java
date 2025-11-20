import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import javax.sound.sampled.*;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class CameraPanel extends JPanel implements Runnable {

    private VideoCapture capture;
    private CascadeClassifier faceCascade, eyeCascade;
    private BufferedImage image;
    private boolean eyesClosed = false;
    private long eyeClosedTime = 0;
    private boolean autoMode = false;
    private boolean stopped = false;
    private boolean waitingForManualConfirm = false;

    public CameraPanel() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            JOptionPane.showMessageDialog(this, "Camera not found!");
            return;
        }

        faceCascade = new CascadeClassifier("resources/haarcascade_frontalface_default.xml");
        eyeCascade = new CascadeClassifier("resources/haarcascade_eye.xml");

        if (faceCascade.empty() || eyeCascade.empty()) {
            JOptionPane.showMessageDialog(this, "Failed to load Haarcascade XML files.");
            return;
        }

        new Thread(this).start();
    }

    @Override
    public void run() {
        Mat frame = new Mat();

        while (capture.isOpened()) {
            capture.read(frame);

            if (!frame.empty()) {
                detectEyes(frame);
                repaint();
            }
        }
    }

    private void detectEyes(Mat frame) {
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);

        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(gray, faces);

        Rect[] facesArray = faces.toArray();
        boolean eyesDetected = false;

        for (Rect face : facesArray) {
            Mat faceROI = gray.submat(face);

            MatOfRect eyes = new MatOfRect();
            eyeCascade.detectMultiScale(faceROI, eyes);

            Rect[] eyesArray = eyes.toArray();
            eyesDetected = eyesArray.length > 0;

            if (!eyesDetected) {
                if (!eyesClosed) {
                    eyesClosed = true;
                    eyeClosedTime = System.currentTimeMillis();
                } else {
                    long duration = System.currentTimeMillis() - eyeClosedTime;
                    if (duration > 3000 && !autoMode && !stopped) {
                        autoMode = true;
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Eyes closed. Shifting to Auto-Driving mode.");
                            DrowsinessApp.messagePanel.addMessage("Eyes closed. Shifting to Auto-Driving mode.");
                        });
                    } else if (duration > 10000 && !stopped) {
                        stopped = true;
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Driver unresponsive. Car is stopping at roadside.");
                            DrowsinessApp.messagePanel.addMessage("Driver unresponsive. Car stopped.");
                        });
                        playAlarmSound(); // 🔔 Play alarm
                    }
                }
            } else {
                if (eyesClosed && !waitingForManualConfirm) {
                    eyesClosed = false;
                    if (autoMode) {
                        waitingForManualConfirm = true;
                        SwingUtilities.invokeLater(() -> {
                            int res = JOptionPane.showConfirmDialog(this,
                                    "Eyes opened. Return to Manual Mode?",
                                    "Switch Mode",
                                    JOptionPane.OK_CANCEL_OPTION);
                            if (res == JOptionPane.OK_OPTION) {
                                autoMode = false;
                                stopped = false;
                                DrowsinessApp.messagePanel.addMessage("Returned to Manual Mode.");
                            }
                            waitingForManualConfirm = false;
                        });
                    }
                }
            }

            Imgproc.rectangle(frame, face.tl(), face.br(), eyesDetected ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255), 2);
        }

        image = matToBufferedImage(frame);
    }

    private BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_3BYTE_BGR;
        if (matrix.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;

        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer);

        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);

        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // 🔊 Method to play alarm sound
    private void playAlarmSound() {
        new Thread(() -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                 new File("resources/mixkit-emergency-alert-alarm-1007.wav"));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
