package edu.uncc.weatherapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import edu.uncc.weatherapp.R;
import edu.uncc.weatherapp.databinding.ForecastListItemBinding;
import edu.uncc.weatherapp.databinding.FragmentWeatherForecastBinding;
import edu.uncc.weatherapp.models.City;
import edu.uncc.weatherapp.models.Forecast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WeatherForecastFragment extends Fragment {

    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    FragmentWeatherForecastBinding binding;

    private final OkHttpClient client = new OkHttpClient();
    ArrayList<Forecast> forecasts = new ArrayList<>();
    WeatherForecastAdapter adapter;

    City mCity;

    public WeatherForecastFragment() {}

    // Receive the argument (city object) sent from CitiesFragment
    public static WeatherForecastFragment newInstance(City city) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Parse the Serializable argument (object) received and assign it to a local instance of City
            mCity = (City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewCityName.setText(mCity.getName() + ", " + mCity.getState());

        // Call the getWeatherApi()
        getWeatherApi();

        // Set the adapter for RecyclerView and render it
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WeatherForecastAdapter();
        binding.recyclerView.setAdapter(adapter);
    }

    // GET HTTP request to Weather API
    public void getWeatherApi() {

        Log.d("debug", "latitude & longitude: " + mCity.getLat() + " " + mCity.getLng());

        // Build URL with with the given latitude and longitude
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://api.weather.gov/points"))
                .newBuilder()
                .addPathSegment(mCity.getLat() + "," + mCity.getLng())
                .build();

        Log.d("debug", "URL Requesting: " + url);

        // Create a request object given URL.
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Make Async call and enqueue the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the Response body as one whole string
                    String body = response.body().string();

                    try {
                        // From that string, create one whole JSON object (similar to the response on Postman)
                        JSONObject weatherApiBody = new JSONObject(body);
                        // In the response object are 3 key-value pairs. Get the 'properties' VALUE which is a JSON object that consist of many Key-Value pairs
                        JSONObject weatherApiProperties = weatherApiBody.getJSONObject("properties");
                        // Further parse the response and inside the properties object, get the 'forecast' VALUE which is a URL to the forecast data for the current city
                        String forecastURL = weatherApiProperties.getString("forecast");
                        // Call the ForecastApi() - which should make a request to the forecastURL
                        getForecastApi(forecastURL);
                        Log.d("debug", "Forecast URL: " + forecastURL);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void getForecastApi(String forecastURL) {
        if (!forecastURL.isEmpty()) {

            // Build a request object given the forecast URL
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            // Make a new Async call to fetch the forecast data
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        try {
                            JSONObject forecastApiBody = new JSONObject(body);
                            // the VALUE of the KEY 'properties' is an object (consist of many key-value pairs)
                            JSONObject forecastApiProperties = forecastApiBody.getJSONObject("properties");
                            // Inside 'properties', we are interested in the property 'periods' which has a value of JSON Array (This array consist of details about the weather forecast for the specific city)
                            JSONArray forecastApiPeriodsArray = forecastApiProperties.getJSONArray("periods");
                            forecasts.clear();

                            // For each forecast period, create a Forecast object and add it to the 'forecasts' local arrayList
                            for (int i = 0; i < forecastApiPeriodsArray.length(); i++) {
                                JSONObject forecastJsonObject = forecastApiPeriodsArray.getJSONObject(i);
                                Forecast forecast = new Forecast(forecastJsonObject);
                                forecasts.add(forecast);
                            }

                            // Notify adapter dataset has changed
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
        } else {
            Log.d("debug", "getForecastApi: foreCastURL is empty");
        }
    }

    // RecyclerView Adapter
    public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.WeatherViewHolder> {

        @NonNull
        @Override
        public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ForecastListItemBinding  viewHolderBinding = ForecastListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new WeatherViewHolder(viewHolderBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
            Forecast forecast = forecasts.get(position);
            holder.setupUI(forecast);
        }

        @Override
        public int getItemCount() {
            return forecasts.size();
        }

        class WeatherViewHolder extends RecyclerView.ViewHolder {

            ForecastListItemBinding mBinding;
            Forecast mForecast;

            public WeatherViewHolder(ForecastListItemBinding viewHolderBinding) {
                super(viewHolderBinding.getRoot());
                mBinding = viewHolderBinding;
            }

            @SuppressLint("SetTextI18n")
            public void setupUI(Forecast forecast) {
                this.mForecast = forecast;
                mBinding.textViewDateTime.setText(mForecast.getStartTime());
                mBinding.textViewTemperature.setText(mForecast.getTemperature() + ".0" + " F");
                mBinding.textViewHumidity.setText("Humidity: " + mForecast.getHumidity() + "%");
                mBinding.textViewWindSpeed.setText("Wind speed: " + mForecast.getWindSpeed());
                mBinding.textViewForecast.setText(mForecast.getShortForecast());
                ImageView imageView = mBinding.imageView;
                Picasso.get().load(mForecast.getIconURL()).into(imageView);
            }
        }
    }
}