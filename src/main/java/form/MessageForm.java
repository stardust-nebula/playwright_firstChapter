package form;

import com.microsoft.playwright.Page;

public class MessageForm extends BaseForm {

    private final String messageBannerLocatorPath = "//div[@id='flash']";

    public static final String SUCCESS_LOGIN_MESSAGE = "You logged into a secure area!";
    public static final String SUCCESS_LOGOUT_MESSAGE = "You logged out of the secure area!";

    public MessageForm(Page page) {
        super(page);
    }

    public MessageForm() {
    }

    public boolean isMessageContainsText(String message) {
        return page.locator(messageBannerLocatorPath)
                .textContent()
                .contains(message);
    }
}
