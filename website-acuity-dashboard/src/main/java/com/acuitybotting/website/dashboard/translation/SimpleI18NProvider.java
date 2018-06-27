package com.acuitybotting.website.dashboard.translation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.vaadin.flow.i18n.I18NProvider;

@Component
public class SimpleI18NProvider implements I18NProvider {

    @Override
    public List<Locale> getProvidedLocales() {
        return Collections.unmodifiableList(Arrays.asList(Locale.ENGLISH));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        return null;
    }
}