package Utilities;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {

	// These three classes are needed for creating Extent report
	private static ExtentSparkReporter sparkReporter;
	public static ExtentReports extent;
	public static ExtentTest test;

	//report initializations
	public static void initReport() { 
		if (extent == null) {
			sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/Reports/extentSparkReport.html");
			sparkReporter.config().setDocumentTitle("Automation Report");
			sparkReporter.config().setReportName("Test Execution Report");
			sparkReporter.config().setTheme(Theme.STANDARD);
			sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
			extent = new ExtentReports();
			extent.attachReporter(sparkReporter);
			extent.setSystemInfo("Tester", "Krishna Kandel");
			extent.setSystemInfo("Environment", "QA");
		}
	}
	//This method can be called without screenshotName argument as well.
	public static String captureScreenshot(WebDriver driver, String screenshotName) throws IOException {																						
		String FileSeparator = System.getProperty("file.separator"); // "/" or "\"
		String Extent_report_path = "." + FileSeparator + "Reports"; // . means parent directory
		File Src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String Screenshotname = screenshotName + Math.random() + ".png";
		File Dst = new File(Extent_report_path + FileSeparator + screenshotName + FileSeparator + Screenshotname);

		try {

			FileUtils.copyFile(Src, Dst);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String absPath = Dst.getAbsolutePath();
		System.out.println("Absolute path is:" + absPath);
		return absPath;
	}
	// Without this function, report can't be written. This moves report from memory to disc.
	public static void flushReport() {
		extent.flush();
	}
}