package main;

import javax.swing.JFrame;

public class GardenApp extends JFrame{
	private static final long serialVersionUID = 1L;

	public GardenApp(String title) {
		super(title);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		this.setLocation(0, 0);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GardenPanel bpnl = new GardenPanel(this);
		this.add(bpnl);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public static void main (String[] args){
		new GardenApp("Garden Simulator");
		
	}
}