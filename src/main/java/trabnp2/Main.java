package trabnp2;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import trabnp2.controller.ClienteController;
import trabnp2.util.DatabaseConnection;

/**
 * Classe principal: ponto de entrada do programa.
 *
 * 1) Testa a conexao com o banco e cria a tabela se necessario.
 * 2) Abre a tela principal (View) atraves do Controller.
 */
public class Main {

    public static void main(String[] args) {
        try {
            DatabaseConnection.initDatabase();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Nao foi possivel conectar ao banco de dados:\n\n" + e.getMessage()
                            + "\n\nConsulte o arquivo CONFIGURACAO_BANCO.txt para configurar a conexao.",
                    "Erro de conexao",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Mantem o tema padrao do Swing em caso de erro.
            }
            ClienteController.iniciar();
        });
    }
}
