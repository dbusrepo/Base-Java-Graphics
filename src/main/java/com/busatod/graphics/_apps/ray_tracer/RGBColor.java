package com.busatod.graphics._apps.ray_tracer;

public class RGBColor
{
	float r, g, b;
	
	public RGBColor()
	{
		this(0, 0, 0);
	}
	
	public RGBColor(float c)
	{
		this(c, c, c);
	}
	
	public RGBColor(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public RGBColor(RGBColor c)
	{
		this(c.r, c.g, c.b);
	}
	
	public RGBColor add(RGBColor c)
	{
		return new RGBColor(this.r + c.r, this.g + c.g, this.b + c.b);
	}
	
	public RGBColor scale(float a)
	{
		return new RGBColor(a * this.r, a * this.g, a * this.b);
	}
	
	// component-wise multiplication of two colors
	public RGBColor mul(RGBColor c)
	{
		return new RGBColor(this.r * c.r, this.g * c.g, this.b * c.b);
	}
	
	// the average of the three components
	public float average()
	{
		return (this.r + this.g + this.b) / 3;
	}
	
	public RGBColor powc(float p)
	{
		return new RGBColor((float) Math.pow(r, p), (float) Math.pow(g, p), (float) Math.pow(b, p));
	}
}

