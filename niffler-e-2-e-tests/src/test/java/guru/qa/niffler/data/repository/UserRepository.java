package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.UserAuthEntity;
import guru.qa.niffler.data.entity.UserDataEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    static UserRepository getInstance() {
        final String repo = System.getProperty("repo");
        if ("sjdbc".equals(repo)) {
            return new UserRepositorySpringJdbc();
        }
        return new UserRepositoryJdbc();
    }

    UserAuthEntity createUserInAuth(UserAuthEntity user);

    UserDataEntity createUserInUserData(UserDataEntity user);

    UserAuthEntity updateUserInAuth(UserAuthEntity user);

    UserDataEntity updateUserInUserdata(UserDataEntity user);

    Optional<UserDataEntity> findUserInUserDataById(UUID uuid);
}
