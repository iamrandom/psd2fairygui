package psd.editor.fairygui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FairyPackage {
	public static FairyPackage instance = new FairyPackage();
	
	public static String XmlTemplate = "";
	static {
		XmlTemplate += Util.XML_HEADER;
		XmlTemplate += "<packageDescription id=\"%s\" >\n";
		XmlTemplate += "  <publish name=\"%s\"/>\n";
		XmlTemplate += "  <resources>\n%s\n%s\n%s  </resources>\n";
		XmlTemplate += "</packageDescription>";
	}
	public String id = "";

	public String toxml(String forlders_str, String images_str,
			String components_str) {
		return String.format(XmlTemplate, id, this.name, forlders_str, images_str,
				components_str);
	}
	
	public class ImageInfo
	{
		public String tag;
		public int width;
		public int height;
		public String id;
		public String name;
		public String xml_name;
		public String path;
		public List<ImageInfo> display_list;
		public boolean exported = false;
		public String scale;
		public String scale9grid;
	}
	
	public Map<String, ImageInfo> image_infos = new HashMap<String, ImageInfo>();
	public Map<String, ImageInfo> comp_infos = new HashMap<String, ImageInfo>();
	
	public Map<String, ImageInfo> image_id_infos = new HashMap<String, ImageInfo>();
	public Map<String, ImageInfo> comp_id_infos = new HashMap<String, ImageInfo>();
	public HashSet<String> ids = new HashSet<String>();
	public String name;
	
	public void parse_package(String work_dir, String name)
	{
		File dir = new File(work_dir);
		Document doc = read_xml(dir.getAbsolutePath() + "/" + name + "/package.xml");
		
		Element packageDescription = doc.getDocumentElement();
		this.id = packageDescription.getAttribute("id");
		NodeList nlst = packageDescription.getChildNodes();
		for(int i = 0; i< nlst.getLength(); ++i)
		{
			Node node = nlst.item(i);
			for(Node n = node.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if(n.getNodeType() == n.ELEMENT_NODE)
				{
					String s_str = n.getNodeName();
					NamedNodeMap nm = n.getAttributes();
					String child_filename = dir.getAbsoluteFile() + "/" + name + nm.getNamedItem("path").getNodeValue() + nm.getNamedItem("name").getNodeValue();
					ImageInfo imgInfo = new ImageInfo();
					imgInfo.id = nm.getNamedItem("id").getNodeValue();
					ids.add(imgInfo.id);
					imgInfo.path = nm.getNamedItem("path").getNodeValue();
					String image_name = nm.getNamedItem("name").getNodeValue();
					imgInfo.xml_name = image_name;
					imgInfo.tag = n.getNodeName();
					imgInfo.name = image_name;
					Node exportedNode = nm.getNamedItem("exported");
					if(exportedNode != null && "true".equals(exportedNode.getNodeValue()))
					{
						imgInfo.exported = true;
					}
					Node scale9gridNode = nm.getNamedItem("scale9grid");
					if(scale9gridNode != null)
					{
						imgInfo.scale9grid = scale9gridNode.getNodeValue();
					}
					
					if("image".equals(n.getNodeName()))
					{
						int lastPoint = image_name.lastIndexOf(".");
						String suf = image_name.substring(lastPoint);
						if(lastPoint > 0)
						{
							image_name = image_name.substring(0, lastPoint);
						}
						
						imgInfo.name = image_name;
						
						File picture = new File(child_filename);
						if(!picture.exists())
						{
							System.err.println("Not exsits file : " + picture.getAbsolutePath());
						}
						else
						{
							try {
								BufferedImage sourceImg =ImageIO.read(new FileInputStream(picture));
								String sz = "_" + sourceImg.getWidth() + "x" + sourceImg.getHeight();
								if(!image_name.endsWith(sz))
								{
									imgInfo.name = image_name + sz + suf;
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
		
						s_str += "	" + imgInfo.name + "	" + imgInfo.xml_name;
						image_infos.put(imgInfo.xml_name, imgInfo);
						image_infos.put(imgInfo.name, imgInfo);
						image_id_infos.put(imgInfo.id, imgInfo);
					}
					else if("component".equals(n.getNodeName()))
					{
						comp_id_infos.put(imgInfo.id, imgInfo);
						comp_infos.put(imgInfo.name, imgInfo);
						s_str += "	" + imgInfo.name + "	" + imgInfo.id;
					}
//					System.out.println(s_str);
				}
			}
		}
		
//		for(String key : this.comp_id_infos.keySet())
//		{
//			ImageInfo info = this.comp_id_infos.get(key);
//			parse_xml(dir.getAbsolutePath(), name, info);
//		}
	}
	
	public void parse_xml(String work_dir, String name, ImageInfo info)
	{
		if(info.display_list != null) return;
		info.display_list = new ArrayList<ImageInfo>();
		File f = new File(work_dir + "/" + name +  info.path);
		Document xml_doc = read_xml( f.getAbsolutePath() + "/"+ info.name);
		if(xml_doc == null)
		{
			return;
		}
		Element element = xml_doc.getDocumentElement();
		NodeList nlst = element.getChildNodes();
		System.out.println(info.path + info.name);
		String key_str = "";
		for(int i = 0; i < nlst.getLength(); ++i)
		{
			Node node = nlst.item(i);
			for(Node n = node.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if(n.getNodeType() == n.ELEMENT_NODE)
				{
					String s_str = n.getNodeName() + "=(";
					
					NamedNodeMap nm = n.getAttributes();
					List<Node> nlist = new ArrayList<Node>();
					nlist.add(nm.getNamedItem("src"));
					nlist.add(nm.getNamedItem("xy"));
					nlist.add(nm.getNamedItem("size"));
					nlist.add(nm.getNamedItem("pkg"));
					for(Node nd : nlist)
					{
						if(nd != null)
						{
							s_str += " " + nd.getNodeName() + "=" + nd.getNodeValue();
						}
					}
					s_str += ")";
					System.out.println(s_str);
				}
			}
		}
	}
	
	public void write_xml_exist(String outpath, String name, String compstr, String imagestr)
	{
		File dir = new File(outpath);
		if(!dir.exists()) return;
		File out_file = new File(dir.getAbsoluteFile() + "/" + name);
		if(!out_file.exists()) return;
		String s = "";

		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(out_file),"UTF8"));  
			String line = null;
			while ((line = reader.readLine()) != null) {				
				if(line.indexOf("</resources") >= 0 || line.indexOf("</ resources") >= 0)
				{
					s += "\n\n";
					s += imagestr;
					s += "\n" + compstr;
				}
				s += line + "\n";
			}
			reader.close();
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(out_file), "UTF-8");
			out.write(s.toCharArray());
			out.flush();
			out.close();
			return;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Document read_xml(String filename) {
		File pkdir = new File(filename);
		try {
			DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputStream input = new FileInputStream(pkdir);
			Document doc = domBuilder.parse(input);
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	

}
