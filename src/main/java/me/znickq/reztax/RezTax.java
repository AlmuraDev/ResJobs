/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.znickq.reztax;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.Keyboard;

/**
 *
 * @author ZNickq
 */
public class RezTax extends JavaPlugin implements Listener, BindingExecutionDelegate {

	private Economy economy;

	@Override
	public void onDisable() {
		RezData.saveData(getDataFolder());
	}

	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		RezData.loadData(getDataFolder());
		setupEconomy();
		getServer().getPluginManager().registerEvents(this, this);
		SpoutManager.getFileManager().addToPreLoginCache(this, "https://dl.dropbox.com/u/62529831/Dark.png");
		SpoutManager.getFileManager().addToPreLoginCache(this, "https://dl.dropbox.com/u/62529831/Light.png");
		SpoutManager.getKeyBindingManager().registerBinding("Tax Panel", Keyboard.KEY_I, "Opens the tax panel", this, this);
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public void keyPressed(KeyBindingEvent event) {
		ClaimedResidence cr = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
		if (!RezData.isResidence(event.getPlayer().getLocation())) {
			return;
		}
		if (event.getScreenType() != ScreenType.GAME_SCREEN) {
			return;
		}
		event.getPlayer().getMainScreen().attachPopupScreen(new RezTaxScreen(this, event.getPlayer(), cr));
	}

	public void keyReleased(KeyBindingEvent event) {
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!RezData.isResidence(event.getBlock().getLocation())) {
			return;
		}
		ClaimedResidence cr = Residence.getResidenceManager().getByLoc(event.getBlock().getLocation());
		String rowner = cr.getOwner();
		RezData rd = RezData.getRezData(cr);
		if (!(rd.handlesPrice(event.getBlock().getType(), false))) {
			return;
		}
		if (!(rd.isAllowed(event.getPlayer().getName())) && !rd.allowEveryone()) {
			return;
		}
		Integer p = rd.getPrice(event.getBlock().getType(), false);
		EconomyResponse er;
		if (p >= 0) {
			er = economy.depositPlayer(event.getPlayer().getName(), p);
			if(er.transactionSuccess()) {
				economy.withdrawPlayer(rowner, p);
			}
			
		} else {
			er = economy.withdrawPlayer(event.getPlayer().getName(), p * -1);
			if(er.transactionSuccess()) {
				economy.depositPlayer(rowner, p * -1);
			}
		}
		if (er.transactionSuccess()) {
			event.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!RezData.isResidence(event.getBlock().getLocation())) {
			return;
		}
		ClaimedResidence cr = Residence.getResidenceManager().getByLoc(event.getBlock().getLocation());
		String rowner = cr.getOwner();
		RezData rd = RezData.getRezData(cr);
		if (!(rd.handlesPrice(event.getBlock().getType(), true))) {
			return;
		}
		if (!(rd.isAllowed(event.getPlayer().getName())) && !rd.allowEveryone()) {
			return;
		}
		Integer p = rd.getPrice(event.getBlock().getType(), true);
		EconomyResponse er;
		if (p >= 0) {
			er = economy.depositPlayer(event.getPlayer().getName(), p);
			if(er.transactionSuccess()) {
				economy.withdrawPlayer(rowner, p);
			}
			
		} else {
			er = economy.withdrawPlayer(event.getPlayer().getName(), p * -1);
			if(er.transactionSuccess()) {
				economy.depositPlayer(rowner, p * -1);
			}
		}
		if (er.transactionSuccess()) {
			event.setCancelled(false);
		}
	}
}
