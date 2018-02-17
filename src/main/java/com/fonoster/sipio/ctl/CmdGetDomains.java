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
public class CmdGetDomains {

    static public void printDomains(String ref, String filter) throws UnirestException {
        CtlUtils ctlUtils = new CtlUtils();
        String result = ctlUtils.getWithToken("domains", "filter=" + filter).getBody().toString();
        Gson gson = new Gson();
        JsonArray domains = gson.fromJson(result, JsonArray.class);

        SimpleTable textTable = SimpleTable.of()
            .nextRow()
            .nextCell().addLine("REF")
            .nextCell().addLine("NAME")
            .nextCell().addLine("URI")
            .nextCell().addLine("EGRESS POLICY")
            .nextCell().addLine("ACL");

        int cnt = 0;

        Iterator i = domains.iterator();

        while(i.hasNext()) {
            JsonElement je = (JsonElement) i.next();
            JsonObject domain = je.getAsJsonObject();
            JsonObject metadata = domain.getAsJsonObject("metadata");
            JsonObject spec = domain.getAsJsonObject("spec");
            JsonObject context = spec.getAsJsonObject("context");
            String domainUri = context.get("domainUri").getAsString();
            String name = metadata.get("name").getAsString();
            String objRef = metadata.get("ref").getAsString();

            String egressPolicy = "--";
            String accessControlList = "--";

            try {
                egressPolicy = context.get("egressPolicy").toString();
            } catch(NullPointerException ex) {}

            try {
                accessControlList = context.get("accessControlList").toString();
            } catch(NullPointerException ex) {}

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
