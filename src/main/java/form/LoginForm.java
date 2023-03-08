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

    public LoginForm enterUsername(String username) {
        page.fill(usernameLocatorPath, username);
        return this;
    }

    public LoginForm enterPassword(String password) {
        page.fill(passwordLocatorPath, password);
        return this;
    }

    public void clickLoginButton() {
        page.click(loginButtonLocatorPath);
    }

    public void fillFormAndLogIn(){
        enterUsername(ConfigReader.getPropValue("usernameAuthForm"));
        enterPassword(ConfigReader.getPropValue("passwordAuthForm"));
        clickLoginButton();
    }

}
