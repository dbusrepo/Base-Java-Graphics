package com.busatod.graphics._apps.ray_tracer.utilities;

public class Constants
{
	public static final double PI         = Math.PI;
	public static final double INV_PI     = (1 / Math.PI);
	public static final double TWO_PI     = (2 * Math.PI);
	public static final double INV_TWO_PI = (1 / (2 * Math.PI));
	public static final double DEG_TO_RAD = (Math.PI / 180);
	
	public static final float EPS = 1e-7f;
	public static final float INF = 1e10f;
	
	public static final RGBColor BLACK = new RGBColor(0);
	public static final RGBColor WHITE = new RGBColor(1);
	public static final RGBColor RED   = new RGBColor(1, 0, 0);
//	public static final double INV_RAND_MAX = 1 / Math.rand;
}
