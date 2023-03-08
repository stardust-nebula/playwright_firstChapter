package form;

import com.microsoft.playwright.Page;

public class MessageForm extends Form {

    private String messageBannerLocatorPath = "//div[@id='flash']";

    public MessageForm(Page page) {
        super(page);
    }

    public boolean isMessageAppears(String message) {
        return page.locator(messageBannerLocatorPath)
                .textContent()
                .contains(message);
    }
}
