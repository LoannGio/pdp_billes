package _defaut;

import controller.Controller;
import model.Circuit;
import view.MainFrame;

public class Main {

	public static void main(String[] args) {
		Circuit c = new Circuit(500, 500);
		Controller cont = new Controller(c);
		MainFrame window = new MainFrame(cont);

	}

}
