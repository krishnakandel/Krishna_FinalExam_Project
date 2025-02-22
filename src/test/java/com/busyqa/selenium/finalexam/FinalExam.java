package com.busyqa.selenium.finalexam;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import Utilities.ExtentReportManager;
import Utilities.LoggerUtility;
import io.github.bonigarcia.wdm.WebDriverManager;

public class FinalExam {

	static WebDriver driver;
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	String excelFilePath = "C:\\Users\\Acer\\eclipse\\finmum.xlsx";
	String screenshotPath;
	
	Workbook workbook = new XSSFWorkbook();
	
	protected static ExtentTest test;
	
	@Test
	public void webDataToExcel() throws InterruptedException, IOException {
		
        test = ExtentReportManager.extent.createTest("Quebec Auction Test - Web Data Copy to Excel Sheets.");
        test.log(Status.INFO, "Navigated to Quebec page");

		driver.get("https://www.finmun.finances.gouv.qc.ca/finmun/f?p=100:3000::RESLT::::");

		// Switch to English
		WebElement englishLink = driver.findElement(By.xpath("//a[contains(text(), 'English')]"));
		englishLink.click();
		
		// Log4j entry
		LoggerUtility.logInfo("Switched to English version.");
		
		// Extent Report entry.
		test.log(Status.INFO, "Page opened successfully.");
		
		// To locate the <tbody> under the table in need.
		List<WebElement> tbodies = driver.findElements(By.xpath("//*[@id='report_BILLETS']/div/div[1]/table//tbody"));

		//for (int i = 1; i <= 1; i++) {
		for (int i = 1; i < 6; i++) {
			
			// Taking screenshot of the opened page.
			screenshotPath = ExtentReportManager.captureScreenshot(driver, "NameLink"); //second argument is just the screenshot name, any string can be passed.
			test.addScreenCaptureFromPath(screenshotPath);
			
			// to find a tags in each <tbody>
			List<WebElement> links = tbodies.get(i).findElements(By.xpath(".//a"));

			System.out.println("The size of links:=>" + links.size());
			//for (int j = 0; j<1; j++) {
			for (int j = 0; j < links.size(); j++) {
				String excelSheetName = links.get(j).getText().trim();

				// Excel sheet name can not be more than 31 characters.
				if (excelSheetName.length() > 31) {
					excelSheetName = excelSheetName.substring(0, 31);// it will extract characters from index 0 up to
																		// (but not including) index 31
				}
				//System.out.println("SheetName==>" + excelSheetName);

				links.get(j).click();
				WebElement iframe = driver.findElement(By.tagName("iframe"));
				driver.switchTo().frame(iframe);
				
				// Log4j entry
				LoggerUtility.logInfo("Switched to iframe.");
			
				screenshotPath = ExtentReportManager.captureScreenshot(driver, excelSheetName); //second argument is just the screenshot name, any string can be passed.
				test.addScreenCaptureFromPath(screenshotPath);
				
	           // test.fail("Test Failed. Screenshot below: " + test.addScreenCaptureFromPath(screenshotPath));
				// Assert.fail("Element not found!");
				// Now extract data from the tables
				
				WebElement iframeTable = driver
						.findElement(By.xpath("//*[@id='R1469412186955323305']/div[2]/div[2]/table[2]")); // Adjust the
																											// table
				List<WebElement> iframeTableRows = iframeTable.findElements(By.xpath(".//tr"));

				// The following line of code works.
				// System.out.println(iframeTableRows.size() + ":" + iframeTable.getText());
				Sheet sheet = workbook.createSheet(excelSheetName);
				for (int k = 0; k < iframeTableRows.size(); k++) {
					List<WebElement> cols = iframeTableRows.get(k).findElements(By.xpath(".//td"));

					Row excelRow = sheet.createRow(k);
					// System.out.println("Table Data");
					for (int m = 0; m < cols.size(); m++) {
						//System.out.print(cols.get(m).getText() + "   ");//to print the table values in console.
						
						Cell cell = excelRow.createCell(m);
						cell.setCellValue(cols.get(m).getText());
					}
				}
			
				driver.switchTo().defaultContent(); // close button of the popup was not in the iframe and it is in main
													// page, so before clicking the button, it is required to go back to
													// the default content.
				
				// Log4j entry
				LoggerUtility.logInfo("Switched back to Default Content.");

				WebElement closeButton = driver.findElement(By.xpath("//button[text()='Fermer']"));
				closeButton.click();
			}
			
			//Extent report entry.  
			test.log(Status.PASS, "Successfully completed all the data copy to Excel and taking screen shots.");
		}

		try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
			workbook.write(outputStream);
			workbook.close();
			System.out.println("Excel file written successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void beforeClass() throws InterruptedException {
		WebDriverManager.chromedriver().setup();
		
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		Thread.sleep(2000);
		
		
		//The screenshots were blank while running from Jenkins. Then Above segment was replaced with the following segment and the content of the screenshot was seen.
	/*
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new"); // Ensures proper rendering
		options.addArguments("--window-size=1920,1080"); // Prevents blank screenshots
		options.addArguments("--disable-gpu"); // Useful for headless mode
		options.addArguments("--no-sandbox"); // Fixes issues in Jenkins
		options.addArguments("--disable-dev-shm-usage"); // Fixes crashes in Docker
		driver = new ChromeDriver(options);
		*/
		
	}

	@AfterClass
	public static void afterClass() {
		driver.quit();
	}
	
    @BeforeSuite
    public void beforeSuite() {
        ExtentReportManager.initReport();
    }

    @AfterSuite
    public void afterSuite() {
        ExtentReportManager.flushReport();
    }
}

