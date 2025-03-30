package dev.filipe.daminhaconta.controller;

import dev.filipe.daminhaconta.model.Charge;
import dev.filipe.daminhaconta.repository.ChargeRepository;
import dev.filipe.daminhaconta.repository.ClientRepository;
import dev.filipe.daminhaconta.service.ChargeService;
import dev.filipe.daminhaconta.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final ClientRepository clientRepository;
    private final ChargeRepository chargeRepository;
    private ChargeService chargeService;

    // Endpoints para gerenciar clientes
    @GetMapping("/clients")
    public List<Client> clientList() {
        return clientRepository.findAll();
    }
    @PostMapping("/clients")
    public Client registerClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }
    @PutMapping("/clients/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client clientUpdated) {
        if (!clientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clientUpdated.setId(id);
        return ResponseEntity.ok(clientRepository.save(clientUpdated));
    }
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (!clientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clientRepository.deleteAllById(Collections.singleton(id));
        return ResponseEntity.noContent().build();
    }

    // Endpoints para cobranças
    @GetMapping("/charges")
    public List<Charge> chargeList() {
        return chargeRepository.findAll();
    }
    @GetMapping("/charges/{id}")
    public ResponseEntity<Charge> getCharge(@PathVariable Long id) {
        Optional<Charge> charge = chargeRepository.findById(id);
        return charge.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/clients/{id}/charges")
    public ResponseEntity<List<Charge>> listChargesClients(@PathVariable Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            return  ResponseEntity.notFound().build();
        }
        List<Charge> charges = chargeRepository.findAll().stream().filter(c -> c.getClient().getId().equals(id) ).toList();
        return ResponseEntity.ok(charges);
    }

    // Endpoint para executar cobranças manualmente
    @PostMapping("/execute-charges")
    public ResponseEntity<String> executeChargesManually() {
        chargeService.processChargeMonth();
        return ResponseEntity.ok("Processso de cobranças iniciado com sucesso");
    }

    // Endpoint para verificar status das cobranças
    @PostMapping("/verify-status")
    public ResponseEntity<String> verifyStatusCharges() {
        chargeService.verifyStatusChargePending();
        return  ResponseEntity.ok("Verificação de status iniciada com sucesso.");
    }
}
