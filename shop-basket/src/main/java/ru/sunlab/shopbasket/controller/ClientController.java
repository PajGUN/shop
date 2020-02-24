package ru.sunlab.shopbasket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sunlab.shopbasket.dto.ClientDto;
import ru.sunlab.shopbasket.dto.MapperUtil;
import ru.sunlab.shopbasket.model.Client;
import ru.sunlab.shopbasket.service.ClientService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/1.0/client")
public class ClientController {

    private final ClientService clientService;
    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/add")
    ResponseEntity<ClientDto> add(@Valid  @RequestBody Client client){
        Client c = clientService.save(client);
        if (c == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.ok(MapperUtil.mapToClientDto(c));
    }

    @GetMapping("/get/{clientId}")
    ResponseEntity<ClientDto> getById(@PathVariable Long clientId){
        Client client = clientService.getById(clientId);
        return ResponseEntity.ok(MapperUtil.mapToClientDto(client));
    }

    @PutMapping("/update/{clientId}")
    ResponseEntity<ClientDto> update(@PathVariable Long clientId,
                                     @Valid @RequestBody Client client){
        Client c = clientService.update(clientId, client);
        return ResponseEntity.ok(MapperUtil.mapToClientDto(c));
    }

    @DeleteMapping("/delete/{clientId}")
    ResponseEntity<ClientDto> delete(@PathVariable Long clientId){
        Client client = clientService.delete(clientId);
        return ResponseEntity.ok(MapperUtil.mapToClientDto(client));
    }

    @GetMapping("/getbyphonenumber/{phoneNumber}")
    public ResponseEntity<ClientDto> getClientByPhoneNumber(@PathVariable String phoneNumber){
        Client client = clientService.getClientByPhoneNumber(phoneNumber);
        if (client == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(MapperUtil.mapToClientDto(client));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClientDto>> getAllClients(
            @PageableDefault(sort = {"lastName", "firstName"},direction = Sort.Direction.ASC) Pageable pageable){
        List<Client> clients = clientService.getAllClients(pageable);
        if (clients.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(MapperUtil.mapToListClientDto(clients));
    }
}
