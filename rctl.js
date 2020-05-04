#!/usr/bin/env node
const CONFIG_PATH=`${require('os').homedir()}/.routr-access.json`
const cmdExists = require('command-exists').sync;
const shell = require('shelljs')
const scapedArgs = process.argv.map(arg => arg = encodeURI(arg).replace(/'/g, "\\'"))
const args = scapedArgs.slice(2, process.argv.length).join().replace(/,/g, ' ')
const home = process.env.JAVA_HOME ? process.env.JAVA_HOME + '/bin/' : ''
const cmd = `${home}java -Dlog4j.configurationFile=${__dirname}/log4j2.yml -Dfile.encoding=UTF-8 -cp ${__dirname}/libs/routr-ctl-all.jar -Dcom.sun.net.ssl.checkRevocation=true io.routr.ctl.Main ${args}`

// NOTICE: This overlaps a functionality of the Main method of getting
// apiUrl and token. This was necessary because there is no apparent way to
// pass parameters to the WSHandler, so we are using an environment variables.
try {
  const config = require(CONFIG_PATH)
  process.env.ROUTR_API_URL=config.apiUrl
  process.env.ROUTR_API_TOKEN=config.token
} catch(e) {}
process.env.ROUTR_WEBAPP=`${__dirname}/libs/webapp`
process.env.ROUTR_CTL_VERSION=require('./package.json').version

// Ensure java command is available
if (process.env.JAVA_HOME || cmdExists('java')) {
    shell.exec(cmd)
} else {
    console.log('Could not find a runtime environment. Please setup the JAVA_HOME environment variable')
}
