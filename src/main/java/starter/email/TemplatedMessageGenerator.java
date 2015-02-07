package starter.email;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class TemplatedMessageGenerator {
    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    protected FreeMarkerConfigurer freeMarkerConfigurer;

    public String createMessageFromTemplate(String templateName, Map<?, ?> params) {
        try {
            Configuration cfg = freeMarkerConfigurer.getConfiguration();

            Template template = cfg.getTemplate(templateName, localeResolver.resolveLocale(httpRequest));

            Writer out = new StringWriter();

            template.process(params, out);

            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
