@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  routr-ctl startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and ROUTR_CTL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\routr-ctl.jar;%APP_HOME%\lib\unirest-java-1.4.9.jar;%APP_HOME%\lib\argparse4j-0.8.1.jar;%APP_HOME%\lib\inamik-text-tables-0.1.jar;%APP_HOME%\lib\gson-2.8.2.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.8.3.jar;%APP_HOME%\lib\jackson-databind-2.8.6.jar;%APP_HOME%\lib\junit-4.13.jar;%APP_HOME%\lib\jetty-webapp-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-proxy-9.4.11.v20180605.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\log4j-1.2-api-2.11.0.jar;%APP_HOME%\lib\log4j-core-2.11.0.jar;%APP_HOME%\lib\log4j-api-2.11.0.jar;%APP_HOME%\lib\slf4j-nop-1.7.25.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\httpasyncclient-4.1.1.jar;%APP_HOME%\lib\httpmime-4.5.2.jar;%APP_HOME%\lib\httpclient-4.5.2.jar;%APP_HOME%\lib\json-20160212.jar;%APP_HOME%\lib\jackson-core-2.8.6.jar;%APP_HOME%\lib\snakeyaml-1.15.jar;%APP_HOME%\lib\jackson-annotations-2.8.0.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar;%APP_HOME%\lib\jetty-xml-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-servlet-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-client-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-security-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-server-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-http-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-io-9.4.11.v20180605.jar;%APP_HOME%\lib\jetty-util-9.4.11.v20180605.jar;%APP_HOME%\lib\httpcore-nio-4.4.4.jar;%APP_HOME%\lib\httpcore-4.4.4.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.9.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar

@rem Execute routr-ctl
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ROUTR_CTL_OPTS%  -classpath "%CLASSPATH%" io.routr.ctl.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable ROUTR_CTL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%ROUTR_CTL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
