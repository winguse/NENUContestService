package cn.edu.nenu.acm.contestservice;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<Integer,ArrayList<String>> h=new HashMap<Integer,ArrayList<String>>();
		ArrayList<String> a=new ArrayList<String>();
		h.put(0, a);
		ArrayList<String> s=h.get(0);
		s.add("ss");
		System.out.println(h.get(0));
		System.out.println("".split("\\|").length);
	}

}
