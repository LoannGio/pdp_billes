package test.view;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import view.DrawingPanel;

public class DrawingPanelTest {
	
	private DrawingPanel _dp;
	private BufferedImage _bi;

	@Before
	public void setUp() throws Exception {
		_dp = new DrawingPanel(new Dimension(500, 500), new JFrame());
	}

	@After
	public void tearDown() throws Exception {
		_dp = null;
	}
	
	@Test
	public void testMySetBounds() {
		_dp.mySetBounds(5, 5, 200, 200);
		Field bi;
		
		//Testing if buffered image is well-resized
		try{
			bi = _dp.getClass().getDeclaredField("_buffer");
			bi.setAccessible(true);
			_bi = (BufferedImage) bi.get(_dp);
			assertEquals(_bi.getWidth(), 200, 0);
			assertEquals(_bi.getHeight(), 200, 0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//Testing if drawing panel is well-resized
		assertEquals(_dp.getX(), 5, 0);
		assertEquals(_dp.getY(), 5, 0);
		assertEquals(_dp.getWidth(), 200, 0);
		assertEquals(_dp.getHeight(), 200, 0);
	}

}
