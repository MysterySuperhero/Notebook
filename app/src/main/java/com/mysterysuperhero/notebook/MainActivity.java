package com.mysterysuperhero.notebook;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.mysterysuperhero.notebook.database.DataBaseContract;
import com.mysterysuperhero.notebook.events.CategoriesLoadedEvent;
import com.mysterysuperhero.notebook.events.ColorChoseEvent;
import com.mysterysuperhero.notebook.events.NotesLoadedEvent;
import com.mysterysuperhero.notebook.fragments.ViewPagerAdapter;
import com.mysterysuperhero.notebook.utils.FragmentsVisiblity;
import com.mysterysuperhero.notebook.utils.SlidingTabLayout;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ColorChooserDialog.ColorCallback {

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence titles[] = {"Записи", "Категории"};
    int tabsCount = 2;

    public FloatingActionButton fab;

    public static final String DEFAULT_COLOR = "#FFFFFF";
    public static final int NOTES_LOADER = 0;
    public static final int CATEGORIES_LOADER = 1;

    public int primaryPreselect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        getSupportLoaderManager().initLoader(NOTES_LOADER, null, this);
        getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
        EventBus.getDefault().post(new ColorChoseEvent(selectedColor));
//        if (getSupportActionBar() != null)
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(selectedColor));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(CircleView.shiftColorDown(selectedColor));
//            getWindow().setNavigationBarColor(selectedColor);
//        }
    }
}
