package com.mysterysuperhero.notebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;

public class MainActivity extends AppCompatActivity {

    private View positiveAction;
    private EditText nameEditText;
    private EditText noteEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        initFloatingActionButton(fab);
    }

    private void initFloatingActionButton(FloatingActionButton button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.action_add)
                        .items(getString(R.string.action_add_note), getString(R.string.action_add_category))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        buildAddNoteDialog();
                                        break;
                                    case 1:
                                        buildAddCategoryDialog();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private void buildAddNoteDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.action_add_note_title)
                .customView(R.layout.add_note_dialog, true)
                .positiveText(R.string.action_add_positive)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        System.out.println("!!!!!!ДОБАВЛЕНО!!!!!!!");
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNameEditText);
        noteEditText = (EditText) dialog.getCustomView().findViewById(R.id.addNoteDialogNoteEditText);

        addTextChangedListener(nameEditText, noteEditText);
        addTextChangedListener(noteEditText, nameEditText);

        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(noteEditText,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        MDTintHelper.setTint(nameEditText,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void addTextChangedListener(EditText editText, final EditText anotherEditText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!anotherEditText.getText().toString().isEmpty()) {
                    positiveAction.setEnabled(s.toString().trim().length() > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void buildAddCategoryDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.action_add_category_title)
                .customView(R.layout.add_category_dialog, true)
                .positiveText(R.string.action_add_positive)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        System.out.println("!!!!!!ДОБАВЛЕНО!!!!!!!");
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
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
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
}
