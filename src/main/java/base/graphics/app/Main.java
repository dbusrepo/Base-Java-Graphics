package base.graphics.app;

public class Main extends GraphicsAppSoftRendering {

	public Main() {
		Settings settings = new Settings();
		settings.title = "Java graphics app";
		start(settings);
	}

	public static void main(String[] args) {
		new Main();
	}
}
