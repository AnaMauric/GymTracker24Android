package si.uni_lj.fe.seminar.gymtracker24;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Preveri, če sta polji prazni
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vnesite uporabniško ime in geslo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Shrani v SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("USERNAME", username);
            editor.putString("PASSWORD", password);
            editor.apply();  // Shranimo asinkrono

            // Ustvari objekt in pokliči metodo za prijavo
            VpisUserja vpis = new VpisUserja(this);
            vpis.izvediPrijavo();  // Ta metoda mora obstajati v VpisUserja


            // Ustvari Intent za prehod na HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Zaključi LoginActivity, da se uporabnik ne vrne nazaj
        });
    }
}
