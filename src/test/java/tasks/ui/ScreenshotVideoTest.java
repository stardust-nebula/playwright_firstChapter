package tasks.ui;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class ScreenshotVideoTest extends BaseTest {
    private static Playwright playwright;
    private static BrowserContext context;
    private static Page page;

    @Test
    public void takeScreenshotTest() {
        String searchInputLocatorPath = "//input[@name='search']";
        page.navigate("https://en.wikipedia.org/wiki/Main_Page");
        page.fill(searchInputLocatorPath, "Cat");
        page.press(searchInputLocatorPath, "Enter");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("screenshot/img_1.png")));
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("screenshot/img_2.png"))
                .setFullPage(true));
        page.locator("//a[@href='/wiki/File:Felis_catus-cat_on_snow.jpg']/img")
                .screenshot(new Locator.ScreenshotOptions()
                        .setPath(Paths.get("screenshot/img_3.png")));
    }

}
