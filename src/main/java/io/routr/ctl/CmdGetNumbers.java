package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;

import static java.lang.System.out;

/**
 * @author Pedro Sanders
 * @since v1
 */
class CmdGetNumbers {

    private static CtlUtils ctlUtils;

    CmdGetNumbers(CtlUtils ctlUtils) {
        CmdGetNumbers.ctlUtils = ctlUtils;
    }

    void printNumbers(String ref, String filter) {
        String response = ctlUtils.getWithToken("numbers", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonObject jObject = gson.fromJson(response, JsonObject.class);
        JsonArray numbers = jObject.getAsJsonArray("data");

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("GW REF")
            .nextCell().addLine("TEL URI")
            .nextCell().addLine("ADDRESS OF RECORD LINK")
            .nextCell().addLine("COUNTRY/CITY");

        int cnt = 0;

        for (JsonElement je : numbers) {
            JsonObject number = je.getAsJsonObject();
            JsonObject metadata = number.getAsJsonObject("metadata");
            JsonObject geoInfo = metadata.getAsJsonObject("geoInfo");
            JsonObject spec = number.getAsJsonObject("spec");
            JsonObject location = spec.getAsJsonObject("location");
            String telUrl = location.get("telUrl").getAsString();
            String aorLink = location.get("aorLink").getAsString();
            String objRef = metadata.get("ref").getAsString();
            String gwRef = metadata.get("gwRef").getAsString();
            String city = "";

            if(geoInfo != null) {
                city = geoInfo.get("city").getAsString();
            }

            if (ref.isEmpty() || ref.equals(objRef)) {
                textTable.nextRow()
                    .nextCell().addLine(objRef)
                    .nextCell().addLine(gwRef)
                    .nextCell().addLine(telUrl)
                    .nextCell().addLine(aorLink)
                    .nextCell().addLine(city);
                cnt++;
            }
        }

        if (cnt > 0) {
            GridTable grid = textTable.toGrid();
            grid = Border.DOUBLE_LINE.apply(grid);
            Util.print(grid);
        } else {
            out.println("Resource/s not found");
        }
    }
}
