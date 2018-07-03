package psd.editor.fairygui;

import java.util.HashSet;

public class Suffix {
	public static HashSet<String> FileSuffixs = new HashSet<String>();
	static
	{
		FileSuffixs.add("png");
		FileSuffixs.add("jpg");
	}
	
	public static String get_suffix(String name)
	{
		int index = name.lastIndexOf(".");
		if(index < 0) return null;
		String suf = name.substring(index + 1);
		if(suf == null || suf.length() == 0) return null;
		suf = suf.toLowerCase();
		if(FileSuffixs.contains(suf))
		{
			return suf;
		}
		else
		{
			return null;
		}
	}
	

	
}
