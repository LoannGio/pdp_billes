package view;

import java.awt.Component;

import javax.swing.JPopupMenu;

import controller.Controller;

public abstract class ARightClickPopUp extends JPopupMenu implements IRightClickPopUpMenu {
	protected Controller _controller;
	protected DrawingPanel _drawingPan;

	public ARightClickPopUp(Controller c, DrawingPanel dp) {
		_controller = c;
		_drawingPan = dp;
	}

	@Override
	public void show(Component eventOrigin, int mouseX, int mouseY) {
		super.show(eventOrigin, mouseX, mouseY);

	}
}
