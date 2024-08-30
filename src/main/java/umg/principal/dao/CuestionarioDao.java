package umg.principal.dao;

import umg.principal.db.DatabaseConnection;
import umg.principal.model.Cuestionario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class CuestionarioDao {
    public void insertUser(Cuestionario cuestionario) throws SQLException {
        String query = "INSERT INTO tb_respuestas(seccion, telegram_id, pregunta_id, respuesta_texto) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cuestionario.getSeccion());
            statement.setLong(2, cuestionario.getTelegramid());
            statement.setInt(3, cuestionario.getPreguntaid());
            statement.setString(4, cuestionario.getResponse());

            statement.executeUpdate();
        }
    }
}