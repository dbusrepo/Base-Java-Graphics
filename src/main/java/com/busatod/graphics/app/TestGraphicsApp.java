package com.busatod.graphics.app;

public class TestGraphicsApp extends GraphicsApplication
{
	public TestGraphicsApp()
	{
		Settings settings = new Settings();
		settings.title = "Test Graphics app";
		start(settings);
	}
	
	@Override
	protected void appInit()
	{
	
	}
	
	@Override
	protected void appUpdate(long elapsedTime)
	{
	
	}
	
	@Override
	protected void appFinishOff()
	{
	
	}
	
	@Override
	protected void appPrintFinalStats()
	{
	
	}
	
	public static void main(String[] args)
	{
		new TestGraphicsApp();
	}
}
