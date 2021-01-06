package base.graphics.app;

public class Main extends GraphAppSoftRendering {

	public Main() throws Exception {
		Settings settings = new Settings();
		settings.title = "Java graphics app";
		settings.showMenu = true;
		start(settings);
	}

	public static void main(String[] args) throws Exception {
		new Main();
	}

	@Override
	public void updateApp(long elapsedTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishOffApp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void printFinalStatsApp() {
		// TODO Auto-generated method stub

	}
}
