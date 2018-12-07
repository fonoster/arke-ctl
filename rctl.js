#!/usr/bin/env node
const shell = require("shelljs")
const args = process.argv.slice(2, process.argv.length).join().replace(/,/g, ' ')
console.log('args: '  + args)
const cmd = "java -cp " + __dirname + "/libs/routr-ctl-all.jar -Dcom.sun.net.ssl.checkRevocation=true com.fonoster.routr.ctl.Main " + args
shell.exec(cmd)
