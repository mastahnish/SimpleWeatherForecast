package com.myos.simpleweatherforecast;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacek on 2016-03-01.
 */
public class RecyclerViewAdapterCities extends RecyclerView.Adapter<RecyclerViewAdapterCities.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;

    List<City> cities = new ArrayList<>();
    private DatabaseHandler db;


    public RecyclerViewAdapterCities(Context context, DisplayRVInferface listener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        db = new DatabaseHandler(context);

        try {
            mCallback = listener;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DisplayRVInferface");
        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.d("tag", getClass().getName() + cities.size() + " " + String.valueOf(cities.isEmpty()));

            view = inflater
                    .inflate(R.layout.recycler_view_row_cities,
                            parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;

    }

    public void addItem(City newCity) {

        cities.add(newCity);
        notifyItemInserted(cities.size());
        notifyDataSetChanged();
        if (!db.checkIfCityExistsAlready(newCity.getCityId())) {
            db.insertCity(newCity.getCityId(), newCity.getCityName(), newCity.getCityJSON(), newCity.getCityFlag());
        }
        mCallback.onDataSetChanged(ADD_FLAG);

    }

    public void removeItem(int position) {
        Log.d("tag", getClass().getName() + " city: " + cities.get(position).getCityName() + " position: " + position);

        db.deleteCity(cities.get(position).getCityId());

        cities.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        mCallback.onDataSetChanged(REMOVE_FLAG);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (!cities.isEmpty()) {
            final City singleCity = cities.get(position);
            Log.d("tag", getClass().getName() + " city name: " + singleCity.getCityName());

            holder.tvCityName.setText(singleCity.getCityName());

            if (singleCity.getCityFlag().matches("1")) {
                holder.cbCityFlag.setChecked(true);
            } else {
                holder.cbCityFlag.setChecked(false);
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeItem(position);
                    return true;
                }
            });

            holder.cbCityFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        db.updateCity(singleCity.getCityId(), DatabaseHandler.getKeyWannaSeeFlag(), "1");
                    } else {
                        db.updateCity(singleCity.getCityId(), DatabaseHandler.getKeyWannaSeeFlag(), "0");
                    }

                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return cities.size();

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbCityFlag;
        TextView tvCityName;

        public MyViewHolder(View itemView) {
            super(itemView);


            cbCityFlag = (CheckBox) itemView.findViewById(R.id.cb_cities_list_flag);
            tvCityName = (TextView) itemView.findViewById(R.id.tv_cities_list_name);
        }


    }

    //Workaround for RecyclerView wrap_content problem
    public final static int ADD_FLAG = 1;
    public final static int REMOVE_FLAG = 0;
    DisplayRVInferface mCallback;

    public interface DisplayRVInferface {
        public void onDataSetChanged(int flag);
    }

}


