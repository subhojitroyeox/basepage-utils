package com.eox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SupportUtils {
	
	public static String filePath;
	
	//Read Property file and return the value
	public SupportUtils(String path) {
		SupportUtils.filePath =path;
	}
	private static Properties properties;
	public static String getProperty(String key) {
		properties = new Properties();
        FileInputStream file;
		try {
			file = new FileInputStream(filePath);
			properties.load(file);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}        
        return properties.getProperty(key);
    }
	
	// Helper method to read test data from resource folder

		public static Object getTestData(String filePath, String key) {
	        try {
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode rootNode = objectMapper.readTree(new File("src/test/resources/testdata/" + filePath));

	            if (rootNode.get(key).isArray()) {
	                return objectMapper.convertValue(rootNode.get(key), List.class);
	            }
	            return rootNode.get(key);
	        } catch (Exception e) {
	            throw new RuntimeException("Error reading JSON data: " + e.getMessage());
	        }
	    }
		
		// Generic Method to Extract Nested Values from JSON
	    public static String getJsonValue(JsonNode jsonNode, String keyPath) {
	        try {
	            String[] keys = keyPath.split("\\."); // Split key path by "."
	            JsonNode currentNode = jsonNode;

	            for (String key : keys) {
	                if (currentNode.has(key)) {
	                    currentNode = currentNode.get(key);
	                } else {
	                    throw new RuntimeException("Key not found in JSON: " + keyPath);
	                }
	            }
	            return currentNode.asText(); // Return the extracted value as String
	        } catch (Exception e) {
	            throw new RuntimeException("Error extracting JSON value for key path '" + keyPath + "': " + e.getMessage());
	        }
	    }
	    
	    // Generate Extend report 
	    private static ExtentReports extent;

	    public static ExtentReports getInstance() {
	        if (extent == null) {
	            //String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	            String reportPath = System.getProperty("user.dir") + "/test-output/Functional/Functional_Report.html";
	            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath)
	                    .viewConfigurer()
	                    .viewOrder()
	                    .as(new ViewName[]{ViewName.DASHBOARD, ViewName.TEST})
	                    .apply();
	            spark.config().setDocumentTitle("Automation Test Report");
	            spark.config().setReportName("Selenium Test Execution Report");
	            spark.config().setTheme(Theme.DARK);
	            spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
	            spark.config().setJs(
	            	    "document.addEventListener('DOMContentLoaded', function() {" +
	            	    "document.querySelector('.logo').style.backgroundImage = \"url('LOGO-EOX.png')\";" +
	            	    "});"
	            	);
	            
	            extent = new ExtentReports();
	            extent.attachReporter(spark);
	            extent.setSystemInfo("OS", System.getProperty("os.name"));
	            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
	            extent.setSystemInfo("Tester", "QA Engineer");
	        }
	        return extent;
	    }
	    // Takes screenshot 
	    public static String takeScreenshot(String testName, WebDriver driver) {
	        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String screenshotPath = System.getProperty("user.dir") + "/test-output/screenshots/" + testName + "_" + timestamp + ".png";

	        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	        try {
	            FileUtils.copyFile(src, new File(screenshotPath));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return screenshotPath;
	    }
	    
	    //Retry for unwanted methods 
	    public static void safeClick(WebElement element, WebDriver driver, int maxRetries) {
	        int attempts = 0;
	        while (attempts < maxRetries) {
	            try {
	                element.click(); // Try clicking normally
	                return; // If successful, exit method
	            } catch (ElementClickInterceptedException e) {
	                attempts++;
	                waitFor(200); // Wait before retrying
	            }
	        }
	        // If all retries fail, click using JavaScript
	        System.out.println("Click intercepted, using JavaScript executor...");
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	    }

	    private static void waitFor(int millis) {
	        try {
	            Thread.sleep(millis);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }

}
