package com.sima.mapsactivity.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.sima.mapsactivity.R;
import com.sima.mapsactivity.databinding.ActivityMainBinding;
import com.sima.mapsactivity.model.Place;
import com.sima.mapsactivity.roomDb.IDOA;
import com.sima.mapsactivity.roomDb.Placedb;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;
    Boolean info;
    Placedb db;
    IDOA placedao;
    double latitude,longitude;
    private final CompositeDisposable compositeDisposable=new CompositeDisposable();
    Place selectedPlace;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLaunch();

        db= Room.databaseBuilder(getApplicationContext(),Placedb.class,"Places").build();
        placedao=db.placedao();


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        Intent intent=getIntent();
        String IntentInfo=intent.getStringExtra("info");

        if(IntentInfo.equals("new")){
            mMap = googleMap;
            mMap.setOnMapLongClickListener(this);

            binding.DeleteButton.setVisibility(View.GONE);
            binding.SaveButton.setVisibility(View.VISIBLE);

            locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);    //değişikliği alacak sınıf
            locationListener=new LocationListener() {                             //değişikliği dinleyip işlem yyapma
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    sharedPreferences=MainActivity.this.getSharedPreferences("com.sima.mapsactivity",MODE_PRIVATE);
                    info=sharedPreferences.getBoolean("info",false);

                    if(info == false){
                        LatLng userlocation=new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,15));
                        sharedPreferences.edit().putBoolean("info",true).apply();
                    }
                }

            };
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.getRoot(),"permission needed for maps", Snackbar.LENGTH_INDEFINITE).setAction("give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                }else{
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }

            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }


        }else{

            mMap.clear();
            selectedPlace=(Place) intent.getSerializableExtra("place");
            LatLng selectedLatLng=new LatLng(selectedPlace.latitude,selectedPlace.longitude);

            mMap.addMarker(new MarkerOptions().position(selectedLatLng).title(selectedPlace.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng,15));

            binding.PlaceNameText.setText(selectedPlace.name);
            binding.SaveButton.setVisibility(View.GONE);
            binding.DeleteButton.setVisibility(View.VISIBLE);

        }




    }

    private void registerLaunch(){
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result==true){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                        Location userlastlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //son konumu tutan sınıftan al

                        if(userlastlocation!=null){
                            LatLng userloc=new LatLng(userlastlocation.getLatitude(),userlastlocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userloc,15));
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                }else{
                    Toast.makeText(MainActivity.this,"permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        latitude=latLng.latitude;
        longitude=latLng.longitude;


    }


    public void Save(View view){

       Place place=new Place(binding.PlaceNameText.getText().toString(),latitude,longitude);
       //placedao.insert(place);
       // --> threadler  MAİN YÜKLENMESİ KÖTÜ OLUR KULLANCI KISMI OLDUĞUNDAN (UI), DEFAULT CPUYU YORAN ARKA PLAN ,IO İSE DATABASE YADA NETWORK THREADLERİ

        compositeDisposable.add(placedao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity.this::HandleResponse)
        );




    }

    private void HandleResponse(){
        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    public void Delete(View view){
       compositeDisposable.add(placedao.delete(selectedPlace).subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(MainActivity.this::HandleResponse));

    }



}

      