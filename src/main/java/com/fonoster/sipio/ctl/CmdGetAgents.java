package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;

import java.util.List;

import static java.lang.System.out;

/**
 * @author Pedro Sanders
 * @since v1
 */
class CmdGetAgents {

    private static CtlUtils ctlUtils;

    CmdGetAgents(CtlUtils ctlUtils) {
        CmdGetAgents.ctlUtils = ctlUtils;
    }

    void printAgents(String ref, String filter) {
        String result = ctlUtils.getWithToken("agents", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonObject response = gson.fromJson(result, JsonObject.class);
        JsonArray agents = response.getAsJsonArray("result");

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("USERNAME")
            .nextCell().addLine("NAME")
            .nextCell().addLine("DOMAIN(S)");

        int cnt = 0;

        for (JsonElement je : agents) {
            JsonObject agent = je.getAsJsonObject();
            JsonObject metadata = agent.getAsJsonObject("metadata");
            JsonObject spec = agent.getAsJsonObject("spec");
            JsonObject credentials = spec.getAsJsonObject("credentials");
            String username = credentials.get("username").getAsString();
            String name = metadata.get("name").getAsString();
            String genRef = metadata.get("ref").getAsString();

            List<String> d = gson.fromJson(spec.getAsJsonArray("domains"), List.class);
            String domains = String.join(",", d);

            if (ref.isEmpty() || genRef.equals(ref)) {
                textTable.nextRow()
                        .nextCell().addLine(genRef)
                        .nextCell().addLine(username)
                        .nextCell().addLine(name)
                        .nextCell().addLine(domains);
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
