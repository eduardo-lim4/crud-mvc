package trabnp2.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * UTIL: classe central da conexao com o banco de dados.
 *
 * As configuracoes ficam no arquivo "database.properties" (na raiz do
 * projeto). Ele suporta dois bancos:
 *
 *   - sqlite (padrao): nao precisa instalar nada, cria o arquivo "banco.db"
 *     automaticamente na pasta do projeto.
 *   - mysql: requer um servidor MySQL rodando (ver CONFIGURACAO_BANCO.txt).
 */
public final class DatabaseConnection {

    private static final Properties PROPS = new Properties();
    private static String tipoBanco;

    static {
        carregarConfiguracoes();
    }

    private DatabaseConnection() {
    }

    private static void carregarConfiguracoes() {
        // 1) Tenta ler o arquivo na raiz do projeto (mais facil de editar).
        try (InputStream in = new FileInputStream("database.properties")) {
            PROPS.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            // 2) Tenta ler do classpath (caso o arquivo nao esteja na pasta atual).
            try (InputStream in = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties")) {
                if (in != null) {
                    PROPS.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                }
            } catch (IOException ignored) {
                // Segue com os valores padrao (SQLite).
            }
        }
        tipoBanco = PROPS.getProperty("db.type", "sqlite").trim().toLowerCase();
    }

    public static String getTipoBanco() {
        return tipoBanco;
    }

    public static boolean isMysql() {
        return "mysql".equals(tipoBanco);
    }

    private static String host() {
        return PROPS.getProperty("mysql.host", "localhost").trim();
    }

    private static String porta() {
        return PROPS.getProperty("mysql.port", "3306").trim();
    }

    private static String banco() {
        return PROPS.getProperty("mysql.database", "bd_trabnp2").trim();
    }

    /**
     * Abre e retorna uma conexao com o banco configurado.
     */
    public static Connection getConnection() throws SQLException {
        if (isMysql()) {
            criarBancoSeNaoExistir();
            String url = "jdbc:mysql://" + host() + ":" + porta() + "/" + banco()
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
            return DriverManager.getConnection(url,
                    PROPS.getProperty("mysql.user", "root").trim(),
                    PROPS.getProperty("mysql.password", ""));
        }
        String arquivo = PROPS.getProperty("sqlite.file", "banco.db").trim();
        return DriverManager.getConnection("jdbc:sqlite:" + arquivo);
    }

    /**
     * No MySQL, cria o banco de dados automaticamente caso ele ainda nao exista.
     */
    private static void criarBancoSeNaoExistir() throws SQLException {
        String urlSemBanco = "jdbc:mysql://" + host() + ":" + porta()
                + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        try (Connection con = DriverManager.getConnection(urlSemBanco,
                PROPS.getProperty("mysql.user", "root").trim(),
                PROPS.getProperty("mysql.password", ""));
             Statement st = con.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + banco() + "` CHARACTER SET utf8mb4");
        }
    }

    /**
     * Cria a tabela "tb_clientes" caso ainda nao exista (roda no inicio do programa).
     */
    public static void initDatabase() {
        String sql = isMysql()
                ? "CREATE TABLE IF NOT EXISTS tb_clientes ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "nome VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(150), "
                        + "telefone VARCHAR(30))"
                : "CREATE TABLE IF NOT EXISTS tb_clientes ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "nome TEXT NOT NULL, "
                        + "email TEXT, "
                        + "telefone TEXT)";
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel conectar ao banco ("
                    + tipoBanco + "). Verifique o arquivo database.properties. Detalhe: " + e.getMessage(), e);
        }
    }
}
