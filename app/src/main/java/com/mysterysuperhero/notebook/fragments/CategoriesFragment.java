package com.mysterysuperhero.notebook.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.mysterysuperhero.notebook.MainActivity;
import com.mysterysuperhero.notebook.R;
import com.mysterysuperhero.notebook.database.DataBaseContract;
import com.mysterysuperhero.notebook.events.CategoriesLoadedEvent;
import com.mysterysuperhero.notebook.events.NotesLoadedEvent;
import com.mysterysuperhero.notebook.utils.Category;
import com.mysterysuperhero.notebook.utils.FragmentsVisiblity;
import com.mysterysuperhero.notebook.utils.Note;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by dmitri on 21.06.16.
 */
public class CategoriesFragment extends Fragment implements FragmentsVisiblity {
    private View positiveAction;
    private EditText nameEditText;
    private RecyclerView categoriesView;

    public int itemsCount = 0;

    public CategoriesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.categories_tab, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EventBus.getDefault().register(this);

        categoriesView = (RecyclerView) getView().findViewById(R.id.categoriesRecyclerView);
        categoriesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoriesView.setItemAnimator(new SlideInUpAnimator());
        categoriesView.setAdapter(new CategoriesAdapter(getActivity(), new ArrayList<Category>(), this));


    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void buildAddCategoryDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.action_add_category_title)
                .customView(R.layout.add_category_dialog, true)
                .positiveText(R.string.action_add_positive)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ContentValues values = new ContentValues();
                        values.put(DataBaseContract.Categories.COLUMN_NAME_NAME, nameEditText.getText().toString());
                        values.put(DataBaseContract.Notes.COLUMN_NAME_COLOR, MainActivity.DEFAULT_COLOR);

                        getActivity().getContentResolver().insert(DataBaseContract.Categories.CONTENT_URI, values);
                        itemsCount++;
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.addCategoryDialogNameEditText);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(nameEditText,
                widgetColor == 0 ? ContextCompat.getColor(getActivity(), R.color.colorAccent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    @Subscribe
    public void onCategoriesLoadedEvent(CategoriesLoadedEvent event) {
        Cursor cursor = event.cursor;
        if (cursor.moveToFirst()) {
            System.out.println("Notes cursor loaded!");
            ArrayList<Category> categories = new ArrayList<>();

            if (this.itemsCount != 0) {
                cursor.move(this.itemsCount - 1);
            }

            do {
                categories.add(new Category(
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Notes._ID)),
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Notes.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndex(DataBaseContract.Notes.COLUMN_NAME_COLOR))
                ));
            } while (cursor.moveToNext());
            ((CategoriesAdapter) this.categoriesView.getAdapter()).addToCategories(categories);
            this.categoriesView.getAdapter().notifyDataSetChanged();
            this.itemsCount = this.categoriesView.getAdapter().getItemCount();
        }
    }

    @Override
    public void fragmentBecameVisible() {
        ((MainActivity) getActivity()).fab.setOnClickListener(null);
        ((MainActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAddCategoryDialog();
            }
        });
    }
}