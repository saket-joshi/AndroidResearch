package com.example.blackbeard.myapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView latView;
    TextView longView;
    Button requestLocationButton;
    Button requestContinuousLocation;
    Button stopLocation;
    ListView locationList;
    LocationManager locationManager;
    ArrayAdapter<String> addressAdapter;
    ArrayList<String> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latView = (TextView)findViewById(R.id.latView);
        longView = (TextView)findViewById(R.id.longView);
        requestLocationButton = (Button)findViewById(R.id.requestLocationButton);
        requestContinuousLocation = (Button)findViewById(R.id.requestContinuousLocation);
        stopLocation = (Button)findViewById(R.id.stopLocation);
        locationList = (ListView)findViewById(R.id.locationList);

        addressList = new ArrayList<String>(); addressList.add("Location Info: ");

        addressAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, addressList);
        locationList.setAdapter(addressAdapter);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        /*List<String> providers = locationManager.getProviders(false);*/
        /*LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);*/

        requestLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // This would invoke the location manager in the application
                    /*Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                } catch (SecurityException ex) {
                    Toast.makeText(MainActivity.this, "GPS Provider is not enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        requestContinuousLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, continuousLocationListener);
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        });

        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationManager.removeUpdates(continuousLocationListener);
                } catch (SecurityException ex) {

                }
            }
        });
    }

    private final LocationListener continuousLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                addressAdapter.add(addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1));
            } catch (IOException ex) {
                Log.e("LOCATION", "IO Exception", ex);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            getStreetAddress(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void getStreetAddress (Location location) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            latView.setText(addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1));
            longView.setText(addresses.get(0).getLocality());

            for (Address address : addresses) {
                Log.d("ADDRESS", address.toString());
            }

            /*SAMPLE OUPTUT (MAX ADDRESSES - 2)
            * Address[addressLines=[0:"127, Sarjaa Rd",1:"Sanewadi, Aundh",2:"Pune, Maharashtra 411007",3:"India"],
            *   locality=Pune,
            *   postalCode=411007,
            *   countryCode=IN,
            *   countryName=India,
            *   phone=null,
            *   url=null,
            *   extras=null]
            *
            * Address[addressLines=[0:"Goodwill Society, Aundh",1:"Pune, Maharashtra 411007",2:"India"],
            *   locality=Pune,
            *   postalCode=null,
            *   countryCode=IN,
            *   countryName=India,
            *   phone=null,
            *   url=null,
            *   extras=null]
            */
        } catch (IOException ex) {
            Log.e("LOCATION", "IO Exception", ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
