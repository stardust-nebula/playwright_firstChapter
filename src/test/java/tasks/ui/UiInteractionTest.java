package tasks.ui;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.FilePayload;
import com.microsoft.playwright.options.MouseButton;
import form.HeaderForm;
import form.LoginForm;
import form.MessageForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.ConfigReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

import static form.MessageForm.SUCCESS_LOGIN_MESSAGE;
import static form.MessageForm.SUCCESS_LOGOUT_MESSAGE;

public class UiInteractionTest extends BaseTest {

    private static String checkboxLocatorPath = "//input[@type='checkbox']";

    @Test()
    @DisplayName("Login and Logout via auth form")
    public void loginViaAuthFormTest() {
        String username = ConfigReader.getPropValue("usernameAuthForm");
        String password = ConfigReader.getPropValue("passwordAuthForm");
        MessageForm messageForm = new MessageForm(page);
        LoginForm loginForm = new LoginForm(page);
        HeaderForm headerForm = new HeaderForm(page);
        page.navigate("/login");
        loginForm.fillFormAndLogIn(username, password);
        boolean isUserLoggedIntoMessageAppears = messageForm.isMessageContainsText(SUCCESS_LOGIN_MESSAGE);
        Assertions.assertTrue(isUserLoggedIntoMessageAppears);
        headerForm.clickLogoutButton();
        boolean isUserLoggedOutMessageAppears = messageForm.isMessageContainsText(SUCCESS_LOGOUT_MESSAGE);
        Assertions.assertTrue(isUserLoggedOutMessageAppears);

    }

    @Test
    @DisplayName("Login via http credentials")
    public void loginViaHttpCredTest() {
        page.navigate("/basic_auth");
        boolean isSuccessMessageDisplays = page.locator("//p").textContent().contains("Congratulations!");
        Assertions.assertTrue(isSuccessMessageDisplays);
    }

    @Test
    @DisplayName("Add and Remove Elements")
    public void addElementsTest() {
        int numberToClick = generateNumber(10);
        Locator.ClickOptions clickOptionsCount = new Locator.ClickOptions().setClickCount(numberToClick);
        page.navigate("/add_remove_elements/");
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
        page.navigate("/context_menu");
        StringBuilder builder = new StringBuilder();
        page.onDialog(dialog -> {
            builder.append(dialog.message());
            dialog.accept();
        });
        page.locator("//div[@oncontextmenu]").click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        Assertions.assertEquals(expectedDialogMessage, builder.toString());
    }

    @Test
    @DisplayName("Upload and download file")
    public void uploadFileTest() {
        String fileName = new Date().getTime() + "0101_name.txt";
        String fileText = new Date().getTime() + "_0202 - Description";
        page.navigate("/upload");
        FilePayload filePayload = new FilePayload(fileName, "text/plain", fileText.getBytes(StandardCharsets.UTF_8));
        page.locator("//input[@id='file-upload']").setInputFiles(filePayload);
        page.click("//input[@id='file-submit']");
        boolean isSuccessMessageDisplays = page.waitForSelector("//h3[contains(text(),'File Uploaded!')]").isVisible();
        Assertions.assertTrue(isSuccessMessageDisplays);
        page.navigate("/download");
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
        page.navigate("/dynamic_controls");
        boolean isInputFieldDisabled = page.isDisabled("//form[@id='input-example']/child::input[@type='text']");
        Assertions.assertTrue(isInputFieldDisabled);
    }

    @Test
    @DisplayName("Check elements once input is enabled")
    public void enableInputTest() {
        String expectedEnabledMessage = "It's enabled!";
        page.navigate("/dynamic_controls");
        page.click("//button[@type='button' and contains(text(),'Enable')]");
        Assertions.assertAll(() -> Assertions.assertEquals(expectedEnabledMessage, page.locator("//p[@id='message']").textContent()), () -> Assertions.assertTrue(page.isEnabled("//form[@id='input-example']/child::input[@type='text']")));
    }

    @Test
    @DisplayName("Checkbox state after it was removed and added")
    public void checkBoxStateAfterRemovedAddedTest() {
        page.navigate("/dynamic_controls");
        page.click("//button[@type='button' and contains(text(),'Remove')]");
        page.click("//button[@type='button' and contains(text(),'Add')]");
        Assertions.assertFalse(page.isChecked("//input[@id='checkbox']"));
    }

    @Test
    @DisplayName("Checkbox state once selected")
    public void selectCheckboxTest() {
        page.navigate("/dynamic_controls");
        page.check(checkboxLocatorPath);
        Assertions.assertTrue(page.isChecked(checkboxLocatorPath));
    }

    @Test
    @DisplayName("Checkbox state once unselected")
    public void unselectCheckboxTest() {
        page.navigate("/dynamic_controls");
        page.check(checkboxLocatorPath);
        page.uncheck(checkboxLocatorPath);
        Assertions.assertFalse(page.isChecked(checkboxLocatorPath));
    }

    @Test
    @DisplayName("Remove checkbox")
    public void removeCheckboxTest() {
        page.navigate("/dynamic_controls");
        page.click("//button[@type='button' and contains(text(),'Remove')]");
        page.waitForSelector("//button[@type='button' and contains(text(),'Add')]");
        boolean isVisible = page.isVisible(checkboxLocatorPath);
        Assertions.assertFalse(isVisible);
    }

}
