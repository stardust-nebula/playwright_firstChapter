package tasks.ui;

import com.microsoft.playwright.*;
import form.FormFactory;
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
    protected FormFactory formFactory;
    private  TestInfo testInfo;

    @BeforeAll
    public void setUp() {
        playwright = Playwright.create();
        browser = getBrowser().launch(getBrowserTypeLaunchOptions());
    }

    @BeforeEach
    public void setUpBeforeEachMethod(TestInfo testInfo) {
        context = browser.newContext(setBrowserNewContext());
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
        formFactory = new FormFactory();
        this.testInfo = testInfo;
    }

    @AfterEach
    public void tearDownAfterEachMethod(TestInfo testInfo) {
        page.close();
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("tracing/" + testInfo.getDisplayName().replaceAll(" ", "") + ".zip")));
        context.close();
    }

    @AfterAll
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    private BrowserType getBrowser() {
        return switch (ConfigReader.getPropValue("browserName")) {
            case "chrome":
                yield playwright.chromium();
            case "firefox":
                yield playwright.firefox();
            default:
                log.warn("Requesting browser is not supported. Launching the default browser");
                yield playwright.chromium();
        };
    }

    private BrowserType.LaunchOptions getBrowserTypeLaunchOptions() {
        double slowMo = Double.parseDouble(ConfigReader.getPropValue("setSlowMoParam"));
        return new BrowserType.LaunchOptions()
                .setHeadless(true)
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

}
