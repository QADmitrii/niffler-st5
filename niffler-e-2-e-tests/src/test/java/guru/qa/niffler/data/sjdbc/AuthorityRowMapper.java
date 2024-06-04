package guru.qa.niffler.data.sjdbc;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthorityRowMapper implements RowMapper<AuthorityEntity> {
    @Override
    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setId((UUID) rs.getObject("id"));
        authority.setUserId((UUID) rs.getObject("user_id"));
        authority.setAuthority(rs.getString("authority"));
        return authority;
    }
}
