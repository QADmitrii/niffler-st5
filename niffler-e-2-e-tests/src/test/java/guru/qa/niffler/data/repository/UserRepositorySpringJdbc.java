package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.DataBase;
import guru.qa.niffler.data.entity.UserAuthEntity;
import guru.qa.niffler.data.entity.UserDataEntity;
import guru.qa.niffler.data.jdbc.DataSourceProvider;
import guru.qa.niffler.data.sjdbc.UserDataEntityRowMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserRepositorySpringJdbc implements UserRepository {

    private final static TransactionTemplate authTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSourceProvider.dataSource(DataBase.AUTH)
            )
    );


    private final static JdbcTemplate authJdbcTemplate = new JdbcTemplate(DataSourceProvider.dataSource(DataBase.AUTH));
    private final static JdbcTemplate userDataJdbcTemplate = new JdbcTemplate(DataSourceProvider.dataSource(DataBase.USERDATA));
    private final static PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UserAuthEntity createUserInAuth(UserAuthEntity user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        return authTemplate.execute(status -> {
            authJdbcTemplate.update(con -> {
                        PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                        "VAlUES (?, ?, ?, ?, ?, ?)",
                                PreparedStatement.RETURN_GENERATED_KEYS);

                        preparedStatement.setString(1, user.getUsername());
                        preparedStatement.setString(2, PASSWORD_ENCODER.encode(user.getPassword()));
                        preparedStatement.setObject(3, user.getEnabled());
                        preparedStatement.setObject(4, user.getAccountNonExpired());
                        preparedStatement.setObject(5, user.getAccountNonLocked());
                        preparedStatement.setObject(6, user.getCredentialsNonExpired());
                        return preparedStatement;
                    }, keyHolder
            );
            user.setId((UUID) keyHolder.getKeys().get("id"));

            authJdbcTemplate.batchUpdate("INSERT INTO authority (user_id, authority) VAlUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, user.getId());
                    ps.setString(2, user.getAuthorities().get(i).getAuthorityEntity().name());
                }

                @Override
                public int getBatchSize() {
                    return user.getAuthorities().size();
                }
            });

            return user;
        });
    }

    @Override
    public UserDataEntity createUserInUserData(UserDataEntity user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        return authTemplate.execute(status -> {
            userDataJdbcTemplate.update(con -> {
                        PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small) VAlUES (?, ?, ?, ?, ?, ?)",
                                PreparedStatement.RETURN_GENERATED_KEYS);
                        preparedStatement.setString(1, user.getUsername());
                        preparedStatement.setString(2, user.getCurrency().name());
                        preparedStatement.setString(3, user.getFirstname());
                        preparedStatement.setString(4, user.getSurname());
                        preparedStatement.setObject(5, user.getPhoto());
                        preparedStatement.setObject(6, user.getPhotoSmall());
                        return preparedStatement;
                    }, keyHolder
            );
            user.setId((UUID) keyHolder.getKeys().get("id"));

            return user;
        });
    }


    @Override
    public UserAuthEntity updateUserInAuth(UserAuthEntity user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        return authTemplate.execute(status -> {
            authJdbcTemplate.update(con -> {

                        PreparedStatement preparedStatement = con.prepareStatement("UPDATE \"user\" SET id=?, username=?, password=?, enabled=?, account_non_expired=?, account_non_locked=?, credentials_non_expired=? " +
                                        "WHERE username =?;",
                                PreparedStatement.RETURN_GENERATED_KEYS);

                        preparedStatement.setObject(1, user.getId());
                        preparedStatement.setString(2, user.getUsername());
                        preparedStatement.setString(3, PASSWORD_ENCODER.encode(user.getPassword()));
                        preparedStatement.setObject(4, user.getEnabled());
                        preparedStatement.setObject(5, user.getAccountNonExpired());
                        preparedStatement.setObject(6, user.getAccountNonLocked());
                        preparedStatement.setObject(7, user.getCredentialsNonExpired());
                        preparedStatement.setObject(8, user.getUsername());

                        return preparedStatement;
                    }, keyHolder
            );
            user.setId((UUID) keyHolder.getKeys().get("id"));


            authJdbcTemplate.batchUpdate("INSERT INTO authority (user_id, authority) VAlUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, user.getId());
                    ps.setString(2, user.getAuthorities().get(i).getAuthorityEntity().name());
                }

                @Override
                public int getBatchSize() {
                    return user.getAuthorities().size();
                }
            });
            return user;
        });
    }

    @Override
    public UserDataEntity updateUserInUserdata(UserDataEntity user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        return authTemplate.execute(status -> {
            userDataJdbcTemplate.update(con -> {
                        PreparedStatement preparedStatement = con.prepareStatement("UPDATE \"user\" " +
                                        "SET username=?, currency=?, firstname=?, surname=?, photo=?, photo_small=? " +
                                        "WHERE id=?",
                                PreparedStatement.RETURN_GENERATED_KEYS);
                        preparedStatement.setString(1, user.getUsername());
                        preparedStatement.setString(2, user.getCurrency().name());
                        preparedStatement.setString(3, user.getFirstname());
                        preparedStatement.setString(4, user.getSurname());
                        preparedStatement.setObject(5, user.getPhoto());
                        preparedStatement.setObject(6, user.getPhotoSmall());
                        preparedStatement.setObject(7, user.getId());

                        return preparedStatement;
                    }, keyHolder
            );
            user.setId((UUID) keyHolder.getKeys().get("id"));

            return user;
        });
    }

    @Override
    public Optional<UserDataEntity> findUserInUserDataById(UUID uuid) {
        try {
            return Optional.of(userDataJdbcTemplate.queryForObject("select * from \"user\" where id=?", new UserDataEntityRowMapper(), uuid));
        } catch (DataRetrievalFailureException e) {
            return Optional.empty();
        }
    }
}
