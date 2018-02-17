package com.fonoster.sipio.ctl;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.inf.Subparsers;

public class CmdConfig {

    public CmdConfig(Subparsers subparsers) {
        subparsers.addParser("configure").aliases("config").help("configures access server");
    }

    void run() throws UnirestException {
        CtlUtils ctlUtils = new CtlUtils();
        // Allow user to enter API URI, Username, Password
        // Request token
        // Store token
    }
}
