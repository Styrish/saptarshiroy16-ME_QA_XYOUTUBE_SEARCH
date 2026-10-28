package demo;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import demo.utils.ExcelDataProvider;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider { // Lets us read the data
        ChromeDriver driver;

        /*
         * TODO: Write your tests here with testng @Test annotation.
         * Follow `testCase01` `testCase02`... format or what is provided in
         * instructions
         */


        @BeforeTest
        public void startBrowser() {
                System.setProperty("java.util.logging.config.file", "logging.properties");

                // NOT NEEDED FOR SELENIUM MANAGER
                // WebDriverManager.chromedriver().timeout(30).setup();

                ChromeOptions options = new ChromeOptions();
                LoggingPreferences logs = new LoggingPreferences();

                logs.enable(LogType.BROWSER, Level.ALL);
                logs.enable(LogType.DRIVER, Level.ALL);
                options.setCapability("goog:loggingPrefs", logs);
                options.addArguments("--remote-allow-origins=*");

                System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

                driver = new ChromeDriver(options);

                driver.manage().window().maximize();
        }

        @BeforeClass
        public void setup(){
                System.out.println("Constructor: Driver");
                driver = new ChromeDriver();
                driver.manage().window().maximize();
                System.out.println("Driver initialized");
        }

        @BeforeMethod
        public void goToYT() throws InterruptedException {
                Wrappers.goToUrlAndWait(driver, "https://www.youtube.com/");
                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000); // 1000 - 400ms
        }

        @Test(enabled = true)
        public void testCase01() throws InterruptedException {
                System.out.println("Running Test Case 01");

                By aboutLocator = By.xpath("//a[contains(text(),'About')]");
                Wrappers.findElementAndClick(driver, aboutLocator);
                // By youtubeContentLocator = By.xpath("//section[@class='ytabout_content']/p");
                // String content1 = Wrappers.findElementAndPrint(driver, youtubeContentLocator, 0);
                // System.out.println(content1);
                // String content2 = Wrappers.findElementAndPrint(driver, youtubeContentLocator, 1);
                // System.out.println(content2);

                By youtubeContentLocator = By.xpath("//p[@data-cy='pageSubHeading']");
                String content = Wrappers.findElementAndPrint(driver, youtubeContentLocator, 0);
                System.out.println(content);
        }

        @Test(enabled = true)
        public void testCase02() throws InterruptedException {
        System.out.println("Running Test Case 02");

        Wrappers.findElementAndClick(
            driver,
            By.xpath("//a[@title='Movies & TV']")
        );

        Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);

        By rightArrow = By.xpath("//span[@id='title' and contains(text(),'Top selling')]/ancestor::div[@id='dismissible']//div[@id='right-arrow']/ytd-button-renderer");
        Wrappers.clickTillUnclickable(driver, rightArrow);

        By movieList = By.xpath("//span[contains(text(), 'Top selling')]/ancestor::div[@id='dismissible']//ytd-grid-movie-renderer");

        List<List<String>> allMovieDetails =
            Wrappers.getMovieDetails(driver, movieList);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(
            allMovieDetails.isEmpty(),
            "No movies were found in the Top selling section."
        );

        if (!allMovieDetails.isEmpty()) {
            List<String> firstMovie = allMovieDetails.get(0);
            String movieCategory = firstMovie.get(0);
            String movieBadge = firstMovie.get(1);

            softAssert.assertTrue(
                Arrays.asList("Comedy", "Animation", "Drama")
                    .contains(movieCategory),
                "Unexpected movie category: " + movieCategory
            );

            softAssert.assertTrue(
                Arrays.asList("A", "U/A 13+", "U/A 16+")
                    .contains(movieBadge),
                "Unexpected movie badge: " + movieBadge
            );
        }

        // softAssert.assertAll();
    }

        @Test(enabled = true)
        public void testCase03() throws InterruptedException {
                System.out.println("Running Test Case 03");
                Wrappers.findElementAndClick(driver, By.xpath("//*[@title='Music']//tp-yt-paper-item[@role='link']"));
                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);
                By locator_musicDetail = By.xpath("//ytd-rich-section-renderer[3]//div[@id='contents']//ytd-rich-item-renderer[last()]//div[@class='ytBadgeShapeText']");
                int songCount = Wrappers.getMusicDetails(driver, locator_musicDetail);
                SoftAssert softAssert = new SoftAssert();
                softAssert.assertTrue(songCount<=50); // 51
                softAssert.assertAll();
        }

        @Test(enabled = false)
        public void testCase04() throws InterruptedException {
                System.out.println("Running Test Case 04");
                Wrappers.findElementAndClick(driver, By.xpath("//a[@title='News']"));
                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                
                // Wait for the element to be clickable
                WebElement contentCards = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='rich-shelf-header-container' and contains(., 'Latest news posts')]//ancestor::div[1]//div[@id='contents']")));

                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);
                long sumOfVotes = 0;
                for (int i = 0; i<3; i++) { // i =0 , 1, 2
                        System.out.println(Wrappers.findElementAndPrintWE(driver, By.xpath("//div[@id='header']"), contentCards, i));
                        System.out.println(Wrappers.findElementAndPrintWE(driver, By.xpath("//div[@id='body']"), contentCards, i));
                        try {
                             String votes = Wrappers.findElementAndPrintWE(driver, By.xpath("//span[@id='vote-count-middle']"), contentCards, i);
                             // sumOfVotes += Wrappers.converToNumericalValues(votes);
                             sumOfVotes = sumOfVotes + Wrappers.convertToNumericalValue(votes); 
                        }
                        catch(NoSuchElementException | IndexOutOfBoundsException exception){
                                System.out.println("Vote count not present - "+ exception.getMessage());
                        }
                        Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);
                }
                System.out.println("Total votes:" + sumOfVotes); // sum of votes
                System.out.println("Ending Test Case 04");
        }
        @Test(enabled = false, dataProvider = "excelData")
        public void testCase05(String searchWord) throws InterruptedException {
                System.out.println("Running Test Case 05 Flow for: " + searchWord);
                Wrappers.sendKeysWrapper(driver, By.xpath("//input[@id='search']"), searchWord);
                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);
                long tally = 0;
                int iter = 1;

        }
        
        @AfterTest
        public void endTest() {
            if (driver != null){
                driver.close();
                driver.quit();

        }
}
}