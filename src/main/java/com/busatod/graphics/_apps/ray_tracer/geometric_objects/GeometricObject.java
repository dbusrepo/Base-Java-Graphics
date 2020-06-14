package com.busatod.graphics._apps.ray_tracer.geometric_objects;

import com.busatod.graphics._apps.ray_tracer.RGBColor;
import com.busatod.graphics._apps.ray_tracer.Ray;
import com.busatod.graphics._apps.ray_tracer.ShadeRec;

public abstract class GeometricObject
{
	public static class HitPoint
	{
		public float    tmin;
		public ShadeRec sr;
	}
	
	protected RGBColor color;    // only used for Bare Bones ray tracing
	
	public RGBColor getColor()
	{
		return color;
	}
	
	public void setColor(RGBColor color)
	{
		this.color = color;
	}
	
	public void setColor(float r, float g, float b)
	{
//		this.color.se
	}
	
	public abstract boolean hit(Ray ray, HitPoint hit_point);
	
}

