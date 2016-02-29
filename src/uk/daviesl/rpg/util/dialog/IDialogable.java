package uk.daviesl.rpg.util.dialog;

import org.newdawn.slick.Image;

public interface IDialogable {
    void dialogCloseRequested();

    String getDialogTitle();

    Image getDialogImage();
}
