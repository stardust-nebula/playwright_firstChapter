package form;

import com.microsoft.playwright.Page;
import lombok.SneakyThrows;

public class FormFactory {

    @SneakyThrows
    public <T extends BaseForm> T getForm(Page page, Class<T> tClass){
        T t = tClass.getDeclaredConstructor().newInstance();
        t.setPage(page);
        return t;
    }
}
