#!/usr/bin/env node
const shell = require('shelljs')
const scapedArgs = process.argv.map(arg => arg = encodeURI(arg).replace(/'/g, "\\'"))
const args = scapedArgs.slice(2, process.argv.length).join().replace(/,/g, ' ')
const cmd = `java -Dfile.encoding=UTF-8 -cp ${__dirname}/libs/routr-ctl-all.jar -Dcom.sun.net.ssl.checkRevocation=true io.routr.ctl.Main ${args}`
process.env.ROUTR_WEBAPP=`${__dirname}/libs/webapp`
process.env.ROUTR_CTL_VERSION=require('./package.json').version
shell.exec(cmd)
