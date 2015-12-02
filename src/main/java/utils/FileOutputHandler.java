package main.java.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileOutputHandler implements OutputHandler{
	
	private FileWriter outWrite;

	public FileOutputHandler(File pathToFile) {
		try{
			outWrite = new FileWriter(pathToFile,true);
		} catch(IOException e) {e.printStackTrace();}
	}
	public void write(String str) {
		try {
			outWrite.write(str);
		} catch (IOException e) {e.printStackTrace();}
	}
	public void close() {
		try {
			outWrite.flush();
			outWrite.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}

