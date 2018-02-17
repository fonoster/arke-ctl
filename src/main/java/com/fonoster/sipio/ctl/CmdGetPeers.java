package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;

import java.util.Iterator;

import static java.lang.System.out;

/**
 * @author Pedro Sanders
 * @since v1
 */
public class CmdGetPeers {

    private static CtlUtils ctlUtils;

    public CmdGetPeers(CtlUtils ctlUtils) {
        this.ctlUtils = ctlUtils;
    }

    public void printPeers(String ref, String filter) {
        String result = ctlUtils.getWithToken("peers", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonArray peers = gson.fromJson(result, JsonArray.class);

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("USERNAME")
            .nextCell().addLine("NAME")
            .nextCell().addLine("DEVICE NAME");

        int cnt = 0;

        Iterator i = peers.iterator();

        while(i.hasNext()) {
            JsonElement je = (JsonElement) i.next();
            JsonObject agent = je.getAsJsonObject();
            JsonObject metadata = agent.getAsJsonObject("metadata");
            JsonObject spec = agent.getAsJsonObject("spec");
            JsonObject credentials = spec.getAsJsonObject("credentials");
            String username = credentials.get("username").getAsString();
            String name = metadata.get("name").getAsString();
            String objRef = metadata.get("ref").getAsString();
            String device = "";

            try {
                device = spec.get("device").getAsString();
            } catch(NullPointerException ex) {
            }

           if (ref.isEmpty() || ref.equals(username)) {
                String deviceName = "--";

                if (!device.isEmpty()) deviceName = device;

                textTable.nextRow()
                    .nextCell().addLine(objRef)
                    .nextCell().addLine(username)
                    .nextCell().addLine(name)
                    .nextCell().addLine(deviceName);
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
