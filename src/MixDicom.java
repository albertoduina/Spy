import java.io.*;
import java.util.ArrayList;
import java.util.List;

import utils.ArrayUtils;
import utils.MyLog;
import utils.ReadDicom;
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

public class MixDicom implements PlugIn {
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

	List<String> arrConta = new ArrayList<String>();
	List<String> arrName = new ArrayList<String>();
	List<String> arrSeqDescr = new ArrayList<String>();
	List<String> arrSerie = new ArrayList<String>();
	List<String> arrAcq = new ArrayList<String>();
	List<String> arrIma = new ArrayList<String>();
	List<String> arrProtocName = new ArrayList<String>();
	List<String> arrScanSeq = new ArrayList<String>();
	List<String> arr_TR = new ArrayList<String>();
	List<String> arr_ECO_TIME = new ArrayList<String>();
	List<String> arr_ECO_NUMBER = new ArrayList<String>();
	List<String> arr_INV_TIME = new ArrayList<String>();
	List<String> arr_THICK = new ArrayList<String>();
	List<String> arr_AVERAGES = new ArrayList<String>();
	List<String> arr_SPACING = new ArrayList<String>();
	List<String> arr_MATRIX = new ArrayList<String>();
	List<String> arr_FLIP = new ArrayList<String>();
	List<String> arr_REC_COIL_1 = new ArrayList<String>();
	List<String> arr_REC_COIL_2 = new ArrayList<String>();
	List<String> arr_ROWS = new ArrayList<String>();
	List<String> arr_COLUMNS = new ArrayList<String>();
	List<String> arr_ENCODING = new ArrayList<String>();
	List<String> arr_BW = new ArrayList<String>();
	List<String> arr_DIR = new ArrayList<String>();
	List<String> arr_ASCCONV1 = new ArrayList<String>();
	List<String> arr_ASCCONV2 = new ArrayList<String>();
	List<String> arr_ASCCONV3 = new ArrayList<String>();
	List<String> arr_ASCCONV4 = new ArrayList<String>();
	List<String> arr_ASCCONV5 = new ArrayList<String>();
	List<String> arr_ASCCONV6 = new ArrayList<String>();
	List<String> arr_ASCCONV7 = new ArrayList<String>();
	List<String> arr_ASCCONV8 = new ArrayList<String>();
	List<String> arr_ASCCONV9 = new ArrayList<String>();

	List<String> arr_SliceLocation = new ArrayList<String>();
	List<String> arr_ReconstructionDiameter = new ArrayList<String>();
	List<String> arr_PixelSpacing = new ArrayList<String>();

	// ==============================================================================
	public void run(String arg) {

		boolean nuovo1;

		nuovo1 = true;

		if (nuovo1 == true) {
			DirectoryChooser od1 = new DirectoryChooser("SELEZIONARE MA NON APRIRE DIRECTORY INIZIO RICERCA");

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

			// IJ.log("Trovati " + numFile + " files");

			// / qui sta il problema: io comunque dimensiono il vettore per
			// tutti i valori.
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
			String name1 = f1.getName();
			if (!f1.isDirectory()) {
				count++;
				IJ.redirectErrorMessages();
				imp1 = o1.openImage(path1);
				if (imp1 == null)
					continue;
				String ascconv = ReadAscconv(path1);

				// MyLog.waitHere("aux11= "+aux11);

				if (ReadPara2(imp1, "0020,0011") != null) {
					count2++;
					arrConta.add("" + count2);
					arrName.add(name1);

					arrSeqDescr.add(ReadPara2(imp1, "0008,103E"));
					arrSerie.add(ReadPara2(imp1, "0020,0011"));
					arrAcq.add(ReadPara2(imp1, "0020,0012"));
					arrIma.add(ReadPara2(imp1, "0020,0013"));
					arrProtocName.add(ReadPara2(imp1, "0018,1030"));
					arrScanSeq.add(ReadPara2(imp1, "0018,0020"));
					arr_TR.add(dotToColon(ReadPara2(imp1, "0018,0080")));
					arr_ECO_TIME.add(dotToColon(ReadPara2(imp1, "0018,0081")));
					arr_ECO_NUMBER.add(dotToColon(ReadPara2(imp1, "0018,0086")));

					arr_INV_TIME.add(dotToColon(ReadPara2(imp1, "0018,0082")));
					arr_AVERAGES.add(ReadPara2(imp1, "0018,0083"));
					arr_THICK.add(dotToColon(ReadPara2(imp1, "0018,0050")));
					arr_SPACING.add(dotToColon(ReadPara2(imp1, "0018,0088")));
					arr_BW.add(dotToColon(ReadPara2(imp1, "0018,0095")));
					arr_MATRIX.add(ReadPara2(imp1, "0018,1310"));
					arr_FLIP.add(dotToColon(ReadPara2(imp1, "0018,1314")));
					arr_REC_COIL_1.add(ReadPara2(imp1, "0018,1250"));
					arr_REC_COIL_2.add(ReadPara2(imp1, "0051,100F"));
					arr_ROWS.add(ReadPara2(imp1, "0028,0010"));
					arr_COLUMNS.add(ReadPara2(imp1, "0028,0011"));
					arr_ENCODING.add(ReadPara2(imp1, "0018,1312"));
					arr_SliceLocation.add(dotToColon(ReadPara2(imp1, "0020,1041")));
					arr_ReconstructionDiameter.add(dotToColon(ReadPara2(imp1, "0018,1100")));
					arr_PixelSpacing.add(ReadPara2(imp1, "0028,0030"));
					arr_DIR.add(directionCalculator(imp1));
					arr_ASCCONV1.add(ReadPara2(imp1, "0029,1020"));

				}
				// else
				// return;
			} else {
				path1 = path1 + "\\";
				loadList(path1);
			}
		}
		return;
	}// chiude loadList

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

	public String ReadAscconv(String path1) {
		String blob = "";
		FileInputStream fis;

		try {
			fis = new FileInputStream(path1);
			BufferedInputStream bis = new BufferedInputStream(fis);
			int size = bis.available();
			String header = getString(bis, size - 20);
			int fromIndex = 1;
			int index1 = header.indexOf("### ASCCONV BEGIN", fromIndex);
			int index2 = header.indexOf("ASCCONV END ###", fromIndex + 1);

			IJ.log("indexBegin= " + index1 + "  indexEnd= " + index2);

			while ((index1 > 0) && (index2 > 0)) {
				blob = header.substring(index1, index2 + 19);
				fromIndex = index2;
				index1 = header.indexOf("### ASCCONV BEGIN", fromIndex);
				index2 = header.indexOf("ASCCONV END ###", fromIndex + 1);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return blob;
	}

	// private static int location;
	static String getString(BufferedInputStream bo, int len) throws IOException {
		int pos = 0;
		byte[] buf = new byte[len];
		// int size = bo.available();
		while (pos < len) {
			int count = bo.read(buf, pos, len - pos);
			pos += count;
		}
		// location += len;
		return new String(buf);
	}

	static String InsideAscconv(String ascconv, String para) {
		int index1 = ascconv.indexOf(para);

		return null;
	}

	// =============================================================
	// La seguente routine, che si occupa di estrarre dati dall'header delle
	// immagini
	// � tratta da QueryDicomHeader.java di Anthony Padua e Daniel Barboriak
	// della Duke University Medical Center
	// =============================================================
	static String ReadPara2(ImagePlus imp, String userInput) {
		// N.B. userInput => 9 characs [group,element] in format: xxxx,xxxx (es:
		// "0020,0013")
		String attribute = "???";
		String value = "???";
		int currSlice = imp.getCurrentSlice();
		ImageStack stack = imp.getStack();
		String header = stack.getSize() > 1 ? stack.getSliceLabel(currSlice) : (String) imp.getProperty("Info");
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
		String[] vetConta = ArrayUtils.arrayListToArrayString(arrConta);
		String[] vetName = ArrayUtils.arrayListToArrayString(arrName);
		String[] vetSeqDescr = ArrayUtils.arrayListToArrayString(arrSeqDescr);
		String[] vetSerie = ArrayUtils.arrayListToArrayString(arrSerie);
		String[] vetAcq = ArrayUtils.arrayListToArrayString(arrAcq);
		String[] vetIma = ArrayUtils.arrayListToArrayString(arrIma);
		String[] vetProtocName = ArrayUtils.arrayListToArrayString(arrProtocName);
		String[] vetScanSeq = ArrayUtils.arrayListToArrayString(arrScanSeq);
		String[] vet_TR = ArrayUtils.arrayListToArrayString(arr_TR);
		String[] vet_ECO_TIME = ArrayUtils.arrayListToArrayString(arr_ECO_TIME);
		String[] vet_ECO_NUMBER = ArrayUtils.arrayListToArrayString(arr_ECO_NUMBER);
		String[] vet_INV_TIME = ArrayUtils.arrayListToArrayString(arr_INV_TIME);
		String[] vet_FLIP = ArrayUtils.arrayListToArrayString(arr_FLIP);
		String[] vet_BW = ArrayUtils.arrayListToArrayString(arr_BW);

		String[] vet_THICK = ArrayUtils.arrayListToArrayString(arr_THICK);
		String[] vet_AVERAGES = ArrayUtils.arrayListToArrayString(arr_AVERAGES);
		String[] vet_SPACING = ArrayUtils.arrayListToArrayString(arr_SPACING);
		String[] vet_MATRIX = ArrayUtils.arrayListToArrayString(arr_MATRIX);
		String[] vet_REC_COIL_1 = ArrayUtils.arrayListToArrayString(arr_REC_COIL_1);
		String[] vet_REC_COIL_2 = ArrayUtils.arrayListToArrayString(arr_REC_COIL_2);
		String[] vet_ROWS = ArrayUtils.arrayListToArrayString(arr_ROWS);
		String[] vet_COLUMNS = ArrayUtils.arrayListToArrayString(arr_COLUMNS);
		String[] vet_ENCODING = ArrayUtils.arrayListToArrayString(arr_ENCODING);
		String[] vet_SliceLocation = ArrayUtils.arrayListToArrayString(arr_SliceLocation);
		String[] vet_ReconstructionDiameter = ArrayUtils.arrayListToArrayString(arr_ReconstructionDiameter);
		String[] vet_PixelSpacing = ArrayUtils.arrayListToArrayString(arr_PixelSpacing);
		String[] vet_DIR = ArrayUtils.arrayListToArrayString(arr_DIR);

		String rigaCompleta = "";

		double fovH = 0;
		double fovV = 0;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			MyLog.waitHere("path= " + path);
			if (bw != null) {
				rigaCompleta = "n" + ";" + "path" + ";" + "serDesc" + ";" + "ser" + ";" + "acq" + ";" + "ima" + ";"
						+ " protName" + ";" + "scanSeq" + ";" + "repT" + ";" + "echoT" + ";" + "echoN" + ";" + "invT"
						+ ";" + "flipAng" + ";" + "bw" + ";" + "thich" + ";" + "aver" + ";" + "spac" + ";" + "acqMat"
						+ ";" + "recCoil" + ";" + "recCoil" + ";" + "row" + ";" + "col" + ";" + "DIR" + ";"
						+ "encodingDir" + ";" + "sliceLoc" + ";" + "recDia" + ";" + "pixSpacing" + ";" + "fovH" + ";"
						+ "fovV" + "\n";
				bw.write(rigaCompleta, 0, rigaCompleta.length());
				for (int a1 = 0; a1 < vetConta.length; a1++) {
					fovH = ReadDicom.readDouble(vet_ROWS[a1])
							* ReadDicom.readDouble(ReadDicom.readSubstring(vet_PixelSpacing[a1], 1));
					fovV = ReadDicom.readDouble(vet_COLUMNS[a1])
							* ReadDicom.readDouble(ReadDicom.readSubstring(vet_PixelSpacing[a1], 2));
					rigaCompleta = vetConta[a1] + ";" + vetName[a1] + ";" + vetSeqDescr[a1] + ";" + vetSerie[a1] + ";"
							+ vetAcq[a1] + ";" + vetIma[a1] + ";" + vetProtocName[a1] + ";" + vetScanSeq[a1] + ";"
							+ vet_TR[a1] + ";" + vet_ECO_TIME[a1] + ";" + vet_ECO_NUMBER[a1] + ";" + vet_INV_TIME[a1]
							+ ";" + vet_FLIP[a1] + ";" + vet_BW[a1] + ";" + vet_THICK[a1] + ";" + vet_AVERAGES[a1] + ";"
							+ vet_SPACING[a1] + ";" + vet_MATRIX[a1] + ";" + vet_REC_COIL_1[a1] + ";"
							+ vet_REC_COIL_2[a1] + ";" + vet_ROWS[a1] + ";" + vet_COLUMNS[a1] + ";" + vet_DIR[a1] + ";"
							+ vet_ENCODING[a1] + ";" + vet_SliceLocation[a1] + ";" + vet_ReconstructionDiameter[a1]
							+ ";" + vet_PixelSpacing[a1] + ";" + fovH + ";" + fovV + "\n";
					bw.write(rigaCompleta, 0, rigaCompleta.length());
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

	public static String directionCalculator(ImagePlus imp1) {

		// http://www.medicalconnections.co.uk/kb/Coronal_Sagittal_Transverse_position_calculation

		double[] pixel_space = new double[2];
		pixel_space[0] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0028,0030"), 1));
		pixel_space[1] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0028,0030"), 2));

		int rows = ReadDicom.readInt(ReadPara2(imp1, "0028,0010"));
		int columns = ReadDicom.readInt(ReadPara2(imp1, "0028,0011"));

		double[] top_left_corner = new double[3];
		top_left_corner[0] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0032"), 1));
		top_left_corner[1] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0032"), 2));
		top_left_corner[2] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0032"), 3));

		double[][] frame_vec = new double[3][3];
		frame_vec[0][0] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0037"), 1)); // top
																											// edge
																											// frame
																											// vector
																											// x1
		frame_vec[0][1] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0037"), 2)); // top
																											// edge
																											// frame
																											// vector
																											// y1
		frame_vec[0][2] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0037"), 3)); // top
																											// edge
																											// frame
																											// vector
																											// z1
		frame_vec[1][0] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0037"), 4)); // left
																											// edge
																											// frame
																											// vector
																											// x1
		frame_vec[1][1] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0037"), 5)); // left
																											// edge
																											// frame
																											// vector
																											// x1
		frame_vec[1][2] = ReadDicom.readDouble(ReadDicom.readSubstring(ReadPara2(imp1, "0020,0037"), 6)); // left
																											// edge
																											// frame
																											// vector
																											// x1
		// calculate the frame Normal the frame normal lets us know if the image
		// is Sag, Tran or Coronal
		frame_vec[2][0] = frame_vec[0][1] * frame_vec[1][2] - frame_vec[0][2] * frame_vec[1][1];
		frame_vec[2][1] = frame_vec[0][2] * frame_vec[1][0] - frame_vec[0][0] * frame_vec[1][2];
		frame_vec[2][2] = frame_vec[0][0] * frame_vec[1][1] - frame_vec[0][1] * frame_vec[1][0];

		// calculat mid frame distance
		double[] mid_frame = new double[3];
		mid_frame[0] = top_left_corner[0] + (columns / 2) * pixel_space[0] * frame_vec[0][0]
				+ (rows / 2) * pixel_space[1] * frame_vec[1][0];

		mid_frame[1] = top_left_corner[1] + (columns / 2) * pixel_space[0] * frame_vec[0][1]
				+ (rows / 2) * pixel_space[1] * frame_vec[1][1];
		mid_frame[2] = top_left_corner[2] + (columns / 2) * pixel_space[0] * frame_vec[0][2]
				+ (rows / 2) * pixel_space[1] * frame_vec[1][2];

		// if Sagittal check
		if ((Math.abs(frame_vec[2][0]) > Math.abs(frame_vec[2][1]))
				&& (Math.abs(frame_vec[2][0]) > Math.abs(frame_vec[2][2]))) {
			return "SAG";
		} else // Cor check
		if ((Math.abs(frame_vec[2][1]) > Math.abs(frame_vec[2][0]))
				&& (Math.abs(frame_vec[2][1]) > Math.abs(frame_vec[2][2]))) {
			return "COR";
		} else // Ax
		{
			return "TRA";
		}

	}
} // ultima
