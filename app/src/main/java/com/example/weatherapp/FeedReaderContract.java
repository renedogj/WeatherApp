package com.example.weatherapp;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    public static class TiempoCiudades implements BaseColumns {
        public static final String TABLE_NAME = "TiempoCiudades";
        public static final String CIUDAD = "ciudad";
        public static final String TIEMPO_ATMOSFERICO = "tiempoAtmosferico";
        public static final String DESCRIPCION_TIEMPO_ATMOSFERICO = "descripcionTiempoAtmosferico";
        public static final String TEMPERATURA = "temperatura";
        public static final String SENSACION_TERMICA = "sensacionTerminca";
        public static final String MIN_TEMPERATURA = "minTemperatura";
        public static final String MAX_TEMPERATURA = "maxTemperatura";
        public static final String PRESION_ATMOSFERICA = "presionAtmosferica";
        public static final String HUMEDAD = "humedad";
        public static final String VELOCIDAD_VIENTO = "velocidadViento";
        public static final String FECHA_ACTUAL = "fechaActual";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        CIUDAD + " TEXT," +
                        TIEMPO_ATMOSFERICO + " TEXT," +
                        DESCRIPCION_TIEMPO_ATMOSFERICO + " TEXT," +
                        TEMPERATURA + " REAL," +
                        SENSACION_TERMICA + " REAL," +
                        MIN_TEMPERATURA + " REAL," +
                        MAX_TEMPERATURA + " REAL," +
                        PRESION_ATMOSFERICA + " REAL," +
                        HUMEDAD + " REAL," +
                        VELOCIDAD_VIENTO + " REAL," +
                        FECHA_ACTUAL + " TEXT)";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
