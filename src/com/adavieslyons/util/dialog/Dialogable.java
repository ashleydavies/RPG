package com.adavieslyons.util.dialog;

import org.newdawn.slick.Image;

public interface Dialogable {
	void dialogCloseRequested();
	
	public String getDialogTitle();
	
	public Image getDialogImage();
}
