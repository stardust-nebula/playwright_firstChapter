package form;

import com.microsoft.playwright.Page;
import util.ConfigReader;

public class LoginForm extends Form {

    private String usernameLocatorPath = "//input[@id='username']";
    private String passwordLocatorPath = "//input[@id='password']";
    private String loginButtonLocatorPath = "//button[@type='submit']";

    public LoginForm(Page page) {
        super(page);
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

    public void fillFormAndLogIn(String usernameValue, String passwordValue){
        enterUsername(usernameValue);
        enterPassword(passwordValue);
        clickLoginButton();
    }

}
