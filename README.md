# Simple Weather App

## MainActivity

Main activity responsible for managing the UI and navigation.

### onCreate()

- Initializes the activity when created.
  - Sets content view to "activity_main" layout.
  - Replaces the root view with CitiesFragment.

### gotoDetails(City city)

- Callback method triggered when a city is selected.
  - Replaces the root view with WeatherForecastFragment for the selected city.
  - Adds the transaction to the back stack for navigation purposes.

## CitiesFragment

Fragment responsible for displaying a list of cities and fetching data from an API.

### onCreateView()

- Inflates the layout for the fragment.

### onViewCreated()

- Sets up the ListView and ArrayAdapter.
- Defines onItemClick listener for ListView items.
- Calls `getCities()` to start fetching data.

### getCities()

- Makes an HTTP GET request to retrieve a list of cities from the API.
- Parses the JSON response and populates the `cities` ArrayList.
- Notifies the adapter of dataset changes on the UI thread.

### onAttach()

- Attaches the fragment to the context to communicate with the MainActivity.

### CitiesFragmentListener

- Interface for communicating with the MainActivity to replace the current fragment with WeatherForecastFragment on item click in the ListView.

## WeatherForecastFragment

Fragment responsible for displaying weather forecasts for a specific city.

### newInstance(City city)

- Static method to create a new instance of WeatherForecastFragment with a given city.
- Sets the city as an argument to pass to the fragment.

### onCreate()

- Initializes the fragment.
- Retrieves the city object passed as an argument.

### onCreateView()

- Inflates the layout for the fragment.

### onViewCreated()

- Displays the city name.
- Calls `getWeatherApi()` to fetch weather data.
- Sets up the RecyclerView and adapter for displaying weather forecasts.

### getWeatherApi()

- Makes an HTTP GET request to retrieve weather data from the Weather API.
- Parses the JSON response to extract the forecast URL.
- Calls `getForecastApi()` with the forecast URL.

### getForecastApi(String forecastURL)

- Makes an HTTP GET request to retrieve forecast data from the forecast URL.
- Parses the JSON response to extract forecast details.
- Updates the RecyclerView adapter with the forecast data.

### WeatherForecastAdapter

- Inner class RecyclerView adapter for displaying weather forecasts.

#### onCreateViewHolder()

- Inflates the layout for forecast list items.

#### onBindViewHolder()

- Binds forecast data to the ViewHolder.

#### getItemCount()

- Returns the number of forecast items.

#### WeatherViewHolder

- Inner class representing ViewHolder for weather forecast items.

##### setupUI()

- Sets up the UI elements with forecast data.

## City Class

Model class representing a city.

### Constructors

Model class representing a cities, states, latitude and longitude.

- `City()`: Default constructor.
- `City(String name, String state, double lat, double lng)`: Constructor with parameters.
- `City(JSONObject jsonObject) throws JSONException`: Constructor that parses a JSON object to initialize city data.

## Forecast

Model class representing a weather forecast.

### Constructors

- `Forecast()`: Default constructor.
- `Forecast(JSONObject jsonObject) throws JSONException`: Constructor that parses a JSON object to initialize forecast data.

**ALL APIs Tested Using Postman**