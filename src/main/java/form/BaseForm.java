package form;

import com.microsoft.playwright.Page;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
public abstract class BaseForm {

    protected Page page;

}
