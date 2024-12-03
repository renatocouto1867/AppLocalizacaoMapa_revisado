package com.example.applocalizacaomapa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentLocalizacao extends Fragment {
    private Button buttonProxima;
    private Button buttonFechar;
    private Button buttonSetPosition;
    private MapView map;
    private Switch switchTravaMapa;
    private TextInputEditText editTextLat, editTextLong;
    private TextView editLatitudeAtual, editLongitudeAtual;
    private Marker marcador;
    private GeoPoint pointInicial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_localizacao, container, false);

        editTextLat = view.findViewById(R.id.inputLatitude);
        editTextLong = view.findViewById(R.id.inputLongitude);
        switchTravaMapa = view.findViewById(R.id.switchTravaMapa);
        editLatitudeAtual = view.findViewById(R.id.editLatitudeAtual);
        editLongitudeAtual = view.findViewById(R.id.editLongitudeAtual);
        buttonSetPosition = view.findViewById(R.id.btnSetLocation);
        map = view.findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        pointInicial = new GeoPoint(-15.7801, -47.9292);

        marcador = new Marker(map);
        marcador.setPosition(pointInicial);
        marcador.setTitle("Brasília");
        map.setMinZoomLevel(4.0);
        map.setMaxZoomLevel(20.0);
        map.getOverlays().add(marcador);
        map.getController().setZoom(7.00);
        map.getController().setCenter(pointInicial);

        buttonSetPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextLong.getText().toString().isEmpty() || editTextLat.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Por favor, insira latitude e longitude", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Double latitudo = Double.valueOf(editTextLat.getText().toString());
                    Double longitude = Double.valueOf(editTextLong.getText().toString());

                    GeoPoint point1 = new GeoPoint(latitudo, longitude);
                    marcador.setPosition(point1);
                    marcador.setTitle("Local Selecionado");
                    map.getOverlays().clear();
                    map.getOverlays().add(marcador);
                    map.getController().setCenter(point1);
                    map.getController().setZoom(5.00);
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Latitude ou Longitude inválida", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        https://pt.stackoverflow.com/questions/223903/selecionar-checkbox-e-habilitar-campos-no-android
        switchTravaMapa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                marcador.setDraggable(b);
            }
        });


        marcador.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                GeoPoint newPoint = marker.getPosition();

                editLatitudeAtual.setText(getString(R.string.latitude_atual) + newPoint.getLatitude());
                editLongitudeAtual.setText(getString(R.string.longitude_atual) + newPoint.getLongitude());

            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                Toast.makeText(getActivity(), "Vamos começar o passeio", Toast.LENGTH_SHORT).show();

            }
        });


        buttonFechar = view.findViewById(R.id.btnSair);
        buttonFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        buttonProxima = view.findViewById(R.id.btnProximaTela);
        buttonProxima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentMapa();
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
        map.onResume();
    }//onResume

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}