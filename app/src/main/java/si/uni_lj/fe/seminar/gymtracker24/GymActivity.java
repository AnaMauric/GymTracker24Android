package si.uni_lj.fe.seminar.gymtracker24;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GymActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        Button btnBackToHome = findViewById(R.id.btnBackToHome);

        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(GymActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}
