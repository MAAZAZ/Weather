package com.example.weather;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView ville_txt, Date_txt, Meteo_txt, tmin_txt, tmax_txt, Pression_txt, Humidite_txt;
    private TextView Pression,humdite;
    private ImageView atmosphere, pressure, humidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ville_txt = (TextView) findViewById(R.id.ville);
        Date_txt = (TextView) findViewById(R.id.date);
        Meteo_txt = (TextView) findViewById(R.id.meteo);
        tmin_txt = (TextView) findViewById(R.id.tmin);
        tmax_txt = (TextView) findViewById(R.id.tmax);
        Pression_txt = (TextView) findViewById(R.id.pression1);
        Humidite_txt = (TextView) findViewById(R.id.humidite1);
        atmosphere = (ImageView) findViewById(R.id.atmosphere);
        pressure=(ImageView) findViewById(R.id.pressionIcon);
        humidity=(ImageView) findViewById(R.id.humiditeIcon);
        Pression=(TextView) findViewById(R.id.pression);
        humdite=(TextView) findViewById(R.id.humidite);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        final Context context=this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ville_txt.setText(query.toUpperCase());
                RequestQueue queue=Volley.newRequestQueue(getApplicationContext());
                String url="http://api.openweathermap.org/data/2.5/weather?q="+query+"&appid=df58989c550edafb4e3136bebff7a117";
                StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Date date=new Date(jsonObject.getLong("dt")*1000);
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                            String dateStr=simpleDateFormat.format(date);
                            JSONObject main=jsonObject.getJSONObject("main");
                            int tmp=(int)(main.getDouble("temp")-273.15);
                            int tempMin=(int)(main.getDouble("temp_min")-273.15);
                            int tempMax=(int)(main.getDouble("temp_max")-273.15);
                            int pression=(int)(main.getDouble("pressure"));
                            int humidite=(int)(main.getDouble("humidity"));
                            JSONArray weather=jsonObject.getJSONArray("weather");
                            Date_txt.setText(dateStr);
                            Meteo_txt.setText(String.valueOf(tmp+" °C"));
                            tmin_txt.setText(String.valueOf(tempMin+" °C"));
                            tmax_txt.setText(String.valueOf(tempMax+" °C"));
                            Pression.setText("Pression");
                            pressure.setImageResource(R.drawable.pression);
                            Pression_txt.setText(String.valueOf(pression+" hPa"));
                            humdite.setText("Humidité");
                            humidity.setImageResource(R.drawable.humidite);
                            Humidite_txt.setText(String.valueOf(humidite+" %"));
                            String meteo=weather.getJSONObject(0).getString("main");
                            photoCorresp(meteo);
                        }catch(Exception e){

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Reset();
                        Toast.makeText(context,"Erreur : problème de la connexion ou la ville saisi n'existe pas",Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    void photoCorresp(String meteo) {
        switch(meteo){
            case "Rain" : atmosphere.setImageResource(R.drawable.rain); break;
            case "Clear" : atmosphere.setImageResource(R.drawable.sun); break;
            case "Thunderstorm" : atmosphere.setImageResource(R.drawable.storm); break;
            case "Clouds" : atmosphere.setImageResource(R.drawable.cloud); break;
        }
    }
    void Reset(){
        ville_txt.setText("Météo");
        Date_txt.setText("");
        Meteo_txt.setText("");
        tmin_txt.setText("");
        tmax_txt.setText("");
        Pression_txt.setText("");
        Humidite_txt.setText("");
        Pression.setText("");
        humdite.setText("");
        humidity.setImageResource(0);
        pressure.setImageResource(0);
    }
}
