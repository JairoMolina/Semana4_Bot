package umg.principal.service;

import umg.principal.dao.CuestionarioDao;
import umg.principal.db.DatabaseConnection;
import umg.principal.db.TransactionManager;
import umg.principal.model.Cuestionario;

import java.sql.Connection;
import java.sql.SQLException;

public class CuestionarioService {
    private CuestionarioDao cuestionarioDao = new CuestionarioDao();

    public void crearUsuario(Cuestionario cuestionario) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            TransactionManager tm = new TransactionManager(connection);
            tm.beginTransaction();
            try {
                cuestionarioDao.insertUser(cuestionario);
                tm.commit();
            } catch (SQLException e) {
                tm.rollback();
                throw e;
            }
        }
    }
}
