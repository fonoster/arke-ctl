package io.routr.ctl;

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
class CmdGetGateways {

    private static CtlUtils ctlUtils;

    CmdGetGateways(CtlUtils ctlUtils) {
        CmdGetGateways.ctlUtils = ctlUtils;
    }

    void printGateways(String ref, String filter) {
        String response = ctlUtils.getWithToken("gateways", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonObject jObject = gson.fromJson(response, JsonObject.class);
        JsonArray gateways = jObject.getAsJsonArray("data");

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("DESC")
            .nextCell().addLine("HOST")
            .nextCell().addLine("AUTH TYPE")
            .nextCell().addLine("REGS");

        int cnt = 0;

        for (JsonElement je : gateways) {
            JsonObject gateway = je.getAsJsonObject();
            JsonObject metadata = gateway.getAsJsonObject("metadata");
            JsonObject spec = gateway.getAsJsonObject("spec");
            JsonObject credentials = spec.getAsJsonObject("credentials");
            String authType = "StaticIP";

            if(spec.has("credentials")) {
                authType = "UserPass";
            }

            String metaRef = metadata.get("ref").getAsString();
            String name = metadata.get("name").getAsString();

            String registries = "";
            String host = spec.get("host").getAsString();

            try {
                List<String> r = gson.fromJson(spec.getAsJsonArray("registries"), List.class);
                registries = String.join(",", r);
            } catch (NullPointerException | ClassCastException ex) {
            }

            if (ref.isEmpty() || ref.equals(metaRef)) {

                if (registries.isEmpty()) registries = "None";
                if (host.isEmpty()) registries = "None";

                textTable.nextRow()
                        .nextCell().addLine(metaRef)
                        .nextCell().addLine(name)
                        .nextCell().addLine(host)
                        .nextCell().addLine(authType)
                        .nextCell().addLine(registries);
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
