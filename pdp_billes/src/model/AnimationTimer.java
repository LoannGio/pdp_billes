package model;

import java.awt.event.ActionListener;

import javax.swing.Timer;

@SuppressWarnings("serial")
public class AnimationTimer extends Timer {

	private static int STEP = 15; // Screen framerate: 1ms

	public AnimationTimer(ActionListener actionListener) {
		super(STEP, actionListener);

	}

}
