package com.busatod.graphics._apps.ray_tracer.utilities;

public class ViewPlane
{
	int   hres;                                       // horizontal image resolution
	int   vres;                                       // vertical image resolution
	float s;                                          // pixel size
	private float gamma;                              // gamma correction factor
	private float inv_gamma;                          // the inverse of the gamma correction factor
	
	public ViewPlane()
	{
		this.hres = 400;
		this.vres = 400;
		this.s = 1;
		this.gamma = 1.0f;
		this.inv_gamma = 1.0f / this.gamma;
		// ...
	}
	
	public ViewPlane(ViewPlane vp)
	{
		this.hres = vp.hres;
		this.vres = vp.vres;
		this.s = vp.s;
		this.gamma = vp.gamma;
		this.inv_gamma = vp.inv_gamma;
	}
	
	public float getGamma()
	{
		return gamma;
	}
	
	public void setGamma(float gamma)
	{
		this.gamma = gamma;
		this.inv_gamma = 1.0f / gamma;
	}
	
	public float getInv_gamma()
	{
		return inv_gamma;
	}
}
