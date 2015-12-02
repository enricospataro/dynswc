package main.java.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WordCloudPlayer extends JPanel {

	private static final long serialVersionUID = 7846636107629567511L;
	
	List<JButton> buttons;
	JPanel buttonsPanel;
	JSlider slider;
	LayoutPanel layout;
	Timer playTimer;
	int delayTimer=50;
	int frames;
	int ticks;
	int nextTick;
	int previousTick;
	int maxPanel;
	JButton playButton;
	JButton resetButton;
	JButton nextWordCloudButton;
	JButton previousWordCloudButton;
	ImageIcon playIcon;
	ImageIcon pauseIcon;
	ImageIcon resetIcon;
	ImageIcon nextIcon;
	ImageIcon previousIcon;
	boolean isPlayClicked=false;
	boolean isResetClicked=true;
	boolean isEndReached=false;
	
	public WordCloudPlayer(LayoutPanel layout,int frames){
		this.layout=layout;
		this.buttons=new ArrayList<>();
		maxPanel=layout.panels.size();
		this.frames=frames;
		ticks=(int) Math.ceil(maxPanel/frames);
		nextTick=1;
		previousTick=0;
		
		initPlayer();
	}

	private void initPlayer() {
		playTimer=new Timer(delayTimer,null);
		playTimer.addActionListener(new ActionListener() {		
			
			@Override
			public void actionPerformed(ActionEvent e) {		
				if(layout.currentPanel==maxPanel) {
					playTimer.stop(); 
					initializePlayButton();
					isEndReached=true;
				}				
				layout.play(nextTick);	
				slider.setValue(layout.currentPanel);
			}
		});
		createLayout();		
	}

	private void createLayout() {
		buttonsPanel=new JPanel();	
		buttonsPanel.setLayout(new FlowLayout());		

		playButton = new JButton();
		playIcon=null;
		try {
			playIcon = new ImageIcon("src/main/resources/img/play.png");
			playButton.setIcon(playIcon);
		} catch(Exception e) {e.printStackTrace();}
		
		pauseIcon=null;
		try {
			pauseIcon = new ImageIcon("src/main/resources/img/pause.png");
		} catch(Exception e) {e.printStackTrace();}
		
		playButton.addActionListener(new ActionListener() {	
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isEndReached) {
					initializeSlider();
					layout.reset();
					isEndReached=false;
				}					
				checkPlayButton();
				if(isPlayClicked) playTimer.start();
				else playTimer.stop();
			}
		});
		
		buttons.add(playButton);
		buttonsPanel.add(playButton);
		
		resetButton = new JButton();
		resetIcon=null;
		try {
			resetIcon = new ImageIcon("src/main/resources/img/reset.png");
			resetButton.setIcon(resetIcon);
		} catch(Exception e) {e.printStackTrace();}
		
		resetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				playTimer.stop();
				layout.reset();
				initializePlayButton();
				initializeSlider();
			}
		});
		buttons.add(resetButton);
		buttonsPanel.add(resetButton);
		
		previousWordCloudButton=new JButton();
		previousIcon=null;
		try {
			previousIcon = new ImageIcon("src/main/resources/img/previous.png");
			previousWordCloudButton.setIcon(previousIcon);
		} catch(Exception e) {e.printStackTrace();}
		
		previousWordCloudButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				slider.setValue((frames+1)*previousTick);	
//				if(isPlayClicked) {
//					playTimer.stop();
//					playButton.setIcon(playIcon);
//				}
			}
		});
		previousWordCloudButton.setEnabled(false);
		buttons.add(previousWordCloudButton);
		buttonsPanel.add(previousWordCloudButton);
		
		nextWordCloudButton=new JButton();
		nextIcon=null;
		try {
			nextIcon = new ImageIcon("src/main/resources/img/next.png");
			nextWordCloudButton.setIcon(nextIcon);
		} catch(Exception e) {e.printStackTrace();}
		
		nextWordCloudButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				slider.setValue((frames+1)*(nextTick));
//				if(isPlayClicked) {
//				playTimer.stop();
//				playButton.setIcon(playIcon);
//			}
			}
		});
		buttons.add(nextWordCloudButton);
		buttonsPanel.add(nextWordCloudButton);
		
		slider=new JSlider(0,maxPanel,0);
		slider.setMajorTickSpacing(frames+1);
		slider.setPaintTicks(true);
		slider.addChangeListener(new ChangeListener() {
	
			@Override
			public void stateChanged(ChangeEvent e) {
				if(slider.getValue()==maxPanel) isEndReached=true;
				
				JSlider source = (JSlider)e.getSource();
				int fps=(int)source.getValue(); 
				updateTicks(fps);
				
				layout.slide(fps);
			}
		});
		
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));		
		
		for(JButton b:buttons) setTransparency(b);
		
		add(slider);
		add(buttonsPanel);		
	}

	private void setTransparency(JButton button) {
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setOpaque(false);
	}

	private void checkPlayButton() {
		isPlayClicked = !isPlayClicked;	
		if(isPlayClicked) playButton.setIcon(pauseIcon);
		else playButton.setIcon(playIcon);
	}

	private void initializeSlider() {
		previousTick=0;
		nextTick=1;
		slider.setValue(0);
	}

	private void initializePlayButton() {
		previousTick=0;
		nextTick=1;
		isPlayClicked=false;
		playButton.setIcon(playIcon);
	}

	private void updateTicks(int fps) {
		for(int i=0;i<=ticks;i++) {
			if(fps==i*(frames+1)) {
				previousTick=i-1;
				nextTick=i+1;
			}
			if(fps>i*(frames+1) && fps<(i+1)*(frames+1)) {
				previousTick=i;
				nextTick=i+1;
			}
		}
			if(fps==0) {
				previousTick=0;
				previousWordCloudButton.setEnabled(false);
			}
			else previousWordCloudButton.setEnabled(true);
			
			if(fps==maxPanel) {
				previousTick=ticks-1;
				nextTick=ticks;
				nextWordCloudButton.setEnabled(false);
			}
			else nextWordCloudButton.setEnabled(true);
	}
}