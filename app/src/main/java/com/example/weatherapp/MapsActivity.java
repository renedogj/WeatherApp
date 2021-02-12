package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MapsActivity extends AppCompatActivity{

    private TextView tvCoordenadas;
    private ItemViewModel viewModel;
    private Handler administradorHilo;
    //private String coordenadas;
    private double[] coordenadas = new double[2];

    public MapsActivity() {
        super(R.layout.activity_maps);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvCoordenadas = findViewById(R.id.tvCoordenadas);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.MapsFragment, MapsFragment.class, null)
                    .commit();
        }
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        administradorHilo = new Handler();
        administradorHilo.postDelayed(hiloRefrescoCoordenadas,100);
    }

    private Runnable hiloRefrescoCoordenadas = new Runnable() {
        @Override
        public void run(){
            coordenadas = viewModel.getSelectedItem();
            if(coordenadas != null){
                String strCoordenadas = "Coordenadas: " + Math.floor(coordenadas[0]*100)/100 + "," + Math.floor(coordenadas[1]*100)/100;
                tvCoordenadas.setText(strCoordenadas);
            }else{
                coordenadas = new double[2];
                coordenadas[0] = 00.000d;
                coordenadas[1] = 00.000d;
            }
            administradorHilo.postDelayed(hiloRefrescoCoordenadas,100);
        }
    };

    public void pasarCoordenadas(View view){
        coordenadas = viewModel.getSelectedItem();
        if(coordenadas != null){
            String strCoordenadas = "Coordenadas: " + Math.floor(coordenadas[0]*1000)/1000 + "," + Math.floor(coordenadas[1]*1000)/1000;
            tvCoordenadas.setText(strCoordenadas);
        }else{
            coordenadas = new double[2];
            coordenadas[0] = 00.000d;
            coordenadas[1] = 00.000d;
        }
        administradorHilo.removeCallbacks(hiloRefrescoCoordenadas);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("Coordenadas",coordenadas);
        setResult(RESULT_OK,intent);
        finish();
    }
}