import java.awt.*;
import java.awt.event.*;

import java.util.Stack;
import ij.plugin.frame.*;
import ij.*;
import ij.process.*;
import ij.gui.*;

import ij.io.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.measure.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;

import utils.MyLog;
import utils.ReadDicom;

import ij.text.TextWindow;
import ij.text.TextPanel;
import ij.util.DicomTools;

public class DicomExamProtocol implements PlugIn {

	public int location;

	// variabili globali
	public static final int RUN = 1;
	public static final int STEP = 0;
	public static final int ABORT = -1;
	public int r1;

	private BufferedInputStream bis;
	ImagePlus imp;

	private static final int PIXEL_DATA = 0x7FE00010;
	private static final int NON_IMAGE = 0x7FE10010;
	private static final int BINARY_DATA = 0x7FE11010;

	// private static final int BINARY_DATA = 269541759;

	public void run(String arg) {
		OpenDialog od1 = new OpenDialog("SELEZIONARE IL FILE EXAM PROTOCOL...",
				"");
		String directory1 = od1.getDirectory();
		String name1 = od1.getFileName();
		if (name1 == null)
			return;
		String path1 = directory1 + name1;

		String info1 = new DICOM().getInfo(path1);

		String para1 = ReadDicom.readDicomParameter(info1, "0029,0010");

		if (para1.equals("SIEMENS CSA NON-IMAGE")) {
			MyLog.waitHere("SIEMENS CSA NON-IMAGE FOUND");

			String info = readSiemensExamProtocol(path1);
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(directory1
						+ "\\pippo.txt"));
				writer.write(info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (writer != null)
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}

	}

	public String getString(BufferedInputStream bo, int len) throws IOException {
		int pos = 0;
		byte[] buf = new byte[len];
		// int size = bo.available();
		while (pos < len) {
			int count = bo.read(buf, pos, len - pos);
			pos += count;
		}
		location += len;
		return new String(buf);
	}

	public static String readSiemensExamProtocol(String fileName1) {
		int totalFileLen = 0;
		byte[] fBufCopy = null;
		String stica = "";

		boolean trovato = false;

		try {
			BufferedInputStream f = new BufferedInputStream(
					new FileInputStream(fileName1));
			totalFileLen = f.available();
			fBufCopy = new byte[totalFileLen];
			f.read(fBufCopy, 0, totalFileLen);
			f.close();
		} catch (Exception e) {
			IJ.showMessage("preFilter", "Error opening " + fileName1
					+ "\n \n\"" + e.getMessage() + "\"");
		}

		int point = 0;
		for (int i1 = 0; i1 < totalFileLen - 4; i1++) {
			int b0 = 0xFF & fBufCopy[i1 + 0];
			int b1 = 0xFF & fBufCopy[i1 + 1];
			int b2 = 0xFF & fBufCopy[i1 + 2];
			int b3 = 0xFF & fBufCopy[i1 + 3];
			int bigEnd = ((b1 << 24) + (b0 << 16) + (b3 << 8) + b2);

			byte[] pippo = new byte[4];
			pippo[0] = (byte) (0xFF & fBufCopy[i1 + 0]);
			pippo[1] = (byte) (0xFF & fBufCopy[i1 + 1]);
			pippo[2] = (byte) (0xFF & fBufCopy[i1 + 2]);
			pippo[3] = (byte) (0xFF & fBufCopy[i1 + 3]);
			if (bigEnd == BINARY_DATA) {
				trovato = true;
				MyLog.waitHere("SIEMENS EXAM PROTOCOL FOUND");
				point = i1 + 12;
				}
			if (trovato) {
				stica = new String(fBufCopy);
			}

		}
		String blob = stica.substring(point, stica.length());
		return blob;
	}

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String asHex(byte[] buf) {
		char[] chars = new char[2 * buf.length];
		for (int i1 = 0; i1 < buf.length; i1++) {
			chars[2 * i1] = HEX_CHARS[(buf[i1] >> 4) & 0X0F];
			chars[2 * i1 + 1] = HEX_CHARS[(buf[i1] & 0X0F)];
		}

		return new String(chars);
	}

	public static String byte2hex(byte by) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		char[] buf2 = new char[2];
		buf2[1] = hexDigits[by & 0xf];
		by >>>= 4;
		buf2[0] = hexDigits[by & 0xf];
		return new String(buf2);
	} // end byte2hex

	public static final byte[] short2Byte(short s) {
		byte[] out = new byte[2];

		out[0] = (byte) ((s >>> 8) & 0xFF);
		out[1] = (byte) ((s >>> 0) & 0xFF);

		return out;
	}

} // ultima parentesi chiusa

