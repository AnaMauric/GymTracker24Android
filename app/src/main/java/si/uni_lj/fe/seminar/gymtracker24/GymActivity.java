package si.uni_lj.fe.seminar.gymtracker24;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;


public class GymActivity extends AppCompatActivity {

    private Spinner exerciseSpinner, deleteExerciseSpinner;
    private EditText setsInput, repsInput, weightInput, dateInput;
    private Button saveButton, deleteButton, btnHome;
    private TextView workoutHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        btnHome = findViewById(R.id.btnHome);

        exerciseSpinner = findViewById(R.id.exerciseSpinner);
        weightInput = findViewById(R.id.weightInput);
        setsInput = findViewById(R.id.setsInput);
        repsInput = findViewById(R.id.repsInput);
        saveButton = findViewById(R.id.saveButton);

        workoutHistory = findViewById(R.id.workoutHistory);

        deleteExerciseSpinner = findViewById(R.id.deleteExerciseSpinner);
        dateInput = findViewById(R.id.dateInput);
        deleteButton = findViewById(R.id.deleteButton);


        // Setup exercise dropdown (Spinner example data)
        String[] exercises = {"Assisted Pull-ups", "Back Squat", "Bench Press", "Biceps curls", "Deadlift", "Hack Squat", "Hip Thrusts", "Shoulder Press", "Triceps Rope Pull-down"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exercises);
        exerciseSpinner.setAdapter(adapter);

        new DobiVajaPodatke(this).izvediDobiVaje();


        saveButton.setOnClickListener(view -> {
            String exercise_name = exerciseSpinner.getSelectedItem().toString();

            String weightText = weightInput.getText().toString().trim();
            float weight = weightText.isEmpty() ? 0.0f : Float.parseFloat(weightText);

            int sets = Integer.parseInt(setsInput.getText().toString());

            String reps = repsInput.getText().toString();

            VpisVaje vpis = new VpisVaje(exercise_name, weight, sets, reps, this);
            vpis.izvediVpiseVaj();

        });


        deleteExerciseSpinner.setAdapter(adapter);
        dateInput.setOnClickListener(view -> showDatePicker());

        deleteButton.setOnClickListener(view -> {
            String exercise_name = deleteExerciseSpinner.getSelectedItem().toString();
            String date = dateInput.getText().toString();

            IzbrisVaje izbris = new IzbrisVaje(exercise_name, date, this);
            izbris.izvediIzbriseVaj();
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(GymActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Zapre trenutno aktivnost
        });

    }


    /**
     * Ta metoda se kliče iz DobiVajaPodatke, ko so podatki pridobljeni.
     *
     * @param history String, ki vsebuje oblikovano zgodovino vaj.
     */
    public void updateWorkoutHistory(String history) {
        workoutHistory.setText(history);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Prikaz izbranega datuma v EditText polju
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}
