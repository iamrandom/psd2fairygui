package psd.editor.fairygui;

public class DisplayLoader extends DisplayImage {
	public static String TAG = "loader";
	private static String LOADER_TEMPLATE = "";

	static {
		LOADER_TEMPLATE += "    <loader id=\"%s\" name=\"%s\" xy=\"%d,%d\" size=\"%d,%d\" url=\"ui://%s%s\" />\n";
	}

	@Override
	public String toxml() {
		// TODO Auto-generated method stub
		String n = this.show_name != null ? this.show_name : this.name;
		String mid = FairyShared.instance.cur_spd_id;
		String item_id = this.src;
		if (this.pkg != null && this.pkg.length() > 0) {
			mid = FairyShared.instance.find_forder_id(this.pkg, this.parent_path + TAG + "_" + this.name);
			item_id = FairyShared.instance.find_resource_id(this.pkg, this.name
					+ "." + this.get_file_format(), this.parent_path + TAG + "_" + this.name);
		}
		return String.format(LOADER_TEMPLATE, this.id, n, this.x
				- this.parent.x, this.y - this.parent.y, this.width,
				this.height, mid, item_id);

	}
}
