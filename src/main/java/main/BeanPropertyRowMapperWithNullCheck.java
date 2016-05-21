package main;


import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BeanPropertyRowMapperWithNullCheck<T> extends BeanPropertyRowMapper<T> {

    @Override
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return rs.getObject(1) == null ? null : super.getColumnValue(rs, index, pd);
    }

}
