package tasks.ui;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import util.ConfigReader;

import java.nio.file.Paths;
import java.util.Random;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(getBrowserTypeLaunchOptions());
    }

    @BeforeEach
    public void setUpBeforeEachMethod() {
        context = browser.newContext(setBrowserNewContext());
        page = context.newPage();
    }

    private BrowserType.LaunchOptions getBrowserTypeLaunchOptions() {
        return new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000)
                .setChannel(ConfigReader.getPropValue("browserName"));
    }

    private Browser.NewContextOptions setBrowserNewContext() {
        return new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("video/"))
                .setRecordVideoSize(1880, 880)
                .setViewportSize(1880, 880);
    }

    protected int generateNumber(int boundary) {
        int numberToClick = new Random().nextInt(boundary);
        if (numberToClick == 0) {
            numberToClick = 1;
        }
        return numberToClick;
    }

    protected void fillFormAndLogIn() {
        page.fill("//input[@id='username']", ConfigReader.getPropValue("usernameAuthForm"));
        page.fill("//input[@id='password']", ConfigReader.getPropValue("passwordAuthForm"));
        page.click("//button[@type='submit']");
    }

    private Page.ScreenshotOptions screenshotOptions(String screenshotFileName) {
        return new Page.ScreenshotOptions()
                .setPath(Paths.get(ConfigReader.getPropValue("screenshotPath") + screenshotFileName));
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
                .setPath(Paths.get(ConfigReader.getPropValue("screenshotPath") + screenshotFileName)));
    }

    @AfterEach
    public void tearDownAfterEachMethod() {
        page.close();
        context.close();
    }

    @AfterAll
    public void tearDown() {
        browser.close();
        playwright.close();
    }

}
