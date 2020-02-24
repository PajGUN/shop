package ru.sunlab.shopbasket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shopbasket.exception.ClientNotFoundException;
import ru.sunlab.shopbasket.model.Client;
import ru.sunlab.shopbasket.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Client save(Client client){
        return clientRepository.save(client);
    }

    @Transactional
    public Client getById(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        clientOptional.orElseThrow(()-> new ClientNotFoundException("Client - " +
                clientId + " not found!"));
        return clientOptional.get();
    }

    @Transactional
    public Client update(Long clientId, Client client) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()){
            client.setId(clientId);
            return clientRepository.save(client);
        } else {
            throw new ClientNotFoundException("Client - "+ clientId + " not found!");
        }
    }

    @Transactional
    public Client delete(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            clientRepository.delete(client);
            return client;
        } else {
            throw new ClientNotFoundException("Client - "+ clientId + " not found!");
        }
    }

    @Transactional
    public Client getClientByPhoneNumber(String phoneNumber) {
        return clientRepository.getClientByPhoneNumber(phoneNumber);
    }

    @Transactional
    public List<Client> getAllClients(Pageable pageable) {
        Page<Client> allClients = clientRepository.findAll(pageable);
        return allClients.getContent();
    }
}
