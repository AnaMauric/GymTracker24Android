package si.uni_lj.fe.seminar.gymtracker24;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class DobiVajaPodatke {

    private final String username;
    private final String urlStoritve;
    private final Activity callerActivity;
    private final GymActivity gymActivity;

    public DobiVajaPodatke(GymActivity gymActivity) {
        this.gymActivity = gymActivity;
        this.callerActivity = gymActivity;

        // Preberi username iz SharedPreferences
        SharedPreferences sharedPreferences = callerActivity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        this.username = sharedPreferences.getString("USERNAME", "");

        // Sestavi URL, pri čemer se uporabniško ime poda kot parameter
        urlStoritve = callerActivity.getString(R.string.URL_base_storitve) + "exercises.php?username=" + username;
    }

    public void izvediDobiVaje() {
        new Thread(() -> {
            String rezultat = dobiVaje();
            // Po želji lahko prikažemo Toast s sporočilom (npr. ob napaki ali uspehu)
            callerActivity.runOnUiThread(() ->
                    Toast.makeText(callerActivity, rezultat, Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    public String dobiVaje() {
        // Preveri internetno povezavo
        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            return "Napaka: Ni internetne povezave.";
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                return connect();
            } catch (Exception e) {
                e.printStackTrace();
                return "Napaka pri komunikaciji s strežnikom.";
            }
        } else {
            return "Napaka: Ni internetne povezave.";
        }
    }

    private String connect() throws Exception {
        URL url = new URL(urlStoritve);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(5000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Log za preverjanje URL-ja
        Log.d("DobiVajaPodatke", "Pridobivanje vaj iz: " + urlStoritve);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Predpostavljamo, da strežnik vrne JSONArray z vajami
            JSONArray jsonArray = new JSONArray(response.toString());
            StringBuilder workoutHistory = new StringBuilder();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String exercise_name = obj.getString("exercise_name");
                String date = obj.getString("date");
                // Če je weight shranjen kot številka (float/double)
                float weight = (float) obj.getDouble("weight");
                int sets = obj.getInt("sets");
                String reps = obj.getString("reps");

                workoutHistory.append(" Date: ").append(date)
                        .append(", Exercise: ").append(exercise_name)
                        .append(", Weight: ").append(weight)
                        .append(", Sets: ").append(sets)
                        .append(", Reps: ").append(reps)
                        .append("\n");
            }
            // Posodobi UI v GymActivity
            callerActivity.runOnUiThread(() -> gymActivity.updateWorkoutHistory(workoutHistory.toString()));

            return "Vaje pridobljene!";
        } else {
            return "Napaka pri pridobivanju vaj. Koda: " + responseCode;
        }
    }
}
