package base.graphics.app;

import java.awt.Graphics2D;

import javax.swing.JMenuBar;

public interface IGraphApp {
	void initApp();

	JMenuBar buildMenuApp();

	void drawFrameApp(Graphics2D g);

	void updateApp(long elapsedTime);

	void finishOffApp();

	void printFinalStatsApp();

}
