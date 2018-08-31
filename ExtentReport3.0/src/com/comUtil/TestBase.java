package com.comUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class TestBase {
	public static AppiumDriver<MobileElement> driver;
	private static final Logger log = Logger.getLogger(TestBase.class.getName());
	public static Properties OR = new Properties();
	public static DesiredCapabilities capabilities;

	static String scrShotDir = "screenshots";
	static File scrShotDirPath = new File("./" + scrShotDir + "//");
	static String destFile;
	
	
	
	
	

	@BeforeClass
	public void setUp() throws IOException {
		// launchAppOnRealDevice();
		launchAppOnEmulator();

	}

	@AfterClass
	public void tearDown() throws InterruptedException {
		closeApp();

	}

	public static void launchAppOnEmulator() throws IOException {

		loadData();
		System.out.println();
		log.info("=================================================================================");
		File appDir = new File("App");
		File app = new File(appDir, "Barrel13.apk");

		// Set the Desired Capabilities
		capabilities = new DesiredCapabilities();

		// Set the Desired Capabilities
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, OR.getProperty("PLATFORM_VERSION"));
		capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 20);
		// capabilities.setCapability(AndroidMobileCapabilityType.NO_SIGN, true);
		capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, true);
		capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, OR.getProperty("APP_PACKAGE_NAME"));
		capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, OR.getProperty("APP_ACTIVITY_NAME"));
		// capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());

		// Instantiate Appium Driver
		try {
			driver = new AndroidDriver<MobileElement>(new URL(OR.getProperty("URL")), capabilities);
			log.info("Driver : " + driver);
			Assert.assertNotNull(driver);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			log.info("App launched successfully.");
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		}
	}

	public static void launchAppOnRealDevice() throws IOException {
		loadData();
		File appDir = new File("App");
		File app = new File(appDir, "Barrel13.apk");

		// Set the Desired Capabilities
		capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "My Phone");
		capabilities.setCapability(MobileCapabilityType.UDID, OR.getProperty("UDID"));
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, OR.getProperty("PLATFORM_VERSION"));
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 20);
		capabilities.setCapability(MobileCapabilityType.NO_RESET, false);

		// capabilities.setCapability(AndroidMobileCapabilityType.NO_SIGN, true);
		capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, true);
		capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, OR.getProperty("APP_PACKAGE_NAME"));
		capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, OR.getProperty("APP_ACTIVITY_NAME"));
		// capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());

		// Instantiate Appium Driver
		try {
			driver = new AndroidDriver<MobileElement>(new URL(OR.getProperty("URL")), capabilities);
			log.info("Driver : " + driver);
			Assert.assertNotNull(driver);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			log.info("App launched successfully.");
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		}
	}

	public static void loadData() throws IOException {
		File file = new File(System.getProperty("user.dir") + "/src/com/pom/comUtil/config.properties");
		FileInputStream fis = new FileInputStream(file);
		OR.load(fis);
		PropertyConfigurator.configure("log4j.properties");
	}

	public static void closeApp() throws InterruptedException {
		if (driver != null) {
			driver.quit();
			log.info("App closed successfully.");

		}

	}

	public static String getToastMessageFromSaveImage() {

		String imgName = takeScreenShotWithSimpleDateFormat();
		String result = null;
		File imageFile = new File(scrShotDirPath, imgName);
		System.out.println("Image name is :" + imageFile.toString());
		ITesseract instance = new Tesseract();
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");
		instance.setDatapath(tessDataFolder.getAbsolutePath());

		try {
			result = instance.doOCR(imageFile);
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String getToastMessageWithoutSaveImage() {

		String result = null;
		File srcFile = driver.getScreenshotAs(OutputType.FILE);
		ITesseract instance = new Tesseract();
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");
		instance.setDatapath(tessDataFolder.getAbsolutePath());

		try {
			result = instance.doOCR(srcFile);
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// Take screen shot and image saved into date format
	public static String takeScreenShotWithSimpleDateFormat() {

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ss_aa");
		// Create folder under project with name "screenshots" if doesn't exist
		new File(scrShotDir).mkdirs();
		// Set file name
		destFile = dateFormat.format(new Date()) + ".png";
		try {
			// Copy
			FileUtils.copyFile(scrFile, new File(scrShotDir + "/" + destFile));
		} catch (IOException e) {
			System.out.println("Image not transfered to screenshot folder");
			e.printStackTrace();
		}
		return destFile;
	}

	// take screen shot and image saved in random name.
	public static String takeScreenShotWithRandomUUID() {

		File targetFile = null;
		try {
			File srcFile = driver.getScreenshotAs(OutputType.FILE);
			String fileName = UUID.randomUUID().toString();
			targetFile = new File("./screenshots/" + fileName + ".png");
			FileUtils.copyFile(srcFile, targetFile);
			System.out.println(targetFile.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return targetFile.toString();

	}

	public static String getToastMessage() throws TesseractException {
		String imgName = takeScreenShotWithSimpleDateFormat();
		String result = null;
		File imageFile = new File(scrShotDirPath, imgName);
		System.out.println("Image name is :" + imageFile.toString());
		ITesseract instance = new Tesseract();
		// Extracts
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");
		// sets tessData
		instance.setDatapath(tessDataFolder.getAbsolutePath());

		try {
			result = instance.doOCR(imageFile);
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void screenRotate() {
		if (driver.getOrientation().equals("LANDSCAPE")) {
			switchToMode("PORTRAIT");
		} else {
			switchToMode("LANDSCAPE");
		}

	}

	public void switchToMode(String modeType) {
		ScreenOrientation currentOrientation = driver.getOrientation();
		log.info("CurrentOrientation : " + currentOrientation);
		if (modeType.equalsIgnoreCase("LANDSCAPE"))
			driver.rotate(ScreenOrientation.LANDSCAPE);
		else if (modeType.equalsIgnoreCase("PORTRAIT")) {
			driver.rotate(ScreenOrientation.PORTRAIT);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		currentOrientation = driver.getOrientation();
		log.info("AfterRotate : " + currentOrientation);
	}

	public static void scrollToMobileElement(String visibleText) {
		driver.findElement(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\""
						+ visibleText + "\").instance(0))"));
	}

}