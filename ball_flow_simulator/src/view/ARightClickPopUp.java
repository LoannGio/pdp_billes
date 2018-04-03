package view;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import controller.Controller;

@SuppressWarnings("serial")
public abstract class ARightClickPopUp extends JPopupMenu implements IRightClickPopUpMenu {
	protected Controller _controller;
	protected DrawingPanel _drawingPan;
	private JMenuItem _suppr;
	private JMenuItem _param;

	public ARightClickPopUp(Controller c, DrawingPanel dp) {
		_controller = c;
		_drawingPan = dp;
	}
	
	protected void initialize() {
		_param = new JMenuItem("Parametres");
		_suppr = new JMenuItem("Supprimer");
		add(_param);
		add(_suppr);

		_param.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				parameter();
			}
		});

		_suppr.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				remove();
			}
		});

	}

	@Override
	public void show(Component eventOrigin, int mouseX, int mouseY) {
		super.show(eventOrigin, mouseX, mouseY);

	}
}
