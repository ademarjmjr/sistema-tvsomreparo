import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    public static void connect() {
        Connection conn = null;
        try {
            // Cria o arquivo do banco na mesma pasta do programa
            String url = "jdbc:sqlite:tvsomreparo.db";
            conn = DriverManager.getConnection(url);
            
            // Cria a tabela de OS se ela não existir
            String sql = "CREATE TABLE IF NOT EXISTS ordens_servico (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "cliente TEXT," +
                         "produto TEXT," +
                         "valor REAL);";
            
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Banco de dados pronto para uso!");
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}