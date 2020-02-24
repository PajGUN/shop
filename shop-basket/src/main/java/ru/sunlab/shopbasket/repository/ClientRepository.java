package ru.sunlab.shopbasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sunlab.shopbasket.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client getClientByPhoneNumber(String phoneNumber);
}
