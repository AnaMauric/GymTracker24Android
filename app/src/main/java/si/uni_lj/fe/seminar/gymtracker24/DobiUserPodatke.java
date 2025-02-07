package si.uni_lj.fe.seminar.gymtracker24;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class DobiUserPodatke {

    private final String username;
    private final String urlStoritve;
    private final Activity callerActivity;
    private final ProfileActivity profileActivity;

    public DobiUserPodatke(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
        this.callerActivity = profileActivity;

        // Preberi username iz SharedPreferences
        SharedPreferences sharedPreferences = callerActivity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        this.username = sharedPreferences.getString("USERNAME", "");

        urlStoritve = callerActivity.getString(R.string.URL_base_storitve) + "userji.php?userVzdevek=" + username;
    }

    public void izvediDobiPodatke() {
        new Thread(() -> {
            String rezultat = dobiPodatke();
        }).start();
    }

    public String dobiPodatke() {
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
        Log.d("DobiUserPodatke", "Pridobivanje podatkov iz: " + urlStoritve);

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

            JSONObject jsonResponse = new JSONObject(response.toString());
            String birthday = jsonResponse.getString("birthday");
            int weight = jsonResponse.getInt("weight");

            callerActivity.runOnUiThread(() -> {
                profileActivity.updateUserData(birthday, weight);
            });

            return "Birthday: " + birthday + ", Weight: " + weight;
        } else {
            return "Napaka pri pridobivanju podatkov. Koda: " + responseCode;
        }
    }
}
