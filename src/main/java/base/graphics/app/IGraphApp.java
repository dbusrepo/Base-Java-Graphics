package base.graphics.app;

import java.awt.Graphics2D;

import javax.swing.JMenuBar;

public interface IGraphApp {
	void init();

	JMenuBar buildMenu();

	void drawFrame(Graphics2D g);

	void update(long elapsedTime);

	void finishOff();

	void printFinalStats();

	void showStats(Graphics2D g);
}
