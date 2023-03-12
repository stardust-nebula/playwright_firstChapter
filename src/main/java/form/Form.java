package form;

import com.microsoft.playwright.Page;

public abstract class Form {

    protected Page page;

    public Form(Page page) {
        this.page = page;
    }
}
