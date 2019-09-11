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
class CmdGetDomains {

    private static CtlUtils ctlUtils;

    CmdGetDomains(CtlUtils ctlUtils) {
        CmdGetDomains.ctlUtils = ctlUtils;
    }

    void printDomains(String ref, String filter) {
        String result = ctlUtils.getWithToken("domains", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonObject response = gson.fromJson(result, JsonObject.class);
        JsonArray domains = response.getAsJsonArray("result");

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("NAME")
            .nextCell().addLine("URI")
            .nextCell().addLine("EGRESS POLICY")
            .nextCell().addLine("ACL");

        int cnt = 0;

        for (JsonElement je : domains) {
            JsonObject domain = je.getAsJsonObject();
            JsonObject metadata = domain.getAsJsonObject("metadata");
            JsonObject spec = domain.getAsJsonObject("spec");
            JsonObject context = spec.getAsJsonObject("context");
            String domainUri = context.get("domainUri").getAsString();
            String name = metadata.get("name").getAsString();
            String objRef = metadata.get("ref").getAsString();

            String egressPolicy = "None";
            String accessControlList = "None";

            try {
                egressPolicy = context.get("egressPolicy").toString();
            } catch (NullPointerException ex) {
            }

            try {
                accessControlList = context.get("accessControlList").toString();
            } catch (NullPointerException ex) {
            }

            if (ref.isEmpty() || ref.equals(objRef)) {
                textTable.nextRow()
                        .nextCell().addLine(objRef)
                        .nextCell().addLine(name)
                        .nextCell().addLine(domainUri)
                        .nextCell().addLine(egressPolicy)
                        .nextCell().addLine(accessControlList);
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
