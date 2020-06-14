package com.busatod.graphics._apps.ray_tracer.geometric_objects;

import com.busatod.graphics._apps.ray_tracer.Normal;
import com.busatod.graphics._apps.ray_tracer.Point3D;
import com.busatod.graphics._apps.ray_tracer.Ray;

public class Plane extends GeometricObject
{
	private static final float EPS = 0.001f;
	
	private Point3D a;    // point through which plane passes
	private Normal  n;    // normal to the plane
	
	public Plane(Point3D a, Normal n)
	{
		this.a = a; // save the references...
		this.n = n;
	}
	
	public Plane(Plane p)
	{
		this(new Point3D(p.a), new Normal(p.n)); // copy the fields...
	}
	
	@Override
	public boolean hit(Ray ray, HitPoint hit_point)
	{
		float t = a.sub(ray.o).dot(n) / (ray.d.dot(n));
		
		if (t > EPS) {
			hit_point.tmin = t;
			hit_point.sr.normal = n;
			hit_point.sr.world_hit_point = ray.o.add(ray.d.scale(t));
			return (true);
		}
		
		return false;
	}
}
