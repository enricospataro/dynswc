package main.java.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import main.java.utils.TextUtils;

public class CreateWordCloudDialog extends JDialog {
	
	private static final long serialVersionUID = -8231221974599125764L;
	
	public CreateWordCloudDialog() {
		initDialog();
	}

	private void initDialog() {
		setTitle("Create a wordcloud");
		setSize(700,800);
		setLocationRelativeTo(getParent());
		Container cont = getContentPane();
		
		JPanel fileAudioPanel = new JPanel();
		JButton openButton = new JButton("Open");
		fileAudioPanel.add(new JLabel("1. Select an audio input file"),BoxLayout.Y_AXIS);
		fileAudioPanel.add(openButton);
		openButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new InputFileFilter());
				int val=fc.showOpenDialog(getContentPane());
				 if(val == JFileChooser.APPROVE_OPTION) {
					 File file = fc.getSelectedFile();
					 fileAudioPanel.add(new JLabel(file.getName()));
				}
			}
		});
		
		JPanel optionsPanel = new JPanel();
		
		String[] numberOfWords = {"50","100","150"};
		optionsPanel.add(new JLabel("Number of words"),BoxLayout.Y_AXIS);
		JComboBox wordsNumBox = new JComboBox(numberOfWords); 
		optionsPanel.add(wordsNumBox,BoxLayout.LINE_AXIS);
		
		String[] rankAlgorithms = {"Term Frequency","TF-IDF","LexRanking"};
		optionsPanel.add(new JLabel("Ranking algorithm"),BoxLayout.Y_AXIS);
		JComboBox rankBox = new JComboBox(rankAlgorithms);
		optionsPanel.add(rankBox,BoxLayout.LINE_AXIS);

		String[] similAlgorithms = {"Cosine Similarity","Jaccard Similarity"};
		optionsPanel.add(new JLabel("Similarity algorithm"),BoxLayout.Y_AXIS);
		JComboBox similBox = new JComboBox(similAlgorithms);
		optionsPanel.add(similBox,BoxLayout.LINE_AXIS);
		
		String[] layoutAlgorithms = {""};
		optionsPanel.add(new JLabel("Layout algorithm"),BoxLayout.Y_AXIS);
		JComboBox layoutBox = new JComboBox(layoutAlgorithms);
		optionsPanel.add(layoutBox,BoxLayout.LINE_AXIS);
		
		//add panels to the container
		cont.add(fileAudioPanel,BoxLayout.Y_AXIS);
		cont.add(optionsPanel,BoxLayout.Y_AXIS);
		
		setVisible(true);
	}
	private class InputFileFilter extends FileFilter {	
		public final static String mp3 = "mp3";
	    public final static String mp4 = "mp4";
	    public final static String wav = "wav";
	    public final static String srt = "srt";
		
	    @Override
		public String getDescription() {return "Audio file or subtitles";}
		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) return true;
		   
			String extension = TextUtils.getFileExtension(f);
			if(extension!=null) {
				if(extension.equals(mp3)) return true;
				if(extension.equals(mp4)) return true;
				if(extension.equals(wav)) return true;
				if(extension.equals(srt)) return true;
			}
			return false;
		}
	}
}
