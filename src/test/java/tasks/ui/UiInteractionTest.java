package tasks.ui;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.FilePayload;
import com.microsoft.playwright.options.MouseButton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

public class UiInteractionTest extends BaseTest {

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
        page.navigate("http://@the-internet.herokuapp.com/basic_auth");
        boolean isSuccessMessageDisplays = page.locator("//p").textContent().contains("Congratulations!");
        Assertions.assertTrue(isSuccessMessageDisplays);
    }

    @Test
    @DisplayName("Add and remove Elements")
    public void addElementsTest() {
        int numberToClick = new Random().nextInt(10);
        if (numberToClick == 0) {
            ++numberToClick;
        }
        Locator.ClickOptions clickOptionsCount = new Locator.ClickOptions().setClickCount(numberToClick);
        page.navigate("http://the-internet.herokuapp.com/add_remove_elements/");
        Locator addElement = page.locator("button", new Page.LocatorOptions().setHasText("Add Element"));
        addElement.click(clickOptionsCount);
        Locator locator = page.locator("button", new Page.LocatorOptions().setHasText("Delete"));
        int numberOfDeleteElements = locator.count();
        Assertions.assertTrue(numberToClick == numberOfDeleteElements);
        locator.first().click(clickOptionsCount);
        Assertions.assertEquals(0, locator.count());
    }

    @Test
    @DisplayName("Open dialog by right-clicking and accept")
    public void rightClickToOpenDialogTest() {
        String expectedDialogMessage = "You selected a context menu";
        StringBuilder builder = new StringBuilder();
        page.navigate("http://the-internet.herokuapp.com/context_menu");
        page.onDialog(dialog ->
                {
                    builder.append(dialog.message());
                    dialog.accept();
                }
        );
        page.locator("//div[@oncontextmenu]").click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        Assertions.assertEquals(expectedDialogMessage, builder.toString());
    }

    @Test
    @DisplayName("Upload and download file")
    public void uploadFileTest() {
        String fileName = new Date().getTime() + "0101_name.txt";
        String fileText = new Date().getTime() + "_0202 - Description";
        page.navigate("http://the-internet.herokuapp.com/upload");
        FilePayload filePayload = new FilePayload(fileName, "text/plain", fileText
                .getBytes(StandardCharsets.UTF_8));
        page.locator("//input[@id='file-upload']").setInputFiles(filePayload);
        page.click("//input[@id='file-submit']");
        boolean isSuccessMessageDisplays = page.waitForSelector("//h3[contains(text(),'File Uploaded!')]").isVisible();
        Assertions.assertTrue(isSuccessMessageDisplays);
        page.navigate("http://the-internet.herokuapp.com/download");
        Download download = page.waitForDownload(() -> {
            page.getByText(fileName).click();
        });
        download.saveAs(Paths.get("src/test/resources/download/" + fileName));
        String uploaded = Arrays.toString(filePayload.buffer);
        String downloaded;
        try {
            downloaded = Arrays.toString(download.createReadStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(uploaded, downloaded);
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
                () -> Assertions.assertEquals(expectedEnabledMessage, page.locator("//p[@id='message']").textContent()),
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
        page.check(checkboxLocatorPath);
        Assertions.assertTrue(page.isChecked(checkboxLocatorPath));
    }

    @Test
    @DisplayName("Checkbox state once unselected")
    public void unselectCheckboxTest() {
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        page.check(checkboxLocatorPath);
        page.uncheck(checkboxLocatorPath);
        Assertions.assertFalse(page.isChecked(checkboxLocatorPath));
    }

    @Test
    @DisplayName("Remove checkbox")
    public void removeCheckboxTest() {
        page.navigate("http://the-internet.herokuapp.com/dynamic_controls");
        page.click("//button[@type='button' and contains(text(),'Remove')]");
        page.waitForSelector("//button[@type='button' and contains(text(),'Add')]");
        boolean isVisible = page.isVisible(checkboxLocatorPath);
        Assertions.assertFalse(isVisible);
    }

}
