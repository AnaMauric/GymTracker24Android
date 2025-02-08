package si.uni_lj.fe.seminar.gymtracker24;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private EditText etExerciseName;
    private Button btnShowGraph;
    private LineChart graphView;
    private Button btnShowGraph2;
    private LineChart graphView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Povezava UI elementov
        etExerciseName = findViewById(R.id.etExerciseName);
        btnShowGraph = findViewById(R.id.btnShowGraph);
        graphView = findViewById(R.id.graphView);

        btnShowGraph2 = findViewById(R.id.btnShowGraph2);
        graphView2 = findViewById(R.id.graphView2);

        // Nastavitev gumba za prikaz grafa
        btnShowGraph.setOnClickListener(view -> {
            String exerciseName = etExerciseName.getText().toString().trim();
            if (exerciseName.isEmpty()) {
                Toast.makeText(this, "Vnesi ime vaje!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Klic metode za pridobivanje podatkov
            new PridobiWeightAndDate(this, exerciseName, this::updateGraph).izvediPridobiWeightInDate();
        });

        // Osnovne nastavitve grafa
        setupGraph();


        // Nastavitev gumba za prikaz grafa
        btnShowGraph2.setOnClickListener(view -> {
            // Klic metode za pridobivanje podatkov
            new PridobiWeight(this, this::updateGraph2).izvediPridobiWeight();
        });

        // Osnovne nastavitve grafa
        setupGraph2();
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

    // Metoda za posodobitev grafa s pridobljenimi podatki
    private void updateGraph(List<Entry> entries) {
        if (entries.isEmpty()) {
            Toast.makeText(this, "Ni podatkov za prikaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Teža (kg)");
        dataSet.setColor(getResources().getColor(R.color.purple_500));
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        graphView.setData(lineData);
        graphView.invalidate(); // Osveži graf
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
            Toast.makeText(this, "Ni podatkov za prikaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Teža (kg)");
        dataSet.setColor(getResources().getColor(R.color.purple_500));
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        graphView2.setData(lineData);
        graphView2.invalidate(); // Osveži graf
    }




}
