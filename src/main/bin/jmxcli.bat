@echo off
@if "%OS%" == "Windows_NT" setlocal

if "%OS%" == "Windows_NT" (
  set "JMXCLI_HOME=%~dp0%"
) else (
  set JMXCLI_HOME=.\
)

set CLASSPATH=lib\

call :SearchForJars "%JMXCLI_HOME%\lib"

java -cp "%CLASSPATH%" org.github.jmxcli.CLI %*

goto :EOF

:SearchForJars
pushd %1
for %%j in (*.jar) do call :ClasspathAdd %1\%%j
popd
goto :EOF

:ClasspathAdd
SET CLASSPATH=%CLASSPATH%;%1
goto :EOF

:EOF