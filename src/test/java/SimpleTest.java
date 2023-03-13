import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleTest {
    @Test
    public void herokuappNavigateToContextMenu() {
        Playwright playwright = Playwright.create();
        BrowserContext context = playwright.chromium().launch(new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setSlowMo(3000))
                .newContext();
        Page page = context.newPage();
        page.navigate("http://the-internet.herokuapp.com/");
        page.click("//a[text()='Context Menu']");
        boolean isContextMenuPageTitleShown = page.locator("//h3[text()='Context Menu']").isVisible();
        Assertions.assertTrue(isContextMenuPageTitleShown);
    }
}
