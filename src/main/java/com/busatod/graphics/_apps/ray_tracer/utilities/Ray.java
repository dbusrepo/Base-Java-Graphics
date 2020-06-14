package com.busatod.graphics._apps.ray_tracer.utilities;

public class Ray
{
	public Point3D  o; // origin
	public Vector3D d; // direction
	
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
