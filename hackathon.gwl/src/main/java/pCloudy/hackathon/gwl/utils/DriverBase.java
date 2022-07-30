package pCloudy.hackathon.gwl.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.http.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.ssts.pcloudy.Connector;
import com.ssts.pcloudy.Version;
import com.ssts.pcloudy.dto.device.MobileDevice;
import com.ssts.pcloudy.exception.ConnectError;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidTouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

@SuppressWarnings({ "rawtypes" })
public class DriverBase extends Listener {
	public static AndroidDriver<MobileElement> driver;
	protected static WebDriverWait wait;
	static Boolean flag = true;
	public static JSONObject requestParams;
	public static RequestSpecification request;
	public static JsonPath jsonPathEvaluator;
	public static String access_Token;
	public static String version;

	public enum ElementType {
		Id, CssSelector, ClassName, Name, LinkText, Xpath, Text, PartialLinkText, AccessibilityId
	}

	/**
	 * Verify Pcloudy device Initialize
	 * 
	 * @return
	 * @throws Exception
	 * @throws InvalidFormatException
	 */
	public void pcloudyDeviceInitialize() throws Exception {
		//Declare method
		
		connectTopCloudyUsingAPI("TokenGenerate");
		//call the method with parameter
		
		connectTopCloudyUsingAPI("GetDevice");
		//call the method with parameter
		
	}
	
	/**
	 * This method is to connect to pCloudy Using API
	 * 
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws ParseException
	 */
	public static void connectTopCloudyUsingAPI(String filename) throws FileNotFoundException, IOException, ParseException {
		String deviceName = null;
		String deviceversion = null;
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			// Declaring capabilities value

			JSONParser jsonparser = new JSONParser();
			// parse the json file

			JSONObject parallel_config = (JSONObject) jsonparser
					.parse(new FileReader(System.getProperty("user.dir") + File.separator + "parallel.conf.json"));
			// Extract the json object from file parallel.conf in project

			@SuppressWarnings("unchecked")
			Map<String, String> commonCapabilities = (Map<String, String>) parallel_config.get("capabilities");
			// pass the capabilities in Map

			String pcloudy_username = commonCapabilities.get("pCloudy_Username");
			// Extract the pcloudy username from capabilities in json and store in string

			String pcloudy_apikey = commonCapabilities.get("pCloudy_ApiKey");
			// Extract the pcloudy apikey from capabilities in json and store in string

			System.out.println(filename);

			JSONParser parser = new JSONParser();
			// parse the json file

			JSONObject config = (JSONObject) parser.parse(new FileReader(
					System.getProperty("user.dir") + File.separator + "payload" + File.separator + filename + ".json"));
			// Extract the json object from filename in project

			@SuppressWarnings("unchecked")
			Map<String, String> requestformParams = (Map<String, String>) config.get("payload");
			// pass the capabilities in Map

			if (filename == "TokenGenerate") {
				// Allow only file name equals TokenGenerate

				request = RestAssured.given().auth().preemptive().basic(pcloudy_username, pcloudy_apikey);
				// pass authorization in request

				RestAssured.baseURI = config.get("baseurl").toString();
				// get the baseurl from file

				System.out.println(RestAssured.baseURI);

				Response response = request.get(RestAssured.baseURI);
				// get the response

				ResponseBody body = response.getBody();
				// get the body

				String bodyAsString = body.asString();
				// convert to string

				Reporter.log(bodyAsString);
				// Reporter log body string value

				System.out.println(bodyAsString);

				jsonPathEvaluator = response.jsonPath();
				// take response and stored in jsonpathevaluator

				access_Token = jsonPathEvaluator.get("result.token").toString();
				// extract the access token

				System.out.println(access_Token);

				flag = access_Token != null;
				// get flag value access token not equal to null

			} else if (filename == "GetDevice") {
				// Allow only file name equal getdevice

				System.out.println("getdevice");

				RestAssured.baseURI = config.get("baseurl").toString();
				// get the baseurl from file

				request = RestAssured.given();
				// request given

				request.header("Content-type", "application/json");
				// pass content type json

				requestformParams.put("token", access_Token);
				// pass the token value to payload

				request.body(((JSONObject) requestformParams).toJSONString());
				// convert json to string

				Response response = request.post(RestAssured.baseURI);
				// pass the request type post

				ResponseBody body = response.getBody();
				// get body from json

				String bodyAsString = body.asString();
				// get the body as string

				Reporter.log(bodyAsString);

				System.out.println(bodyAsString);

				JSONParser parser1 = new JSONParser();
				// parse the json

				JSONObject config1 = (JSONObject) parser1.parse(bodyAsString);
				// extract json object from body

				JSONObject config2 = (JSONObject) config1.get("result");
				// Extract result from json

				JSONArray mobiledetails = (JSONArray) config2.get("models");
				// declare json array as mobile detail

				System.out.println(mobiledetails.size());
				// print number of device available

				Map<String, String> devicelist = new HashMap<String, String>();
				// declare map devicelist

				int j = 0;
				// initialise j=0

				for (int i = 0; i < mobiledetails.size(); i++) {
					// declare the for loop

					@SuppressWarnings("unchecked")
					Map<String, String> currentmobiledetail = (Map<String, String>) mobiledetails.get(i);
					// declare the map as current mobile detail

					version = currentmobiledetail.get("version");
					// store version

					if ((version.contains("11")) || (version.contains("12"))) {
						// allow only if version contain 11 & 12

						System.out.println(j);
						// print the number of device

						String fullname = currentmobiledetail.get("full_name");
						// store the full name

						version = currentmobiledetail.get("version");
						// store the vaersion

						devicelist.put("fullname" + j, fullname);
						// store the fullname in devicelist

						devicelist.put("version" + j, version);
						// store the version in devicelist

						System.out.println(fullname);

						System.out.println("version" + version);

						j++;
						// increment j

					}
				}
				for (int k = 0; k < j; k++) {
					// Declare the for loop k

					String devicebrand = devicelist.get("fullname" + k);
					// get the device brand

					if (devicebrand.contains("Samsung")) {
						// if devicebrand contain samsung

						deviceName = devicelist.get("fullname" + k);
						// get the fullname as samsung

						deviceversion = devicelist.get("version" + k);
						// get the version

						System.out.println(deviceName);

						System.out.println(deviceversion);

						break;
						// break the statement

					} else {
						// if Samsung is not available it will select the last available device

						if (k == j - 1) {
							// last condition in for loop (j-1)

							System.out.println("Specified Device not found, selecting the available device");
							// print the statement

							deviceName = devicelist.get("fullname" + k);
							// get the device name

							deviceversion = devicelist.get("version" + k);
							// get the device version

							System.out.println("last device name" + deviceName);
							System.out.println("last device version" + deviceversion);
							break;
							// break the statement
						} else
							// if not last condition enter else part
							continue;
						// continue the statement
					}
				}

				Iterator it = commonCapabilities.entrySet().iterator();
				// pass the capabilities in iteration

				while (it.hasNext()) {
					// While loop started

					Map.Entry pair = (Map.Entry) it.next();
					// pass the iteration value to map pair

					if (capabilities.getCapability(pair.getKey().toString()) == null) {
						// it allows only if pair key value is null

						capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
						// pass the capabilities key and value

					}
				}

				capabilities.setCapability("pCloudy_DeviceFullName", deviceName);
				// set cappabilities final device name

				capabilities.setCapability("pCloudy_DeviceVersion", deviceversion);
				// set the capabilities final device version

				capabilities.setCapability("unlockKey", "pin");
				// set the capabilities for unlock key pin

				String URL = "https://device.pcloudy.com/appiumcloud/wd/hub";
				// store pcloudy url

				System.out.println(capabilities);
				// print the capabilities

				driver = new AndroidDriver<MobileElement>(new URL(URL), capabilities);
				// pass the capabilities & url in driver

				String waitTime = "15";
				// initialize wait time
				driver.manage().timeouts().implicitlyWait(Long.parseLong(waitTime), TimeUnit.SECONDS);
				// implicit wait
			}
		} catch (Exception e) {
			// catch block

			e.printStackTrace();
			// print exception

		}
	}


	/**
	 * Quit the browser
	 * 
	 * @author Gokul S
	 * @throws Exception
	 */
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	/**
	 * This method is to double click
	 * 
	 * @param locator
	 * @throws InterruptedException
	 */
	public void doubleTap(String locator) throws InterruptedException {
		Thread.sleep(2000);
		MobileElement element = (MobileElement) new WebDriverWait(driver, 30)
				.until(ExpectedConditions.elementToBeClickable(MobileBy.xpath(locator)));
		Thread.sleep(1000);
		Point source = element.getCenter();
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		Sequence tap = new Sequence(finger, 1);
		tap.addAction(
				finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), source.x, source.y));
		tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		tap.addAction(new Pause(finger, Duration.ofMillis(200)));
		tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		tap.addAction(new Pause(finger, Duration.ofMillis(40)));
		tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		tap.addAction(new Pause(finger, Duration.ofMillis(200)));
		tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Arrays.asList(tap));
		Thread.sleep(2000);
	}

	/**
	 * Quit the browser
	 * 
	 * @author Gokul S
	 * @throws ConnectError 
	 * @throws IOException 
	 * @throws Exception
	 */
	public void compareImage() throws IOException, ConnectError {
		Connector con = new Connector("https://device.pcloudy.com/api/");
		//To get the Access key - Login to pCloudy platform->Go to Profile and click on Settings->Copy the access key
		String authToken = con.authenticateUser("karthickrajan.k@galaxyweblinks.in", "xh7pzsspr9gjcqscfp7fdtbp");
		File fileToBeUploaded = new File(System.getProperty("user.dir") +  File.separator + "Screenshots" +  File.separator);
		String baseImage = con.getImageId(authToken, fileToBeUploaded);
		String secondaryImage = con.getImageId(authToken, fileToBeUploaded);
		Map< String, Object> params = new HashMap<>();
		//Declare Image ID of Base image
		params.put("baseImageId", baseImage);
		//declare Image ID of second image
		params.put("secondImageId", secondaryImage);
		//Find the difference between two image
		String base64=(String) driver.executeScript("mobile:visual:imageDiff",params);
		//Enter path
		File imgFile = new File("diff.png");
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(org.apache.commons.codec.binary.Base64.decodeBase64(base64)));
		ImageIO.write(img, "png", imgFile);
		
		//Compare text
		Map< String, Object> newParams = new HashMap<>();
		System.out.println(driver.executeScript("mobile:ocr:text",newParams));
		Map< String, Object> imgparams = new HashMap<>();
		params.put("imageId", baseImage);
		params.put("word", "cleartrip");
		System.out.println(driver.executeScript("mobile:ocr:textExists",imgparams)); 
		Map< String, Object> compParams = new HashMap<>();
		params.put("imageId", baseImage);
		params.put("word", "Titans");
		System.out.println(driver.executeScript("mobile:ocr:coordinate",compParams));
		
	}
	
	/**
	 * click an element
	 * 
	 * @author Gokul S
	 * @param element
	 * @param elementType
	 * @return boolean - element present/not
	 */
	public Boolean clickAnElement(String element, ElementType elementType) {
		try {
			switch (elementType) {
			case Id: {
				driver.findElementById(element).click();
			}

			case Name: {
				driver.findElementByName(element).click();
				break;
			}

			case Xpath: {
				driver.findElementByXPath(element).click();
				break;
			}

			case CssSelector: {
				driver.findElementByCssSelector(element).click();
				break;
			}

			case ClassName: {
				driver.findElementByClassName(element).click();
				break;
			}

			case LinkText: {
				driver.findElementByLinkText(element).click();
				break;
			}

			case Text: {
				driver.findElementByAndroidUIAutomator("new UiSelector().text(\"" + element + "\")").click();
				break;
			}

			case AccessibilityId: {
				driver.findElementByAccessibilityId(element).click();
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println(element);
			return false;
		}
		return true;
	}

	/**
	 * Enter Text
	 * 
	 * @author Gokul S
	 * @param element
	 * @param value
	 * @param elementType
	 * @return boolean - element present/not
	 */
	public Boolean sendTextToAnElement(String element, String value, ElementType elementType) {
		try {
			switch (elementType) {
			case Id: {
				driver.findElementById(element).sendKeys(value);
				break;
			}

			case Name: {
				driver.findElementByName(element).sendKeys(value);
				break;
			}

			case Xpath: {
				driver.findElementByXPath(element).sendKeys(value);
				break;
			}

			case CssSelector: {
				driver.findElementByCssSelector(element).sendKeys(value);
				break;
			}

			case ClassName: {
				driver.findElementByClassName(element).sendKeys(value);
				break;
			}

			case LinkText: {
				driver.findElementByLinkText(element).sendKeys(value);
				break;
			}

			case AccessibilityId: {
				driver.findElementByAccessibilityId(element).sendKeys(value);
				;
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println(element);
			return false;
		}
		return true;
	}

	/**
	 * Scroll To Element
	 * 
	 * @author Gokul S
	 * @param element
	 */
	public void scrollToAnElementByText(String element) {
		try {
			driver.findElementByAndroidUIAutomator(
					"new UiScrollable(new UiSelector()).scrollIntoView(text(\"" + element + "\"))");
		} catch (Exception e) {
			System.out.println(element);
		}
	}

	/**
	 * Double click on the Element
	 * 
	 * @author Gokul S
	 * @param element
	 * @param elementtype
	 */
	public void doubleClick(String element, ElementType elementtype) {
		Actions action = new Actions(driver);
		try {
			if (elementtype == ElementType.Id) {
				WebElement webElement = driver.findElementById(element);
				action.doubleClick(webElement).perform();
			} else if (elementtype == ElementType.Name) {
				WebElement webElement = driver.findElementByName(element);
				action.doubleClick(webElement).perform();
			} else if (elementtype == ElementType.Xpath) {
				WebElement webElement = driver.findElementByXPath(element);
				action.doubleClick(webElement).perform();
			} else if (elementtype == ElementType.CssSelector) {
				WebElement webElement = driver.findElementByCssSelector(element);
				action.doubleClick(webElement).perform();
			} else if (elementtype == ElementType.ClassName) {
				WebElement webElement = driver.findElementByClassName(element);
				action.doubleClick(webElement).perform();
			} else if (elementtype == ElementType.LinkText) {
				WebElement webElement = driver.findElementByLinkText(element);
				action.doubleClick(webElement).perform();
			} else if (elementtype == ElementType.Text) {
				WebElement element1 = driver
						.findElementByAndroidUIAutomator("new UiSelector().text(\"" + element + "\")");
				action.doubleClick(element1).perform();
			} else if (elementtype == ElementType.AccessibilityId) {
				WebElement element1 = driver.findElementByAccessibilityId(element);
				action.doubleClick(element1).perform();
			}
		} catch (Exception e) {
			System.out.println(element);
		}
	}

	/**
	 * Press Tab Key
	 * 
	 * @author Gokul S
	 */
	public void pressTabKey() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.TAB).build().perform();
	}

	/**
	 * Will return the Text
	 * 
	 * @author Gokul S
	 * @param element
	 * @param elementtype
	 * @return string
	 */
	public String getText(String element, ElementType elementtype) {
		String temp = null;
		try {
			if (elementtype == ElementType.Id)
				temp = driver.findElementById(element).getText();
			else if (elementtype == ElementType.Name)
				temp = driver.findElementByName(element).getText();
			else if (elementtype == ElementType.Xpath)
				temp = driver.findElementByXPath(element).getText();
			else if (elementtype == ElementType.CssSelector)
				temp = driver.findElementByCssSelector(element).getText();
			else if (elementtype == ElementType.ClassName)
				temp = driver.findElementByClassName(element).getText();
			else if (elementtype == ElementType.AccessibilityId)
				temp = driver.findElementByAccessibilityId(element).getText();
		} catch (Exception e) {
			System.out.println(element);
		}
		return temp;
	}

	/**
	 * Clear text area
	 * 
	 * @author Gokul S
	 */
	public void clearTextAreaById(String element, ElementType elementtype) {
		try {
			if (elementtype == ElementType.Id)
				driver.findElementById(element).clear();
			else if (elementtype == ElementType.Name)
				driver.findElementByName(element).clear();
			else if (elementtype == ElementType.Xpath)
				driver.findElementByXPath(element).clear();
			else if (elementtype == ElementType.CssSelector)
				driver.findElementByCssSelector(element).clear();
			else if (elementtype == ElementType.ClassName)
				driver.findElementByClassName(element).clear();
			else if (elementtype == ElementType.AccessibilityId)
				driver.findElementByAccessibilityId(element).clear();
		} catch (Exception e) {
			System.out.println(element);
		}
	}

	/**
	 * Press Escape Key
	 * 
	 * @author Gokul S
	 */
	public void pressEscapeKey() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.ESCAPE).build().perform();
	}

	/**
	 * Press Enter Key
	 * 
	 * @author Gokul S
	 */
	public void pressEnterKey() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.ENTER).build().perform();
	}

	/**
	 * Press downarrow Key
	 * 
	 * @author Gokul S
	 */
	public void pressDownArrowKey() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.ARROW_DOWN).build().perform();
	}

	/**
	 * Press Refresh Key
	 * 
	 */
	public void pressRefreshKey() {
		Actions builder = new Actions(driver);
		builder.keyDown(Keys.CONTROL).sendKeys(Keys.F5).keyUp(Keys.CONTROL).perform();
	}

	/**
	 * Press uparrow Key
	 * 
	 * @author Gokul S
	 */
	public void pressUpArrowKey() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.ARROW_UP).build().perform();
	}

	/**
	 * Press downarrow Key
	 * 
	 * @author Gokul S
	 */
	public void pressRightArrowKey() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.ARROW_RIGHT).build().perform();
	}

	/**
	 * Press Back Space
	 * 
	 * @author Gokul S
	 */
	public void pressBackSpace() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.BACK_SPACE).build().perform();
	}

	/**
	 * Press End Key
	 * 
	 * @author Gokul S
	 */
	public void pressEndKey() {
		Actions builder = new Actions(driver);
		builder.sendKeys(Keys.END).build().perform();
	}

	/**
	 * Move to Element
	 * 
	 * @author Praveen Kumar R
	 * @throws InterruptedException
	 */
	public void moveToElement(String element, ElementType elementType) throws InterruptedException {
		switch (elementType) {
		case Id: {
			Actions action = new Actions(driver);
			WebElement Element = driver.findElementById(element);
			action.moveToElement(Element).perform();
			Thread.sleep(2500);
			break;
		}

		case Name: {
			Actions action = new Actions(driver);
			WebElement Element = driver.findElementByName(element);
			action.moveToElement(Element).perform();
			Thread.sleep(2500);
			break;
		}

		case Xpath: {
			Actions action = new Actions(driver);
			WebElement Element = driver.findElementByXPath(element);
			action.moveToElement(Element).perform();
			Thread.sleep(2500);
			break;
		}

		case CssSelector: {
			Actions action = new Actions(driver);
			WebElement Element = driver.findElementByCssSelector(element);
			action.moveToElement(Element).perform();
			Thread.sleep(2500);
			break;
		}

		case ClassName: {
			Actions action = new Actions(driver);
			WebElement Element = driver.findElementByClassName(element);
			action.moveToElement(Element).perform();
			Thread.sleep(2500);
			break;
		}

		case LinkText: {
			Actions action = new Actions(driver);
			WebElement Element = driver.findElementByLinkText(element);
			action.moveToElement(Element).perform();
			Thread.sleep(2500);
			break;
		}

		case AccessibilityId: {
			Actions action = new Actions(driver);
			WebElement Element = driver.findElementByAccessibilityId(element);
			action.moveToElement(Element).perform();
			Thread.sleep(2500);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * Capture screenshot
	 * 
	 * @author Gokul S
	 * @param screenShotName
	 * @return
	 * @throws IOException
	 */
	public String capture(String screenShotName) throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		System.out.println(System.getProperty("user.dir"));
		String dest = System.getProperty("user.dir") +  File.separator + "Screenshots" +  File.separator  + screenShotName + ".png";
		File destination = new File(dest);
		FileUtils.copyFile(source, destination);
		return dest;
	}
	
	/**
	 * Get the screenshot
	 * 
	 * @param name
	 * @throws IOException
	 */
	public void getScreenshot(String name) throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		System.out.println(System.getProperty("user.dir"));
		File newFile = null;
		try {
			FileUtils.copyFile(scrFile,
					newFile = new File(System.getProperty("user.dir")+  File.separator + "Screenshots" +  File.separator  + name + ".png"));
			Reporter.log("<a href='" + newFile + "'> " + "<img src='" + newFile + "' height='250' width='175'/> </a>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Switch Frame by Index
	 * 
	 * @author Gokul S
	 * @throws InterruptedException
	 */
	public void switchFrame(int i) throws InterruptedException {
		driver.switchTo().frame(i);
		Thread.sleep(3000);
	}
	
	/**
	 * Click Back Button
	 * 
	 * @author Gokul S
	 */
	public void clickBackButton() {
		driver.navigate().back();
	}

	/**
	 * Switch to Child Window
	 * 
	 * @author Gokul S
	 * @param element
	 * @throws InterruptedException
	 */
	public void switchToChildWindow() throws InterruptedException {
		driver.switchTo().defaultContent();
		Thread.sleep(5000);
		String MainWindow = driver.getWindowHandle();
		Set<String> s1 = driver.getWindowHandles();
		Iterator<String> i1 = s1.iterator();
		while (i1.hasNext()) {
			String ChildWindow = i1.next();
			if (!MainWindow.equalsIgnoreCase(ChildWindow)) {
				driver.switchTo().window(ChildWindow);
				Thread.sleep(5000);
				break;
			}
		}
	}

	/**
	 * Verify the Check box Selected or not
	 * 
	 * @author Gokul S
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unused")
	public boolean checkboxSelected(String element) {
		WebElement checked = driver.findElementById(element);
		boolean isSelected = checked.isSelected();
		return flag;
	}

	/**
	 * Select the value from dropdown by using option value
	 * 
	 * @author Gokul S
	 * @param element
	 * @param value
	 * @return
	 */
	public boolean selectDropDownOptionsById(String element, String value) {
		try {
			Select dropdown = new Select(driver.findElementById(element));
			dropdown.selectByValue(value);
		} catch (Exception e) {
			Reporter.log("Element is not found in the page " + element + " Exception " + e, true);
			return false;
		}
		return true;
	}

	/**
	 * Clear text
	 * 
	 * @author Gokul S
	 * @param element
	 */
	public void clearText(String element) {
		WebElement toClear = driver.findElementById(element);
		toClear.sendKeys(Keys.CONTROL + "a");
		toClear.sendKeys(Keys.DELETE);
	}

	/**
	 * Verify Element is Displayed
	 * 
	 * @return
	 * 
	 * @author Praveen Kumar R
	 */
	public boolean verifyElementIsDisplayed(String element, ElementType elementType) {
		try {
			switch (elementType) {
			case Id: {
				flag = driver.findElementById(element).isDisplayed();
				break;
			}

			case Name: {
				flag = driver.findElementByName(element).isDisplayed();
				break;
			}

			case Xpath: {
				flag = driver.findElementByXPath(element).isDisplayed();
				break;
			}

			case CssSelector: {
				flag = driver.findElementByCssSelector(element).isDisplayed();
				break;
			}

			case ClassName: {
				flag = driver.findElementByClassName(element).isDisplayed();
				break;
			}

			case LinkText: {
				flag = driver.findElementByLinkText(element).isDisplayed();
				break;
			}

			case AccessibilityId: {
				flag = driver.findElementByAccessibilityId(element).isDisplayed();
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return flag;
	}

	/**
	 * Select the text from dropdown by using text
	 * 
	 * @author Praveen Kumar R
	 * @param element
	 * @param value
	 * @return
	 */
	public boolean selectDropDownOptionsByText(String element, String value, ElementType elementtype) {
		try {
			switch (elementtype) {
			case Id: {
				Select dropdown = new Select(driver.findElementById(element));
				dropdown.selectByVisibleText(value);
				break;
			}

			case Name: {
				Select dropdown = new Select(driver.findElementByName(element));
				dropdown.selectByVisibleText(value);
				break;
			}

			case Xpath: {
				Select dropdown = new Select(driver.findElementByXPath(element));
				dropdown.selectByVisibleText(value);
				break;
			}

			case CssSelector: {
				Select dropdown = new Select(driver.findElementByCssSelector(element));
				dropdown.selectByVisibleText(value);
				break;
			}

			case ClassName: {
				Select dropdown = new Select(driver.findElementByClassName(element));
				dropdown.selectByVisibleText(value);
				break;
			}

			case LinkText: {
				Select dropdown = new Select(driver.findElementByLinkText(element));
				dropdown.selectByVisibleText(value);
				break;
			}

			case AccessibilityId: {
				Select dropdown = new Select(driver.findElementByAccessibilityId(element));
				dropdown.selectByVisibleText(value);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * long press
	 * 
	 * @author Deepan Shanmugarajan M
	 * @param element
	 * @param value
	 * @return
	 */
	public boolean longPress(String element, ElementType elementtype) {
		try {
			switch (elementtype) {
			case Id: {
				MobileElement element1 = driver.findElementById(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3))).perform();
				break;
			}

			case Name: {
				MobileElement element1 = driver.findElementByName(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3))).perform();
				break;
			}

			case Xpath: {
				MobileElement element1 = driver.findElementByXPath(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3))).perform();
				break;
			}

			case CssSelector: {
				MobileElement element1 = driver.findElementByCssSelector(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3))).perform();
				break;
			}

			case ClassName: {
				MobileElement element1 = driver.findElementByClassName(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3))).perform();
				break;
			}

			case LinkText: {
				MobileElement element1 = driver.findElementByLinkText(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3))).perform();
				break;
			}

			case AccessibilityId: {
				MobileElement element1 = driver.findElementByAccessibilityId(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3))).perform();
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Drag And Drop
	 * 
	 * @author Deepan Shanmugarajan M
	 * @param element
	 * @param value
	 * @return
	 */
	public boolean dragAndDrop(String element, String targetelement, ElementType elementtype) {
		try {
			switch (elementtype) {
			case Id: {
				MobileElement element1 = driver.findElementById(element);
				MobileElement element2 = driver.findElementById(targetelement);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
						.moveTo(ElementOption.element(element2)).release().perform();
				break;
			}

			case Name: {
				MobileElement element1 = driver.findElementByName(element);
				MobileElement element2 = driver.findElementByName(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
						.moveTo(ElementOption.element(element2)).release().perform();
				break;
			}

			case Xpath: {
				MobileElement element1 = driver.findElementByXPath(element);
				MobileElement element2 = driver.findElementByXPath(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
						.moveTo(ElementOption.element(element2)).release().perform();
				break;
			}

			case CssSelector: {
				MobileElement element1 = driver.findElementByCssSelector(element);
				MobileElement element2 = driver.findElementByCssSelector(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
						.moveTo(ElementOption.element(element2)).release().perform();
				break;
			}

			case ClassName: {
				MobileElement element1 = driver.findElementByClassName(element);
				MobileElement element2 = driver.findElementByClassName(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
						.moveTo(ElementOption.element(element2)).release().perform();
				break;
			}

			case LinkText: {
				MobileElement element1 = driver.findElementByLinkText(element);
				MobileElement element2 = driver.findElementByLinkText(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
						.moveTo(ElementOption.element(element2)).release().perform();
				break;
			}

			case AccessibilityId: {
				MobileElement element1 = driver.findElementByAccessibilityId(element);
				MobileElement element2 = driver.findElementByAccessibilityId(element);
				new AndroidTouchAction(driver)
						.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element1)))
						.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
						.moveTo(ElementOption.element(element2)).release().perform();
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Tap an element
	 * 
	 * @author Deepan Shanmugarajan M
	 * @param element
	 * @param value
	 * @return
	 */
	public boolean tapAnElement(String element, ElementType elementtype) {
		try {
			switch (elementtype) {
			case Id: {
				MobileElement element1 = driver.findElementById(element);
				new AndroidTouchAction(driver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element1)))
						.perform();
				break;
			}

			case Name: {
				MobileElement element1 = driver.findElementByName(element);
				new AndroidTouchAction(driver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element1)))
						.perform();
				break;
			}

			case Xpath: {
				MobileElement element1 = driver.findElementByXPath(element);
				new AndroidTouchAction(driver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element1)))
						.perform();
				break;
			}

			case CssSelector: {
				MobileElement element1 = driver.findElementByCssSelector(element);
				new AndroidTouchAction(driver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element1)))
						.perform();
				break;
			}

			case ClassName: {
				MobileElement element1 = driver.findElementByClassName(element);
				new AndroidTouchAction(driver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element1)))
						.perform();
				break;
			}

			case LinkText: {
				MobileElement element1 = driver.findElementByLinkText(element);
				new AndroidTouchAction(driver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element1)))
						.perform();
				break;
			}

			case AccessibilityId: {
				MobileElement element1 = driver.findElementByAccessibilityId(element);
				new AndroidTouchAction(driver).tap(TapOptions.tapOptions().withElement(ElementOption.element(element1)))
						.perform();
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Wait for an Element to be visible
	 * 
	 * @author Gokul S
	 * @param element
	 * @param elementType
	 * @return boolean - element present/not
	 */
	public Boolean waitForElement(String element, ElementType elementType) {
		try {
			switch (elementType) {
			case Id: {
				flag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(element))).isDisplayed();
				break;
			}

			case Name: {
				flag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(element))).isDisplayed();
				break;
			}

			case Xpath: {
				flag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(element))).isDisplayed();
				break;
			}

			case CssSelector: {
				flag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(element))).isDisplayed();
				break;
			}

			case ClassName: {
				flag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(element))).isDisplayed();
				break;
			}

			case LinkText: {
				flag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(element))).isDisplayed();
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			reportLog("Element is not found in the page " + element + " Exception " + e);
			Reporter.log("Element is not found in the page " + element + " Exception " + e, true);
			return false;
		}
		return flag;
	}
}
