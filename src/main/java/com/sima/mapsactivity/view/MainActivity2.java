package com.sima.mapsactivity.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sima.mapsactivity.R;
import com.sima.mapsactivity.adapter.JavaAdapter;
import com.sima.mapsactivity.databinding.ActivityMain2Binding;
import com.sima.mapsactivity.model.Place;
import com.sima.mapsactivity.roomDb.IDOA;
import com.sima.mapsactivity.roomDb.Placedb;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();
    Placedb db;
    IDOA placedao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMain2Binding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        db= Room.databaseBuilder(getApplicationContext(),Placedb.class,"Places").build();
        placedao=db.placedao();

        compositeDisposable.add(placedao.getall()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity2.this::handlerresponse));

    }
    public void handlerresponse(List<Place> placelist){
        binding.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        JavaAdapter adapter=new JavaAdapter(placelist);
        binding.RecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.places){
            Intent intent= new Intent(MainActivity2.this, MainActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}