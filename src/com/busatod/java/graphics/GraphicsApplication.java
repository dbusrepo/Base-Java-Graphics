package com.busatod.java.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;

// vedere:
// https://stackoverflow.com/questions/35516191/what-is-the-correct-way-to-use-createbufferstrategy

public class GraphicsApplication extends JFrame {

    private static final String TITLE = "Java Graphics";
    private static final int NUM_BUFFERS = 3;
    private static final boolean VSYNC = true;
    private static final long NANO_IN_MILLI = 1000000L;
    // num of iterations with a sleep delay of 0ms before
    // game loop yields to other threads.
    private static final int NUM_DELAYS_PER_YIELD = 16;
    // max num of renderings that can be skipped in one game loop,
    // game's internal state is updated but not rendered on screen.
    private static int MAX_RENDER_SKIPS = 5;
    private static boolean LOCK_FPS = false;
    private static int TARGET_FPS = 20; // -1 if not used

//    private GraphicsEnvironment graphicsEnvironment;
//    private GraphicsDevice screenDevice;

    private InputManager inputManager;
    private InputAction exitAction;
    private InputAction pauseAction;
    private boolean isRunning;
    private boolean isPaused;
    // TODO gestire nuova action per il toggling del fullscreen
    private boolean toogleFullscreenKeyPressed;

    private boolean isWindowed;
    private int winWidth;
    private int winHeight;
    private int winXPos, winYPos;

    private long startTime;
    private long fps;
    private long frameCounter;
    private long lastFpsTime;
    private long rendersSkipped;
    private Font fpsFont = new Font("Consolas", Font.PLAIN, 25);

    public GraphicsApplication() {
        setTitle(TITLE);
//        graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        screenDevice = graphicsEnvironment.getDefaultScreenDevice();
    }

    // init for windowed mode
    public void init(int width, int height, boolean startWindowed) {
        System.out.println("Initializing the graphics display...");
        setIgnoreRepaint(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // do we want window decorations like the title bar?
        setUndecorated(!startWindowed);
//            setDefaultLookAndFeelDecorated(true);
//            setSize(windowedWidth, windowedHeight);
        this.winWidth = width;
        this.winHeight = height;
        adjustWinSize();
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);
        setVisible(true);

        this.isWindowed = startWindowed;
        if (!startWindowed) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            getGraphicsConfiguration().getDevice().setFullScreenWindow(this);
        }
//            int windowBarHeight = (int) (windowedHeight - getContentPane().getSize().getHeight());
//            setSize(windowedWidth, windowedHeight + windowBarHeight);
        //            setSize(windowedWidth, windowedHeight);
        // TODO
        createBufferStrategy();
        // TODO crearli qui??
        setupWindowListener();

        this.inputManager = new InputManager(this);
        createInputActions();

    }
    private void createInputActions() {
        exitAction = new InputAction("exit", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
        pauseAction = new InputAction("pause", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
        inputManager.mapToKey(exitAction, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pauseAction, KeyEvent.VK_F1);
    }

    private void createBufferStrategy() {
        // avoid potential deadlock in 1.4.1_02
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    createBufferStrategy(NUM_BUFFERS);
                }
            });
        } catch (InterruptedException ex) {
            // ignore
        } catch (InvocationTargetException ex) {
            // ignore
        }
    }

    private void adjustWinSize() {
        //windowBarHeight = (int) (HEIGHT - getContentPane().getSize().getHeight());
        //setSize(WIDTH, HEIGHT + windowBarHeight);
        getContentPane().setPreferredSize(new Dimension(winWidth, winHeight));
        pack();
    }

    public void run() {
        System.out.println("Entering the main loop...");
        try {
            execLoop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Exiting...");
        // make sure we restore the video mode before exiting
        initFromScreen();
    }

    // https://stackoverflow.com/questions/16364487/java-rendering-loop-and-logic-loop
    private void execLoop() {
        isRunning = true;

        // calc the frame period and print it
        long period = 0L;  // rendering FPS (nanosecs/targetFPS)
        if (LOCK_FPS) {
            period = (1000L * NANO_IN_MILLI) / TARGET_FPS;
            System.out.println("FPS: " + TARGET_FPS + ", vsync=" + VSYNC);
            System.out.println("FPS period: " + period);
        }

        // Cache the buffer strategy
        BufferStrategy bufferStrategy = getBufferStrategy();

        rendersSkipped = 0L;
        startTime = System.nanoTime();

        // local variables loop initialization
        long overSleepTime = 0L;
        int numDelays = 0;
        long excess = 0L;
        long beforeTime = startTime;

        // Main loop
        while (isRunning) {
            // **2) execute physics
            if (toogleFullscreenKeyPressed) {
                toggleFullscreen();
                toogleFullscreenKeyPressed = false;
            }

            update(0);

            Graphics graphics = bufferStrategy.getDrawGraphics();
            render(graphics);
            graphics.dispose();

            if (VSYNC) {
                Toolkit.getDefaultToolkit().sync();
            }

            // Flip the buffer
            if (!bufferStrategy.contentsLost()) {
                bufferStrategy.show();
            }

            long afterTime = System.nanoTime();
            calculateFramesPerSecond(afterTime);
            if (!LOCK_FPS) {
                continue;
            }

            long timeDiff = afterTime - beforeTime;
            long sleepTime = (period - timeDiff) - overSleepTime;
            if (sleepTime > 0) { // time left in cycle
                //System.out.println("sleepTime: " + (sleepTime/NANO_IN_MILLI));
                try {
                    Thread.sleep(sleepTime / NANO_IN_MILLI);//nano->ms
                } catch (InterruptedException ex) {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else { // sleepTime <= 0
                System.out.println("Rendering too slow");
                // this cycle took longer than period
                excess -= sleepTime;
                // store excess time value
                overSleepTime = 0L;
                if (++numDelays >= NUM_DELAYS_PER_YIELD) {
                    Thread.yield();
                    // give another thread a chance to run
                    numDelays = 0;
                }
            }

            beforeTime = System.nanoTime();

            /* If the rendering is taking too long, then
                  update the game state without rendering
                  it, to get the UPS nearer to the
                  required frame rate. */
            int skips = 0;
            while ((excess > period) && (skips < MAX_RENDER_SKIPS)) {
                // update state but don’t render
                System.out.println("Skip renderFPS, run updateFPS");
                excess -= period;
                update(0); // period as elapsedTime?
                skips++;
            }
            rendersSkipped += skips;
        }

    }

    private void calculateFramesPerSecond(long curRenderTime) {
        if (curRenderTime - lastFpsTime >= NANO_IN_MILLI * 1000) {
            fps = frameCounter;
            frameCounter = 0;
            lastFpsTime = curRenderTime;
        }
        frameCounter++;
    }

    private void update(long elapsedTime) {
        // check input that can happen whether paused or not
        checkSystemInput();
        if (!isPaused) {
            // TODO check app input
            updateWorld(elapsedTime);
        }
    }

    private void checkSystemInput() {
        if (pauseAction.isPressed()) {
            isPaused = !isPaused;
        }
        if (exitAction.isPressed()) {
            stop();
        }
    }

    private void updateWorld(long l) {
    }

    private void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(fpsFont);
        g.setColor(Color.YELLOW);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        g.drawString("FPS: " + fps, 10, 60);
    }

    /**
     * Remove the window from the screen, if we are in full screen
     * mode then we need to reset the video mode.
     */
    // TODO
    public void initFromScreen() {
        if (!this.isWindowed) {
            getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
        }
        setVisible(false); //you can't see me!
        dispose(); //Destroy the JFrame object
    }

//    /**
//     * Exit the program if the escape key is typed
//     */
//    private void keyPressedHandler(KeyEvent evt) {
//        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
//            stop();
//        }
//        if (evt.getKeyCode() == KeyEvent.VK_F1) {
//            toogleFullscreenKeyPressed = true;
//        }
//    }

    /**
     * Signals the main loop that it's time to quit
     */
    private void stop() {
        isRunning = false;
    }

    public void toggleFullscreen() {
        setVisible(false);
        dispose();
        if (isWindowed) {
            winXPos = getX();
            winYPos = getY();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);
            getGraphicsConfiguration().getDevice().setFullScreenWindow(this);
        } else {
            setExtendedState(JFrame.NORMAL);
            setUndecorated(false);
            getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
            setLocation(winXPos, winYPos);
        }
        adjustWinSize();
        isWindowed = !isWindowed;
        setVisible(true);
    }

    /**
     * We may need to handle different window events
     * so we set up a window listener for this Frame.
     */
    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });
    }

    /**
     * Handle any special processing needed if the window is activated.
     */
    protected void formWindowActivated(java.awt.event.WindowEvent evt) {
    }

    /**
     * Handle any special processing needed if the window is deactivated.
     */
    protected void formWindowDeactivated(java.awt.event.WindowEvent evt) {
    }

    /**
     * Handle any special processing needed if the window is deiconified.
     */
    protected void formWindowDeiconified(java.awt.event.WindowEvent evt) {
    }

    /**
     * Handle any special processing needed if the window is iconified.
     */
    protected void formWindowIconified(java.awt.event.WindowEvent evt) {
    }

    /**
     * Handle any special processing needed if the window is closed.
     */
    protected void exitForm(java.awt.event.WindowEvent evt) {
        exitAction.press();
    }
}
