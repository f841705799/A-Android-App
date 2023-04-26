package a0547110.tees.ac.uk.eatwell;



import android.annotation.SuppressLint;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mRecyclerView;
    private ShopAdapter ShopAdapter;
    private List<Shop> list = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private ProgressBar progressBar;
    private String latitude;
    private String longitude;
    private String Response;
    private JSONObject jsonObject;



    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        // Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getContext());
        */
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                fineLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                            }
                            Boolean coarseLocationGranted = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            }
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.

                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                                Log.d("DiscoverFragment", "No location access granted.");
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        mRecyclerView = view.findViewById (R.id.reclcler_view);
        progressBar = view.findViewById(R.id.progressBar);
        // Inflate the layout for this fragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(currentlocation -> {
                    // Got last known location. In some rare situations this can be null.
                    if (currentlocation != null) {
                        latitude = String.valueOf(currentlocation.getLatitude());
                        longitude = String.valueOf(currentlocation.getLongitude());
                        Log.d("DiscoverFragment", "onCreate: " + currentlocation.toString());
                        String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+"%2C"+longitude+"&radius=1500&type=restaurant&keyword=restaurant&key="+BuildConfig.MAPS_API_KEY;
                        Log.d("DiscoverFragment", "initData: " + url);
                        // Request a string response from the provided URL.
                        sendRequestWithHttpURLConnection(url);

                    }
                    else {
                        Log.d("DiscoverFragment", "onCreate: location is null");
                    }
                });

        mRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        ShopAdapter = new ShopAdapter (getActivity (),list);
        mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setItemAnimator (new DefaultItemAnimator());
        mRecyclerView.setAdapter (ShopAdapter);
        mRecyclerView.addItemDecoration (new DividerItemDecoration (getActivity (), DividerItemDecoration.VERTICAL));

        return view;
    }


    private void updateUI() {
        ShopAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void sendRequestWithHttpURLConnection(String Url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(Url);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.v("DiscoverFragment", response.toString());
                    parseJSONWithJSONObject(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();

                    }
                }
            }
        }).start();
    }

        private void parseJSONWithJSONObject(String jsonData){
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                if (jsonObject.getString("status").equals("OK")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        String address = result.getString("vicinity");
                        Double rating = result.getDouble("rating");
                        String photo_url;
                        try {
                            String photo_reference = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                            photo_url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+photo_reference+"&key="+BuildConfig.MAPS_API_KEY;
                        }catch (Exception e){
                            photo_url = "https://archive.org/download/no-photo-available/no-photo-available.png";
                        }
                        Shop shop = new Shop(name, address, photo_url, rating);
                        list.add(shop);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            updateUI();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }
    }
