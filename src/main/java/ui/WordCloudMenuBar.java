package main.java.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

public class WordCloudMenuBar extends JMenuBar {
	
	private static final long serialVersionUID = -1560479926196485038L;
	
	
	public WordCloudMenuBar(List<WordCloudBarChart> charts) {
		initFileMenu(charts);
	}

	private void initFileMenu(List<WordCloudBarChart> charts) {
		JMenu file = new JMenu();
		file.setText("File");
		JMenu edit = new JMenu();
		edit.setText("Edit");
		
		//an item that opens a window to choose the settings of the wordcloud before creating it
		JMenuItem createItem = new JMenuItem("Create WordCloud");
		createItem.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent event){
		    	CreateWordCloudDialog dial = new CreateWordCloudDialog();
		    	
		    }});
		
		//the exit item
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			
		    @Override
		    public void actionPerformed(ActionEvent event) {System.exit(0);}
		    });
		
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		
		file.add(createItem);
		file.add(exitItem);
		add(file);
		
		// show statistics item
		JCheckBoxMenuItem statsItem = new JCheckBoxMenuItem("Show statistics",false);
		JDialog fr = new JDialog();
		statsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(statsItem.isSelected()) {					
					fr.setTitle("Top words");
					fr.add(LayoutPanel.wordCloudChartPanel);
					fr.setSize(400,200);
					fr.setLocation(100,650);
					fr.setAlwaysOnTop(true);
					fr.setVisible(true);
				}
				if(!statsItem.isSelected()) fr.setVisible(false);
			}
		});
		fr.addWindowListener(new DialogWindow(statsItem));
		edit.add(statsItem);
		add(edit);
		
	}
	
	private class DialogWindow extends WindowAdapter {
		
		private JCheckBoxMenuItem item;
		
		public DialogWindow(JCheckBoxMenuItem item) {this.item=item;}
		@Override
		public void windowClosing(WindowEvent e) {
			item.setSelected(false); 				
		}
	}
}
