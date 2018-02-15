package view;

import java.awt.Component;

public interface IRightClickPopUpMenu {

	public void initialize();

	public void parametre();

	public void supprimer();

	public void show(Component eventOrigin, int mouseX, int mouseY);
}
