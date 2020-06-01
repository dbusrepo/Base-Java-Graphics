package com.busatod.graphics.app;

public interface IAppLogic {
	public void init();
	void draw();
	void finish();

	void printFinalStats();

	void update(long elapsedTime);
}
