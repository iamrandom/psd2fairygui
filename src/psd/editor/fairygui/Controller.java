package psd.editor.fairygui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
	
	public static Map<String, Integer> PAGES = new HashMap<String, Integer>();
	public static Map<Integer, String> INDEX_PAGES = new HashMap<Integer, String>();
	static
	{
		PAGES.put("up", 0);
		PAGES.put("down", 1);
		PAGES.put("over", 2);
		PAGES.put("selectedOver", 3);
		
		for(String key : PAGES.keySet())
		{
			INDEX_PAGES.put(PAGES.get(key), key);
		}
	}
	public String name;
	public Map<String, DisplayImage> images = new HashMap<String, DisplayImage>(); 
	public Controller(String name)
	{
		this.name = name;
	}
	
	public String to_resource_xml(String active_page)
	{
		return String.format("      <gearDisplay controller=\"%s\" pages=\"%d\"/>\n", name, PAGES.get(active_page));
	}
	
	public String toxml()
	{
		List<Integer> list = new ArrayList<Integer>(INDEX_PAGES.keySet());
	    Collections.sort(list);
	    String s = "";
	    for(Integer it : list)
	    {
	    	String n = INDEX_PAGES.get(it);
	    	DisplayImage img = images.get(n);
	    	if(img == null)
	    		continue;
	    	s += "," + it + "," + INDEX_PAGES.get(it) ;
	    }
	    if(s.length() > 0)
	    {
	    	s = s.substring(1);
	    }
	    return String.format("<controller name=\"%s\" pages=\"%s\"/>", name, s);
	}

}
