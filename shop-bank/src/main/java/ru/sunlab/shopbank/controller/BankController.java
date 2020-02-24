package ru.sunlab.shopbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sunlab.shopbank.service.RmqListener;

@RestController
@RequestMapping("api/1.0/bank")
public class BankController {

    private final RmqListener rmqListener;
    @Autowired
    public BankController(RmqListener rmqListener) {
        this.rmqListener = rmqListener;
    }

    @PutMapping("/changemode")
    public ResponseEntity<String> changeMode(){
        if(rmqListener.confirm){
            rmqListener.confirm = false;
            return ResponseEntity.ok("Mode - always reject");
        } else {
            rmqListener.confirm = true;
            return ResponseEntity.ok("Mode - always confirm");
        }
    }
}
