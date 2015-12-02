package main.java.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class FileInputHandler implements InputHandler{
	
	private BufferedReader inRead;
	private String line;
	private int c;

	public FileInputHandler(File pathToFile) {
		try{
			inRead = new BufferedReader(new FileReader(pathToFile));
		} catch(FileNotFoundException e) {e.printStackTrace();}
	}
	public String readLine() {
		try{
			line=inRead.readLine();
		} catch(IOException e) {e.printStackTrace();}
		return line;
	}
	public int read() {
		try {
			c=inRead.read();
		} catch (IOException e) {e.printStackTrace();}
		return c;
	}
	public void close() {
		try {
			inRead.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
