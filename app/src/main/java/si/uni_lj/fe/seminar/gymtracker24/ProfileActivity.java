package si.uni_lj.fe.seminar.gymtracker24;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etBirthday;
    private NumberPicker npWeight;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Poveži UI elemente
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        npWeight = findViewById(R.id.npWeight);
        btnSave = findViewById(R.id.btnSave);

        // Nastavimo DatePicker na klik EditText-a
        etBirthday.setOnClickListener(view -> showDatePicker());

        // Preberi podatke iz SharedPreferences in jih vpiši v polja
        loadUserData();


        // Nastavimo NumberPicker za težo (od 30 do 150 kg)
        npWeight.setMinValue(30);
        npWeight.setMaxValue(150);
        npWeight.setValue(70); // Privzeta vrednost

        // Pridobi uporabniške podatke ob odprtju profila
        new DobiUserPodatke(this).izvediDobiPodatke();


        btnSave.setOnClickListener(view -> {
            String password = etPassword.getText().toString();
            String birthday = etBirthday.getText().toString();
            int weight = npWeight.getValue();

            VpisUserPodatkov vpis = new VpisUserPodatkov(password, birthday, weight, this);
            vpis.izvediVpise();

            //Toast.makeText(ProfileActivity.this, rezultat, Toast.LENGTH_SHORT).show();
        });

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(view -> {
            IzbrisUserja izbris = new IzbrisUserja(this);
            izbris.izvediIzbris();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }


    public void updateUserData(String birthday, int weight) {
        etBirthday.setText(birthday);
        npWeight.setValue(weight);
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
                    etBirthday.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }


    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("USERNAME", "");
        String savedPassword = sharedPreferences.getString("PASSWORD", "");

        etUsername.setText(savedUsername);
        etPassword.setText(savedPassword);
    }
}
