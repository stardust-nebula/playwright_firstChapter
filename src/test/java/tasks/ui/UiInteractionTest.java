package tasks.ui;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FilePayload;
import com.microsoft.playwright.options.MouseButton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

public class UiInteractionTest {

    private static Page page;

    @BeforeAll
    public static void setUp() {
        Playwright playwright = Playwright.create();
        BrowserContext context = playwright.chromium().launch(new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(1000))
                .newContext();
        page = context.newPage();
    }

    @Test
    @DisplayName("Login via auth form")
    public void loginViaAuthFormTest() {
        String expectedLoginMessage = "You logged into a secure area!";
        page.navigate("http://the-internet.herokuapp.com/login");
        fillFormAndLogIn();
        boolean isUserLoggedIntoMessageAppears = page.locator("//div[@id='flash']")
                .textContent()
                .contains(expectedLoginMessage);
        Assertions.assertTrue(isUserLoggedIntoMessageAppears);
    }

    @Test
    @DisplayName("Logout via auth form")
    public void logoutViaAuthFormTest() {
        String expectedLogoutMessage = "You logged out of the secure area!";
        page.navigate("http://the-internet.herokuapp.com/login");
        fillFormAndLogIn();
        page.click("//a[@href='/logout']");
        boolean isUserLoggedOutMessageAppears = page.locator("//div[@id='flash']")
                .textContent()
                .contains(expectedLogoutMessage);
        Assertions.assertTrue(isUserLoggedOutMessageAppears);
    }

    @Test
    @DisplayName("Login via http credentials")
    public void loginViaHttpCredTest() {
        page.navigate("http://admin:admin@the-internet.herokuapp.com/basic_auth");
        boolean isSuccessMessageDisplays = page.locator("//p").textContent().contains("Congratulations!");
        Assertions.assertTrue(isSuccessMessageDisplays);
    }

    @Test
    @DisplayName("Add Elements")
    public void addElementsTest() {
        int numberToClick = generateNumber(10);
        page.navigate("http://the-internet.herokuapp.com/add_remove_elements/");
        page.locator("//button[contains(text(), 'Add Element')]").click(new Locator.ClickOptions().setClickCount(numberToClick));
        Locator locator = page.locator("//button[contains(text(),'Delete')]");
        int numberOfDeleteElements = locator.count();
        Assertions.assertTrue(numberToClick == numberOfDeleteElements);
    }

    @Test
    @DisplayName("Remove Elements")
    public void removeElementsTest() {
        int numberToClick = generateNumber(10);
        page.navigate("http://the-internet.herokuapp.com/add_remove_elements/");
        page.locator("//button[contains(text(), 'Add Element')]").click(new Locator.ClickOptions().setClickCount(numberToClick));
        Locator locator = page.locator("//button[contains(text(),'Delete')]");
        for (int i = 0; i < numberToClick; i++) {
            locator.last().click();
        }
        Assertions.assertTrue(locator.count() == 0);
    }

    @Test
    @DisplayName("Open dialog by right-clicking and accept")
    public void rightClickToOpenDialogTest() {
        String expectedDialogMessage = "You selected a context menu";
        String[] actual = new String[1];
        page.navigate("http://the-internet.herokuapp.com/context_menu");
        page.onDialog(dialog ->
                {
                    actual[0] = dialog.message();
                    dialog.accept();
                }
        );
        page.locator("//div[@oncontextmenu]").click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        Assertions.assertTrue(actual[0].equals(expectedDialogMessage));
    }

    @Test
    @DisplayName("Upload file")
    public void uploadFileTest() {
        String fileName = generateNumber(50) + "0101_name.txt";
        page.navigate("http://the-internet.herokuapp.com/upload");
        page.locator("//input[@id='file-upload']")
                .setInputFiles(new FilePayload(fileName, "text/plain", "My description"
                        .getBytes(StandardCharsets.UTF_8)));
        page.click("//input[@id='file-submit']");
        boolean isSuccessMessageDisplays = page.isVisible("//h3[contains(text(),'File Uploaded!')]");
        Assertions.assertTrue(isSuccessMessageDisplays);
    }

    @Test
    @DisplayName("Download uploaded file")
    public void uploadDownloadFileTest() {
        String fileName = generateNumber(50) + "_0202_name.txt";
        String fileText = generateNumber(50) + "_0202 - Description";
        String actualText = null;
        FilePayload filePayload = new FilePayload(fileName, "text/plain", fileText
                .getBytes(StandardCharsets.UTF_8));
        page.navigate("http://the-internet.herokuapp.com/upload");
        page.locator("//input[@id='file-upload']").setInputFiles(filePayload);
        page.click("//input[@id='file-submit']");
        page.navigate("http://the-internet.herokuapp.com/download");
        Download download = page.waitForDownload(() -> {
            page.getByText(fileName).click();
        });
        download.saveAs(Paths.get("src/test/resources/download/" + fileName));

        try (FileReader reader = new FileReader("src/test/resources/download/" + fileName)) {
            char[] buf = new char[256];
            int c;
            while ((c = reader.read(buf)) > 0) {
                if (c < 256) {
                    buf = Arrays.copyOf(buf, c);
                }
            }
            actualText = String.valueOf(buf);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Assertions.assertTrue(actualText.equals(fileText));
    }

    @Test
    @DisplayName("Check disabled input field")
    public void disabledInputFieldTest() {
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        boolean isInputFieldDisabled = page.isDisabled("//form[@id='input-example']/child::input[@type='text']");
        Assertions.assertTrue(isInputFieldDisabled);
    }

    @Test
    @DisplayName("Check elements once input is enabled")
    public void enableInputTest() {
        String expectedEnabledMessage = "It's enabled!";
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        page.click("//button[@type='button' and contains(text(),'Enable')]");
        Assertions.assertAll(
                () -> Assertions.assertTrue(page.locator("//p[@id='message']").textContent().equals(expectedEnabledMessage)),
                () -> Assertions.assertTrue(page.isEnabled("//form[@id='input-example']/child::input[@type='text']"))
        );
    }

    @Test
    @DisplayName("Checkbox state after it was removed and added")
    public void checkBoxStateAfterRemovedAddedTest() {
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        page.click("//button[@type='button' and contains(text(),'Remove')]");
        page.click("//button[@type='button' and contains(text(),'Add')]");
        Assertions.assertFalse(page.isChecked("//input[@id='checkbox']"));
    }

    @Test
    @DisplayName("Checkbox state once selected")
    public void selectCheckboxTest() {
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        page.check("//input[@type='checkbox']");
        Assertions.assertTrue(page.isChecked("//input[@type='checkbox']"));
    }

    @Test
    @DisplayName("Checkbox state once unselected")
    public void unselectCheckboxTest() {
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        page.check("//input[@type='checkbox']");
        page.uncheck("//input[@type='checkbox']");
        Assertions.assertFalse(page.isChecked("//input[@type='checkbox']"));
    }

    @Test
    @DisplayName("Remove checkbox")
    public void removeCheckboxTest() {
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        page.click("//button[@type='button' and contains(text(),'Remove')]");
        page.waitForSelector("//button[@type='button' and contains(text(),'Add')]");
        boolean isVisible = page.isVisible("//input[@type='checkbox']");
        Assertions.assertFalse(isVisible);
    }


    private void fillFormAndLogIn() {
        page.fill("//input[@id='username']", "tomsmith");
        page.fill("//input[@id='password']", "SuperSecretPassword!");
        page.click("//button[@type='submit']");
    }

    private int generateNumber(int boundary) {
        int numberToClick = new Random().nextInt(boundary);
        if (numberToClick == 0) {
            numberToClick = 1;
        }
        return numberToClick;
    }
}
