package com.shailu.deposito_dental_pos.scheduler;

import com.shailu.deposito_dental_pos.service.ReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportsScheduler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReportsService reportsService ;

    @Scheduled(cron = "0 0 20 * * *", zone = "America/Mexico_City")
    public void executeDailyReport(){
        logger.info("Generating daily Excel report...");
        reportsService.createDailyReport();
        logger.info("Excel report created");
    }
}
