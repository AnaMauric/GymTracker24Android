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

class IzbrisUserja {

    private final String username, urlStoritve;
    private final Activity callerActivity;

    public IzbrisUserja(Activity callerActivity) {

        SharedPreferences sharedPreferences = callerActivity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        this.username = sharedPreferences.getString("USERNAME", "");
        this.callerActivity = callerActivity;

        urlStoritve = callerActivity.getString(R.string.URL_base_storitve) + callerActivity.getString(R.string.URL_rel_vpis_user);

    }


    public void izvediIzbris() {
        new Thread(() -> {
            String rezultat = izbris();

            callerActivity.runOnUiThread(() ->
                    Toast.makeText(callerActivity, rezultat, Toast.LENGTH_SHORT).show()
            );
        }).start();
    }


    private String izbris() {
        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            return callerActivity.getResources().getString(R.string.napaka_omrezje);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                int responseCode = connect(username);

                if (responseCode == 200 || responseCode == 204) {
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

    private int connect(String username) throws IOException {
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

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json.toString());
            writer.flush();
            writer.close();
            os.close();
        } catch (Exception e) {
            Log.e("izbrisUserja", "Napaka pri pošiljanju podatkov: " + e.getMessage());
            e.printStackTrace();
        }

        int responseCode = conn.getResponseCode();
        return responseCode;
    }
}
