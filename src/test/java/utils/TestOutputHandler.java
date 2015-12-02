package test.java.utils;

import main.java.utils.OutputHandler;

public class TestOutputHandler implements OutputHandler {
	String s;

	public TestOutputHandler(String str){
		this.s = str;
	}
	
	@Override
	public void write(String str) {
		s+=str;
	}
	
	@Override
	public String toString(){
		return s;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
