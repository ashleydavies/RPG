package uk.daviesl.rpg.util.xml;

import uk.daviesl.rpg.util.dialog.DialogNode;
import org.newdawn.slick.Image;

/**
 * Created by Ashley on 24/02/2016.
 */
public class MobData {
    private String name;
    private Image dialogImage;
    private Image image;
    private DialogNode[] dialog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getDialogImage() {
        return dialogImage;
    }

    public void setDialogImage(Image dialogImage) {
        this.dialogImage = dialogImage;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public DialogNode[] getDialog() {
        return dialog;
    }

    public void setDialog(DialogNode[] dialog) {
        this.dialog = dialog;
    }
}
