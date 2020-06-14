package com.busatod.graphics._apps.ray_tracer.utilities;

public class Normal
{
	float x, y, z;
	
	public Normal()
	{
		this(0, 0, 0);
	}
	
	public Normal(float a)
	{
		this(a, a, a);
	}
	
	public Normal(float nx, float ny, float nz)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Normal(Normal n)
	{
		this(n.x, n.y, n.z);
	}
	
	public Normal(Vector3D v)
	{
		this(v.x, v.y, v.z);
	}
	
	public Normal negate()
	{
		return new Normal(this.x, this.y, this.z);
	}
	
	public Normal scale(float a)
	{
		return new Normal(a * this.x, a * this.y, a * this.z);
	}
	
	public Normal add(Normal n)
	{
		return new Normal(this.x + n.x, this.y + n.y, this.z + n.z);
	}
	
	public float dot(Vector3D v)
	{
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
	public void normalize()
	{
		float len = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		assert len >= Constants.EPS;
		this.x /= len;
		this.y /= len;
		this.z /= len;
	}
}
