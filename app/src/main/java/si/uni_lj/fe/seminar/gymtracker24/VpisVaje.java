package si.uni_lj.fe.seminar.gymtracker24;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class VpisVaje {
    private final String username;
    private final String exercise_name;
    private final float weight;
    private final int sets;
    private final String reps;
    private final String urlStoritve;
    private final Activity callerActivity;

    public VpisVaje(String exercise_name, float weight, int sets, String reps, Activity callerActivity){

        this.callerActivity = callerActivity;

        // Preberi username iz SharedPreferences
        SharedPreferences sharedPreferences = callerActivity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        this.username = sharedPreferences.getString("USERNAME", "");

        this.exercise_name = exercise_name;
        this.weight = weight;
        this.sets = sets;
        this.reps = reps;

        urlStoritve = callerActivity.getString(R.string.URL_base_storitve) + callerActivity.getString(R.string.URL_rel_vpis_vaja);

    }

    public void izvediVpiseVaj() {
        new Thread(() -> {
            String rezultat = vpisVaj();

            // Posodobitev UI-ja mora teči na glavnem threadu
            callerActivity.runOnUiThread(() ->
                    Toast.makeText(callerActivity, rezultat, Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    public String vpisVaj() {
        // Preverimo internetno povezavo
        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            return "Napaka: Ni internetne povezave.";
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                int responseCode = connect(username, exercise_name, weight, sets, reps);

                if (responseCode == 200 || responseCode == 201) {
                    return "Podatki uspešno posodobljeni!";
                } else {
                    return "Napaka pri shranjevanju podatkov. Koda: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Napaka pri komunikaciji s strežnikom.";
            }
        } else {
            return "Napaka: Ni internetne povezave.";
        }
    }


    private int connect(String username, String exercise_name, float weight, int sets, String reps) throws Exception {
        URL url = new URL(urlStoritve);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(5000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true); // Potrebno za pošiljanje podatkov

        // Log za preverjanje URL-ja
        Log.d("VpisVaje", "Pošiljanje podatkov na: " + urlStoritve);

        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("exercise_name", exercise_name);
            json.put("weight", weight);
            json.put("sets", sets);
            json.put("reps", reps);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json.toString());
            writer.flush();
            writer.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn.getResponseCode();
    }



}

