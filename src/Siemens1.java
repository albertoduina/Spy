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

import ij.text.TextWindow;
import ij.text.TextPanel;

public class Siemens1 implements PlugIn {

	public int location;

	// variabili globali
	public static final int RUN = 1;
	public static final int STEP = 0;
	public static final int ABORT = -1;
	public int r1;

	private BufferedInputStream bis;
	ImagePlus imp;

	/***
	 * Questa routine deriva da un vecchio lavoro, sviluppato in fortran,
	 * indipendentemente dalle informazioni presenti su internet. Unico tool
	 * utilizzato ai temi era l'HexWorkshop. Ora i miei "findings" sono stati
	 * trovati anche da altri. Su internet ho trovato, per matlab una routine
	 * facente parte del Dicom Toolbox, di Dirk-Jan Kroon
	 * "www.mathworks.com/matlabcentral/fileexchange/27941-dicom-toolbox/content/SiemensInfo.m"
	 * in essa viene fatta una ricerca all'interno del Siemens Private Tag
	 * 0029,1020. Questo Tag non e'conosciuto da ImageJ, ma alla fine questo
	 * poco importa, tanto alla fine si tratta di fare una ricerca su di una
	 * stringa contenente tutta l'header Dicom o giu' di li'
	 * 
	 */
	public void run(String arg) {
		//
		// -------------( selezione ed apertura immagine )--------------------
		//
		OpenDialog od1 = new OpenDialog("SELEZIONARE IMMAGINE...", "");
		String directory1 = od1.getDirectory();
		String name1 = od1.getFileName();
		if (name1 == null)
			return;
		String path1 = directory1 + name1;
		IJ.showStatus("Opening: " + path1);
		// Opener o1 = new Opener();
		// ImagePlus imp1 = o1.openImage(path1);
		// if (imp1!=null) imp1.show();
		// imp1.setCalibration(null);
		// ImageProcessor ip1=imp1.getProcessor();

		try {
			FileInputStream fis = new FileInputStream(path1);
			BufferedInputStream bis = new BufferedInputStream(fis);
			int size = bis.available();
			String header = getString(bis, size - 20);
			int fromIndex = 1;
			int index1 = header.indexOf("### ASCCONV BEGIN", fromIndex);
			int index2 = header.indexOf("ASCCONV END ###", fromIndex + 1);
			String blob = "";
			IJ.log("indexBegin= " + index1 + "  indexEnd= " + index2);

			while ((index1 > 0) && (index2 > 0)) {
				blob = header.substring(index1, index2 + 19);
				IJ.log(blob);
				fromIndex = index2;
				index1 = header.indexOf("### ASCCONV BEGIN", fromIndex);
				index2 = header.indexOf("ASCCONV END ###", fromIndex + 1);
			}

		} catch (IOException e) {
			return;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			bis = null; // BufferedInputStream.close() erst ab Java 1.2
						// definiert
		}

	}

	public String getString(BufferedInputStream bo, int len) throws IOException {
		int pos = 0;
		byte[] buf = new byte[len];
		int size = bo.available();
		while (pos < len) {
			int count = bo.read(buf, pos, len - pos);
			pos += count;
		}
		location += len;
		return new String(buf);
	}

} // ultima parentesi chiusa

