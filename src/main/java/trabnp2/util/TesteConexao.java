package trabnp2.util;

import trabnp2.dao.ClienteDAO;
import trabnp2.model.Cliente;

/**
 * UTIL: programa de teste que executa as 4 operacoes do CRUD direto no banco,
 * sem abrir a interface grafica. Util para confirmar que a conexao esta ok.
 *
 * Execute com (a partir da pasta do projeto):
 *   java -cp "bin;lib\*;." trabnp2.util.TesteConexao
 */
public class TesteConexao {

    public static void main(String[] args) throws Exception {
        System.out.println("Tipo de banco configurado: " + DatabaseConnection.getTipoBanco());
        System.out.println("Testando conexao e criando a tabela (se necessario)...");
        DatabaseConnection.initDatabase();
        System.out.println("Conexao OK!");

        ClienteDAO dao = new ClienteDAO();

        // CREATE
        Cliente cliente = new Cliente(null, "Cliente de Teste", "teste@email.com", "(00) 00000-0000");
        dao.salvar(cliente);
        System.out.println("[CREATE] Cliente salvo: " + cliente);

        // READ
        Cliente buscado = dao.buscarPorId(cliente.getId());
        System.out.println("[READ]   Cliente buscado: " + buscado);
        System.out.println("[READ]   Total de clientes na tabela: " + dao.listar().size());

        // UPDATE
        buscado.setEmail("email.atualizado@email.com");
        dao.atualizar(buscado);
        System.out.println("[UPDATE] Cliente atualizado: " + dao.buscarPorId(buscado.getId()));

        // DELETE
        dao.excluir(buscado.getId());
        System.out.println("[DELETE] Cliente excluido. Ainda existe? "
                + (dao.buscarPorId(buscado.getId()) != null ? "SIM (erro!)" : "NAO (ok!)"));

        System.out.println();
        System.out.println("CRUD testado com sucesso no banco " + DatabaseConnection.getTipoBanco() + "!");
    }
}
