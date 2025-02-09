package si.uni_lj.fe.seminar.gymtracker24;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class IzbrisVaje {
    private final String username, exercise_name, date, urlStoritve;
    private final Activity callerActivity;

    public IzbrisVaje(String exercise_name, String date, Activity callerActivity){

        this.callerActivity = callerActivity;

        SharedPreferences sharedPreferences = callerActivity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        this.username = sharedPreferences.getString("USERNAME", "");

        this.exercise_name = exercise_name;
        this.date = date;

        urlStoritve = callerActivity.getString(R.string.URL_base_storitve) + callerActivity.getString(R.string.URL_rel_vpis_vaja);

    }

    public void izvediIzbriseVaj() {
        new Thread(() -> {
            String rezultat = izbrisVaj();

            callerActivity.runOnUiThread(() ->
                    Toast.makeText(callerActivity, rezultat, Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    public String izbrisVaj() {
        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            return "Napaka: Ni internetne povezave.";
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                int responseCode = connect(username, exercise_name, date);

                if (responseCode == 200) {
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

    private int connect(String username, String exercise_name, String date) throws Exception {
        URL url = new URL(urlStoritve);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(5000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("exercise_name", exercise_name);
            json.put("date", date);

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
