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

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;


public class GymActivity extends AppCompatActivity {

    private Spinner exerciseSpinner, deleteExerciseSpinner;
    private NumberPicker setsPicker;
    private EditText repsInput, weightInput, dateInput;
    private Button saveButton, deleteButton;
    // Novo polje za prikaz zgodovine vadb
    private TextView workoutHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        // Initialize UI components
        exerciseSpinner = findViewById(R.id.exerciseSpinner);
        weightInput = findViewById(R.id.weightInput);
        setsPicker = findViewById(R.id.setsPicker);
        repsInput = findViewById(R.id.repsInput);
        saveButton = findViewById(R.id.saveButton);

        deleteExerciseSpinner = findViewById(R.id.deleteExerciseSpinner);
        dateInput = findViewById(R.id.dateInput);
        deleteButton = findViewById(R.id.deleteButton);

        // Povežemo novo polje workoutHistory (TextView mora obstajati v activity_gym.xml z ustreznim id-jem)
        workoutHistory = findViewById(R.id.workoutHistory);


        // Setup exercise dropdown (Spinner example data)
        String[] exercises = {"Squat", "Bench Press", "Deadlift", "Pull-up", "Shoulder Press"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exercises);
        exerciseSpinner.setAdapter(adapter);

        deleteExerciseSpinner.setAdapter(adapter);

        // Setup sets picker (e.g., from 1 to 10 sets)
        setsPicker.setMinValue(1);
        setsPicker.setMaxValue(10);

        // Setup date picker
        dateInput.setOnClickListener(view -> showDatePicker());

        new DobiVajaPodatke(this).izvediDobiVaje();


        saveButton.setOnClickListener(view -> {
            String exercise_name = exerciseSpinner.getSelectedItem().toString();

            String weightText = weightInput.getText().toString().trim();
            float weight = weightText.isEmpty() ? 0.0f : Float.parseFloat(weightText);

            int sets = setsPicker.getValue();

            String reps = repsInput.getText().toString();

            VpisVaje vpis = new VpisVaje(exercise_name, weight, sets, reps, this);
            vpis.izvediVpiseVaj();

            //Toast.makeText(ProfileActivity.this, rezultat, Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(view -> {
            String exercise_name = deleteExerciseSpinner.getSelectedItem().toString();
            String date = dateInput.getText().toString();

            IzbrisVaje izbris = new IzbrisVaje(exercise_name, date, this);
            izbris.izvediIzbriseVaj();

            //Toast.makeText(ProfileActivity.this, rezultat, Toast.LENGTH_SHORT).show();
        });





        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(GymActivity.this, HomeActivity.class);
            startActivity(intent);
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
