package model;

import java.awt.event.ActionListener;

import javax.swing.Timer;

public class AnimationTimer extends Timer {

	// Quelques constantes
	static int STEP = 15; // duree de rafraichissement de l'ecran: 1ms
	public static double MSSTEP = STEP / 2500.0;

	public static double getMSSTEP() {
		return MSSTEP;
	}

	public static void setMSSTEP(double mSSTEP) {
		MSSTEP = mSSTEP;
	}

	public AnimationTimer(ActionListener actionListener){
		super(STEP, actionListener);

	}

}
