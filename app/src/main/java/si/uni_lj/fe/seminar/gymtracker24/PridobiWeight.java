package si.uni_lj.fe.seminar.gymtracker24;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PridobiWeight {
    private final Context context;
    private final OnDataReceivedListener listener;

    public interface OnDataReceivedListener {
        void onDataReceived(List<Entry> entries);
    }

    public PridobiWeight(Context context, OnDataReceivedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void izvediPridobiWeight() {
        new GetDataTask().execute();
    }

    private class GetDataTask extends AsyncTask<Void, Void, List<Entry>> {
        @Override
        protected List<Entry> doInBackground(Void... voids) {
            List<Entry> entries = new ArrayList<>();
            try {

                // Pridobi username iz SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("USERNAME", "");

                // Preveri, če je username prazen
                if (username.isEmpty()) {
                    return entries; // Vrni prazno listo, da onPostExecute sproži napako
                }

                // Pridobi base URL iz strings.xml
                String baseUrl = context.getString(R.string.URL_base_storitve);
                String apiUrl = baseUrl + "weight.php?username=" + username;


                //String username = "anamauric"; // Zamenjaj s pravim username
                //String apiUrl = "http://192.168.1.103/gymvaja2/graphStats.php?username=" + username + "&exercise=" + exerciseName;

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String date = obj.getString("date");
                    float weight = (float) obj.getDouble("weight");

                    entries.add(new Entry(i, weight)); // X = index, Y = weight
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return entries;
        }

        @Override
        protected void onPostExecute(List<Entry> entries) {
            if (entries.isEmpty()) {
                Toast.makeText(context, "Napaka pri pridobivanju podatkov!", Toast.LENGTH_SHORT).show();
            }
            listener.onDataReceived(entries);
        }
    }
}
