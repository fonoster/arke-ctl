package com.fonoster.sipio.ctl;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.apache.http.NoHttpResponseException;

import static java.lang.System.out;

public class CmdStop {

    public CmdStop(Subparsers subparsers) {
        subparsers.addParser("stop").help("stops server");
    }

    void run() throws UnirestException {
        CtlUtils ctlUtils = new CtlUtils();

        try {
            ctlUtils.postWithToken("system/status/halt", null);
        } catch(Exception ex) {
            if(ex instanceof NoHttpResponseException) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        out.print("Done.");
    }
}
