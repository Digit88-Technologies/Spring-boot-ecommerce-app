package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for the VerificationToken data.
 */
public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);

    List<VerificationToken> findByUser_IdOrderByIdDesc(Long id);

}
