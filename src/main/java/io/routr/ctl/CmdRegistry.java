package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import net.sourceforge.argparse4j.inf.Subparsers;

import static java.lang.System.out;

class CmdRegistry {

    private static CtlUtils ctlUtils;

    CmdRegistry(Subparsers subparsers, CtlUtils ctlUtils) {
        subparsers.addParser("registry").aliases("reg").help("shows gateways registrations");
        CmdRegistry.ctlUtils = ctlUtils;
    }

    void run() {
        String response = ctlUtils.getWithToken("registry", "").getBody().toString();
        Gson gson = new Gson();
        JsonObject jObject = gson.fromJson(response, JsonObject.class);
        JsonArray registries = jObject.getAsJsonArray("data");

        SimpleTable textTable = SimpleTable.of()
                .nextRow()
                .nextCell().addLine("USER")
                .nextCell().addLine("HOST")
                .nextCell().addLine("IP ADDRESS")
                .nextCell().addLine("REGISTERED");

        int cnt = 0;

        for (JsonElement je : registries) {
            JsonObject reg = je.getAsJsonObject();
            String username = reg.get("username").getAsString();
            String host = reg.get("host").getAsString();
            String ip = reg.get("ip").getAsString();
            String regOnFormatted = reg.get("regOnFormatted").getAsString();

            textTable.nextRow()
                    .nextCell().addLine(username)
                    .nextCell().addLine(host)
                    .nextCell().addLine(ip)
                    .nextCell().addLine(regOnFormatted);

            cnt++;
        }

        if (cnt > 0) {
            GridTable grid = textTable.toGrid();
            grid = Border.DOUBLE_LINE.apply(grid);
            Util.print(grid);
        } else {
            out.println("Not registered to any provider at this time");
        }
    }
}
