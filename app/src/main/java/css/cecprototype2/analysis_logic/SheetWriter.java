package css.cecprototype2.analysis_logic;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *  This code writes to the Google sheet at <a href="https://docs.google.com/spreadsheets/d/1ddKMcrmgoL6ImzSS3A5NXnaVW5gmLX7j2gk90CX3QOs/edit?usp=sharing">Google sheet </a>
 *  ----
 *  It uses a Apps Script named Chem Sheet Writer. The script assumes the HTTP post contains an action parameters that
 *     must be either "calibrate" or "analysis"
 *  Link to Apps Script: <a href="https://docs.google.com/spreadsheets/d/1ddKMcrmgoL6ImzSS3A5NXnaVW5gmLX7j2gk90CX3QOs/edit?usp=sharing">Link to Apps Script</a>
 */
public class SheetWriter {

    Context context;        // we need the app context to write the Volley HTTP post
    static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now;
    String formattedDateTime;

    public SheetWriter(Context context) {
        this.context = context;
        now = LocalDateTime.now();
        formattedDateTime = DTF.format(now);
    }

    public void writeCalibrationToSheets(String description, String notes, String filename, ArrayList<Double> calibrateValues, Double slope, Double rSquared, ArrayList<Double> concentrations) {
        Log.d("CIS 4444", "SheetWriter --- writeCalibrationToSheets ");
        formattedDateTime = DTF.format(now);
        // The Google Sheets URL is stored in the strings.xml file
        String urlCalibrate = context.getResources().getString(context.getResources().getIdentifier("google_sheets_url_calibrate", "string", context.getPackageName()));
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCalibrate,
                response -> {Log.d("CIS 4444", "SheetWriter --- HTTP Response Received: " + response);},
                error -> {Log.d("CIS 4444", "SheetWriter --- HTTP Error Received: " + error);}
        ) {
            @Override
            protected Map<String, String> getParams() {
                Log.d("CIS 4444", "Params being set");
                Map<String, String> params = new HashMap<>();
                params.put("action", "calibrate");
                params.put("date", formattedDateTime);
                params.put("description", description);
                params.put("notes", notes);
                params.put("filename", filename);
                params.put("slope", slope.toString());
                params.put("rsquared", rSquared.toString());
                params.put("c1", calibrateValues.get(0).toString());
                params.put("c2", calibrateValues.get(1).toString());
                params.put("c3", calibrateValues.get(2).toString());
                params.put("c4", calibrateValues.get(3).toString());
                params.put("c5", calibrateValues.get(4).toString());
                params.put("c6", calibrateValues.get(5).toString());

                params.put("concentration1", concentrations.get(0).toString());
                params.put("concentration2", concentrations.get(1).toString());
                params.put("concentration3", concentrations.get(2).toString());
                params.put("concentration4", concentrations.get(3).toString());
                params.put("concentration5", concentrations.get(4).toString());
                params.put("concentration6", concentrations.get(5).toString());
                return params;
            }
        };
        // Set a longer timeout for request
        int TIME_OUT = 2000; //use 5 sec it will work fine with it..
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    //TODO: MOVE TO SEPERATE COLUMN IN SHEET TO DIFFERENTIATE AVERAGES AND SUMS OR TAG WITH "AVERAGES"
    public void writeCalibrationAveragesToSheets(String description, String notes, String filename, ArrayList<Double> calibrationIntensityAverages, Double slope, Double rSquared, ArrayList<Double> concentrations)
    {
        Log.d("CIS 4444", "SheetWriter --- writeCalibrationAveragesToSheets ");
        formattedDateTime = DTF.format(now);
        // The Google Sheets URL is stored in the strings.xml file
        String urlCalibrate = context.getResources().getString(context.getResources().getIdentifier("google_sheets_url_calibrate", "string", context.getPackageName()));
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCalibrate,
                response -> {Log.d("CIS 4444", "SheetWriter --- HTTP Response Received: " + response);},
                error -> {Log.d("CIS 4444", "SheetWriter --- HTTP Error Received: " + error);}
        ) {
            @Override
            protected Map<String, String> getParams() {
                //Log.d("CIS 4444", "Params being set");
                Map<String, String> params = new HashMap<>();
                params.put("action", "calibrateFromAverage");
                params.put("date", formattedDateTime);
                params.put("description", description);
                params.put("notes", notes);
                params.put("filename", filename);
                params.put("slope", slope.toString());
                params.put("rsquared", rSquared.toString());
                params.put("c1", calibrationIntensityAverages.get(0).toString());
                params.put("c2", calibrationIntensityAverages.get(1).toString());
                params.put("c3", calibrationIntensityAverages.get(2).toString());
                params.put("c4", calibrationIntensityAverages.get(3).toString());
                params.put("c5", calibrationIntensityAverages.get(4).toString());
                params.put("c6", calibrationIntensityAverages.get(5).toString());

                params.put("concentration1", concentrations.get(0).toString());
                params.put("concentration2", concentrations.get(1).toString());
                params.put("concentration3", concentrations.get(2).toString());
                params.put("concentration4", concentrations.get(3).toString());
                params.put("concentration5", concentrations.get(4).toString());
                params.put("concentration6", concentrations.get(5).toString());
                return params;
            }
        };

        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Set a longer timeout for request
        int TIME_OUT = 2000; //use 5 sec it will work fine with it..
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    public void writeAnalysisToSheets(ArrayList<Double> analyzeValues) {
        formattedDateTime = DTF.format(now);
        // The Google Sheets URL is stored in the strings.xml file
        String urlAnalyze = context.getResources().getString(context.getResources().getIdentifier("google_sheets_url_analysis", "string", context.getPackageName()));
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlAnalyze,
                response -> Log.d("CIS 4444", "HTTP Response Received: " + response),
                error -> Log.d("CIS 4444", "HTTP Error Received: " + error)
        ) {
            @Override
            protected Map<String, String> getParams() {
                //Log.d("CIS 4444", "Params being set");
                Map<String, String> params = new HashMap<>();
                params.put("action", "analysis");
                params.put("date", formattedDateTime);
                params.put("a1", analyzeValues.get(0).toString());
                params.put("a2", analyzeValues.get(1).toString());
                params.put("a3", analyzeValues.get(2).toString());
                params.put("a4", analyzeValues.get(3).toString());
                params.put("a5", analyzeValues.get(4).toString());
                params.put("a6", analyzeValues.get(5).toString());
                return params;
            }
        };

        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);

    }
}
