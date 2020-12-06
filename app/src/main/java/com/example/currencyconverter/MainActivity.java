package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// URL that sent through internet(dynamic) and Input x Rate (final data) is printed,can be seen in Run section.
//For some randomly choosen conversion,converter itself returns with value 0 ,even for some dates of conversion to USD-TRY.Can be checked manually through : http://currencyconverter.kowabunga.net/converter.asmx?op=GetConversionAmount

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String inputstr;
    String inputDate;
    String dynURL;
    String dynURL2;
    double currencyrate=0;
    private Spinner spinner;
    private Spinner spinner2;
    private static final String[] paths = {"AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZN","BAM","BBD","BDT","BGN","BHD","BIF","BND","BOB","BRL","BSD","BWP","BYN","BZD","CAD","CDF","CHF","CLP","CNY","COP","CRC","CUP","CVE","CYP","CZK","DJF","DKK","DOP","DZD","EEK","EGP","ERN","ETB","EUR","FJD","GBP","GEL","GHS","GIP","GMD","GNF","GTQ","GYD","HKD","HNL","HRK","HTG","HUF","IDR","ILS","INR","IQD","IRR","ISK","JMD","JOD","JPY","KES","KGS","KHR","KMF","KRW","KWD","KZT","LAK","LBP","LKR","LRD","LSL","LTL","LVL","LYD","MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRO","MRU","MTL","MUR","MVR","MWK","MXN","MYR","MZN","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR","PLN","PYG","QAR","RON","RSD","RUB","RWF","SAR","SBD","SCR","SDG","SEK","SGD","SIT","SKK","SLL","SOS","SRD","SSP","STN","SVC","SYP","SZL","THB","TJS","TMT","TND","TOP","TRY","TTD","TWD","TZS","UAH","UGX","USD","UYU","UZS","VES","VND","VUV","WST","XAF","XCD","XOF","XPF","YER","ZAR","ZMW"};
    private static final String[] paths2 = {"AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZN","BAM","BBD","BDT","BGN","BHD","BIF","BND","BOB","BRL","BSD","BWP","BYN","BZD","CAD","CDF","CHF","CLP","CNY","COP","CRC","CUP","CVE","CYP","CZK","DJF","DKK","DOP","DZD","EEK","EGP","ERN","ETB","EUR","FJD","GBP","GEL","GHS","GIP","GMD","GNF","GTQ","GYD","HKD","HNL","HRK","HTG","HUF","IDR","ILS","INR","IQD","IRR","ISK","JMD","JOD","JPY","KES","KGS","KHR","KMF","KRW","KWD","KZT","LAK","LBP","LKR","LRD","LSL","LTL","LVL","LYD","MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRO","MRU","MTL","MUR","MVR","MWK","MXN","MYR","MZN","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR","PLN","PYG","QAR","RON","RSD","RUB","RWF","SAR","SBD","SCR","SDG","SEK","SGD","SIT","SKK","SLL","SOS","SRD","SSP","STN","SVC","SYP","SZL","THB","TJS","TMT","TND","TOP","TRY","TTD","TWD","TZS","UAH","UGX","USD","UYU","UZS","VES","VND","VUV","WST","XAF","XCD","XOF","XPF","YER","ZAR","ZMW"};

    public void XMLprs (StringBuffer STR) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput( new StringReader( STR.toString()) );
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if(eventType == XmlPullParser.START_TAG) {
                System.out.println("Start tag "+xpp.getName());
            } else if(eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag "+xpp.getName());
            } else if(eventType == XmlPullParser.TEXT) {
                currencyrate=Double.parseDouble(xpp.getText());
                System.out.println("Input x Rate  : "+currencyrate);
            }
            eventType = xpp.next();
        }
        System.out.println("End document");
    }
    EditText edit;
    EditText edit2;
    EditText edit3;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit=findViewById(R.id.firstbar);
        edit2=findViewById(R.id.secondbar);
        edit3 =findViewById(R.id.datebar);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner2 = (Spinner)findViewById(R.id.spinner2);

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,paths);
        ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        textView = findViewById(R.id.textarea);
       Button button = (Button) findViewById(R.id.Button1);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                inputstr = edit.getText().toString();
                edit2.setText(String.valueOf(currencyrate));
                inputDate = edit3.getText().toString();

                new RequestTask().execute();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        if (parent.getId() == R.id.spinner) {
            for (int i = 0; i < 157; i++) {
                if (position == i) {
                    dynURL = paths[i];
                            //"http://currencyconverter.kowabunga.net/converter.asmx/GetConversionAmount?CurrencyFrom=" + paths[i] + "&CurrencyTo=try&RateDate=05.12.2020&Amount=100";
                    new RequestTask().execute();
                }
            }
        }
        if(parent.getId() == R.id.spinner2)
        {
            for (int j = 0; j < 157; j++) {
                if (position == j) {
                    dynURL2 = paths2[j];
                            //"http://currencyconverter.kowabunga.net/converter.asmx/GetConversionAmount?CurrencyFrom=" + paths[i] + "&CurrencyTo="+paths2[i]+"&RateDate=05.12.2020&Amount=100";
                    new RequestTask().execute();
                }
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
    class RequestTask extends AsyncTask<String, String, StringBuffer> {
        @Override
        protected StringBuffer doInBackground(String... uri) {
           StringBuffer responseString = new StringBuffer();
            try {
                String myurl = "http://currencyconverter.kowabunga.net/converter.asmx/GetConversionAmount?CurrencyFrom=" + dynURL + "&CurrencyTo="+dynURL2+"&RateDate="+inputDate+"&Amount="+inputstr; //Final url sent through internet
                System.out.println("URL sent is : "+myurl);
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                responseString.append(inputLine); }
                in.close();
                } catch (Exception e) {}
            return responseString;
        }
        @Override
        protected void onPostExecute(StringBuffer result) {
            super.onPostExecute(result);
            try {
                XMLprs(result);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}