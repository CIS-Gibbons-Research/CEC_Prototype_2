package css.cecprototype2.analysis_logic;

import android.util.Log;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class SheetsReader {

    public static List<List<Object>> readFromGoogleSheets(String spreadsheetId, String sheetName, int numRowsToSkip) throws IOException, GeneralSecurityException {
        Sheets sheetsService = SheetsWriter.getSheetsService(); // Assuming SheetsWriter has a getSheetsService method

        String range = sheetName + "!A" + (numRowsToSkip + 1) + ":Z"; // Assuming data starts from column A

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            Log.d("SheetsReader", "No data found.");
            return new ArrayList<>();
        }

        // Skip header row
        if (numRowsToSkip > 0) {
            values = values.subList(numRowsToSkip, values.size());
        }

        return values;
    }
}
