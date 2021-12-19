package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final long WAITING_TIME = TimeUnit.SECONDS.toMillis(10);
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private final Agent counter = new Agent();

    /**
     *  Builds a GUI according to the specifications of the exercise.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.setContentPane(panel);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        new Thread(counter).start();

        up.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                counter.upDirection();
            }
        });

        down.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!counter.subtract) {
                    counter.downDirection();
                }
            }
        });

        stop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                counter.stopCounting();
                down.setEnabled(false);
                up.setEnabled(false);
                stop.setEnabled(false);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WAITING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AnotherConcurrentGUI.this.stopCounting();
            }
        }).start();
    }

    private void stopCounting() {
        counter.stopCounting();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            }
        });
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean subtract;
        private volatile int counter;

        @Override
        public void run() {
            try {
                while (!stop) {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            AnotherConcurrentGUI.this.display.setText(Integer.toString(Agent.this.counter));
                        }
                    });
                    Thread.sleep(100);
                    if (!subtract) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                }
            } catch (InvocationTargetException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void upDirection() {
            this.subtract = false;
        }

        public void downDirection() {
            this.subtract = true;
        }

        public void stopCounting() {
            this.stop = true;
        }

    }
}
