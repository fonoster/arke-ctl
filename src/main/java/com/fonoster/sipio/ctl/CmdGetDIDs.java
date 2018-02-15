package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Iterator;

import static java.lang.System.out;

/**
 * @author Pedro Sanders
 * @since v1
 */
public class CmdGetDIDs {

    static public void printDIDs(String ref, String filter) throws UnirestException {
        CtlUtils ctlUtils = new CtlUtils();
        String result = ctlUtils.getWithToken("dids", "filter=" + filter);
        Gson gson = new Gson();
        JsonArray dids = gson.fromJson(result, JsonArray.class);

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("GW REF")
            .nextCell().addLine("TEL URI")
            .nextCell().addLine("ADDRESS OF RECORD LINK")
            .nextCell().addLine("COUNTRY/CITY");

        int cnt = 0;

        Iterator i = dids.iterator();

        while(i.hasNext()) {
            JsonElement je = (JsonElement) i.next();
            JsonObject did = je.getAsJsonObject();
            JsonObject metadata = did.getAsJsonObject("metadata");
            JsonObject geoInfo = metadata.getAsJsonObject("geoInfo");
            JsonObject spec = did.getAsJsonObject("spec");
            JsonObject location = spec.getAsJsonObject("location");
            String telUrl = location.get("telUrl").getAsString();
            String aorLink = location.get("aorLink").getAsString();
            String metaRef = metadata.get("ref").getAsString();
            String gwRef = metadata.get("gwRef").getAsString();
            String city = geoInfo.get("city").getAsString();

            if (ref.isEmpty() || ref.equals(metaRef)) {
                textTable.nextRow()
                    .nextCell().addLine(metaRef)
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
            out.print("Resource/s not found.");
        }
    }
}
