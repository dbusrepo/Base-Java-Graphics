package com.busatod.graphics._apps.ray_tracer;

public class Ray
{
	Point3D  o; // origin
	Vector3D d; // direction
	
	public Ray()
	{
		this.o = new Point3D(0);
		this.d = new Vector3D(0, 0, 1);
	}
	
	public Ray(Ray ray)
	{
		this.o = new Point3D(ray.o);
		this.d = new Vector3D(ray.d);
	}
}
