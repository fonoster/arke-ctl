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
import net.sourceforge.argparse4j.inf.Subparsers;

import static java.lang.System.out;

class CmdLocate {

    private static CtlUtils ctlUtils;

    CmdLocate(Subparsers subparsers, CtlUtils ctlUtils) {
        subparsers.addParser("locate").aliases("loc").help("locate sip device(s)");
        CmdLocate.ctlUtils = ctlUtils;
    }

    void run() throws UnirestException {
        String result = ctlUtils.getWithToken("location", "").getBody().toString();
        Gson gson = new Gson();
        JsonObject response = gson.fromJson(result, JsonObject.class);
        JsonArray locEntries = response.getAsJsonArray("result");

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("ADDRESS OF RECORD")
            .nextCell().addLine("CONTACT INFO");

        int cnt = 0;

        for (JsonElement je : locEntries) {
            JsonObject entry = je.getAsJsonObject();
            String aor = entry.get("addressOfRecord").getAsString();
            String contactInfo = entry.get("contactInfo").getAsString();

            textTable.nextRow()
                    .nextCell().addLine(aor)
                    .nextCell().addLine(contactInfo);
            cnt++;
        }

        if (cnt > 0) {
            GridTable grid = textTable.toGrid();
            grid = Border.DOUBLE_LINE.apply(grid);
            Util.print(grid);
        } else {
            out.print("No registered devices at this time.");
        }
    }
}
