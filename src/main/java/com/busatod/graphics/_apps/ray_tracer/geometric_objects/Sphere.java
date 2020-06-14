package com.busatod.graphics._apps.ray_tracer.geometric_objects;

import com.busatod.graphics._apps.ray_tracer.Normal;
import com.busatod.graphics._apps.ray_tracer.Point3D;
import com.busatod.graphics._apps.ray_tracer.Ray;
import com.busatod.graphics._apps.ray_tracer.Vector3D;

public class Sphere extends GeometricObject
{
	private static final float EPS = 0.001f;
	
	private Point3D center;            // center coordinates as a point
	private float   radius;                // the radius
	
	public Sphere()
	{
		this(new Point3D(0), 1); // origin, radius 1
	}
	
	public Sphere(Point3D center, float r)
	{
		this.center = center;
		this.radius = r;
	}
	
	public Point3D getCenter()
	{
		return center;
	}
	
	public void setCenter(Point3D center)
	{
		this.center = center;
	}
	
	public void setCenter(float cx, float cy, float cz)
	{
		this.center.x = cx;
		this.center.y = cy;
		this.center.z = cz;
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public void setRadius(float radius)
	{
		this.radius = radius;
	}
	
	@Override
	public boolean hit(Ray ray, HitPoint hit_point)
	{
		Vector3D temp = ray.o.sub(center);
		float a = ray.d.dot(ray.d);
		float b = 2.0f * temp.dot(ray.d);
		float c = temp.dot(temp) - radius * radius;
		float disc = b * b - 4.0f * a * c;
		
		if (disc < 0.0) {
			return (false);
		} else {
			float e = (float) Math.sqrt(disc);
			float denom = 2.0f * a;
			float t = (-b - e) / denom;    // smaller root
			
			if (t > EPS) {
				hit_point.tmin = t;
				hit_point.sr.normal = new Normal(temp.add(ray.d.scale(t)).scale(1 / radius));
				hit_point.sr.world_hit_point = ray.o.add(ray.d.scale(t));
				return (true);
			}
			
			t = (-b + e) / denom;    // larger root
			
			if (t > EPS) {
				hit_point.tmin = t;
				hit_point.sr.normal = new Normal(temp.add(ray.d.scale(t)).scale(1 / radius));
				hit_point.sr.world_hit_point = ray.o.add(ray.d.scale(t));
				return true;
			}
		}
		
		return false;
	}
}
