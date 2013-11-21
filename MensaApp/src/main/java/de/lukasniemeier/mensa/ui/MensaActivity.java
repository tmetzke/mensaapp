package de.lukasniemeier.mensa.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Collection;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Mensa;
import de.lukasniemeier.mensa.ui.adapter.CardState;
import de.lukasniemeier.mensa.ui.adapter.MensaAdapter;
import de.lukasniemeier.mensa.ui.preference.SettingsActivity;
import de.lukasniemeier.mensa.utils.DefaultMensaManager;

public class MensaActivity extends ThemedActivity {

    public static final String ABOUT_DIALOG_TAG = "AboutDialog";
    public static final String EXTRA_NO_DEFAULT_REDIRECT = "EXTRA_NO_DEFAULT_REDIRECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        redirectOnDefaultMensa(Mensa.getMensas());

        setContentView(R.layout.activity_mensa);
        boolean isFirstStart = savedInstanceState == null;

        setupGridView(isFirstStart);
    }

    private void setupGridView(boolean firstStart) {
        MensaAdapter adapter = new MensaAdapter(this);
        GridView mensaList = (GridView) findViewById(R.id.mensa_list_view);
        mensaList.setAdapter(adapter);
        adapter.addAll(Mensa.getMensas(), firstStart);
        mensaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                @SuppressWarnings("unchecked") CardState<Mensa> selectedMensa = (CardState<Mensa>) adapterView.getItemAtPosition(i);
                selectMensa(selectedMensa.getValue());
            }
        });
    }

    private boolean redirectOnDefaultMensa(Collection<Mensa> mensas) {
        Intent startIntent = getIntent();
        if (startIntent.hasExtra(EXTRA_NO_DEFAULT_REDIRECT)) {
            return false;
        }
        startIntent.removeExtra(EXTRA_NO_DEFAULT_REDIRECT);
        DefaultMensaManager defaultManager = new DefaultMensaManager(this);
        if (defaultManager.hasDefault()) {
            for (Mensa mensa : mensas) {
                if (mensa.getShortName().equals(defaultManager.getDefaultMensaShortName())) {
                    selectMensa(mensa);
                    return true;
                }
            }
        }
        return false;
    }

    private void selectMensa(Mensa selectedMensa) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(MenuActivity.EXTRA_MENSA_NAME, selectedMensa.getName());
        intent.putExtra(MenuActivity.EXTRA_MENSA_SHORTNAME, selectedMensa.getShortName());
        intent.putExtra(MenuActivity.EXTRA_MENSA_URL, selectedMensa.getDetailMenuURL());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mensa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.show(getFragmentManager(), ABOUT_DIALOG_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}