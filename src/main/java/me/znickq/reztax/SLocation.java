/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.znickq.reztax;

import java.io.Serializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author ZNickq
 */
class SLocation implements Serializable{
	private int x,y,z;
	private String world;
	
	public SLocation(Location loc) {
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		world = loc.getWorld().getName();
	}
	
	public Location getLocation() {
		return Bukkit.getWorld(world).getBlockAt(x, y, z).getLocation();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SLocation)) {
			return false;
		}
		SLocation sll = (SLocation) o;
		return x == sll.x && y == sll.y && z == sll.z && world.equals(sll.world);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + this.x;
		hash = 97 * hash + this.y;
		hash = 97 * hash + this.z;
		hash = 97 * hash + (this.world != null ? this.world.hashCode() : 0);
		return hash;
	}
	
}



