package form;

import com.microsoft.playwright.Page;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public abstract class BaseForm {

    protected Page page;

}
