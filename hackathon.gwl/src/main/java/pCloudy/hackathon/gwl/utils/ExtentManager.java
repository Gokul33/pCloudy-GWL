package pCloudy.hackathon.gwl.utils;

/**
 * @author Gokul S
 * @company Galaxyweblinks
 * July 02, 2021
 */

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.Platform;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ExtentManager {
	public static String fileName = null;
	private static ExtentReports extent;
	private static Platform platform;
	private static String reportFileName = "ExtentReport.html";
	private static String macPath = System.getProperty("user.dir") + "/test-output/ExtentReport";
	private static String windowsPath = System.getProperty("user.dir") + "\\test-output\\ExtentReport";
	private static String macReportFileLoc = macPath + "/" + reportFileName;
	private static String winReportFileLoc = windowsPath + "\\" + reportFileName;

	public static ExtentReports getInstance() {
		if (extent == null)
			try {
				extent = createInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return extent;
	}

	public static ExtentSparkReporter htmlReporter;

	/**
	 * Create an extent report instance
	 * 
	 * @author Gokul S
	 * @return extent report
	 * @throws IOException
	 */
	public static ExtentReports createInstance() throws IOException {
		FileReader reader = new FileReader("config.properties");
		Properties prop = new Properties();
		prop.load(reader);
		String environment = System.getProperty("environment");
		String platformName = System.getProperty("platformName");
		platform = getCurrentPlatform();
		fileName = getReportFileLocation(platform);
		htmlReporter = new ExtentSparkReporter(fileName);
		htmlReporter.config().setReportName("pCloudy Hackathon demo from Galaxy - Avengers team");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Build", "1.1");
		if ((platformName.contains("chrome")) || (platformName.contains("CHROME"))) {
			extent.setSystemInfo("Platform_Name", " Chrome ");
		} else if ((platformName.contains("Firefox")) || (platformName.contains("FIREFOX"))) {
			extent.setSystemInfo("Platform_Name", " Firefox ");
		} else {
			extent.setSystemInfo("Platform_Name", " Pcloudy Device ");
		}
		extent.setSystemInfo("User Name", "Gokul");
		htmlReporter.config().setDocumentTitle("Gupshup Extent Report");
		htmlReporter.config().setTheme(Theme.STANDARD);
		return extent;
	}

	/**
	 * Select the extent report file location based on platform
	 * 
	 * @author Gokul S
	 * @param platform
	 * @return file location
	 */
	private static String getReportFileLocation(Platform platform) {
		String reportFileLocation = null;
		switch (platform) {
		case MAC:
			reportFileLocation = macReportFileLoc;
			createReportPath(macPath);
			System.out.println("ExtentReport Path for MAC: " + macPath + "\n");
			break;
		case WINDOWS:
			reportFileLocation = winReportFileLoc;
			createReportPath(windowsPath);
			System.out.println("ExtentReport Path for WINDOWS: " + windowsPath + "\n");
			break;
		case LINUX:
			reportFileLocation = macReportFileLoc;
			createReportPath(macPath);
			System.out.println("ExtentReport Path for Linux: " + macPath + "\n");
			break;
		default:
			System.out.println("ExtentReport path has not been set! There is a problem!\n");
			break;
		}
		return reportFileLocation;
	}

	/**
	 * Create the report path if it does not exist
	 * 
	 * @author Gokul
	 * @param path
	 */
	private static void createReportPath(String path) {
		File testDirectory = new File(path);
		if (!testDirectory.exists()) {
			if (testDirectory.mkdir()) {
				System.out.println("Directory: " + path + " is created!");
			} else {
				System.out.println("Failed to create directory: " + path);
			}
		} else {
			System.out.println("Directory already exists: " + path);
		}
	}

	/**
	 * Get current platform
	 * 
	 * @author Gokul S
	 * @return platformName
	 */
	private static Platform getCurrentPlatform() {
		if (platform == null) {
			String operSys = System.getProperty("os.name").toLowerCase();
			System.out.println(operSys);
			if (operSys.contains("win")) {
				platform = Platform.WINDOWS;
			} else if (operSys.contains("nix") || operSys.contains("nux") || operSys.contains("aix")) {
				platform = Platform.LINUX;
			} else if (operSys.contains("mac")) {
				platform = Platform.MAC;
			}
		}
		return platform;
	}
}