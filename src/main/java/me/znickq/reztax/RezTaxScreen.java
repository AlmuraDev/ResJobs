/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.znickq.reztax;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericCheckBox;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;

/**
 *
 * @author ZNickq
 */
class RezTaxScreen extends GenericPopup {

	private RezTax pplugin;
	private RezData rd;
	private static List<Material> toPick = Arrays.asList(Material.DIRT, Material.STONE, Material.SAND, Material.GRAVEL, Material.WOOD, Material.SAND);

	private GenericCheckBox allowE;
	private GenericLabel upl;
	private GenericTextField allowF;
	private List<CheckPack> got = new ArrayList<CheckPack>();
	
	public RezTaxScreen(RezTax aThis, Player who, ClaimedResidence res) {
		pplugin = aThis;

		rd = RezData.getRezData(res);

		//First, the layout
		GenericTexture darkT = new GenericTexture("https://dl.dropbox.com/u/62529831/Dark.png");
		darkT.shiftXPos(30).shiftYPos(20).setWidth(380).setHeight(200);
		darkT.setPriority(RenderPriority.Highest);

		GenericTexture fLightT = new GenericTexture("https://dl.dropbox.com/u/62529831/Light.png");
		fLightT.shiftXPos(darkT.getX() + 10).shiftYPos(darkT.getY() + 30).setWidth(170).setHeight(140);
		fLightT.setPriority(RenderPriority.High);

		GenericTexture sLightT = new GenericTexture("https://dl.dropbox.com/u/62529831/Light.png");
		sLightT.shiftXPos(fLightT.getX() + fLightT.getWidth() + 10).shiftYPos(darkT.getY() + 30).setWidth(170).setHeight(140);
		sLightT.setPriority(RenderPriority.High);

		//Now, text fields and check boxes
		allowE = new GenericCheckBox("Allow Everyone");
		allowE.setChecked(rd.allowEveryone());
		allowE.setHeight(11).setWidth(GenericLabel.getStringWidth("Allow Everyone") + 15);
		allowE.shiftXPos(fLightT.getX() + 10).shiftYPos(fLightT.getY() + 10);
		allowF = new GenericTextField();
		allowF.setText(rd.getAllowed());
		allowF.shiftXPos(allowE.getX()).shiftYPos(allowE.getY() + 40).setWidth(150).setHeight(75);

		//Finally, text
		GenericLabel pname = new GenericLabel("Job Site");
		pname.shiftXPos(darkT.getX() + 145).shiftYPos(darkT.getY() + 5);
		pname.setScale(2.0f).setHeight(GenericLabel.getStringHeight("Job Site", 2.0f)).setWidth(50);

		GenericLabel at = new GenericLabel("Allow:");
		at.shiftXPos(allowF.getX()).shiftYPos(allowF.getY() - 14).setWidth(GenericLabel.getStringWidth(at.getText())).setHeight(11);

		upl = new GenericLabel("Updated at "+rd.getLastUpdate());
		upl.setHeight(11).setWidth(GenericLabel.getStringWidth("Updated at x"));
		upl.shiftXPos(fLightT.getX()).shiftYPos(fLightT.getY() + fLightT.getHeight() + 10);

		GenericButton rem = new WorkButton("Remove", this);
		rem.setHeight(15).setWidth(GenericLabel.getStringWidth("Remove") + 8);
		rem.shiftXPos(sLightT.getX()).shiftYPos(upl.getY());

		GenericButton upb = new WorkButton("Update", this);
		upb.setHeight(15).setWidth(GenericLabel.getStringWidth("Update") + 8);
		upb.shiftXPos(sLightT.getX() + rem.getWidth() + 10).shiftYPos(upl.getY());

		GenericButton close = new WorkButton("Close", this);
		close.setHeight(15).setWidth(GenericLabel.getStringWidth("Close") + 8);
		close.shiftXPos(upb.getX() + upb.getWidth() + 10).shiftYPos(upl.getY());

		int xPos = sLightT.getX() + 10, yPos = sLightT.getY() + 15;

		GenericLabel brkl = new GenericLabel("Break");
		GenericLabel plcl = new GenericLabel("Place");
		brkl.setHeight(11).setWidth(GenericLabel.getStringWidth("Break")).shiftXPos(xPos + 45).shiftYPos(yPos - 10);
		plcl.setHeight(11).setWidth(GenericLabel.getStringWidth("Place")).shiftXPos(xPos + 90).shiftYPos(yPos - 10);

		for (Material mat : toPick) {
			GenericItemWidget gw = new GenericItemWidget();
			gw.setHeight(15).setWidth(15);
			gw.setTypeId(mat.getId());
			gw.shiftXPos(xPos).shiftYPos(yPos);

			GenericCheckBox gcb = new GenericCheckBox();
			gcb.setChecked(rd.handlesPrice(mat, false));
			gcb.setText("");
			gcb.setHeight(15).setWidth(15);
			gcb.shiftXPos(xPos + 20).shiftYPos(yPos);

			GenericTextField brkf = new GenericTextField();
			Integer pr = rd.getPrice(mat, false);
			if (pr == null) {
				brkf.setText("");
			} else {
				brkf.setText("" + pr);
			}
			brkf.setHeight(15).setWidth(40);
			brkf.shiftXPos(xPos + 45).shiftYPos(yPos);

			GenericTextField plcf = new GenericTextField();
			pr = rd.getPrice(mat, true);
			if (pr == null) {
				plcf.setText("");
			} else {
				plcf.setText("" + pr);
			}
			plcf.setHeight(15).setWidth(40);
			plcf.shiftXPos(xPos + 90).shiftYPos(yPos);

			attachWidgets(pplugin, gw, gcb, brkf, plcf);
			yPos += 20;
			CheckPack cp = new CheckPack(gw, gcb, brkf, plcf);
			got.add(cp);
		}



		attachWidgets(pplugin, darkT, fLightT, sLightT);
		attachWidgets(pplugin, allowE, allowF);
		attachWidgets(pplugin, pname, at, upl);
		attachWidgets(pplugin, brkl, plcl);

		if (res.getOwner().equals(who.getName())) {
			attachWidgets(pplugin, rem, upb, close);
		}

	}

	private void onButtonClick(ButtonClickEvent event) {
		if(event.getButton().getText().equals("Update")) {
			rd.setAllowEveryone(allowE.isChecked());
			rd.parseAllowed(allowF.getText());
			for(CheckPack cc : got) {
				if(!cc.gcb.isChecked()) {
					rd.setPrice(Material.getMaterial(cc.giw.getTypeId()), null, false);
					rd.setPrice(Material.getMaterial(cc.giw.getTypeId()), null, true);
				} else {
					try {
					rd.setPrice(Material.getMaterial(cc.giw.getTypeId()), Integer.parseInt(cc.b.getText()), false);
					rd.setPrice(Material.getMaterial(cc.giw.getTypeId()), Integer.parseInt(cc.p.getText()), true);
					} catch(Exception ex) {
						cc.b.setText("Invalid");
						cc.p.setText("Invalid");
						cc.b.setDirty(true);
						cc.p.setDirty(true);
						return;
					}
					}
			}
			rd.setLastUpdate();
			upl.setText("Updated at "+rd.getLastUpdate());
		}
		if(event.getButton().getText().equals("Close")) {
			getPlayer().getMainScreen().closePopup();
		}
		if(event.getButton().getText().equals("Remove")) {
			for(CheckPack cc : got) {
				cc.gcb.setChecked(false);
				cc.b.setText("");
				cc.p.setText("");
			}
		}
	}

	private static class CheckPack {
		private GenericItemWidget giw;
		private GenericCheckBox gcb;
		private GenericTextField b;
		private GenericTextField p;

		public CheckPack(GenericItemWidget giw, GenericCheckBox gcb, GenericTextField b, GenericTextField p) {
			this.giw = giw;
			this.gcb = gcb;
			this.b = b;
			this.p = p;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 97 * hash + (this.giw != null ? this.giw.hashCode() : 0);
			hash = 97 * hash + (this.gcb != null ? this.gcb.hashCode() : 0);
			hash = 97 * hash + (this.b != null ? this.b.hashCode() : 0);
			hash = 97 * hash + (this.p != null ? this.p.hashCode() : 0);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final CheckPack other = (CheckPack) obj;
			if (this.giw != other.giw && (this.giw == null || !this.giw.equals(other.giw))) {
				return false;
			}
			if (this.gcb != other.gcb && (this.gcb == null || !this.gcb.equals(other.gcb))) {
				return false;
			}
			if (this.b != other.b && (this.b == null || !this.b.equals(other.b))) {
				return false;
			}
			if (this.p != other.p && (this.p == null || !this.p.equals(other.p))) {
				return false;
			}
			return true;
		}
	}

	private class WorkButton extends GenericButton {
		private RezTaxScreen run;
		
		public WorkButton(String name, RezTaxScreen sc) {
			super(name);
			run = sc;
		}

		@Override
		public void onButtonClick(ButtonClickEvent event) {
			run.onButtonClick(event);
		}
	}
}
