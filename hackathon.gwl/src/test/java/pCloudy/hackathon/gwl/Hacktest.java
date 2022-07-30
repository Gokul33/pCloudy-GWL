package pCloudy.hackathon.gwl;

import static org.testng.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ssts.pcloudy.exception.ConnectError;

import pCloudy.hackathon.gwl.commonFunctions.CommonFunctions;
import pCloudy.hackathon.gwl.locators.AppLocators;
import pCloudy.hackathon.gwl.utils.DriverBase;
import pCloudy.hackathon.gwl.utils.DriverBase.ElementType;

public class Hacktest {
	SoftAssert asrt = new SoftAssert();
	private DriverBase appiumDB;
	boolean flag = false;
	private CommonFunctions commonFunctions;

	@SuppressWarnings("static-access")
	@BeforeTest
	public void beforeClass() throws Exception {
		appiumDB = new DriverBase();
		commonFunctions = new CommonFunctions(appiumDB);
		appiumDB.pcloudyDeviceInitialize();
	}
	
	@Test(description = "Test to verify Scenario 1")
	public void LaunchTheApp() throws Exception {
		flag = true;
		assertTrue(flag);
	}
	
	/**
	 * Test case to verify Scenario 1 - Double Click
	 * 
	 * @return
	 * @throws Exception
	 */
	public void Scenario1() throws Exception {
		Thread.sleep(5000);
		appiumDB.clickAnElement(AppLocators.LOGIN, ElementType.Xpath);
		Thread.sleep(2000);
		appiumDB.clickAnElement(AppLocators.DBLTAP, ElementType.Text);
		Thread.sleep(3000);
		appiumDB.doubleTap(AppLocators.CLICKHERE);
		appiumDB.clickAnElement(AppLocators.OK, ElementType.Xpath);
		appiumDB.capture("doubleclick");
		appiumDB.clickBackButton();
	}
	
	/**
	 * Test case to verify Scenario 3 - Drag and Drop
	 * 
	 * @return
	 * @throws Exception
	 */
	public void Scenario3() throws Exception {
		appiumDB.clickAnElement(AppLocators.DRAGANDDROP, ElementType.Text);
		//click an element
		Thread.sleep(2000);
		appiumDB.dragAndDrop(AppLocators.HOLDDRAGANDDROP, AppLocators.DROPSPACE, ElementType.AccessibilityId);
		appiumDB.capture("draganddrop");
		appiumDB.clickBackButton();
	}

	@Test(description = "Test to verify Scenario 4")
	public void Scenario4() throws Exception, IOException, ConnectError {
		appiumDB.clickAnElement(AppLocators.LOGIN, ElementType.Xpath);
		appiumDB.clickAnElement("Base Image", ElementType.Text);
		Thread.sleep(3000);
		appiumDB.capture("baseImage");
		Thread.sleep(1000);
		appiumDB.clickBackButton();
		appiumDB.clickAnElement("Seconday Image", ElementType.Text);
		Thread.sleep(3000);
		appiumDB.capture("secondaryImage");
		Thread.sleep(1000);
		appiumDB.compareImage();
	}

	@AfterTest
	public void getResult() throws Exception {
		appiumDB.tearDown();
	}
}
