package com.mysterysuperhero.notebook.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.mysterysuperhero.notebook.R;
import com.mysterysuperhero.notebook.events.LanguageChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final String LOCALE = "locale";
    public static final String RUSSIAN = "Русский";
    public static final String ENGLISH = "English";

    public static final String THEME = "theme";

    Locale myLocale;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            preference.setSummary(stringValue);
            switch (preference.getKey()) {
                case LOCALE:
                    String code = "";
                    switch ((String) value) {
                        case RUSSIAN:
                            myLocale = new Locale("ru");
                            code = "ru";
                            break;
                        case ENGLISH:
                            myLocale = new Locale("en");
                            code = "en";
                            break;
                    }
                    EventBus.getDefault().postSticky(new LanguageChangedEvent(code));
                    break;
                case THEME:
                    System.out.println("________________________THEME_______________________");
                    break;
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
//                this.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE));
//        this.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference("locale"));
        bindPreferenceSummaryToValue(findPreference("theme"));
        Snackbar.make(SettingsActivity.this.getListView(), getString(R.string.settings_warning), Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Subscribe
    public void onLanguageChangedEvent(LanguageChangedEvent event) {
        SharedPreferences settings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings != null) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(MainActivity.APP_PREFERENCES_LOCALE, event.locale);
            editor.apply();
        }
    }
}
