package wrime.spring.webmvc.functors;

import org.springframework.context.MessageSource;

import java.util.Locale;

@SuppressWarnings("UnusedDeclaration")
public class L18nFunctor {
    private final MessageSource source;
    private final Locale locale;

    public L18nFunctor() {
        this(null, null);
    }

    public L18nFunctor(MessageSource source, Locale locale) {
        this.source = source;
        this.locale = locale;
    }

    public String t(String code, Object... args) {
        return source.getMessage(code, args, code, locale);
    }
}
