package com.mainactivity.gasprices;

import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    String selected_gas_type = "";
    String selected_city = "";
    Button submitBtn;
    RadioButton petrolButton;
    RadioButton dieselButton;
    Spinner citySpinner;
    TextView price_text, detail_text, date_text;
    CoordinatorLayout main_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnSpinnerItemSelection();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setElevation(0);
        }

        main_activity = (CoordinatorLayout) findViewById(R.id.main_activity);
        // Strict mode for running GET req in main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        addListenerRadioButton();
        submitBtn = (Button) findViewById(R.id.get_price_button);
        price_text = (TextView) findViewById(R.id.price_text);
        detail_text = (TextView) findViewById(R.id.detail_text);
        date_text = (TextView) findViewById(R.id.date_text);
        addListenerSubmitButton();


    }

    public void addListenerOnSpinnerItemSelection() {
        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        citySpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        // Toast.makeText(MainActivity.this,selected_city,Toast.LENGTH_LONG).show();
    }

    private void addListenerRadioButton() {
        final RadioGroup rg1 = (RadioGroup) findViewById(R.id.gas_type_rd_group);
        petrolButton = (RadioButton) findViewById(R.id.radio_petrol);
        dieselButton = (RadioButton) findViewById(R.id.radio_diesel);

        petrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = rg1.getCheckedRadioButtonId();
                RadioButton petrolButton = (RadioButton) findViewById(selected);
                selected_gas_type = (String) petrolButton.getText();
                //Toast.makeText(MainActivity.this, selected_gas_type, Toast.LENGTH_LONG).show();
            }
        });
        dieselButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = rg1.getCheckedRadioButtonId();
                RadioButton dieselButton = (RadioButton) findViewById(selected);
                selected_gas_type = (String) dieselButton.getText();
                //Toast.makeText(MainActivity.this, selected_gas_type, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addListenerSubmitButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(MainActivity.this,"Button Clicked",Toast.LENGTH_LONG).show();

                HttpURLConnection urlConnection = null;
                URL urlString = null;
                selected_city = citySpinner.getSelectedItem().toString();
                String url = "http://45.63.0.61:5001/getprice?city=" + selected_city + "&kind=" + selected_gas_type.toLowerCase();
                // System.out.print(url);
                if (Objects.equals(selected_city, "") || Objects.equals(selected_gas_type, "")) {
                    //Toast.makeText(MainActivity.this, "Please select both City and Gas type.", Toast.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar
                            .make(main_activity, "Please select both City and Gas Type", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    try {
                        urlString = new URL(url);
                        //Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {
                        assert urlString != null;
                        urlConnection = (HttpURLConnection) urlString.openConnection();
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlString.openStream()));
                            String line;
                            StringBuilder sb = new StringBuilder();
                            while ((line = br.readLine()) != null) {
                                sb.append(line);
                            }

                            JSONObject json = new JSONObject(sb.toString());

                            String price = json.getString("price");
                            String date = json.getString("last_update");


                            // System.out.print(sb);
                            // Toast.makeText(MainActivity.this,"Connect! "+price,Toast.LENGTH_LONG).show();
                            detail_text.setText(selected_gas_type + " Price\nin " + selected_city);
                            price_text.setText("₹" + price.trim());
                            date_text.setText("Last Update: " + date.trim());
                        } catch (JSONException e) {
                            //Toast.makeText(MainActivity.this, "Error occured in getting price! Are you connected to Internet?", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
