package learn.Cloud.repository;

import learn.Cloud.entity.UserEntityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntityInfo,Long> {
    Optional<UserEntityInfo> findUserEntitiesByEmail(String email);

    @Query(value = "select u from UserEntityInfo u where u.email = :email" )
    Optional<UserEntityInfo> findByEmail (String email);

    boolean existsByEmail(String email);
}
