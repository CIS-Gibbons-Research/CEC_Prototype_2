package css.cecprototype2.analysis_logic;

import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetsWriter {

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        // Load the credentials from the JSON file
        try (InputStream inputStream = SheetsWriter.class.getResourceAsStream("/ServiceAccountWriterKey.json")) {
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/spreadsheets"));

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory,
                    requestInitializer)
                    .setApplicationName("CECDetectorPrototype2")
                    .build();
        }
    }

    public static void writeToGoogleSheets(String spreadsheetId, String range, List<List<Object>> values) throws IOException, GeneralSecurityException {
        Sheets sheetsService = getSheetsService();

        ValueRange body = new ValueRange()
                .setValues(values);

        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

        // Handle the result as needed
        Log.d("SheetsWriter", "Write to Google Sheets result: " + result);
    }

    public static void appendRowsToGoogleSheets(String spreadsheetId, String sheetName, List<List<Object>> values, int startRow) throws IOException, GeneralSecurityException {
        Sheets sheetsService = SheetsWriter.getSheetsService(); // Assuming SheetsWriter has a getSheetsService method

        String range = sheetName + "!A" + startRow + ":Z"; // Assuming data starts from column A

        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }
}
