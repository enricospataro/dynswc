package main.java.utils;

public class SrtFileCleaner {
	
	private String text;
	private FileInputHandler in;
	private OutputHandler out;
	
	public SrtFileCleaner(FileInputHandler in, OutputHandler out) {
		this.in=in;
		this.out=out;
	}
	public void clean() {
		while(in.read()!=-1) {
			in.readLine();  // read line number
			in.readLine();  // read time code
			while(!(text = in.readLine()).equals("")) {
				out.write(" "+text);
			}
		}
		in.close();
		out.close();
	}	
}