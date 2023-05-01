package a0547110.tees.ac.uk.eatwell;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouriteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String userid;
    public List<Shop> list = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public ShopAdapter ShopAdapter;
    private TextView textView;


    public FavouriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouriteFragment newInstance(String param1, String param2) {
        FavouriteFragment fragment = new FavouriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        mRecyclerView = view.findViewById (R.id.reclcler_f_view);
        textView = view.findViewById(R.id.Text_without_login);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            // user is logged in
            userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SQLiteOpenHelper dbHelper = new SQLiteHelper(getActivity().getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor1 = db.query("favourite",null,"Userid = ?",new String[]{String.valueOf(userid)},null,null,null);
            while (cursor1.moveToNext()){
                String placeid = cursor1.getString(0);
                String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeid + "&key=" + BuildConfig.MAPS_API_KEY;
                sendRequestWithHttpURLConnection(url);
            }
            ShopAdapter = new ShopAdapter (getActivity (),list,1);
            mRecyclerView.setLayoutManager (new LinearLayoutManager(getActivity (), LinearLayoutManager.VERTICAL,false));
            mRecyclerView.setItemAnimator (new DefaultItemAnimator());
            mRecyclerView.setAdapter (ShopAdapter);
            mRecyclerView.addItemDecoration (new DividerItemDecoration(getActivity (), DividerItemDecoration.VERTICAL));
        } else {
            // user is not logged in
            mRecyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();

        }


        // Inflate the layout for this fragment
        return view;
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
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            if (jsonObject.getString("status").equals("OK")) {
                JSONObject result = jsonObject.getJSONObject("result");
                String name = result.getString("name");
                String address = result.getString("vicinity");
                Double rating = result.getDouble("rating");
                String Id = result.getString("place_id");
                String photo_url;
                    try {
                        String photo_reference = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                        photo_url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photo_reference + "&key=" + BuildConfig.MAPS_API_KEY;
                    } catch (Exception e) {
                        photo_url = "https://archive.org/download/no-photo-available/no-photo-available.png";
                    }
                    Shop shop = new Shop(name, address, photo_url, rating, Id);
                    list.add(shop);
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
    private void updateUI() {
        ShopAdapter.notifyDataSetChanged();
    }
}