package trabnp2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import trabnp2.model.Cliente;
import trabnp2.util.DatabaseConnection;

/**
 * DAO (Data Access Object): concentra todo o acesso ao banco de dados
 * para a entidade Cliente. Cada metodo executa uma operacao do CRUD:
 *
 *   salvar()    -> CREATE
 *   buscarPorId() / listar() -> READ
 *   atualizar() -> UPDATE
 *   excluir()   -> DELETE
 */
public class ClienteDAO {

    /** CREATE: insere um novo cliente e preenche o id gerado pelo banco. */
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO tb_clientes (nome, email, telefone) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getTelefone());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setId(chaves.getInt(1));
                }
            }
        }
    }

    /** READ: busca um unico cliente pelo id (ou null se nao existir). */
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, email, telefone FROM tb_clientes WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarCliente(rs);
                }
            }
        }
        return null;
    }

    /** READ: retorna todos os clientes ordenados por nome. */
    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT id, nome, email, telefone FROM tb_clientes ORDER BY nome";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(montarCliente(rs));
            }
        }
        return clientes;
    }

    /** UPDATE: atualiza os dados de um cliente existente. */
    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE tb_clientes SET nome = ?, email = ?, telefone = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getTelefone());
            ps.setInt(4, cliente.getId());
            ps.executeUpdate();
        }
    }

    /** DELETE: remove um cliente pelo id. */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM tb_clientes WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Cliente montarCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("telefone"));
    }
}
