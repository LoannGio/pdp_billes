package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class InclinationPreview extends JPanel {
	private double _angle;

	public InclinationPreview(double angle) {
		super();
		_angle = angle;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double radianAngle = _angle * Math.PI / 180;
		double startX = 5;
		double startY = 20;
		double endX = startX + 100 * Math.cos(radianAngle);
		double endY = startY + 100 * Math.sin(radianAngle);
		Line2D.Double indicator = new Line2D.Double(startX, startY, endX, endY);
		g2.draw(indicator);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 100); // As suggested by camickr
	}

	public void set_angle(double a) {
		_angle = a;
	}
}
