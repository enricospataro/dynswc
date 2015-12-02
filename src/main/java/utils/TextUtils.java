package main.java.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TextUtils {
	
	private static List<String> textParts;
	private static StringBuilder fileToString;
	
	public static List<String> splitText(String text,int bytesToRead) {
		textParts = new ArrayList<>();
		int i=0;
		while(true) {
			String str="";
			if(i+bytesToRead>=text.length()) {		
				str=text.substring(i,text.length());
				textParts.add(str);
				return textParts;
			}
			str=text.substring(i,i+bytesToRead);
			i=i+bytesToRead;
			while(text.charAt(i-1) !=' ' && text.charAt(i) !=' ') {
				str+=text.charAt(i);
				i++;
				if(i==text.length()) {
					textParts.add(str);
					return textParts;
				}
			}
			textParts.add(str);
		}
	}
	public static String readFileToString(File pathToFile) throws IOException{
	    fileToString = new StringBuilder();
	    BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
	    char[] buffer = new char[512];
	    int num = 0;
	    while((num = reader.read(buffer)) != -1){
	        String current = String.valueOf(buffer, 0, num);
	        fileToString.append(current);
	        buffer = new char[512];
	    }
	    reader.close();
	    return fileToString.toString();
	}
	public static String getFileType(File f) throws IOException {return Files.probeContentType(f.toPath());}
	public static String getFileExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i=s.lastIndexOf('.');
	        if(i>0 && i<s.length()-1) ext = s.substring(i+1).toLowerCase();
	        return ext;
	}
}
