package demo.wrappers;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public final class Wrappers {
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private Wrappers() {
    }

    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, TIMEOUT);
    }

    public static void goToUrlAndWait(WebDriver driver, String url) {
        driver.get(url);

        wait(driver).until(webDriver -> {
            String state = ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState")
                .toString();

            return state.equals("interactive") || state.equals("complete");

    });
    }

    public static void findElementAndClick(WebDriver driver, By locator) {
        wait(driver).until(
            ExpectedConditions.elementToBeClickable(locator)
        ).click();
    }

    public static List<WebElement> findElements(
            WebDriver driver, By locator) {

        return wait(driver).until(
            ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
        );
    }

    public static String findElementAndPrint(
            WebDriver driver, By locator, int index) {

        List<WebElement> elements = findElements(driver, locator);

        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException(
                "Invalid index: " + index
                    + ", elements found: " + elements.size()
            );
        }

        String text = elements.get(index).getText();
        System.out.println(text);
        return text;
    }

    public static String findElementAndPrintWE(
            WebDriver driver,
            By locator,
            WebElement parent,
            int index) {

        List<WebElement> elements = parent.findElements(locator);

        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException(
                "Invalid index: " + index
                    + ", elements found: " + elements.size()
            );
        }

        String text = elements.get(index).getText();
        System.out.println(text);
        return text;
    }

    public static void scrollToElement(WebDriver driver, By locator) {
        WebElement element = wait(driver).until(
            ExpectedConditions.presenceOfElementLocated(locator)
        );

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            element
        );
    }

    public static void javascriptClick(WebDriver driver, By locator) {
        WebElement element = wait(driver).until(
            ExpectedConditions.elementToBeClickable(locator)
        );

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click();",
            element
        );
    }

    public static void clickTillUnclickable(
            WebDriver driver, By locator) {

        WebDriverWait shortWait = new WebDriverWait(
            driver,
            Duration.ofSeconds(2)
        );

        for (int i = 0; i < 100; i++) {
            try {
                WebElement element = shortWait.until(
                    ExpectedConditions.elementToBeClickable(locator)
                );
                element.click();
            } catch (TimeoutException exception) {
                return;
            }
        }
    }

    public static void enterText(
            WebDriver driver, By locator, String text) {

        WebElement element = wait(driver).until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );

        element.clear();
        element.sendKeys(text);
    }

    public static void pressKey(
            WebDriver driver, By locator, Keys key) {

        WebElement element = wait(driver).until(
            ExpectedConditions.elementToBeClickable(locator)
        );

        element.sendKeys(key);
    }

    public static String getText(WebDriver driver, By locator) {
        return wait(driver).until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        ).getText();
    }

    public static int getElementCount(
            WebDriver driver, By locator) {

        return driver.findElements(locator).size();
    }

    public static List<List<String>> getMovieDetails(
            WebDriver driver, By movieLocator) {

        List<List<String>> movieDetails = new ArrayList<>();

        Pattern categoryPattern = Pattern.compile(
            "(?i)\\b(Comedy|Animation|Drama)\\b"
        );
        Pattern badgePattern = Pattern.compile(
            "(?i)(?:\\bA\\b|U/A\\s*13\\+|U/A\\s*16\\+)"
        );

        List<WebElement> movies = wait(driver).until(
        ExpectedConditions.presenceOfAllElementsLocatedBy(movieLocator)
    );

    for (WebElement movie : movies) {
        String movieText = movie.getDomProperty("textContent");

        if (movieText == null || movieText.isBlank()) {
            continue;
        }

        String category = findFirstMatch(movieText, categoryPattern);
        String badge = findFirstMatch(movieText, badgePattern);

        movieDetails.add(Arrays.asList(category, badge));
    }

    return movieDetails;
}

    public static int getMusicDetails(
            WebDriver driver, By musicShelfLocator) {

        WebElement musicShelf = wait(driver).until(
            ExpectedConditions.visibilityOfElementLocated(musicShelfLocator)
        );

        return musicShelf.findElements(
            By.cssSelector("a#video-title")
        ).size();
    }

    public static long convertToNumericalValue(String value) {
        Matcher matcher = Pattern.compile(
            "([0-9]+(?:\\.[0-9]+)?)\\s*([KMB])?",
            Pattern.CASE_INSENSITIVE
        ).matcher(value.replace(",", ""));

        if (!matcher.find()) {
            return 0;
        }

        double number = Double.parseDouble(matcher.group(1));
        String suffix = matcher.group(2);

        if ("K".equalsIgnoreCase(suffix)) {
            number *= 1_000;
        } else if ("M".equalsIgnoreCase(suffix)) {
            number *= 1_000_000;
        } else if ("B".equalsIgnoreCase(suffix)) {
            number *= 1_000_000_000;
        }

        return Math.round(number);
    }

    private static String findFirstMatch(
            String text, Pattern pattern) {

        Matcher matcher = pattern.matcher(text);

        return matcher.find() ? matcher.group() : "";
    }

    public static void sendKeysWrapper(ChromeDriver driver, By xpath, String searchWord) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendKeysWrapper'");
    }
}
