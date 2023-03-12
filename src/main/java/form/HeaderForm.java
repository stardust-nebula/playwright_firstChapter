package form;

import com.microsoft.playwright.Page;

public class HeaderForm extends Form {

    private String logoutButtonLocatorPath = "//a[@href='/logout']";

    public HeaderForm(Page page) {
        super(page);
    }

    public void clickLogoutButton(){
        page.click(logoutButtonLocatorPath);
    }


}
