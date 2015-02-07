package starter.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.ui.freemarker.SpringTemplateLoader;

public class AutoEscapeFileTemplateLoader extends SpringTemplateLoader {
    public AutoEscapeFileTemplateLoader(String templateLoaderPath) {
        super(new DefaultResourceLoader(), templateLoaderPath);
    }

    public static final String ESCAPE_PREFIX = "<#escape x as x?html>";
    public static final String ESCAPE_SUFFIX = "</#escape>";

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        String template = IOUtils.toString(super.getReader(templateSource, encoding));

        if (!template.startsWith("<#ftl")) {
            return new StringReader(ESCAPE_PREFIX + template + ESCAPE_SUFFIX);
        }

        return new StringReader(template);
    }
}
