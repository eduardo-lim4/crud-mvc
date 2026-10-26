@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ============================================
echo  CRUD Java Swing (MVC) - trabnp2
echo ============================================
echo.

if not exist "lib\mysql-connector-j-9.1.0.jar" (
    echo [AVISO] Driver JDBC do MySQL nao encontrado na pasta lib^!
    echo Baixe-o conforme instrucoes do CONFIGURACAO_BANCO.txt
    pause
    exit /b 1
)

echo Compilando o projeto...
if not exist bin mkdir bin
dir /s /b src\main\java\*.java > "%TEMP%\sources_trabnp2.txt"
javac -encoding UTF-8 -cp "lib\*" -d bin "@%TEMP%\sources_trabnp2.txt"
del "%TEMP%\sources_trabnp2.txt" >nul 2>&1
if errorlevel 1 (
    echo.
    echo [ERRO] Falha na compilacao. Verifique as mensagens acima.
    pause
    exit /b 1
)
echo Compilacao concluida com sucesso!
echo.

echo Iniciando o programa...
rem Evita aviso de "native access" em JDKs novos; ignorado em JDKs antigos
set JAVA_FLAGS=
java --enable-native-access=ALL-UNNAMED -version >nul 2>&1
if not errorlevel 1 set "JAVA_FLAGS=--enable-native-access=ALL-UNNAMED"
java %JAVA_FLAGS% -cp "bin;lib\*;." trabnp2.Main
