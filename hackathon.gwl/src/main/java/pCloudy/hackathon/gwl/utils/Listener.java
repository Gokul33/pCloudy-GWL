package pCloudy.hackathon.gwl.utils;

import java.util.Objects;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * @author Gokul S
 * @company Galaxyweblinks
 * July 02, 2021
 */

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class Listener implements ITestListener {
	public static int Testcase_passed;
	public static int Testcase_failed;
	public static int Testcase_skipped;

	/**
	 * While test is started
	 * 
	 * @author Gokul S
	 */
	@Override
	public synchronized void onStart(ITestContext context) {
		System.out.println("Extent Report started!");
	}

	ExtentReports extent = ExtentManager.getInstance();
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	/**
	 * While test is finished
	 * 
	 * @author Gokul S
	 */
	@Override
	public synchronized void onFinish(ITestContext context) {
		System.out.println(("Extent Report ends!"));
		extent.flush();
	}

	/**
	 * When the test begins
	 * 
	 * @author Gokul S
	 */
	@Override
	public synchronized void onTestStart(ITestResult result) {
		System.out.println(result.getMethod().getMethodName() + " started!");
		ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
		test.set(extentTest);
	}

	/**
	 * While test is passed
	 * 
	 * @author Gokul S
	 */
	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		Testcase_passed++;
		System.out.println((result.getMethod().getMethodName() + " passed!"));
		test.get().log(Status.PASS, result.getMethod().getMethodName() + " passed!");
	}

	/**
	 * While test is failure
	 * 
	 * @author Gokul S
	 */
	@Override
	public synchronized void onTestFailure(ITestResult result) {
		Testcase_failed++;
		WebDriver driver = DriverBase.driver;
		String base64Screenshot = "data:image/png;base64,"
				+ ((TakesScreenshot) Objects.requireNonNull(driver)).getScreenshotAs(OutputType.BASE64);		
		System.out.println((result.getMethod().getMethodName() + " failed!"));
		test.get().fail(result.getThrowable());
		test.get().addScreenCaptureFromBase64String(base64Screenshot).getModel().getMedia().get(0);
	}

	/**
	 * While test is skipped
	 * 
	 * @author Gokul S
	 */
	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		Testcase_skipped++;
		System.out.println((result.getMethod().getMethodName() + " skipped!"));
		test.get().skip(result.getThrowable());
		test.get().log(Status.SKIP, result.getMethod().getDescription());
		extent.flush();
	}

	/**
	 * While test on Test Failed But With in Success Percentage
	 * 
	 * @author Gokul S
	 */
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println(("onTestFailedButWithinSuccessPercentage for " + result.getMethod().getMethodName()));
	}

	/**
	 * Method for adding logs passed from test cases
	 * 
	 * @author Gokul S
	 * @param message
	 */
	public void reportLog(String message) {
		test.get().log(Status.INFO, message);// For extentTest HTML report
		Reporter.log(message);
	}
}