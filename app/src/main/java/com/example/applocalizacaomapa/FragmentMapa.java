package com.example.applocalizacaomapa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentMapa extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button buttonTelaAnterior;
    private Button buttonLocalizacao;
    private MapView mapView;
    private FusedLocationProviderClient clientLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        mapView = view.findViewById(R.id.mapLocalizacao);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        clientLocation = LocationServices.getFusedLocationProviderClient(getContext());
        buttonLocalizacao = view.findViewById(R.id.btnSetLocation);

        buttonLocalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obterLocalizacao();
            }
        });

        buttonTelaAnterior = view.findViewById(R.id.btnTelaAnterior);

        buttonTelaAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentLocalizacao();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    //para pegar a resposta do usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obterLocalizacao();
            } else {
                Toast.makeText(getContext(), "Permissão não obtida", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

//    https://developer.android.com/training/permissions/requesting?hl=pt-br#java
    private void obterLocalizacao() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            clientLocation.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                    addMarcador(point);
                } else {
                    Toast.makeText(getContext(), "Erro ao obter localização. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getContext(),
                    "Este App precisa acessar sua localização.",
                    Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
//obterLocalizacao

    private void addMarcador(GeoPoint point) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle("Voce está aqui");
        mapView.getOverlays().add(marker);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(point);
    }


}