package trabnp2.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import trabnp2.controller.ClienteController;
import trabnp2.model.Cliente;

/**
 * VIEW: toda a interface grafica (Java Swing). Ela nao conhece o banco de
 * dados - apenas exibe dados e avisa o Controller quando o usuario age.
 */
public class ClienteView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ClienteController controller;

    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoEmail = new JTextField(25);
    private final JTextField campoTelefone = new JTextField(25);

    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[] { "ID", "Nome", "E-mail", "Telefone" }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // tabela somente leitura
        }
    };
    private final JTable tabela = new JTable(modeloTabela);

    private final JLabel labelStatus = new JLabel();
    private Integer idSelecionado;
    private String infoBanco = "";

    public ClienteView(ClienteController controller) {
        this.controller = controller;
        montarJanela();
    }

    private void montarJanela() {
        setTitle("Cadastro de Clientes - CRUD Java Swing (MVC)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(740, 520);
        setLocationRelativeTo(null);

        // ----- Formulario (topo) -----
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelForm.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        painelForm.add(campoNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        painelForm.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        painelForm.add(campoEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        painelForm.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        painelForm.add(campoTelefone, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        painelForm.add(new JLabel("Selecione uma linha da tabela para editar ou excluir."), gbc);

        // ----- Tabela (centro) -----
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                preencherFormularioComLinhaSelecionada();
            }
        });
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Clientes cadastrados"));

        // ----- Botoes (rodape) -----
        JButton botaoNovo = new JButton("Novo");
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoExcluir = new JButton("Excluir");
        JButton botaoLimpar = new JButton("Limpar");

        botaoNovo.addActionListener(event -> controller.novo());
        botaoSalvar.addActionListener(event -> controller.salvar());
        botaoExcluir.addActionListener(event -> controller.excluir());
        botaoLimpar.addActionListener(event -> controller.limpar());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        painelBotoes.add(botaoNovo);
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoLimpar);

        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelStatus.add(labelStatus);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelBotoes, BorderLayout.CENTER);
        painelSul.add(painelStatus, BorderLayout.SOUTH);

        add(painelForm, BorderLayout.NORTH);
        add(scrollTabela, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);
    }

    // ----- Metodos usados pelo CONTROLLER para ler a tela -----

    public String getCampoNome() {
        return campoNome.getText().trim();
    }

    public String getCampoEmail() {
        return campoEmail.getText().trim();
    }

    public String getCampoTelefone() {
        return campoTelefone.getText().trim();
    }

    public Integer getIdSelecionado() {
        return idSelecionado;
    }

    // ----- Metodos usados pelo CONTROLLER para atualizar a tela -----

    public void atualizarTabela(List<Cliente> clientes) {
        modeloTabela.setRowCount(0);
        for (Cliente cliente : clientes) {
            modeloTabela.addRow(new Object[] {
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getEmail(),
                    cliente.getTelefone()
            });
        }
        atualizarLabelStatus(clientes.size());
    }

    public void preencherFormulario(Cliente cliente) {
        idSelecionado = cliente.getId();
        campoNome.setText(cliente.getNome());
        campoEmail.setText(cliente.getEmail());
        campoTelefone.setText(cliente.getTelefone());
    }

    public void limparFormulario() {
        idSelecionado = null;
        campoNome.setText("");
        campoEmail.setText("");
        campoTelefone.setText("");
        tabela.clearSelection();
    }

    public void focarCampoNome() {
        campoNome.requestFocusInWindow();
    }

    public void setStatusBanco(String info) {
        this.infoBanco = info;
        atualizarLabelStatus(modeloTabela.getRowCount());
    }

    // ----- Mensagens para o usuario -----

    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Informacao", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmarExclusao() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este cliente?",
                "Confirmar exclusao",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return opcao == JOptionPane.YES_OPTION;
    }

    // ----- Internos -----

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (Integer) modeloTabela.getValueAt(linha, 0);
        campoNome.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        campoEmail.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        campoTelefone.setText(String.valueOf(modeloTabela.getValueAt(linha, 3)));
    }

    private void atualizarLabelStatus(int quantidade) {
        String separador = infoBanco.isEmpty() ? "" : "  |  ";
        labelStatus.setText(" " + infoBanco + separador + quantidade + " cliente(s) cadastrado(s)");
    }
}
