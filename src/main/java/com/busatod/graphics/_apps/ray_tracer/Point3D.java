package com.busatod.graphics._apps.ray_tracer;

public class Point3D
{
	float x, y, z;
	
	public Point3D()
	{
		this(0, 0, 0);
	}
	
	public Point3D(float a)
	{
		this(a, a, a);
	}
	
	public Point3D(float px, float py, float pz)
	{
		this.x = px;
		this.y = py;
		this.z = pz;
	}
	
	public Point3D(Point3D p)
	{
		this(p.x, p.y, p.z);
	}
	
	public Point3D negate()
	{
		return new Point3D(-this.x, -this.y, -this.z);
	}
	
	public Point3D scale(float a)
	{
		return new Point3D(a * this.x, a * this.y, a * this.z);
	}
	
	public Point3D add(Vector3D v)
	{
		return new Point3D(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	
	public Point3D sub(Vector3D v)
	{
		return new Point3D(this.x - v.x, this.y - v.y, this.z - v.z);
	}
	
	//	the vector that joins point p to this
	public Vector3D joining_vec(Point3D p)
	{
		return new Vector3D(this.x - p.x, this.y - p.y, this.z - p.z);
	}
	
	public float len_squared(Point3D p)
	{
		return (this.x - p.x) * (this.x - p.x)
				+ (this.y - p.y) * (this.y - p.y)
				+ (this.z - p.z) * (this.z - p.z);
	}
	
	public float distance(Point3D p)
	{
		return (float) Math.sqrt(len_squared(p));
	}
}
