package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import com.opencsv.CSVWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.List;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            // INSERT YOUR CODE HERE
            
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> lines = reader.readAll();
            String[] header = lines.get(0);
            
            //JSON Object & Arrays
            JsonObject jsonObj = new JsonObject();
            JsonArray jsonCol = new JsonArray();
            JsonArray jsonNum = new JsonArray();
            JsonArray jsonData = new JsonArray();
            
            jsonCol.addAll(Arrays.asList(header)); 
            
            for (int i = 1; i < lines.size(); i++) {
                
                String[] data = lines.get(i);
                jsonNum.add(data[0]);
                JsonArray jsonhold = new JsonArray();
                
                for(int j = 1; j < data.length; j++) {
                    
                    if (j == jsonCol.indexOf("Episode") || j == jsonCol.indexOf("Season")) {
                        jsonhold.add(Integer.parseInt(data[j])); 
                    }
                    else {
                        jsonhold.add(data[j]); 
                    }
                    
                }
                
                jsonData.add(jsonhold);
            }
            
            jsonObj.put("ColHeadings", jsonCol);
            jsonObj.put("ProdNums", jsonNum);
            jsonObj.put("Data", jsonData);
            
            result = Jsoner.serialize(jsonObj);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        DecimalFormat decFor = new DecimalFormat("00");
        
        try {
            
            // INSERT YOUR CODE HERE
            
            JsonObject jsonObj = Jsoner.deserialize(jsonString, new JsonObject());
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            
            
            JsonArray jsonCol = (JsonArray) jsonObj.get("ColHeadings");
            JsonArray jsonNum = (JsonArray) jsonObj.get("ProdNums");
            JsonArray jsonData = (JsonArray) jsonObj.get("Data");

            String[] header = new String[jsonCol.size()];
            
            for(int i = 0; i < jsonCol.size(); i++) {
                header[i] = jsonCol.get(i).toString();
            }
            
            csvWriter.writeNext(header);
            
            for(int i = 0; i < jsonNum.size(); i++) {
                JsonArray info = (JsonArray) jsonData.get(i);
                String[] nums = new String[jsonCol.size()];
                nums[0] = jsonNum.get(i).toString();
                
                for(int j = 0; j < info.size(); j++) {
                    
                    if(info.get(j)==info.get(jsonCol.indexOf("Episode")-1)) {
                        int num = Integer.parseInt(info.get(j).toString());
                        String formating = decFor.format(num);
                        nums[j + 1] = formating;
                    }
                    
                    else {
                        nums[j + 1] = info.get(j).toString();
                    }
                    
                }
                csvWriter.writeNext(nums);
            }
            
            result = writer.toString();
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
