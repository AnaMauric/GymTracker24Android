package si.uni_lj.fe.seminar.gymtracker24;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etBirthday, npWeight;
    private Button btnSave, btnLogout, btnBackToHome, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        npWeight = findViewById(R.id.npWeight);
        btnSave = findViewById(R.id.btnSave);

        etBirthday.setOnClickListener(view -> showDatePicker());

        btnLogout = findViewById(R.id.btnLogout);
        btnBackToHome = findViewById(R.id.btnHome);
        btnDelete = findViewById(R.id.btnDelete);

        loadUserData();

        new DobiUserPodatke(this).izvediDobiPodatke();

        btnSave.setOnClickListener(view -> {
            String password = etPassword.getText().toString();
            String birthday = etBirthday.getText().toString();
            float weight = Float.parseFloat(npWeight.getText().toString());

            VpisUserPodatkov vpis = new VpisUserPodatkov(password, birthday, weight, this);
            vpis.izvediVpise();

            VpisWeight vpis2 = new VpisWeight(weight, this);
            vpis2.izvediVpiseWeight();
        });


        btnDelete.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Profile")
                    .setMessage("Are you sure you want to delete your profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        IzbrisUserja izbris = new IzbrisUserja(this);
                        izbris.izvediIzbris();

                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });


        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }


    public void updateUserData(String birthday, int weight) {
        etBirthday.setText(birthday);
        npWeight.setText(String.valueOf(weight));
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
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
