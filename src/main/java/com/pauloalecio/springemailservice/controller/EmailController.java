package com.pauloalecio.springemailservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pauloalecio.springemailservice.dto.Email;
import com.pauloalecio.springemailservice.service.EmailService;

@RestController
@RequestMapping("/api/v1/public")
public class EmailController {
  
  private final EmailService service;
  public EmailController(EmailService service){
    this.service = service;
    
  }

  @PostMapping("/email")
  public void sendEmail(@RequestBody Email email){
    service.sendEmail(email);
  }

  @PostMapping("/registration/users")
    public ResponseEntity<Void> registerUser(@RequestBody Email email){
        service.registerUser(email);
        return ResponseEntity.noContent().build();
    }


   
   @GetMapping("/registrationConfirm/users")
    public ResponseEntity<?> confirmRegistrationUser(@RequestParam("token") String token){
       return ResponseEntity.ok().body("Success");
    }

}
