package form;

import com.microsoft.playwright.Page;

public class LoginForm extends BaseForm {

    private final String usernameLocatorPath = "//input[@id='username']";
    private final String passwordLocatorPath = "//input[@id='password']";
    private final String loginButtonLocatorPath = "//button[@type='submit']";

    public LoginForm(Page page) {
        super(page);
    }

    public LoginForm() {
    }

    public void enterUsername(String username) {
        page.fill(usernameLocatorPath, username);
    }

    public void enterPassword(String password) {
        page.fill(passwordLocatorPath, password);
    }

    public void clickLoginButton() {
        page.click(loginButtonLocatorPath);
    }

    public void fillFormAndLogIn(String usernameValue, String passwordValue) {
        enterUsername(usernameValue);
        enterPassword(passwordValue);
        clickLoginButton();
    }

}
