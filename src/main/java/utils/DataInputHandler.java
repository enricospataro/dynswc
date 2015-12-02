package main.java.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class DataInputHandler implements InputHandler{
	
	private FileInputStream fin;
	private ObjectInputStream ois;
	private int c;
	
	public DataInputHandler(File fileToPath) {
		try {
			fin = new FileInputStream(fileToPath);
			ois = new ObjectInputStream(fin);
		} catch (IOException e) {e.printStackTrace();}
	}
	public ObjectInputStream getObjectInputStream(){return ois;}
	public int read() {
		try {
			c=ois.read();
		} catch (IOException e) {e.printStackTrace();}
		return c;
	}
	public void close() {
		try {
			ois.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
