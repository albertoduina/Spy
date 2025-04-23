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

import utils.MyConst;
import utils.MyLog;
import utils.ReadAscconv;
import utils.ReadDicom;
import utils.ReportStandardInfo;
import utils.UtilAyv;
import ij.text.TextWindow;
import ij.text.TextPanel;
import ij.util.DicomTools;

public class NewSiemensParameters implements PlugIn {

	public int location;

	// variabili globali
	public static final int RUN = 1;
	public static final int STEP = 0;
	public static final int ABORT = -1;
	public int r1;

	private BufferedInputStream bis;
	ImagePlus imp;

	public void run(String arg) {
		do {
			OpenDialog od1 = new OpenDialog("SELEZIONARE IMMAGINE...", "");
			String directory1 = od1.getDirectory();
			String name1 = od1.getFileName();
			if (name1 == null)
				return;
			String path1 = directory1 + name1;
			IJ.showStatus("Opening: " + path1);
			// qui leggo i parametri che mi servono dall'header Dicom
			ImagePlus imp1 = UtilAyv.openImageNoDisplay(path1, false);
			String path2 = directory1 + "data.txt";
			String[] info1 = ReportStandardInfo.getMiniStandardInfo(path1,
					imp1, false);
			ResultsTable rt = ReportStandardInfo.putMiniStandardInfoRT(info1);
			ReadAscconv ra1 = new ReadAscconv();
			String blob = ra1.read(path1);
			String[] primoPasso = ra1.parseParameters(blob);
			String[] terzoPasso = ra1.searchPartialName(primoPasso, "FFT");
			for (int i1 = 0; i1 < terzoPasso.length; i1++) {
				rt.incrementCounter();
				rt.addValue("0", terzoPasso[i1]);
			}
//			try {
//		//		UtilAyv.mySaveAs(path2);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} while (true);
	}

} // ultima parentesi chiusa

