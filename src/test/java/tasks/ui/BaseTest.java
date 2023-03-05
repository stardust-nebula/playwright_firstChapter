package tasks.ui;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import util.ConfigReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

public abstract class BaseTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    private static ConfigReader configReader;

    @BeforeAll
    public static void setUp() throws IOException {
        configReader = new ConfigReader();
        playwright = Playwright.create();
        browser = playwright.chromium().launch(setBrowserTypeOptionsLaunchOptions());
    }

    @BeforeEach
    public void setUpBeforeEachMethod() {
        context = setBrowserNewContext();
        page = context.newPage();
    }

    private static BrowserType.LaunchOptions setBrowserTypeOptionsLaunchOptions() throws IOException {
        return new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000)
                .setChannel(configReader.getPropValue("browserName"));
    }


    private static BrowserContext setBrowserNewContext() {
        return browser.newContext(
                new Browser.NewContextOptions()
                        .setRecordVideoDir(Paths.get("video/"))
                        .setRecordVideoSize(1880, 880)
                        .setViewportSize(1880, 880)
        );
    }

    protected int generateNumber(int boundary) {
        int numberToClick = new Random().nextInt(boundary);
        if (numberToClick == 0) {
            numberToClick = 1;
        }
        return numberToClick;
    }

    protected void fillFormAndLogIn() {
        page.fill("//input[@id='username']", "tomsmith");
        page.fill("//input[@id='password']", "SuperSecretPassword!");
        page.click("//button[@type='submit']");
    }

    private Page.ScreenshotOptions screenshotOptions(String screenshotFileName) {
        return new Page.ScreenshotOptions()
                .setPath(Paths.get(configReader.getPropValue("screenshotPath") + screenshotFileName));
    }

    protected void takeScreenshot(String screenshotFileName) {
        page.screenshot(screenshotOptions(screenshotFileName));
    }

    protected void takeScreenshotFullPage(String screenshotFileName) {
        page.screenshot(screenshotOptions(screenshotFileName)
                .setFullPage(true));
    }

    protected void takeScreenshotByLocator(String locatorElement, String screenshotFileName) {
        page.locator(locatorElement).screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get(configReader.getPropValue("screenshotPath") + screenshotFileName)));
    }

    @AfterEach
    public void tearDownAfterEachMethod() {
        page.close();
        context.close();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

}
