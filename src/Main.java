import com.busatod.java.graphics.GraphicsApplication;

import java.util.Map;

public class Main {

    private static final boolean START_WINDOWED = true;
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;

    static class ArgSettings {

        // TARGET_FPS, VSYNC, ...
        //"true".equalsIgnoreCase(args.get("vsync"));

        private Map<String, String> settings;
        // javaGraphics -f 1024 768

        ArgSettings(String[] args) {
        }

        boolean useFullScreen() {
            return !START_WINDOWED;
        }

        int getWidth() {
            return DEFAULT_WIDTH;
        }

        int getHeight() {
            return DEFAULT_HEIGHT;
        }

    }

    public static void main(String[] args) {
        ArgSettings settings = new ArgSettings(args);
        settings.useFullScreen();

        GraphicsApplication graphicsApplication = new GraphicsApplication(settings.getWidth(), settings.getHeight(), settings.useFullScreen());
        graphicsApplication.run();
//        System.out.println("Exiting main...");
    }

}
