package com.frothapps.bite.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.leotrieu.foodtasker.R;
import com.frothapps.bite.Objects.Restaurant;
import com.frothapps.bite.Adapters.RestaurantAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantListFragment extends Fragment {

    private ArrayList<Restaurant> restaurantArrayList;
    private RestaurantAdapter adapter;
    private Restaurant[] restaurants = new Restaurant[]{};

    public RestaurantListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        restaurantArrayList = new ArrayList<Restaurant>();
        adapter = new RestaurantAdapter(this.getActivity(), restaurantArrayList);

        ListView restaurantListView = (ListView) getActivity().findViewById(R.id.restaurant_list);
        restaurantListView.setAdapter(adapter);

        // Get list of restaurants
        getRestaurants();

        // Add the Search function
        addSearchFunction();
    }

    private void getRestaurants() {
        String url = getString(R.string.API_URL) + "/customer/restaurants/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESTAURANTS LIST", response.toString());

                        // Convert JSON data to JSON Array
                        JSONArray restaurantsJSONArray = null;

                        try {
                            restaurantsJSONArray = response.getJSONArray("restaurants");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Convert Json Array to Restaurant Array
                        Gson gson = new Gson();
                        restaurants = gson.fromJson(restaurantsJSONArray.toString(), Restaurant[].class);

                        // Refresh ListView with up-to-date data
                        restaurantArrayList.clear();
                        restaurantArrayList.addAll(new ArrayList<Restaurant>(Arrays.asList(restaurants)));
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jsonObjectRequest);
    }

    private void addSearchFunction() {
        EditText searchInput = (EditText) getActivity().findViewById(R.id.res_search);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("SEARCH", charSequence.toString());

                // Update the Restaurant List
                restaurantArrayList.clear();
                for (Restaurant restaurant : restaurants) {
                    if (restaurant.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        restaurantArrayList.add(restaurant);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
