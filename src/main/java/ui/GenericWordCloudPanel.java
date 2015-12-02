package main.java.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;

public abstract class GenericWordCloudPanel<T> extends JPanel{
	
	private static final long serialVersionUID = -4730360446518846123L;
	
	List<T> cards;
	CardLayout cardLayout;
	int currentPanel;
	int frames;
	
	public GenericWordCloudPanel(List<T> cards,int frames) {
		this.cards=cards;
		this.frames=frames;
		initUI();
	}
	
	private void initUI() {
		setBackground(Color.WHITE);
		setLayout(new CardLayout());
		cardLayout=(CardLayout)getLayout();
		
		JPanel initialPanel = new JPanel();
		initialPanel.setBackground(Color.white);
		add(initialPanel,""+0);		
		addCards();
	}
	
	protected abstract void addCards();
	
	public void showCard(int panel) {
		cardLayout.show(this,""+panel);
	}
	
	public void play(){	
		showCard(currentPanel);		
		currentPanel++;
	}

	public void reset() {
		cardLayout.first(this);
		currentPanel=0;
	}
	
	public void slide(int panel) {
		showCard(panel);		
		currentPanel=panel;
	}
}
