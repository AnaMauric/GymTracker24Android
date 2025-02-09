package si.uni_lj.fe.seminar.gymtracker24;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private Spinner exerciseSpinner;
    private Button btnShowGraph, btnHome, btnShowGraph2;
    private LineChart graphView, graphView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        exerciseSpinner = findViewById(R.id.exerciseSpinner);
        btnShowGraph = findViewById(R.id.btnShowGraph);
        graphView = findViewById(R.id.graphView);

        btnShowGraph2 = findViewById(R.id.btnShowGraph2);
        graphView2 = findViewById(R.id.graphView2);

        btnHome = findViewById(R.id.btnHome);

        String[] exercises = {"Assisted Pull-ups", "Back Squat", "Bench Press", "Biceps Curls", "Deadlift", "Hack Squat", "Hip Thrusts", "Shoulder Press", "Triceps Rope Pull-downs"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exercises);
        exerciseSpinner.setAdapter(adapter);


        btnShowGraph.setOnClickListener(view -> {
            String exerciseName = exerciseSpinner.getSelectedItem().toString();
            new PridobiWeightAndDate(this, exerciseName, this::updateGraph).izvediPridobiWeightInDate();
        });

        setupGraph();

        btnShowGraph2.setOnClickListener(view -> {
            new PridobiWeight(this, this::updateGraph2).izvediPridobiWeight();
        });

        setupGraph2();

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupGraph() {
        graphView.getDescription().setEnabled(false);
        graphView.setDrawGridBackground(false);

        XAxis xAxis = graphView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = graphView.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        graphView.getAxisRight().setEnabled(false);
    }

    private void updateGraph(List<Entry> entries) {
        if (entries.isEmpty()) {
            Toast.makeText(this, "No data to show!", Toast.LENGTH_SHORT).show();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight (kg)");
        dataSet.setColor(getResources().getColor(R.color.purple_500));
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        graphView.setData(lineData);
        graphView.invalidate();
    }


    private void setupGraph2() {
        graphView2.getDescription().setEnabled(false);
        graphView2.setDrawGridBackground(false);

        XAxis xAxis = graphView2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = graphView2.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        graphView2.getAxisRight().setEnabled(false);
    }


    private void updateGraph2(List<Entry> entries) {
        if (entries.isEmpty()) {
            Toast.makeText(this, "No data to show!", Toast.LENGTH_SHORT).show();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight (kg)");
        dataSet.setColor(getResources().getColor(R.color.purple_500));
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        graphView2.setData(lineData);
        graphView2.invalidate();
    }

}
