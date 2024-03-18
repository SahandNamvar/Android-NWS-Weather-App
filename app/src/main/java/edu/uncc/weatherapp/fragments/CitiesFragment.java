package edu.uncc.weatherapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.uncc.weatherapp.R;
import edu.uncc.weatherapp.databinding.FragmentCitiesBinding;
import edu.uncc.weatherapp.models.City;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CitiesFragment extends Fragment {

    FragmentCitiesBinding binding;

    private final OkHttpClient client = new OkHttpClient();

    ArrayList<City> cities = new ArrayList<>();

    ListView listView;

    ArrayAdapter<City> adapter;

    public CitiesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = binding.listView;

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, cities);
        listView.setAdapter(adapter);

        // ListView onItem Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Select the city object from the 'cities' arrayList based on the position of the item
                City citySelected = cities.get(position);
                mListener.gotoDetails(citySelected);
            }
        });

        // Main function to start fetching data
        getCities();
    }

    // HTTP Request to get cities
    public void getCities() {

        // Build a request with URL
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/cities")
                .build();

        // Async call to enqueue the HTTP request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            // Triggered when response comes back
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    // Get the body of the response as string
                    String body = response.body().string();
                    Log.d("debug", "HTTP GET Cities body: " + body);

                    try {
                        // Convert the string into JSON object
                        JSONObject citiesJson = new JSONObject(body);
                        Log.d("debug", "HTTP GET Cities JSON: " + citiesJson);
                        // 'cities' is one of the KEYS  in the JSON object with the VALUE of an Array of objects (each object consist of name, state, lan, lng)
                        JSONArray citiesJsonArray = citiesJson.getJSONArray("cities");
                        cities.clear();

                        // For each object in the array, create a local instance of that object and add it to the 'cities' arrayList
                        for (int i = 0; i < citiesJsonArray.length(); i++) {
                            JSONObject cityJsonObject = citiesJsonArray.getJSONObject(i);
                            City city = new City(cityJsonObject);
                            cities.add(city);
                            // Log.d("debug", "city: " + city.getName());
                        }

                        // Notify the Adapter that dataset has changed
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
    }

    // Interface for communicating with the Main Activity to replace current Fragment with WeatherForecastFragment onItem click in the ListView
    CitiesFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CitiesFragmentListener) context;
    }

    public interface CitiesFragmentListener {
        void gotoDetails(City city);
    }
}