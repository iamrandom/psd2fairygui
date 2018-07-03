package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;

public class ColorSpace {
	public final  static int psd_color_space_dummy = -1;
	public final  static int psd_color_space_rgb = 0;
	public final  static int psd_color_space_hsb = 1;
	public final  static int psd_color_space_cmyk = 2;
	public final  static int psd_color_space_pantone = 3;
	public final  static int psd_color_space_focoltone = 4;
	public final  static int psd_color_space_trumatch = 5;
	public final  static int psd_color_space_toyo = 6;
	public final  static int psd_color_space_lab = 7;
	public final  static int psd_color_space_gray = 8;
	public final  static int psd_color_space_wide_cmyk = 9;
	public final  static int psd_color_space_hks = 10;
	public final  static int psd_color_space_dic = 11;
	public final  static int psd_color_space_total_ink = 12;
	public final  static int psd_color_space_monitor_rgb = 13;
	public final  static int psd_color_space_duotone = 14;
	public final  static int psd_color_space_opacity = 15;

	public int colorSpace = 0;
	public int[] color = { 0, 0, 0, 0, 0, 0, 0, 0};
	public int red;
	public int green;
	public int blue;
	
	public int value;

	public int psd_hue_to_color(int hue, double m1, double m2) {
		double v;

		hue %= 360;
		if (hue < 60)
			v = m1 + (m2 - m1) * hue / 60;
		else if (hue < 180)
			v = m2;
		else if (hue < 240)
			v = m1 + (m2 - m1) * (240 - hue) / 60;
		else
			v = m1;

		return (int) (v * 255);
	}

	public void psd_ahsb_to_color(double alpha, int hue, double saturation,
			double brightness) {
		double m1, m2;

		if (saturation == 0.0) {
			blue = green = red = (int) (255 * brightness);
		} else {
			if (brightness <= 0.5)
				m2 = brightness * (1 + saturation);
			else
				m2 = brightness + saturation - brightness * saturation;
			m1 = 2 * brightness - m2;
			red = psd_hue_to_color(hue + 120, m1, m2);
			green = psd_hue_to_color(hue, m1, m2);
			blue = psd_hue_to_color(hue - 120, m1, m2);
		}
	}

	public int PSD_CONSTRAIN(int value, int lo, int hi) {
		return Math.min(Math.max(value, lo), hi);
	}

	public void psd_cmyk_to_color(double cyan, double magenta, double yellow,
			double black) {
		red = (int) (1.0 - (cyan * (1 - black) + black)) * 255;
		green = (int) (1.0 - (magenta * (1 - black) + black)) * 255;
		blue = (int) (1.0 - (yellow * (1 - black) + black)) * 255;
		red = PSD_CONSTRAIN(red, 0, 255);
		green = PSD_CONSTRAIN(green, 0, 255);
		blue = PSD_CONSTRAIN(blue, 0, 255);
	}

	public void psd_alab_to_color(int alpha, int lightness, int a, int b) {
		// For the conversion we first convert values to XYZ and then to RGB
		// Standards used Observer = 2, Illuminant = D65
		// ref_X = 95.047, ref_Y = 100.000, ref_Z = 108.883
		double x, y, z;
		double ref_x = 95.047;
		double ref_y = 100.000;
		double ref_z = 108.883;

		double var_y = ((double) lightness + 16.0) / 116.0;
		double var_x = (double) a / 500.0 + var_y;
		double var_z = var_y - (double) b / 200.0;
		if (Math.pow(var_y, 3) > 0.008856)
			var_y = Math.pow(var_y, 3);
		else
			var_y = (var_y - 16 / 116) / 7.787;

		if (Math.pow(var_x, 3) > 0.008856)
			var_x = Math.pow(var_x, 3);
		else
			var_x = (var_x - 16 / 116) / 7.787;

		if (Math.pow(var_z, 3) > 0.008856)
			var_z = Math.pow(var_z, 3);
		else
			var_z = (var_z - 16 / 116) / 7.787;

		x = ref_x * var_x;
		y = ref_y * var_y;
		z = ref_z * var_z;

		psd_axyz_to_color(alpha, x, y, z);
	}

	public void psd_axyz_to_color(int alpha, double x, double y, double z) {
		// Standards used Observer = 2, Illuminant = D65
		// ref_X = 95.047, ref_Y = 100.000, ref_Z = 108.883

		double var_x = x / 100.0;
		double var_y = y / 100.0;
		double var_z = z / 100.0;

		double var_r = var_x * 3.2406 + var_y * (-1.5372) + var_z * (-0.4986);
		double var_g = var_x * (-0.9689) + var_y * 1.8758 + var_z * 0.0415;
		double var_b = var_x * 0.0557 + var_y * (-0.2040) + var_z * 1.0570;

		if (var_r > 0.0031308)
			var_r = 1.055 * (Math.pow(var_r, 1 / 2.4)) - 0.055;
		else
			var_r = 12.92 * var_r;

		if (var_g > 0.0031308)
			var_g = 1.055 * (Math.pow(var_g, 1 / 2.4)) - 0.055;
		else
			var_g = 12.92 * var_g;

		if (var_b > 0.0031308)
			var_b = 1.055 * (Math.pow(var_b, 1 / 2.4)) - 0.055;
		else
			var_b = 12.92 * var_b;

		red = (int) (var_r * 256.0);
		green = (int) (var_g * 256.0);
		blue = (int) (var_b * 256.0);
	}

	public void read(PsdInputStream stream) throws IOException {
		this.colorSpace = stream.readShort();
		for (int i = 0; i < color.length; ++i) {
			color[i] = stream.read();
		}
		switch (colorSpace) {
		case psd_color_space_rgb:
			this.red = this.color[0];
			this.green = this.color[2];
			this.blue = this.color[4];
			break;
//
		case psd_color_space_hsb:
			psd_ahsb_to_color(255, color[0], (double) color[2] / 100.0,
					(double) color[4] / 100.0);
			break;
//
		case psd_color_space_cmyk:
			psd_cmyk_to_color((double) color[0] / 100.0,
					(double) color[2] / 100.0, (double) color[4] / 100.0,
					(double) color[6] / 100.0);
			break;
//
		case psd_color_space_lab:
			psd_alab_to_color(255, color[0], color[2], color[4]);
			break;
//
		default:
			break;
		}
		
		value = (red << 16) +  (green << 8) +  blue;
		
	}
}
