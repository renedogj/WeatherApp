package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{

    private ItemViewModel viewModel;
    private GoogleMap mMap;
    private Marker markerDefault;
    private double[] coordenadas = new double[2];

    public MapsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLngMarcador = new LatLng(0,0);
        markerDefault = googleMap.addMarker(new MarkerOptions()
                .position(latLngMarcador)
                .title("Marcador")
                .draggable(true)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMarcador,3));
        googleMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if(marker.equals(markerDefault)){
            coordenadas[0] = marker.getPosition().latitude;
            coordenadas[1] = marker.getPosition().longitude;
            viewModel.selectItem(coordenadas);
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}