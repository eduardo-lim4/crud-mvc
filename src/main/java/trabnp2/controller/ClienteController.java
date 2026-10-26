package trabnp2.controller;

import java.sql.SQLException;

import javax.swing.SwingUtilities;

import trabnp2.dao.ClienteDAO;
import trabnp2.model.Cliente;
import trabnp2.util.DatabaseConnection;
import trabnp2.view.ClienteView;

/**
 * CONTROLLER: faz a ponte entre a VIEW (tela) e o MODEL/DAO (dados).
 * A tela nunca fala direto com o banco; o banco nunca fala direto com a tela.
 */
public class ClienteController {

    private final ClienteView view;
    private final ClienteDAO dao;

    public ClienteController() {
        this.dao = new ClienteDAO();
        this.view = new ClienteView(this);
        this.view.setStatusBanco("Banco em uso: " + DatabaseConnection.getTipoBanco().toUpperCase());
        this.view.setVisible(true);
        atualizarTabela();
    }

    /** Carrega todos os clientes do banco e mostra na tabela da tela. */
    public void atualizarTabela() {
        try {
            view.atualizarTabela(dao.listar());
        } catch (SQLException e) {
            view.mostrarErro("Erro ao listar clientes: " + e.getMessage());
        }
    }

    /** CREATE ou UPDATE: salva o formulario (novo cliente ou edicao do selecionado). */
    public void salvar() {
        String nome = view.getCampoNome();
        if (nome.isEmpty()) {
            view.mostrarErro("O campo Nome e obrigatorio.");
            return;
        }
        Cliente cliente = new Cliente(
                view.getIdSelecionado(),
                nome,
                view.getCampoEmail(),
                view.getCampoTelefone());
        try {
            if (cliente.getId() == null) {
                dao.salvar(cliente);
                view.mostrarMensagem("Cliente cadastrado com sucesso (ID " + cliente.getId() + ").");
            } else {
                dao.atualizar(cliente);
                view.mostrarMensagem("Cliente atualizado com sucesso.");
            }
            view.limparFormulario();
            atualizarTabela();
        } catch (SQLException e) {
            view.mostrarErro("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    /** DELETE: exclui o cliente selecionado na tabela (com confirmacao). */
    public void excluir() {
        Integer id = view.getIdSelecionado();
        if (id == null) {
            view.mostrarErro("Selecione um cliente na tabela para excluir.");
            return;
        }
        if (!view.confirmarExclusao()) {
            return;
        }
        try {
            dao.excluir(id);
            view.mostrarMensagem("Cliente excluido com sucesso.");
            view.limparFormulario();
            atualizarTabela();
        } catch (SQLException e) {
            view.mostrarErro("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    /** Limpa o formulario para comecar um novo cadastro. */
    public void novo() {
        view.limparFormulario();
        view.focarCampoNome();
    }

    /** Limpa o formulario (mesma acao do botao "Novo"). */
    public void limpar() {
        view.limparFormulario();
    }

    /** Abre a tela em uma thread segura do Swing (chamado pelo Main). */
    public static void iniciar() {
        SwingUtilities.invokeLater(ClienteController::new);
    }
}
