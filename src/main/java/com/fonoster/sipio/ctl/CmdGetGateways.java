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
import java.util.List;

import static java.lang.System.out;

/**
 * @author Pedro Sanders
 * @since v1
 */
public class CmdGetGateways {

    private static CtlUtils ctlUtils;

    public CmdGetGateways(CtlUtils ctlUtils) {
        this.ctlUtils = ctlUtils;
    }

    public void printGateways(String ref, String filter) {
        String result = ctlUtils.getWithToken("gateways", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonArray gateways = gson.fromJson(result, JsonArray.class);

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("USER")
            .nextCell().addLine("DESC")
            .nextCell().addLine("HOST")
            .nextCell().addLine("REGS");

        int cnt = 0;

        Iterator i = gateways.iterator();

        while(i.hasNext()) {
            JsonElement je = (JsonElement) i.next();
            JsonObject gateway = je.getAsJsonObject();
            JsonObject metadata = gateway.getAsJsonObject("metadata");
            JsonObject spec = gateway.getAsJsonObject("spec");
            JsonObject regService = spec.getAsJsonObject("regService");
            JsonObject credentials = regService.getAsJsonObject("credentials");
            String username = credentials.get("username").getAsString();
            String metaRef = metadata.get("ref").getAsString();
            String name = metadata.get("name").getAsString();

            String registries = "";
            String host = "";

            try {
                host = regService.get("host").getAsString();
            } catch(NullPointerException ex) {
            }

            try {
                List<String> r = gson.fromJson(regService.getAsJsonArray("registries"), List.class);
                registries = String.join(",", r);
            } catch(NullPointerException ex) {
            }

            if (ref.isEmpty() || ref.equals(metaRef)) {

                if (registries.isEmpty()) registries = "--";
                if (host.isEmpty()) registries = "--";

                textTable.nextRow()
                    .nextCell().addLine(metaRef)
                    .nextCell().addLine(username)
                    .nextCell().addLine(name)
                    .nextCell().addLine(host)
                    .nextCell().addLine(registries);
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
