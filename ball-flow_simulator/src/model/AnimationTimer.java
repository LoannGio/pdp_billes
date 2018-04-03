package model;

import java.awt.event.ActionListener;

import javax.swing.Timer;

@SuppressWarnings("serial")
public class AnimationTimer extends Timer {

	// Number of milliseconds between 2 events
	private static int STEP = 15; 

	public AnimationTimer(ActionListener actionListener) {
		super(STEP, actionListener);

	}

}
