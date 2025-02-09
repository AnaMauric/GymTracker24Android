package si.uni_lj.fe.seminar.gymtracker24;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnGym = findViewById(R.id.btnGym);
        Button btnStatistics = findViewById(R.id.btnStatistics);
        Button btnProfile = findViewById(R.id.btnProfile);

        btnGym.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, GymActivity.class);
            startActivity(intent);
        });

        btnStatistics.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
