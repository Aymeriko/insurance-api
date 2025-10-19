package ch.insurance.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.insurance.api.domain.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  Optional<Client> findByEmail(String email);

  boolean existsByEmail(String email);
}
