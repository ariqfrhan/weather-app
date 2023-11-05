package maf.mobile.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ItemVH> {
    private Context context;
    private ArrayList<Weather> weatherData;

    public WeatherAdapter(Context context, ArrayList<Weather> weatherData){
        this.context = context;
        this.weatherData = weatherData;
    }

    @NonNull
    @Override
    public WeatherAdapter.ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(context)
                .inflate(R.layout.weather_item,parent, false);
        ItemVH viewholder = new ItemVH(card);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ItemVH holder, int position) {
        Weather w = weatherData.get(position);
        holder.tvDate.setText(w.date);
        holder.ivIcon.setImageResource(w.icon);
        holder.tvCondition.setText(w.condition);
    }

    @Override
    public int getItemCount() {
        return weatherData.size();
    }

    class ItemVH extends RecyclerView.ViewHolder {

        private ImageView ivIcon;
        private TextView tvCondition;
        private TextView tvDate;
        private int weatherCode;

        public ItemVH(@NonNull View itemView) {
            super(itemView);
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDateModal);
            this.ivIcon = (ImageView) itemView.findViewById(R.id.ivIconModal);
            this.tvCondition = (TextView) itemView.findViewById(R.id.tvConditionModal);
        }
    }
}
