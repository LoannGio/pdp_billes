package model;

import java.awt.event.ActionListener;

import javax.swing.Timer;

@SuppressWarnings("serial")
public class AnimationTimer extends Timer {

	private static int STEP = 15; // duree de rafraichissement de l'ecran: 1ms

	public AnimationTimer(ActionListener actionListener) {
		super(STEP, actionListener);

	}

}
