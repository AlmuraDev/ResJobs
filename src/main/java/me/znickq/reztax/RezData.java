/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.znickq.reztax;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author ZNickq
 */
public class RezData implements Serializable{

	private static Map<String, RezData> saved = new HashMap<String, RezData>();
	
	static void saveData(File dataFolder) {
		File ff = new File(dataFolder, "rezData.dat");
		try {
			if(ff.exists()) {
				ff.delete();
			}
			ff.createNewFile();
			FileOutputStream fos = new FileOutputStream(ff);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(saved);
			oos.flush();
			oos.close();
		} catch(Exception ex) {
			Logger.getLogger(RezData.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	static void loadData(File dataFolder) {
		File ff = new File(dataFolder, "rezData.dat");
		if(!ff.exists()) {
			return;
		}
		try {
			FileInputStream fis = new FileInputStream(ff);
			ObjectInputStream ois = new ObjectInputStream(fis);
			saved = (Map<String, RezData>) ois.readObject();
		} catch (Exception ex) {
			Logger.getLogger(RezData.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private Map<Material, Integer> bprices = new EnumMap<Material, Integer>(Material.class);
	private Map<Material, Integer> pprices = new EnumMap<Material, Integer>(Material.class);
	private List<String> allowed = new ArrayList<String>();
	private boolean allowEveryone = false;
	private String lastUpdate = "Now";

	public void setPrice(Material material, Integer val, boolean place) {
		//System.out.println("Setting price: "+material.name()+" "+val+" "+place);
		if(place) {
			if(val != null) {
				pprices.put(material, val);
			} else {
				pprices.remove(material);
			}
		} else {
			if(val != null) {
				bprices.put(material, val);
			} else {
				bprices.remove(material);
			}
		}
	}

	public boolean allowEveryone() {
		return allowEveryone;
	}

	public void setAllowEveryone(boolean nAE) {
		allowEveryone = nAE;
		//System.out.println("Allow everyone: "+nAE);
	}

	public boolean handlesPrice(Material mat, boolean place) {
		if (place) {
			return pprices.containsKey(mat);
		}
		return bprices.containsKey(mat);
	}

	public Integer getPrice(Material mat, boolean place) {
		if (place) {
			return pprices.get(mat);
		} else {
			return bprices.get(mat);
		}
	}

	public String getLastUpdate() {
		if (lastUpdate.equals("Now")) {
			setLastUpdate();
		}
		return lastUpdate;
	}

	public void setLastUpdate() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM");
		Date date = new Date();
		lastUpdate = dateFormat.format(date);
	}

	public String getAllowed() {
		String toRet = "";
		for (String ss : allowed) {
			toRet += ss + ",";
		}
		if (allowed.size() >= 1) {
			toRet = toRet.substring(0, toRet.length() - 1);
		}
		return toRet;
	}

	public boolean isAllowed(String who) {
		return allowed.contains(who);
	}

	public void parseAllowed(String unparsed) {
		//System.out.println("Allowed: ");
		allowed.clear();
		String[] spl = unparsed.split(",");
		for(int i=0;i<spl.length;i++) {
			spl[i] = spl[i].trim();
			//System.out.println(spl[i]);
		}
		allowed.addAll(Arrays.asList(spl));
	}

	public static RezData getRezData(ClaimedResidence cr) {
		if (saved.containsKey(cr.getName())) {
			return saved.get(cr.getName());
		}
		RezData rd = new RezData();
		saved.put(cr.getName(), rd);
		return saved.get(cr.getName());

	}

	public static boolean isResidence(Location loc) {
		return Residence.getResidenceManager().getByLoc(loc) != null;
	}
}
