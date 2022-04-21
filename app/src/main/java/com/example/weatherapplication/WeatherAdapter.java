package com.example.weatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder> {

    private Context context;
    private List<Weather> weatherList;

    public WeatherAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new WeatherHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
        Weather weather = weatherList.get(position);

        holder.tvListDay.setText(weather.day);
        holder.tvListStatus.setText(weather.status);
        holder.tvListMin.setText(weather.max + "°");
        holder.tvListMax.setText(weather.min + "°");

        switch (weather.getImage()) {
            case "01d":
            case "01n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_clear_sky);
                break;
            case "02d":
            case "02n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_few_cloud);
                break;
            case "03d":
            case "03n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_scattered_clouds);
                break;
            case "04d":
            case "04n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_broken_clouds);
                break;
            case "09d":
            case "09n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_shower_rain);
                break;
            case "10d":
            case "10n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_rain);
                break;
            case "11d":
            case "11n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_thunderstorm);
                break;
            case "13d":
            case "13n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_snow);
                break;
            case "15d":
            case "15n":
                holder.imageStatus.setImageResource(R.drawable.ic_weather_mist);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    class WeatherHolder extends RecyclerView.ViewHolder {

        private TextView tvListDay, tvListStatus, tvListMax, tvListMin;
        private ImageView imageStatus;

        public WeatherHolder(@NonNull View itemView) {
            super(itemView);
            tvListDay = itemView.findViewById(R.id.tvListDay);
            tvListStatus = itemView.findViewById(R.id.tvListStatus);
            tvListMax = itemView.findViewById(R.id.tvListMax);
            tvListMin = itemView.findViewById(R.id.tvListMin);
            imageStatus = itemView.findViewById(R.id.imageStatus);
        }
    }
}
