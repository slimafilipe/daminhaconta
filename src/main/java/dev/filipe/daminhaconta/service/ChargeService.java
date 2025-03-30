package dev.filipe.daminhaconta.service;

import dev.filipe.daminhaconta.model.Charge;
import dev.filipe.daminhaconta.model.Client;
import dev.filipe.daminhaconta.repository.ChargeRepository;
import dev.filipe.daminhaconta.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargeService {

    private final ChargeRepository chargeRepository;

    private final ClientRepository clientRepository;

    private final MercadoPagoService mercadoPagoService;



    @Transactional
    public void processChargeMonth(){
        log.info("Iniciando processamento de cobrança mensais");

        List<Client> clients = clientRepository.findAll();
        log.info("Total de clientes para cobrar: {}", clients.size());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusMonths(1); // Vencimento em 1 mês

        for (Client client : clients) {
            try {
                log.info("Processando cobrança para cliente: {}", client.getName());

                //Criar registro da cobrança
                Charge charge = new Charge();
                charge.setClient(client);
                charge.setValue(client.getValuePayment());
                charge.setIssueDate(now);
                charge.setDueDate(dueDate);
                charge.setStatus("PENDENTE");

                // Salvar cobrança antes de processar pagamento
                charge = chargeRepository.save(charge);

                // Processar pagamento de acordo com a preferência do cliente
                String linkOrCode;
                if ("pix".equals(client.getPaymentPreferenceMethod())) {
                    linkOrCode = mercadoPagoService.createPaymentPix(client, charge);
                } else {
                    linkOrCode = mercadoPagoService.createLinkCharge(client, charge);
                }

                // Atualizar cobrança com informações do pagamento
                charge.setLinkPayment(linkOrCode);
                chargeRepository.save(charge);

                log.info("Cobrança gerada com sucesso para o cliente: {}", client.getName());
            } catch (Exception e) {
                log.error("Erro ao processar cobrança para o cliente: {}", client.getName(), e);

                // Registrar falha na cobrança
                Charge chargeFail = new Charge();
                chargeFail.setClient(client);
                chargeFail.setValue(client.getValuePayment());
                chargeFail.setIssueDate(now);
                chargeFail.setDueDate(dueDate);
                chargeFail.setStatus("FALHA");
                chargeFail.setDetailsErro(e.getMessage());
                chargeRepository.save(chargeFail);
            }
        }
        log.info("Processamento de cobranças mensais concluído");
    }

    @Transactional
    public void verifyStatusChargePending() {
        log.info("Verificando status de cobranças pendentes");

        List<Charge> chargesPending = chargeRepository.findByStatus("PENDENTE");

        for (Charge charge : chargesPending) {
            try {
                if (charge.getIdExternMecadoPago() != null) {
                    String status = mercadoPagoService.checkStatusPayment(charge.getIdExternMecadoPago());

                    if ("approved".equals(status)) {
                        charge.setStatus("PAGA");
                        chargeRepository.save(charge);
                        log.info("Cobrança {} marcada como PAGA", charge.getId());
                    } else if ("rejected".equals(status) || "cancelled".equals(status)) {
                        charge.setStatus("CANCELADA");
                        chargeRepository.save(charge);
                        log.info("Cobrança {} marcada como CANCELADA", charge.getId());
                    }
                }
            } catch (Exception e) {
                log.error("Erro ao verificar status da cobrança: {}", charge.getId(), e);
            }
        }
        log.info("Verificação de status concluída");
    }

}
