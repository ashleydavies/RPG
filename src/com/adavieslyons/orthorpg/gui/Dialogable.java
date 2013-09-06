package com.adavieslyons.orthorpg.gui;

import org.newdawn.slick.Image;

public interface Dialogable {
	void dialogCloseRequested();
	public String getDialogTitle();
	public Image getDialogImage();
}
