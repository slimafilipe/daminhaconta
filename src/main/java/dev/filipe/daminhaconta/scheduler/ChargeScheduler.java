package dev.filipe.daminhaconta.scheduler;

import dev.filipe.daminhaconta.service.ChargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChargeScheduler {

    private final ChargeService chargeService;

    // Executa todas as segunda-feiras às 8h da manhã
    @Scheduled(cron = "${charge.month.cron}")
    public void executeChargeMonth() {
        log.info("Iniciando execução agendada de cobranças mensais");
        chargeService.processChargeMonth();
    }

    // Verifica status de pagamento pendentes a cada 4 horas
    @Scheduled(fixedRate = 4 * 60 * 60 * 1000)
    public void verifyStatusPayment() {
        log.info("Iniciando verificação de status de pagamentos pendentes");
        chargeService.verifyStatusChargePending();
    }

}
