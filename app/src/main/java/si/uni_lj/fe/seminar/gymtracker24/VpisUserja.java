package si.uni_lj.fe.seminar.gymtracker24;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

interface LoginCallback {
    void onLoginResult(int statusCode);
}

class VpisUserja {
    private final String username, password, urlStoritve;
    private final Activity callerActivity;

    public VpisUserja(Activity callerActivity) {

        SharedPreferences sharedPreferences = callerActivity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        this.username = sharedPreferences.getString("USERNAME", "");
        this.password = sharedPreferences.getString("PASSWORD", "");
        this.callerActivity = callerActivity;

        urlStoritve = callerActivity.getString(R.string.URL_base_storitve) + callerActivity.getString(R.string.URL_rel_vpis_user);
    }

    public void izvediPrijavo(LoginCallback callback) {
        new Thread(() -> {
            int responseCode = vpis();

            callerActivity.runOnUiThread(() -> {
                callback.onLoginResult(responseCode);
            });
        }).start();
    }

    private int vpis() {
        ConnectivityManager connMgr = (ConnectivityManager) callerActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            return 0;
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                return connect(username, password);
            } catch (IOException e) {
                e.printStackTrace();
                return 500;
            }
        } else {
            return 0;
        }
    }

    private int connect(String username, String password) throws IOException {
        URL url = new URL(urlStoritve);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(5000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);

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
            e.printStackTrace();
        }

        int responseCode = conn.getResponseCode();
        return responseCode;
    }
}
