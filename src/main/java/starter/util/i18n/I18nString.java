package starter.util.i18n;

import java.util.Locale;

public class I18nString extends I18nInfo<String> {
    public I18nString() {
        putLocale(Locale.US, "");
    }

    public I18nString(String str){
        putLocale(Locale.US, str);
    }
}