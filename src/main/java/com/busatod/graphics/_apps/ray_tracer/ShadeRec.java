package com.busatod.graphics._apps.ray_tracer;

public class ShadeRec
{
	boolean  hit_an_object;        // did the ray hit an object?
	Point3D  world_hit_point;    // world coordinates of hit point on untransformed object (used for texture transformations)
	Normal   normal;                // normal at hit point
	RGBColor color;                // used in the Chapter 3 only
	World    world;                    // world reference
	
	public ShadeRec(World w)
	{
		this.color = Constants.BLACK;
		this.world = w;
	}
	
	public ShadeRec(ShadeRec sr)
	{
		this.hit_an_object = sr.hit_an_object;
		this.world_hit_point = new Point3D(sr.world_hit_point);
		this.normal = new Normal(sr.normal);
		this.color = new RGBColor(sr.color);
		this.world = sr.world;
	}
}
