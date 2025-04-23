import java.io.*;

import ij.*;
import ij.io.*;
import ij.plugin.*;

//=====================================================
//     Programma per lettura ricorsiva all'interno delle directory
//     dei dati dell'header delle immagini
//     25 giugno 2004 
//     By A.Duina - Servizio di Fisica Sanitaria
//     Spedali Civili di Brescia
//     Linguaggio: Java per ImageJ
//=====================================================

public class OldSpy implements PlugIn {
	public static final int RUN = 1;

	public static final int STEP = 0;

	public static final int ABORT = -1;

	public int r1;

	public File[] list;

	public String file2 = "spy.csv";

	public String[][] strRiga2;

	public String[] blob;

	public int numFile;
	public int numTotal;

	public int count2;

	public int count;

	// ==============================================================================
	public void run(String arg) {
		boolean nuovo1;

		nuovo1 = true;

		if (nuovo1 == true) {
			DirectoryChooser od1 = new DirectoryChooser(
					"SELEZIONARE MA NON APRIRE DIRECTORY INIZIO RICERCA");

			//
			// ---------------------------------------------------------------------------------------------------------
			// Viene chiamata, in maniera ricorsiva se esistono sottodirectory
			// la funzione loadList, che scrive nel file di output i dati delle
			// immagini
			// da analizzare
			// ---------------------------------------------------------------------------------------------------------
			//		
			numFile = 0;
			count2 = -1;
			count = 0;
			String dir = od1.getDirectory();
			File fx = new File(dir);
			fx.delete();

			// carico la lista in memoria
			numTotal = dir.length();
			numFile = countFiles(dir);
			// new WaitForUserDialog("Do something, then click OK.").show();

			IJ.log("Trovati " + numFile + " files");

			strRiga2 = new String[numFile + 1][25];

			loadList(dir);
			String file3 = dir + "/" + file2;
			scrivi(file3, strRiga2);
			IJ.showMessage("FINE LAVORO");
		}
	} // chiude run

	// ===========================================================================

	private void loadList(String dir1) {

		// attenzione che loadList � chiamato ricorsivamente, deve utilizzare
		// una strRiga pubblica e
		// dimensionata nel programma chiamante

		String path1 = "";
		ImagePlus imp1;
		boolean trovato;

		Opener o1 = new Opener();

		String[] list2 = new File(dir1).list();
		int len1 = list2.length;

		// dimensiono tabella dati
		for (int i1 = 0; i1 < len1; i1++) {

			
			IJ.showStatus("loadList " + count + "/" + numFile);
			path1 = dir1 + list2[i1];
			File f1 = new File(path1);
			if (!f1.isDirectory()) {
				count++;
				IJ.redirectErrorMessages();
				imp1 = o1.openImage(path1);
				if (imp1 == null)
					continue;

				trovato = true;
				if (trovato) {
					count2++;
					strRiga2[count2][0] = "" + count2;
					strRiga2[count2][1] = path1;
					strRiga2[count2][2] = ReadPara2(imp1, "0008,103E"); // seriesDescription
					strRiga2[count2][3] = ReadPara2(imp1, "0020,0011"); // series
					// Number
					strRiga2[count2][4] = ReadPara2(imp1, "0020,0012"); // acquisition
					// Number
					strRiga2[count2][5] = ReadPara2(imp1, "0020,0013"); // image
					// Number
					strRiga2[count2][6] = ReadPara2(imp1, "0018,1030"); // protocolName
					strRiga2[count2][7] = ReadPara2(imp1, "0018,0020"); // scanningSequence
					strRiga2[count2][8] = dotToColon(ReadPara2(imp1,
							"0018,0080")); // repetitionTime
					strRiga2[count2][9] = dotToColon(ReadPara2(imp1,
							"0018,0081")); // echo Time
					strRiga2[count2][10] = dotToColon(ReadPara2(imp1,
							"0018,0086")); // echo number
					strRiga2[count2][11] = dotToColon(ReadPara2(imp1,
							"0018,0082")); // inversionTime
					strRiga2[count2][12] = ReadPara2(imp1, "0018,0083"); // averages
					strRiga2[count2][13] = dotToColon(ReadPara2(imp1,
							"0018,0050")); // thickness
					strRiga2[count2][14] = dotToColon(ReadPara2(imp1,
							"0018,0088")); // spacing
					strRiga2[count2][15] = ReadPara2(imp1, "0018,1310"); // acquisitionMatrix
					strRiga2[count2][16] = dotToColon(ReadPara2(imp1,
							"0018,1314")); // flipAngle
					strRiga2[count2][17] = ReadPara2(imp1, "0018,1250"); // receivingCoil
					strRiga2[count2][18] = ReadPara2(imp1, "0051,100F"); // receivingCoil
					strRiga2[count2][19] = ReadPara2(imp1, "0028,0010"); // rows
					strRiga2[count2][20] = ReadPara2(imp1, "0028,0011"); // columns
					strRiga2[count2][21] = dotToColon(ReadPara2(imp1,
							"0020,1041")); // sliceLocation
					strRiga2[count2][22] = dotToColon(ReadPara2(imp1,
							"0018,1100")); // reconstructionDiameter

				} else
					return;
			} else {
				path1 = path1 + "\\";
				loadList(path1);
			}
		}
		return;
	} // chiude loadList


	public String dotToColon(String ingresso) {
		String uscita = ingresso;
		String beforeDot = "";
		String afterDot = "";
		if (uscita == null)
			return (uscita);
		int dot = uscita.indexOf('.');
		while (dot > -1) {
			beforeDot = uscita.substring(0, dot);
			afterDot = uscita.substring(dot + 1, uscita.length());
			uscita = beforeDot + "," + afterDot;
			dot = uscita.indexOf('.');
		}
		return uscita;
	} // chiude dotToColon

	// =============================================================
	// La seguente routine, che si occupa di estrarre dati dall'header delle
	// immagini
	// � tratta da QueryDicomHeader.java di Anthony Padua e Daniel Barboriak
	// della Duke University Medical Center
	// =============================================================
	String ReadPara2(ImagePlus imp, String userInput) {
		// N.B. userInput => 9 characs [group,element] in format: xxxx,xxxx (es:
		// "0020,0013")
		String attribute = "???";
		String value = "???";
		int currSlice = imp.getCurrentSlice();
		ImageStack stack = imp.getStack();
		String header = stack.getSize() > 1 ? stack.getSliceLabel(currSlice)
				: (String) imp.getProperty("Info");
		if (header != null) {
			int idx1 = header.indexOf(userInput);
			int idx2 = header.indexOf(":", idx1);
			int idx3 = header.indexOf("\n", idx2);
			if (idx1 >= 0 && idx2 >= 0 && idx3 >= 0) {
				try {
					attribute = header.substring(idx1 + 9, idx2);
					attribute = attribute.trim();
					value = header.substring(idx2 + 1, idx3);
					value = value.trim();
					return (value);
				} catch (Throwable e) { // Anything else
					return (value);
				}
			} else {
				attribute = "MISS";
				return (attribute);
			}
		} else {
			// IJ.error("Header is null.");
			attribute = null;
			return (attribute);
		}
	}

	public void scrivi(String path, String[][] strRiga) {
		int j1 = 0;

		String rigaCompleta = "";

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			if (bw != null) {
				rigaCompleta = "n" + ";" + "path" + ";" + "serDesc" + ";"
						+ "ser" + ";" + "acq" + ";" + "ima" + ";" + " protName"
						+ ";" + "scanSeq" + ";" + "repT" + ";" + "echoT" + ";"
						+ "echoN" + ";" + "invT" + ";" + "aver" + ";" + "thick"
						+ ";" + "spac" + ";" + "acqMat" + ";" + "flipAng" + ";"
						+ "recCoil" + ";" + "recCoil" + ";" + "row" + ";"
						+ "col" + ";" + "sliceLoc" + ";" + "recDia" + ";"
						+ "\n";
				bw.write(rigaCompleta, 0, rigaCompleta.length());
				rigaCompleta = "";
				while (j1 < strRiga.length) {
					for (int a1 = 0; a1 < 25; a1++) {
						rigaCompleta = rigaCompleta + strRiga[j1][a1] + ";";
					}
					rigaCompleta = rigaCompleta + "\n";
					j1++;
					bw.write(rigaCompleta, 0, rigaCompleta.length());
					rigaCompleta = "";
				}
			}
			bw.close();
		} catch (IOException e) {
			IJ.showMessage("Error on Save As! ");
		}
	} // chiude scrivi

	/**
	 * Conta i file in maniera ricorsiva
	 * 
	 * @param filePath
	 *            path della directory di partenza, verranno lette anche tutte
	 *            le sottocartelle
	 * @return int totale dei files
	 */
	public static int countFiles(String filePath) {
		String[] list2 = new File(filePath).list();
		int count = 0;
		for (int i1 = 0; i1 < list2.length; i1++) {
			String path1 = filePath + "/" + list2[i1];
			File f1 = new File(path1);
			if (f1.isDirectory()) {
				count = count + countFiles(path1);
			} else {
				IJ.redirectErrorMessages();
				count++;
			}
		}
		return count;
	}
	
	
	
} // ultima
