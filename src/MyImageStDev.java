import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import utils.ImageUtils;
import utils.MyLog;
import utils.UtilAyv;

public class MyImageStDev implements PlugInFilter {
	ImagePlus imp1;

	public int setup(String arg, ImagePlus imp1) {
		this.imp1 = imp1;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip1) {
		ImagePlus SDimage = ImageUtils.generaStandardDeviationImage(imp1, 10);
		UtilAyv.showImageMaximized(SDimage);

	}

}
