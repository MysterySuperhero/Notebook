package com.mysterysuperhero.notebook.events;

/**
 * Created by dmitri on 22.06.16.
 */
public class LanguageChangedEvent {
    public String locale;

    public LanguageChangedEvent(String locale) {
        this.locale = locale;
    }
}
