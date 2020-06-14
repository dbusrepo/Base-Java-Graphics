package com.busatod.graphics._apps.ray_tracer.utilities;

public class Vector3D
{
	float x, y, z;
	
	public Vector3D()
	{
		this(0, 0, 0);
	}
	
	public Vector3D(float a)
	{
		this(a, a, a);
	}
	
	public Vector3D(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3D(Vector3D v)
	{
		this(v.x, v.y, v.z);
	}
	
	public Vector3D(Normal n)
	{
		this(n.x, n.y, n.z);
	}
	
	public Vector3D(Point3D p)
	{
		this(p.x, p.y, p.z);
	}
	
	public Vector3D negate()
	{
		return new Vector3D(-this.x, -this.y, -this.z);
	}
	
	public Vector3D scale(float a)
	{
		return new Vector3D(a * this.x, a * this.y, a * this.z);
	}
	
	public Vector3D add(Vector3D v)
	{
		return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	
	public void cadd(Vector3D v)
	{
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	
	public Vector3D sub(Vector3D v)
	{
		return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
	}
	
	public Vector3D sub(Normal n)
	{
		return new Vector3D(this.x - n.x, this.y - n.y, this.z - n.z);
	}
	
	public float dot(Vector3D v)
	{
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
	public float dot(Normal n)
	{
		return this.x * n.x + this.y * n.y + this.z * n.z;
	}
	
	public Vector3D cross(Vector3D v)
	{
		return new Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
	}
	
	public float length()
	{
		return (float) Math.sqrt(this.dot(this));
	}
	
	public void normalize()
	{
		float len = this.length();
		assert (len >= Constants.EPS);
		this.x /= len;
		this.y /= len;
		this.z /= len;
	}
}
