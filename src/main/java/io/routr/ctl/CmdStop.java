package io.routr.ctl;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import net.sourceforge.argparse4j.impl.Arguments;
import static java.lang.System.out;

class CmdStop {

    private static CtlUtils ctlUtils;

    CmdStop(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser sys = subparsers.addParser("stop").help("stops engine");
        sys.addArgument("--now").dest("now").action(Arguments.storeTrue()).setDefault(false).help("stops engine immediately");
        this.ctlUtils = ctlUtils;
    }

    void run(boolean nowFlag) {
      if (nowFlag) {
          this.ctlUtils.postWithToken("system/status/down", null, "now=true");
      } else {
          this.ctlUtils.postWithToken("system/status/down", null, null);
      }
      out.println("Done");
    }
}
