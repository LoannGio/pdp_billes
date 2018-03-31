package view;

import java.awt.Component;

/* Minimal services proposed by a right click pop-up on an object*/
public interface IRightClickPopUpMenu {

	public void parameter();

	public void remove();

	public void show(Component eventOrigin, int mouseX, int mouseY);
}
