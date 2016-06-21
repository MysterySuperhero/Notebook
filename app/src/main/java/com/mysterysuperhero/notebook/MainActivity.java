package com.mysterysuperhero.notebook;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mysterysuperhero.notebook.database.DataBaseContract;
import com.mysterysuperhero.notebook.events.NotesLoadedEvent;
import com.mysterysuperhero.notebook.fragments.ViewPagerAdapter;
import com.mysterysuperhero.notebook.utils.SlidingTabLayout;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence titles[] = { "Записи", "Категории" };
    int tabsCount = 2;

    public FloatingActionButton fab;

    public static final String DEFAULT_COLOR = "#FFFFFF";
    public static final int NOTES_LOADER = 0;

    public static final int CATEGORIES_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, tabsCount);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

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

        getSupportLoaderManager().initLoader(NOTES_LOADER, null, this);
        getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
    }

//    private void initFloatingActionButton(FloatingActionButton button) {
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new MaterialDialog.Builder(MainActivity.this)
//                        .title(R.string.action_add)
//                        .items(getString(R.string.action_add_note), getString(R.string.action_add_category))
//                        .itemsCallback(new MaterialDialog.ListCallback() {
//                            @Override
//                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                switch (which) {
//                                    case 0:
//                                        buildAddNoteDialog();
//                                        break;
//                                    case 1:
//                                        buildAddCategoryDialog();
//                                        break;
//                                    default:
//                                        break;
//                                }
//                            }
//                        })
//                        .show();
//            }
//        });
//
//    }

//    private void buildAddCategoryDialog() {
//        MaterialDialog dialog = new MaterialDialog.Builder(this)
//                .title(R.string.action_add_category_title)
//                .customView(R.layout.add_category_dialog, true)
//                .positiveText(R.string.action_add_positive)
//                .negativeText(android.R.string.cancel)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        ContentValues values = new ContentValues();
//                        values.put(DataBaseContract.Categories.COLUMN_NAME_NAME, nameEditText.getText().toString());
//                        values.put(DataBaseContract.Notes.COLUMN_NAME_COLOR, DEFAULT_COLOR);
//
//                        getContentResolver().insert(DataBaseContract.Categories.CONTENT_URI, values);
//                    }
//                }).build();
//
//        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
//        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.addCategoryDialogNameEditText);
//
//        nameEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                positiveAction.setEnabled(s.toString().trim().length() > 0);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        int widgetColor = ThemeSingleton.get().widgetColor;
//
//        MDTintHelper.setTint(nameEditText,
//                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);
//
//        dialog.show();
//        positiveAction.setEnabled(false); // disabled by default
//    }

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
                    if (cursor.moveToFirst()) {
                        System.out.println("Categories cursor loaded!");
                        do {
                            System.out.println(cursor.getString(cursor.getColumnIndex(
                                    DataBaseContract.Categories.COLUMN_NAME_NAME
                            )));
                        } while (cursor.moveToNext());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
