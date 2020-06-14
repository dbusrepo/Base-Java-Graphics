package com.busatod.graphics._apps.ray_tracer.worlds;

import com.busatod.graphics._apps.ray_tracer.geometric_objects.GeometricObject;
import com.busatod.graphics._apps.ray_tracer.tracers.Tracer;
import com.busatod.graphics._apps.ray_tracer.utilities.*;

import java.util.List;

public class World
{
	private ViewPlane             view_plane;
	private RGBColor              background_color;
	private Tracer                tracer;
	private List<GeometricObject> objects;
	
	public ViewPlane getView_plane()
	{
		return view_plane;
	}
	
	public void setView_plane(ViewPlane view_plane)
	{
		this.view_plane = view_plane;
	}
	
	public RGBColor getBackground_color()
	{
		return background_color;
	}
	
	public void setBackground_color(RGBColor background_color)
	{
		this.background_color = background_color;
	}
	
	public void addObject(GeometricObject object)
	{
		objects.add(object);
	}
	
	public ShadeRec hit_objects(Ray ray, Float tmin_) // tmin_ = kHugeValue
	{
		ShadeRec sr = new ShadeRec(this);
		GeometricObject.HitPoint hit_point = new GeometricObject.HitPoint();
		
		float tmin = tmin_ != null ? tmin_ : Constants.INF;
		
		for (GeometricObject obj : objects) {
			if (obj.hit(ray, hit_point) && (hit_point.tmin < tmin)) {
				sr.hit_an_object = true;
				tmin = hit_point.tmin;
				sr.color = obj.getColor();
			}
		}
		
		return sr;
	}
	
	public void build()
	{
	
	}
	
	public void render_scene()
	{
	}
}
