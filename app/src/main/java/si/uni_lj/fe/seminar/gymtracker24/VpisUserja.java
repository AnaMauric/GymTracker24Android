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

class VpisUserja {
    private final String username;
    private final String password;
    private final String urlStoritve;
    private final Activity callerActivity;

    public VpisUserja(Activity callerActivity) {
        // Preberi podatke iz SharedPreferences
        SharedPreferences sharedPreferences = callerActivity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        this.username = sharedPreferences.getString("USERNAME", "");
        this.password = sharedPreferences.getString("PASSWORD", "");
        this.callerActivity = callerActivity;

        urlStoritve = callerActivity.getString(R.string.URL_base_storitve) + callerActivity.getString(R.string.URL_rel_vpis_user);

    }

    public void izvediPrijavo() {
        new Thread(() -> {
            String rezultat = vpis();

            // Posodobitev UI-ja mora teči na glavnem threadu
            callerActivity.runOnUiThread(() ->
                    Toast.makeText(callerActivity, rezultat, Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    private String vpis() {
        // Preverimo internetno povezavo
        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            return callerActivity.getResources().getString(R.string.napaka_omrezje);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                int responseCode = connect(username, password);

                if (responseCode == 200 || responseCode == 201) {
                    return callerActivity.getResources().getString(R.string.rest_uporabnik_uspesno);
                } else {
                    return callerActivity.getResources().getString(R.string.rest_nepricakovan_odgovor) + " " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return callerActivity.getResources().getString(R.string.napaka_storitev);
            }
        } else {
            return callerActivity.getResources().getString(R.string.napaka_omrezje);
        }
    }

    private int connect(String username, String password) throws IOException {
        Log.d("VpisUserja", "Poskušam povezavo na: " + urlStoritve);
        URL url = new URL(urlStoritve);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(5000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true); // Potrebno za pošiljanje podatkov

        Log.d("VpisUserja", "Povezava vzpostavljena, pošiljam podatke...");

        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json.toString());
            writer.flush();
            writer.close();
            os.close();
        } catch (Exception e) {
            Log.e("VpisUserja", "Napaka pri pošiljanju podatkov: " + e.getMessage());
            e.printStackTrace();
        }

        int responseCode = conn.getResponseCode();
        Log.d("VpisUserja", "Odziv strežnika: " + responseCode);

        return responseCode;
    }

}


