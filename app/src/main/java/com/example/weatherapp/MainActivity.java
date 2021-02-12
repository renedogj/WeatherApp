package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    final String API_KEY = "Introduce tu API_KEY";

    androidx.constraintlayout.widget.ConstraintLayout mainActivy;

    private Spinner spinner;
    private TextView tvCiudad;
    private TextView tvTemperatua;
    private TextView tvTiempoAtmosferico;
    private TextView tvSensacionTermica;
    private TextView tvHumedad;
    private TextView tvPresion;
    private TextView tvVelocidadViento;
    private TextView tvTemperaturaMin;
    private TextView tvTemperaturaMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCiudad = findViewById(R.id.tvCiudad);
        tvTemperatua = findViewById(R.id.tvTemperatua);
        tvTiempoAtmosferico = findViewById(R.id.tvTiempoAtmosferico);
        tvSensacionTermica = findViewById(R.id.tvSensacionTermica);
        tvHumedad = findViewById(R.id.tvHumedad);
        tvPresion = findViewById(R.id.tvPresion);
        tvVelocidadViento = findViewById(R.id.tvVelocidadViento);
        tvTemperaturaMin = findViewById(R.id.tvTemperaturaMin);
        tvTemperaturaMax = findViewById(R.id.tvTemperaturaMax);
        spinner = findViewById(R.id.spinner);
        mainActivy = findViewById(R.id.mainActivy);

        find_weather(getUrl("Madrid"));
        ArrayList<String> ciudades = recuperarCiudades();
        String arrayCiudades[] = new String[ciudades.size()];
        for(int i=0;i<ciudades.size();i++){
            arrayCiudades[i] = ciudades.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, arrayCiudades);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int codigoSolicitud,int codigoResultado, Intent intent) {
        super.onActivityResult(codigoSolicitud, codigoResultado, intent);
        if(codigoSolicitud!=1 || codigoResultado!=RESULT_OK) return;
        double[] coordenadas = intent.getDoubleArrayExtra("Coordenadas");
        find_weather(getUrl(coordenadas));
    }

    public void find_weather(String urlString) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONObject("main");
                    JSONObject windObject = response.getJSONObject("wind");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject weatherObject = array.getJSONObject(0);

                    String ciudad = response.getString("name");

                    String tiempoAtmosferico= weatherObject.getString("main");
                    String descripcionTiempoAtmosferico = weatherObject.getString("description");

                    double temperatura = mainObject.getDouble("temp");
                    double sensacionTermica = mainObject.getDouble("feels_like");
                    double minTemperatura =  mainObject.getDouble("temp_min");
                    double maxTemperatura = mainObject.getDouble("temp_max");
                    double presionAtmosferica = mainObject.getDouble("pressure");
                    double humedad = mainObject.getDouble("humidity");

                    double velocidadViento = windObject.getDouble("speed");

                    Date fechaActual = new Date();
                    fechaActual.getTime();

                    insertarTexView(ciudad,descripcionTiempoAtmosferico,temperatura,sensacionTermica,
                            minTemperatura,maxTemperatura,presionAtmosferica,humedad,velocidadViento);
                    ponerFotoFondo(descripcionTiempoAtmosferico);
                    insertarTiempoCiudad(ciudad,tiempoAtmosferico,descripcionTiempoAtmosferico,
                            temperatura,sensacionTermica,minTemperatura,maxTemperatura,
                            presionAtmosferica,humedad,velocidadViento,fechaActual.toString());

                    ArrayList<String> ciudades = recuperarCiudades();
                    boolean ciudadGuardada = false;
                    for(int i=0;i<ciudades.size();i++){
                        if(ciudad.equals(ciudades.get(i))){
                            ciudadGuardada = true;
                            break;
                        }
                    }
                    if(!ciudadGuardada){
                        insertarCiudad(ciudad);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error: " +error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    public void irMaps(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivityForResult(intent,1);
    }

    public String getUrl(String localizacion){
        return "https://api.openweathermap.org/data/2.5/weather?q=" + localizacion + "&lang=es&appid="+ API_KEY + "&units=metric";
    }

    public String getUrl(double[] coordenadas){
        return "https://api.openweathermap.org/data/2.5/weather?lat="+ coordenadas[0] +"&lon="+ coordenadas[1] + "&lang=es&appid="+ API_KEY + "&units=metric";
    }

    public void insertarTexView(
            String ciudad,
            String descripcionTiempoAtmosferico,
            double temperatura,
            double sensacionTermica,
            double minTemperatura,
            double maxTemperatura,
            double presionAtmosferica,
            double humedad,
            double velocidadViento) {

        tvCiudad.setText(ciudad);
        tvTiempoAtmosferico.setText(descripcionTiempoAtmosferico);
        tvTemperatua.setText(temperatura+"ºC");
        tvSensacionTermica.setText(sensacionTermica+"ºC");
        tvHumedad.setText(humedad+"%");
        tvPresion.setText(presionAtmosferica+"hPa");
        tvVelocidadViento.setText(velocidadViento+"km/h");
        tvTemperaturaMin.setText(((int) minTemperatura)+"ºC");
        tvTemperaturaMax.setText(((int) maxTemperatura)+"ºC");
    }

    public long insertarTiempoCiudad(
            String ciudad,
            String tiempoAtmosferico,
            String descripcionTiempoAtmosferico,
            double temperatura,
            double sensacionTermica,
            double minTemperatura,
            double maxTemperatura,
            double presionAtmosferica,
            double humedad,
            double velocidadViento,
            String fechaActual) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.TiempoCiudades.CIUDAD, ciudad);
        values.put(FeedReaderContract.TiempoCiudades.TIEMPO_ATMOSFERICO, tiempoAtmosferico);
        values.put(FeedReaderContract.TiempoCiudades.DESCRIPCION_TIEMPO_ATMOSFERICO, descripcionTiempoAtmosferico);
        values.put(FeedReaderContract.TiempoCiudades.TEMPERATURA, temperatura);
        values.put(FeedReaderContract.TiempoCiudades.SENSACION_TERMICA, sensacionTermica);
        values.put(FeedReaderContract.TiempoCiudades.MIN_TEMPERATURA, minTemperatura);
        values.put(FeedReaderContract.TiempoCiudades.MAX_TEMPERATURA, maxTemperatura);
        values.put(FeedReaderContract.TiempoCiudades.PRESION_ATMOSFERICA, presionAtmosferica);
        values.put(FeedReaderContract.TiempoCiudades.HUMEDAD, humedad);
        values.put(FeedReaderContract.TiempoCiudades.VELOCIDAD_VIENTO, velocidadViento);
        values.put(FeedReaderContract.TiempoCiudades.FECHA_ACTUAL, fechaActual);
        return db.insert(FeedReaderContract.TiempoCiudades.TABLE_NAME, null, values);
    }

    public void insertarCiudad(String ciudad) {
        ArrayList<String> ciudades = recuperarCiudades();
        String strCiudades = "";
        for(int i=0;i<ciudades.size();i++){
            strCiudades += ciudades.get(i) + "\n";
        }
        strCiudades += ciudad + "\n";
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("Ciudades.txt", Activity.MODE_PRIVATE));
            archivo.write(strCiudades);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            Toast.makeText(this, "Se ha producido un error al guardar la ciudad", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> recuperarCiudades(){
        String[] archivoApuestas = fileList();
        ArrayList<String> ciudades = new ArrayList<>();
        if(existeArchivo(archivoApuestas,"Ciudades.txt")){
            try{
                InputStreamReader archivo = new InputStreamReader(openFileInput("Ciudades.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while(linea!=null){
                    ciudades.add(linea);
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            }catch(IOException e){
                Toast.makeText(this,"Se ha producido un error al extraer las ciudades",Toast.LENGTH_SHORT).show();
            }
        }
        return ciudades;
    }

    public static boolean existeArchivo(String[] archivos,String nombreArchivo){
        for(int i=0;i<archivos.length;i++){
            if(nombreArchivo.equals(archivos[i])){
                return true;
            }
        }
        return false;
    }

    public void selecionarCiudad(View view){
        String ciudad = spinner.getSelectedItem().toString();
        find_weather(getUrl(ciudad));
    }

    public void ponerFotoFondo(String descripcionTiempoAtmosferico){
        switch (descripcionTiempoAtmosferico){
            case "algo de nubes":
                mainActivy.setBackgroundResource(R.drawable.algodenubes);
                break;
            case "cielo claro":
                mainActivy.setBackgroundResource(R.drawable.cieloclaro);
                break;
            case "nieve":
                mainActivy.setBackgroundResource(R.drawable.nieve);
                break;
            case "nubes":
                mainActivy.setBackgroundResource(R.drawable.nubes);
                break;
            case "nubes dispersas":
                mainActivy.setBackgroundResource(R.drawable.nubesdispersas);
                break;
            case "niebla":
                mainActivy.setBackgroundResource(R.drawable.niebla);
                break;
            case "nevada ligera":
                mainActivy.setBackgroundResource(R.drawable.nevadaligera);
                break;
            case "lluvia ligera":
                mainActivy.setBackgroundResource(R.drawable.lluvialigera);
                break;
            case "muy nuboso":
                mainActivy.setBackgroundResource(R.drawable.muynuboso);
                break;
            case "lluvia moderada":
                mainActivy.setBackgroundResource(R.drawable.lluviamoderada);
                break;
            case "lluvia de gran intensidad":
            case "lluvia muy fuerte":
                mainActivy.setBackgroundResource(R.drawable.lluviadegranintensidad);
                break;
        }
    }

    public void recuperarTiempoCiudad(String ciudad){
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TiempoCiudades._ID,
                FeedReaderContract.TiempoCiudades.CIUDAD,
                FeedReaderContract.TiempoCiudades.TIEMPO_ATMOSFERICO,
                FeedReaderContract.TiempoCiudades.DESCRIPCION_TIEMPO_ATMOSFERICO,
                FeedReaderContract.TiempoCiudades.TEMPERATURA,
                FeedReaderContract.TiempoCiudades.SENSACION_TERMICA,
                FeedReaderContract.TiempoCiudades.MIN_TEMPERATURA,
                FeedReaderContract.TiempoCiudades.MAX_TEMPERATURA,
                FeedReaderContract.TiempoCiudades.PRESION_ATMOSFERICA,
                FeedReaderContract.TiempoCiudades.HUMEDAD,
                FeedReaderContract.TiempoCiudades.VELOCIDAD_VIENTO
        };
        String columnaWhere = FeedReaderContract.TiempoCiudades._ID + " = ?";
        String[] valorWhere = {ciudad };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TiempoCiudades.TABLE_NAME,   // The table to query
                columnasARetornar,             // The array of columns to return (pass null to get all)
                columnaWhere,              // The columns for the WHERE clause
                valorWhere,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while(cursorConsulta.moveToNext()){
            //et_id.setText(cursorConsulta.getLong(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID))+"");
            //et_nombre.setText(cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_1)));
            //et_valor.setText(cursorConsulta.getLong(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_2))+"");
        }
        cursorConsulta.close();
    }
}
