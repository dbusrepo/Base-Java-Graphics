package base.graphics.app;

public class Main extends GraphicsApplication {
	public Main() {
		Settings settings = new Settings();
		settings.title = "Base Graphics App with Java";
		start(settings);
	}

	@Override
	protected void appInit() {

	}

	@Override
	protected void appUpdate(long elapsedTime) {

	}

	@Override
	protected void appFinishOff() {

	}

	@Override
	protected void appPrintFinalStats() {

	}

	public static void main(String[] args) {
		new Main();
	}
}
