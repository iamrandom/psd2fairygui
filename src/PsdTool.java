import java.io.File;
import java.io.IOException;

import psd.editor.fairygui.FairyShared;
import psd.editor.fairygui.FairyXml;
import psd.model.Psd;

public class PsdTool {

	public static void main(String[] args) {
		if(args.length < 2)
		{
			return;
		}
		String filename = args[0];
		String work_dir = args[1];
		
//		String filename = "F:/RANDOM/BOOK/psd/psd/test11.psd";
//		String filename = "F:/RANDOM/BOOK/psd/psd/ready/战争沙盘 -命名整理.psd";
//		String filename = "F:/RANDOM/BOOK/psd/psd/ready/英雄演练-命名整理.psd";
//		String work_dir = "E:/ly11/U3DMake/FairyGUI/Default/SimplifiedChinese/assets";
		File psdFile = new File(filename);
		if(!psdFile.exists() || psdFile.isDirectory())
		{
			FairyShared.Error("Fail to find the File " + filename);
			return;
		}
		if(!filename.endsWith(".psd"))
		{
			FairyShared.Error("We need a psd file, not " + filename);
			return;
		}
		if(work_dir == null || work_dir.length() == 0)
		{
			work_dir = ".";
		}
		FairyShared.Log("Start to command " + filename);
		FairyShared.Log("work dir : " + work_dir + "\n");
		Psd psd;
		try {
			psd = new Psd(psdFile);
			FairyXml fx = new FairyXml(psd);
			fx.parse(work_dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FairyShared.Log("\nEnd");
	}
}
