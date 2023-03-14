package form;

import com.microsoft.playwright.Page;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HeaderForm extends BaseForm {

    private final String logoutButtonLocatorPath = "//a[@href='/logout']";

    public HeaderForm(Page page) {
        super(page);
    }

    public HeaderForm() {
    }

    public void clickLogoutButton(){
        page.click(logoutButtonLocatorPath);
    }


}
