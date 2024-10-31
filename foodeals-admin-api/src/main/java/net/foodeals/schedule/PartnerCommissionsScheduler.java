package net.foodeals.schedule;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.foodeals.schedule.utils.PartnerCommissionsUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class PartnerCommissionsScheduler {

    private final PartnerCommissionsUtil partnerCommissionsService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdownNow();
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void createMonthlyPartnerCommissions() {
        log.info("Starting monthly partner commissions creation");
        try {
            partnerCommissionsService.createMonthlyPartnerCommissions();
        } catch (Exception e) {
            log.error("Error in monthly partner commissions creation", e);
        }
        log.info("Finished monthly partner commissions creation");
    }
}
