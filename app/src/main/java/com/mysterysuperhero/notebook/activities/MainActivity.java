package com.mysterysuperhero.notebook.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.mysterysuperhero.notebook.R;
import com.mysterysuperhero.notebook.database.DataBaseContract;
import com.mysterysuperhero.notebook.events.CategoriesLoadedEvent;
import com.mysterysuperhero.notebook.events.ColorChosenEvent;
import com.mysterysuperhero.notebook.events.FilterChosenEvent;
import com.mysterysuperhero.notebook.events.LanguageChangedEvent;
import com.mysterysuperhero.notebook.events.NotesLoadedEvent;
import com.mysterysuperhero.notebook.fragments.ViewPagerAdapter;
import com.mysterysuperhero.notebook.utils.CategoriesGetter;
import com.mysterysuperhero.notebook.utils.Category;
import com.mysterysuperhero.notebook.utils.FragmentsVisiblity;
import com.mysterysuperhero.notebook.utils.SlidingTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ColorChooserDialog.ColorCallback {

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence titles[];
    int tabsCount = 2;

    SharedPreferences settings;

    public FloatingActionButton fab;
    public static final String DEFAULT_COLOR = "#FFFFFF";
    public static final int NOTES_LOADER = 0;
    public static final int CATEGORIES_LOADER = 1;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FILTER = "filter"; // имя кота
    public static final String APP_PREFERENCES_LOCALE = "locale"; // имя кота

    public int primaryPreselect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MainActivity.APP_PREFERENCES_FILTER, "-1");
        editor.apply();
        setLocale(settings.getString(APP_PREFERENCES_LOCALE, "ru"));

        titles = new String[] {
                getResources().getString(R.string.notes_tab),
                getResources().getString(R.string.categories_tab)
        };
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, tabsCount);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float v, final int i2) {
            }

            @Override
            public void onPageSelected(final int position) {
                FragmentsVisiblity fragment = (FragmentsVisiblity) adapter.instantiateItem(pager, position);
                if (fragment != null) {
                    fragment.fragmentBecameVisible();
                }
            }

            @Override
            public void onPageScrollStateChanged(final int position) {
            }
        });

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabScrollColor);
            }
        });

        tabs.setViewPager(pager);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);


    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(NOTES_LOADER, null, this);
        getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        getSupportLoaderManager().destroyLoader(NOTES_LOADER);
        getSupportLoaderManager().destroyLoader(CATEGORIES_LOADER);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_info:
                String url = "https://github.com/MysterySuperhero";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));
//                intent = new Intent(this, InfoActivity.class);
//                startActivity(intent);
                break;
            case R.id.action_filter:
                this.buildCategoryChooserDialog();
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri CONTACT_URI = null;
        switch (id) {
            case NOTES_LOADER:
                CONTACT_URI = DataBaseContract.Notes.CONTENT_URI;
                break;
            case CATEGORIES_LOADER:
                CONTACT_URI = DataBaseContract.Categories.CONTENT_URI;
                break;
            default:
                break;
        }
        return new CursorLoader(this, CONTACT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            switch (loader.getId()) {
                case NOTES_LOADER:
                    EventBus.getDefault().post(new NotesLoadedEvent(cursor));
                    break;
                case CATEGORIES_LOADER:
                    EventBus.getDefault().post(new CategoriesLoadedEvent(cursor));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        EventBus.getDefault().post(new ColorChosenEvent(selectedColor));
//        if (getSupportActionBar() != null)
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(selectedColor));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(CircleView.shiftColorDown(selectedColor));
//            getWindow().setNavigationBarColor(selectedColor);
//        }
    }

    public void buildCategoryChooserDialog() {
        ArrayList<String> categoriesNames = new ArrayList<>();
        categoriesNames.add(getResources().getString(R.string.default_filter));
        final ArrayList<Category> categories = new ArrayList<>();
        CategoriesGetter.getCategories(this, categoriesNames, categories);
        new MaterialDialog.Builder(this)
                .title(R.string.action_filter_title)
                .items(categoriesNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        SharedPreferences.Editor editor = settings.edit();
                        if (which != 0) {
                            editor.putString(MainActivity.APP_PREFERENCES_FILTER, categories.get(which - 1).getId());
                        } else {
                            editor.putString(MainActivity.APP_PREFERENCES_FILTER, "-1");
                        }
                        editor.apply();
                        EventBus.getDefault().post(
                                new FilterChosenEvent(settings.getString(MainActivity.APP_PREFERENCES_FILTER, ""))
                        );
                    }
                })
                .positiveText(android.R.string.cancel)
                .show();
    }

//    @Subscribe(sticky = true)
//    public void onLanguageChangedEvent(LanguageChangedEvent event) {
//        if (settings != null) {
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putString(MainActivity.APP_PREFERENCES_LOCALE, event.locale);
//            editor.apply();
//        }
//    }

    private void setLocale(String code) {
        Locale myLocale = new Locale(code);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
