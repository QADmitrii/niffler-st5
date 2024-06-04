package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.DataBase;
import guru.qa.niffler.data.entity.AuthEntity;
import guru.qa.niffler.data.entity.CurrencyValues;
import guru.qa.niffler.data.entity.UserAuthEntity;
import guru.qa.niffler.data.entity.UserDataEntity;
import guru.qa.niffler.data.jdbc.DataSourceProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {
    private final static DataSource authDataSource = DataSourceProvider.dataSource(DataBase.AUTH);
    private final static DataSource udDataSource = DataSourceProvider.dataSource(DataBase.USERDATA);
    private final static PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UserAuthEntity createUserInAuth(UserAuthEntity user) {
        try (Connection connection = authDataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement userPs = connection
                    .prepareStatement("INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                    "VAlUES (?, ?, ?, ?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );
                 PreparedStatement authPs = connection
                         .prepareStatement("INSERT INTO authority (user_id, authority) " +
                                 "VAlUES (?, ?)"
                         )) {
                userPs.setString(1, user.getUsername());
                userPs.setString(2, PASSWORD_ENCODER.encode(user.getPassword()));
                userPs.setObject(3, user.getEnabled());
                userPs.setObject(4, user.getAccountNonExpired());
                userPs.setObject(5, user.getAccountNonLocked());
                userPs.setObject(6, user.getCredentialsNonExpired());
                userPs.executeUpdate();

                UUID generatedUserId = null;
                try (ResultSet resultSet = userPs.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        generatedUserId = UUID.fromString(resultSet.getString("id"));
                    } else {
                        throw new IllegalArgumentException("Не удалось получить uuid");
                    }
                }
                user.setId(generatedUserId);

                for (AuthEntity authority : user.getAuthorities()) {
                    authPs.setObject(1, generatedUserId);
                    authPs.setString(2, authority.getAuthorityEntity().name());
                    authPs.addBatch();
                    authPs.clearParameters();
                }
                authPs.executeBatch();
                connection.commit();
                return user;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDataEntity createUserInUserData(UserDataEntity user) {
        try (Connection connection = udDataSource.getConnection();
             PreparedStatement userPs = connection
                     .prepareStatement("INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small) " +
                                     "VAlUES (?, ?, ?, ?, ?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS
                     )) {

            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setObject(5, user.getPhoto());
            userPs.setObject(6, user.getPhotoSmall());
            userPs.executeUpdate();

            UUID generatedUserId = null;
            try (ResultSet resultSet = userPs.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedUserId = UUID.fromString(resultSet.getString("id"));
                } else {
                    throw new IllegalArgumentException("Не удалось получить uuid");
                }
            }
            user.setId(generatedUserId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserAuthEntity updateUserInAuth(UserAuthEntity user) {
        try (Connection connection = authDataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement userPs = connection
                    .prepareStatement("UPDATE \"user\" SET id=?, username=?, password=?, enabled=?, account_non_expired=?, account_non_locked=?, credentials_non_expired=? " +
                                    "WHERE id =?;",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );
                 PreparedStatement authPs = connection
                         .prepareStatement("INSERT INTO authority (user_id, authority) " +
                                 "VAlUES (?, ?)"
                         )) {
                userPs.setObject(1, user.getId());
                userPs.setString(2, user.getUsername());
                userPs.setString(3, PASSWORD_ENCODER.encode(user.getPassword()));
                userPs.setObject(4, user.getEnabled());
                userPs.setObject(5, user.getAccountNonExpired());
                userPs.setObject(6, user.getAccountNonLocked());
                userPs.setObject(7, user.getCredentialsNonExpired());
                userPs.setObject(8, user.getId());
                userPs.executeUpdate();

                UUID generatedUserId = null;
                try (ResultSet resultSet = userPs.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        generatedUserId = UUID.fromString(resultSet.getString("id"));
                    } else {
                        throw new IllegalArgumentException("Не удалось получить uuid");
                    }
                }
                user.setId(generatedUserId);

                for (AuthEntity authority : user.getAuthorities()) {
                    authPs.setObject(1, generatedUserId);
                    authPs.setString(2, authority.getAuthorityEntity().name());
                    authPs.addBatch();
                    authPs.clearParameters();
                }
                authPs.executeBatch();
                connection.commit();
                return user;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDataEntity updateUserInUserdata(UserDataEntity user) {
        try (Connection connection = udDataSource.getConnection();
             PreparedStatement prepareStatement = connection
                     .prepareStatement("UPDATE \"user\" " +
                                     "SET username=?, currency=?, firstname=?, surname=?, photo=?, photo_small=? " +
                                     "WHERE id=?",
                             PreparedStatement.RETURN_GENERATED_KEYS
                     )) {

            prepareStatement.setString(1, user.getUsername());
            prepareStatement.setString(2, user.getCurrency().name());
            prepareStatement.setString(3, user.getFirstname());
            prepareStatement.setString(4, user.getSurname());
            prepareStatement.setObject(5, user.getPhoto());
            prepareStatement.setObject(6, user.getPhotoSmall());
            prepareStatement.setObject(7, user.getId());

            prepareStatement.executeUpdate();

            UUID generatedUserId = null;
            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedUserId = UUID.fromString(resultSet.getString("id"));
                } else {
                    throw new IllegalArgumentException("Не удалось получить uuid");
                }
            }
            user.setId(generatedUserId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserDataEntity> findUserInUserDataById(UUID uuid) {
        UserDataEntity userDataEntity = new UserDataEntity();

        try (Connection connection = udDataSource.getConnection();
             PreparedStatement prepareStatement = connection
                     .prepareStatement("SELECT * FROM \"user\" where  id= ?",
                             PreparedStatement.RETURN_GENERATED_KEYS
                     );
        ) {
            prepareStatement.setObject(1, uuid);
            prepareStatement.execute();
            try (ResultSet resultSet = prepareStatement.getResultSet();) {
                if (resultSet.next()) {
                    userDataEntity.setId((UUID) resultSet.getObject("id"));
                    userDataEntity.setUsername(resultSet.getString("username"));
                    userDataEntity.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                    userDataEntity.setFirstname(resultSet.getString("firstname"));
                    userDataEntity.setSurname(resultSet.getString("surname"));
                    userDataEntity.setPhoto(resultSet.getBytes("photo"));
                } else {
                    Optional.empty();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.of(userDataEntity);
    }
}
