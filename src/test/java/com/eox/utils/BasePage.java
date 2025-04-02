package com.eox.utils;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class BasePage {
	
	public WebDriver driver;
	public WebDriverWait wait;
	
	//This is constructor
	public BasePage(int time) {
		 this.driver = WebDriverUtils.getDriver();
		 this.wait = new WebDriverWait(driver, Duration.ofSeconds(time));
		
	}
	
	// Click method with explicit wait
    public void elementClick(WebElement element) {
    	wait.until(ExpectedConditions.elementToBeClickable(element));
        SupportUtils.safeClick(element,driver,3);
    } 
	

}
