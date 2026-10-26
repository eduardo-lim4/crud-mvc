@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ============================================
echo  Teste de conexao com o banco de dados
echo ============================================
echo.
if not exist bin (
    echo [ERRO] Projeto ainda nao compilado. Execute "executar.bat" primeiro
    echo         ou compile com: javac -encoding UTF-8 -cp "lib\*" -d bin src\main\java\trabnp2\util\*.java src\main\java\trabnp2\model\*.java src\main\java\trabnp2\dao\*.java
    pause
    exit /b 1
)
set JAVA_FLAGS=
java --enable-native-access=ALL-UNNAMED -version >nul 2>&1
if not errorlevel 1 set "JAVA_FLAGS=--enable-native-access=ALL-UNNAMED"
java %JAVA_FLAGS% -cp "bin;lib\*;." trabnp2.util.TesteConexao
pause
