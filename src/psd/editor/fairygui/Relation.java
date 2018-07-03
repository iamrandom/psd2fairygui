package psd.editor.fairygui;

public class Relation {
	public String target = "";
	public String sidePair = "";
	
	public Relation(String target, String sidePair)
	{
		this.target = target;
		this.sidePair = sidePair;
	}
	
	public String to_resource_xml()
	{
		return String.format( "      <relation target=\"%s\" sidePair=\"%s\"/>\n", target, sidePair);
	}
}
