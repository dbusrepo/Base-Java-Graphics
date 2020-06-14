package com.busatod.graphics._apps.ray_tracer;

import com.busatod.graphics.app.GraphicsApplication;
import com.busatod.graphics.app.Settings;

public class RayTracer extends GraphicsApplication
{
	private World world;
	
	public RayTracer()
	{
		Settings settings = new Settings();
		settings.title = "Ray tracing";
		start(settings);
	}
	
	public static void main(String[] args)
	{
		new RayTracer();
	}
	
	@Override
	protected void appInit()
	{
		world = new World();
		world.build();
	}
	
	@Override
	protected void appUpdate(long elapsedTime)
	{
	
	}
	
	@Override
	protected void appDraw()
	{
		super.appDraw();
		world.render_scene();
	}
	
	@Override
	protected void appFinishOff()
	{
	
	}
	
	@Override
	protected void appPrintFinalStats()
	{
	
	}
}
