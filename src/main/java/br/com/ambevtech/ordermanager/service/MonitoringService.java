package br.com.ambevtech.ordermanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MonitoringService {

    public void sendAlert(String title, String message, String severity) {
        log.warn("ALERTA [{}]: {} - {}", severity, title, message);

        // Ajustar o que for necessario para monitoramento.
    }
}
