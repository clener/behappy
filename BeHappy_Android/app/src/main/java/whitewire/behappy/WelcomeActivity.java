package whitewire.behappy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Claudio on 17-Apr-17.
 */

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Button submitButton = (Button) findViewById(R.id.introSubmit);

        final Spinner ageSpinner = (Spinner) findViewById(R.id.ageSpinner);
        ArrayList<Integer> ageArray = new ArrayList<>();
        final Spinner genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        ArrayList<String> genderArray = new ArrayList<>();

        for (int i=1; i<=120; i++){
            ageArray.add(i);
        }

        genderArray.add("Male");
        genderArray.add("Female");
        genderArray.add("Other");

        // Creating adapters for spinners
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, ageArray);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, genderArray);

        // Setting dropdown view for spinners
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setting adapters to spinners
        ageSpinner.setAdapter(ageAdapter);
        genderSpinner.setAdapter(genderAdapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int age = Integer.parseInt(ageSpinner.getSelectedItem().toString());
                String genderString = genderSpinner.getSelectedItem().toString();
                int gender;

                if (genderString == "Male") {
                    gender = 0;
                } else if (genderString == "Female") {
                    gender = 1;
                } else {
                    gender = 2;
                }

                // Creating shared preferences to remember the user's age and sex throughout time
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(WelcomeActivity.this);
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt("age", age);
                edit.putInt("gender", gender);
                edit.apply();

                // Setting first run to false to know to not display this activity automatically
                // after the first run
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}