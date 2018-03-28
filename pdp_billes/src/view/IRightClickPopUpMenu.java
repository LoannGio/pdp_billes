package view;

import java.awt.Component;

public interface IRightClickPopUpMenu {
	/* Services minimums rendus par un popUp de clic droit sur objet */

	public void initialize();

	public void parametre();

	public void supprimer();

	public void show(Component eventOrigin, int mouseX, int mouseY);
}
