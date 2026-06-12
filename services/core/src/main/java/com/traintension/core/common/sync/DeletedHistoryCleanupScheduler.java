package com.traintension.core.common.sync;

import com.traintension.core.generated.Routines;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedHistoryCleanupScheduler {
    private final DSLContext dsl;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupDeletedHistory() {
        Integer deletedCount = Routines.cleanupDeletedHistory(dsl.configuration());
        if (deletedCount != null && deletedCount > 0) {
            log.info("Cleaned {} deleted history rows older than 60 days", deletedCount);
        }
    }
}
