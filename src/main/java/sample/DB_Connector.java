package sample;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

public class DB_Connector {
    private DB_Connector(){
    }

    public static DataSource getDataSource() {
        if(dataSource == null) {
            dataSource = dataSource("127.0.0.1", "3306", "construction", "root", "qwerty");
        }
        return dataSource;
    }

    private static DataSource dataSource;


    public static DataSource dataSource(String address,
                                        String port,
                                        String dbName,
                                        String dbUser,
                                        String dbPassword) {
        try {
            ComboPooledDataSource c3p0 = new ComboPooledDataSource();
            c3p0.setDriverClass("com.mysql.jdbc.Driver");
            c3p0.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", address, port, dbName));
            c3p0.setUser(dbUser);
            c3p0.setPassword(dbPassword);
            return c3p0;
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    public static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    public static NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(getDataSource());
    }

    public static SimpleJdbcCall getSimpleJdbcCall(){
        return new SimpleJdbcCall(getDataSource());
    }
}
