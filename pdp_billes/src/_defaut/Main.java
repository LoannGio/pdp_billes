package _defaut;

import controller.Controller;
import view.MainFrame;

public class Main {

	public static void main(String[] args) {
		// Initialiser l'instance du controller qui instancie le model
		Controller.getInstance();
		MainFrame window = new MainFrame();

	}
}
