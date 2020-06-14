package com.busatod.graphics._apps.ray_tracer.tracers;

import com.busatod.graphics._apps.ray_tracer.Constants;
import com.busatod.graphics._apps.ray_tracer.RGBColor;
import com.busatod.graphics._apps.ray_tracer.Ray;
import com.busatod.graphics._apps.ray_tracer.World;

public class Tracer
{
	private World world;
	
	public Tracer()
	{
	}
	
	public Tracer(World world)
	{
		this.world = world;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}
	
	public RGBColor trace_ray(Ray ray)
	{
		return Constants.BLACK;
	}
	
	public RGBColor trace_ray(Ray ray, int depth)
	{
		return Constants.BLACK;
	}
	
}
