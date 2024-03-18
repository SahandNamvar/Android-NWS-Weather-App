package edu.uncc.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import edu.uncc.weatherapp.fragments.CitiesFragment;
import edu.uncc.weatherapp.fragments.WeatherForecastFragment;
import edu.uncc.weatherapp.models.City;

public class MainActivity extends AppCompatActivity implements CitiesFragment.CitiesFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CitiesFragment())
                .commit();
    }

    @Override
    public void gotoDetails(City city) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, WeatherForecastFragment.newInstance(city))
                .addToBackStack(null)
                .commit();
    }
}