package whitewire.behappy;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import whitewire.behappy.Fragments.DatabaseResultsFragment;
import whitewire.behappy.Fragments.NetworkResultsFragment;
import whitewire.behappy.Fragments.QuestionFragment;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    boolean gps_enabled = false;
    boolean network_enabled = false;

    public void checkPreference () {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    public void checkLocation () {
        // Checking if location is turned on
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) { }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) { }

        if (gps_enabled == false && network_enabled == false) {
            Toast.makeText(this, "Please enable your location and restart the app",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, WelcomeActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            displayView(item.getItemId());
            return true;
        }

    };

    public void displayView (int viewId) {
        Fragment fr = null;
        switch (viewId) {
            case R.id.navigation_home:
                fr = new QuestionFragment();
                break;
            case R.id.navigation_personal:
                fr = new DatabaseResultsFragment();
                break;
            case R.id.navigation_global:
                fr = new NetworkResultsFragment();
                break;
        }

        if (fr != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.content, fr);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPreference();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new QuestionFragment()).commit();

        checkLocation();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}