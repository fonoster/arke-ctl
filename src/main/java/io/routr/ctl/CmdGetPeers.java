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
class CmdGetPeers {

    private static CtlUtils ctlUtils;

    CmdGetPeers(CtlUtils ctlUtils) {
        CmdGetPeers.ctlUtils = ctlUtils;
    }

    void printPeers(String ref, String filter) {
        String response = ctlUtils.getWithToken("peers", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonObject jObject = gson.fromJson(response, JsonObject.class);
        JsonArray peers = jObject.getAsJsonArray("data");

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("USERNAME")
            .nextCell().addLine("NAME")
            .nextCell().addLine("DEVICE NAME");

        int cnt = 0;

        for (JsonElement je : peers) {
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
            } catch (NullPointerException ex) {
            }

            if (ref.isEmpty() || ref.equals(username)) {
                String deviceName = "None";

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
