package com.qk.reg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.qk.reg.Headless;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

public class App {
	public static AppiumDriver<WebElement> driver;
	public static List<WebElement> oTests = null;

	public static void main(String[] args) {
		//download latest apk from Gmail - Testfairy
		try {
			Headless.apkDownload();
			
			Runtime run = Runtime.getRuntime();
	
			Process pr = run.exec("cmd.exe /c cd \""
					+ "C:\\ \" && start node \"C:\\Program Files (x86)\\Appium\\node_modules\\appium\\bin\\appium.js\" -a 127.0.0.1 -p 4723");
	
			Thread.sleep(10000);
	
			File path = new File("D://apks/app-debug.apk");
	
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("deviceName", "emulator");// ("deviceName","emulator");
			capabilities.setCapability("appPackage", "com.qk.alertdialogtest");
			capabilities.setCapability("appActivity", "com.qk.alertdialogtest.MainActivity");
			capabilities.setCapability("newCommandTimeout", "300");
			capabilities.setCapability("app", path.getAbsolutePath());
			capabilities.setCapability("no-reset", "true");
			capabilities.setCapability("full-reset", "False");

			driver = new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
			Thread.sleep(5000);

			FluentWait wait;
			wait = new WebDriverWait(driver, 30).withTimeout(10, TimeUnit.MINUTES).pollingEvery(2, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.EditText[@index=0]")));

			WebElement login = driver.findElement(By.xpath("//android.widget.EditText[@index=0]"));
			login.clear();
			login.sendKeys("Srikanta");
			System.out.println("Entered Name Successfully");

			WebElement password = driver.findElement(By.xpath("//android.widget.EditText[@index=1]"));
			password.clear();
			password.sendKeys("test");

			driver.hideKeyboard();
			System.out.println("Entered Password Successfully");

			Thread.sleep(2000);

			driver.findElement(By.xpath("//android.widget.Button[@text='Register']")).click();

			System.out.println("Clicked on Register button");

			wait = new WebDriverWait(driver, 30).withTimeout(10, TimeUnit.MINUTES).pollingEvery(2, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.Button[@index='2']")));

			// exit app button click
			oTests = driver.findElements(By.xpath("//android.widget.Button[@index='2']"));

			if (oTests.size() > 0) {
				driver.findElement(By.xpath("//android.widget.Button[@index='2']")).click();

				/*wait = new WebDriverWait(driver, 30).withTimeout(10, TimeUnit.MINUTES).pollingEvery(2, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/message")));
				Thread.sleep(2000);
				WebElement msg = driver.findElement(By.id("android:id/message"));*/
				
				wait = new WebDriverWait(driver, 30).withTimeout(10, TimeUnit.MINUTES).pollingEvery(2, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@text,'Failed') or contains(@text,'Success')]")));
				Thread.sleep(2000);
				WebElement msg = driver.findElement(By.xpath("//android.widget.TextView[@index='0']"));

				String TextMsg = msg.getText();

				if (TextMsg.contains("Success")) {
					
					System.out.println("Test Case Passed.");
					
				} else {
					System.out.println("Test Case Failed." + TextMsg);
					//throw new Exception("Test Case failed. msg=" + TextMsg);
				}

				driver.findElement(By.xpath("//*[contains(@text,'OK')]")).click();
				
				//System.out.println("Clicked on Exit App button");
				//System.out.println("Test Case Pass.");

			}
			
			driver.removeApp("com.qk.alertdialogtest");

		} catch (Exception e) {
			System.out.println("Test Case failed." + e.getMessage());

		}

		finally {
			driver.quit();

			System.out.println("Execution Completed");

		}

	}

}