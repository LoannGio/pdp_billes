package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import controller.Controller;
import model.ObstacleLine;

public class RightClickPopUpLine extends ARightClickPopUp {
	private ObstacleLine _line;
	private JMenuItem _suppr;
	private JMenuItem _param;

	public RightClickPopUpLine(ObstacleLine ol, Controller c, DrawingPanel dp) {
		super(c, dp);
		_line = ol;
		initialize();
	}

	@Override
	public void initialize() {
		_param = new JMenuItem("Parametres");
		_suppr = new JMenuItem("Supprimer");
		add(_param);
		add(_suppr);

		_param.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				parametre();
			}
		});

		_suppr.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				supprimer();
			}
		});
	}

	@Override
	public void parametre() {
		ParamLine pl = new ParamLine(_line, _controller, _drawingPan);

	}

	@Override
	public void supprimer() {
		_controller.removeLine(_line);
		_drawingPan.repaintBufferedImageObstacles(_controller.get_lines());
		_drawingPan.repaint();
	}
}
