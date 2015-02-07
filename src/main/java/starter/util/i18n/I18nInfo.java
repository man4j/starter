package starter.util.i18n;

import java.util.LinkedHashMap;
import java.util.Locale;

public class I18nInfo<T> extends LinkedHashMap<Locale, T> {
    public T putLocale(Locale key, T value) {
        return put(key, value);
    }

    public T inLocale(Locale locale) {
        return get(locale);
    }

    public T inLocaleOrDefault(Locale locale) {
        T ob = inLocale(locale);

        return ob != null ? ob : inDefaultLocale();
    }

    public T inDefaultLocale() {
        return get(Locale.US);
    }
}
