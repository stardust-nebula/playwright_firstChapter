package tasks.ui;

import com.microsoft.playwright.*;
import form.HeaderForm;
import form.LoginForm;
import form.MessageForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import util.ConfigReader;

import java.nio.file.Paths;
import java.util.Random;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    public void setUp() {
        playwright = Playwright.create();
        switch (ConfigReader.getPropValue("browserName")) {
            case "chrome":
                browser = playwright.chromium().launch(getBrowserTypeLaunchOptions());
                break;
            case "firefox":
                browser = playwright.firefox().launch(getBrowserTypeLaunchOptions());
                break;
            default:
                log.info("Requesting browser is not supported. Launching the default browser");
                browser = playwright.chromium().launch(getBrowserTypeLaunchOptions());
                break;
        }

    }

    @BeforeEach
    public void setUpBeforeEachMethod() {
        context = browser.newContext(setBrowserNewContext());
        page = context.newPage();
    }

    private BrowserType.LaunchOptions getBrowserTypeLaunchOptions() {
        double slowMo = Double.parseDouble(ConfigReader.getPropValue("setSlowMoParam"));
        return new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(slowMo)
                .setChannel(ConfigReader.getPropValue("browserName"));
    }

    private Browser.NewContextOptions setBrowserNewContext() {
        int recordVideoWidth = Integer.parseInt(ConfigReader.getPropValue("recordVideSizeByWidth"));
        int recordVideoHeight = Integer.parseInt(ConfigReader.getPropValue("recordVideSizeByHeight"));
        int viewPortWidth = Integer.parseInt(ConfigReader.getPropValue("viewPortByWidth"));
        int viewPortHeight = Integer.parseInt(ConfigReader.getPropValue("viewPortByHeight"));

        return new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("video/"))
                .setRecordVideoSize(recordVideoWidth, recordVideoHeight)
                .setViewportSize(viewPortWidth, viewPortHeight)
                .setBaseURL(ConfigReader.getPropValue("baseWebAppUrls"))
                .setHttpCredentials("admin", "admin");
    }

    protected int generateNumber(int boundary) {
        return new Random().nextInt(boundary) + 1;
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
