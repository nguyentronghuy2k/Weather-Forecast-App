package com.example.weatherapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeWeather extends AppCompatActivity {

    private TextView tvCurrentTemp, tvMaxMinTemp, tvStatus, tvCity, tvDay;
    private Spinner search_spinner;
    private ArrayAdapter<String> cityAdapter;
    private List<Weather> weathers;
    private WeatherAdapter weatherAdapter;
    private RecyclerView recyclerView;
    private ImageView imageWeather;
    private LocationManager locationManager;
    private String longitude = "";
    private String latitude = "";
    ImageView imgView;
    TextView name;
    Button btnSignOut;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_weather);

        tvCurrentTemp = findViewById(R.id.tvCurrentTemp);
        tvMaxMinTemp = findViewById(R.id.tvMaxMinTemp);
        tvStatus = findViewById(R.id.tvStatus);
        tvCity = findViewById(R.id.tvCity);
        tvDay = findViewById(R.id.tvDay);
        imageWeather = findViewById(R.id.imageWeather);
        search_spinner = findViewById(R.id.search_spinner);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(HomeWeather.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(HomeWeather.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeWeather.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {


            @Override
            public void onLocationChanged(@NonNull Location location) {
                String lon = String.valueOf(location.getLongitude());
                String lat = String.valueOf(location.getLatitude());

                if (search_spinner.getSelectedItemPosition() == 0 && !lon.equals(longitude) && !lat.equals(latitude)) {
                    //neu lon-lat thay doi thi cap nhat lai weather



                    longitude = lon;
                    latitude = lat;

                    //cập nhật weather
                    String lat_lon = "lat=" + latitude + "&lon=" + longitude;
                    //api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}
                    String baseURL1 = "https://api.openweathermap.org/data/2.5/weather?";
                    String baseURL2 = "https://api.openweathermap.org/data/2.5/forecast?";
                    getWeather(lat_lon, baseURL1);
                    weathers.clear();
                    getWeatherFiveDay(lat_lon, baseURL2);
                    weatherAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        });

        initView();
    }

    private void setIconWeather(String icon) {
        switch (icon) {
            case "01d":
            case "01n":
                imageWeather.setImageResource(R.drawable.ic_weather_clear_sky);
                break;
            case "02d":
            case "02n":
                imageWeather.setImageResource(R.drawable.ic_weather_few_cloud);
                break;
            case "03d":
            case "03n":
                imageWeather.setImageResource(R.drawable.ic_weather_scattered_clouds);
                break;
            case "04d":
            case "04n":
                imageWeather.setImageResource(R.drawable.ic_weather_broken_clouds);
                break;
            case "09d":
            case "09n":
                imageWeather.setImageResource(R.drawable.ic_weather_shower_rain);
                break;
            case "10d":
            case "10n":
                imageWeather.setImageResource(R.drawable.ic_weather_rain);
                break;
            case "11d":
            case "11n":
                imageWeather.setImageResource(R.drawable.ic_weather_thunderstorm);
                break;
            case "13d":
            case "13n":
                imageWeather.setImageResource(R.drawable.ic_weather_snow);
                break;
            case "15d":
            case "15n":
                imageWeather.setImageResource(R.drawable.ic_weather_mist);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_about:
                showAboutDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About!");
        builder.setMessage("This is Weather Forecast Application");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", null);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void getWeather(String city, String baseURL) {
        RequestQueue requestQueue = Volley.newRequestQueue(HomeWeather.this);
        String keyAPI = "30a076c98219ac9f253bd6774b152601";
        String url = baseURL + city + "&units=metric&lang=vi&appid=" + keyAPI;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String day = jsonObject.getString("dt");
                            String name = jsonObject.getString("name").toString();

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String temp = jsonObjectMain.getString("temp");
                            String temp_max = jsonObjectMain.getString("temp_max");
                            String temp_min = jsonObjectMain.getString("temp_min");

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String str = jsonObjectWeather.getString("description");
                            String icon = jsonObjectWeather.getString("icon");
                            String status = str.substring(0, 1).toUpperCase() + str.substring(1);


                            tvCity.setText(name);
                            tvCurrentTemp.setText(temp + "°");
                            tvMaxMinTemp.setText(temp_max + "° / " + temp_min + "°");
                            tvStatus.setText(status);
                            setIconWeather(icon);

                            long time = Long.valueOf(day);

                            Date date = new Date(time * 1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEEE");

                            String Day = simpleDateFormat.format(date);
                            String Day1 = simpleDateFormat1.format(date);


                            if (Day1.equals("Monday")) {
                                Day1 = "Thứ hai";
                            }
                            if (Day1.equals("Tuesday")) {
                                Day1 = "Thứ ba";
                            }
                            if (Day1.equals("Wednesday")) {
                                Day1 = "Thứ tư";
                            }
                            if (Day1.equals("Thursday")) {
                                Day1 = "Thứ năm";
                            }
                            if (Day1.equals("Friday")) {
                                Day1 = "Thứ sáu";
                            }
                            if (Day1.equals("Saturday")) {
                                Day1 = "Thứ bảy";
                            }
                            if (Day1.equals("Sunday")) {
                                Day1 = "Chủ nhật";
                            }
                            Day = Day1 + " " + Day;






                            tvDay.setText(Day);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeWeather.this, "Fail to take data!Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void getWeatherFiveDay(String city, String baseURL) {
        RequestQueue requestQueue = Volley.newRequestQueue(HomeWeather.this);
        String keyAPI = "30a076c98219ac9f253bd6774b152601";
        String url = baseURL + city + "&units=metric&lang=vi&appid=" + keyAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArrayList.length(); i++) {
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                String day = jsonObjectList.getString("dt");
                                long time = Long.valueOf(day);
                                Date date = new Date(time * 1000L);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM");
                                String Day = simpleDateFormat.format(date);
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEEE");

                                String Day1 = simpleDateFormat1.format(date);


                                if (Day1.equals("Monday")) {
                                    Day1 = "Thứ hai";
                                }
                                if (Day1.equals("Tuesday")) {
                                    Day1 = "Thứ ba";
                                }
                                if (Day1.equals("Wednesday")) {
                                    Day1 = "Thứ tư";
                                }
                                if (Day1.equals("Thursday")) {
                                    Day1 = "Thứ năm";
                                }
                                if (Day1.equals("Friday")) {
                                    Day1 = "Thứ sáu";
                                }
                                if (Day1.equals("Saturday")) {
                                    Day1 = "Thứ bảy";
                                }
                                if (Day1.equals("Sunday")) {
                                    Day1 = "Chủ nhật";
                                }
                                Day = Day1 + " " + Day;

                                JSONObject jsonObjectMain = jsonObjectList.getJSONObject("main");
                                String temp_max = jsonObjectMain.getString("temp_max");
                                String temp_min = jsonObjectMain.getString("temp_min");

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String str = jsonObjectWeather.getString("description");
                                String icon = jsonObjectWeather.getString("icon");
                                String status = str.substring(0, 1).toUpperCase() + str.substring(1);

                                weathers.add(new Weather(Day, status, icon, temp_max, temp_min));

                            }
                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "oke");
                Toast.makeText(HomeWeather.this, "Lỗi khi lấy dữ liệu!Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void initView() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        imgView = findViewById(R.id.imgView);
        name = findViewById(R.id.name);
        btnSignOut = findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    case R.id.btnSignOut:
                        signOut();
                        break;

                }

            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();

            name.setText(personName);
            Glide.with(this).load(String.valueOf(personPhoto)).into(imgView);

        }


        cityAdapter = new ArrayAdapter<String>(HomeWeather.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.city_name));
        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        search_spinner.setAdapter(cityAdapter);
        search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    if (latitude != "" && longitude != "") {
                        String lat_lon = "lat=" + latitude + "&lon=" + longitude;

                        //api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}
                        String baseURL1 = "https://api.openweathermap.org/data/2.5/weather?";
                        String baseURL2 = "https://api.openweathermap.org/data/2.5/forecast?";
                        getWeather(lat_lon, baseURL1);
                        weathers.clear();
                        getWeatherFiveDay(lat_lon, baseURL2);
                        weatherAdapter.notifyDataSetChanged();
                    }

                } else {
                    String str = parent.getItemAtPosition(position).toString();
                    String city = "q=" + str;
                    String baseURL1 = "https://api.openweathermap.org/data/2.5/weather?";
                    String baseURL2 = "https://api.openweathermap.org/data/2.5/forecast?";
                    getWeather(city, baseURL1);
                    weathers.clear();
                    getWeatherFiveDay(city, baseURL2);
                    weatherAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        weathers = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(HomeWeather.this, weathers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeWeather.this, RecyclerView.VERTICAL, false);
        DividerItemDecoration decoration = new DividerItemDecoration(HomeWeather.this, RecyclerView.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(weatherAdapter);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HomeWeather.this, "Bạn đã đăng xuất!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
