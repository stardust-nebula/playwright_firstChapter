package tasks.ui;

import org.junit.jupiter.api.Test;

public class ScreenshotVideoTest extends BaseTest {

    @Test
    public void takeScreenshotTest() {
        String searchInputLocatorPath = "//input[@name='search']";
        page.navigate("https://en.wikipedia.org/wiki/Main_Page");
        page.fill(searchInputLocatorPath, "Cat");
        page.press(searchInputLocatorPath, "Enter");
        takeScreenshot("img_1.png");
        takeScreenshotFullPage("img_2.png");
        takeScreenshotByLocator("//a[@href='/wiki/File:Felis_catus-cat_on_snow.jpg']/img", "img_3.png");
    }

}
