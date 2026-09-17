package com.finance.dispatch.worker.service.reminder;

import com.finance.dispatch.worker.service.TelegramService;
import com.finance.dispatch.worker.service.task.MarketNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketReminder {

    private final TelegramService telegramService;
    private final MarketNewsService marketNewsService;

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void onReminder_alert30minBeforeEvent() {
        LocalTime now = LocalTime.now();

        boolean withinSchedule = !now.isBefore(LocalTime.of(18, 0)) ||
                        now.isBefore(LocalTime.of(2, 0));

        if (!withinSchedule) {
            return;
        }

        ZoneId zone = ZoneId.of("Asia/Phnom_Penh");

        OffsetDateTime current = OffsetDateTime.now(zone);
        OffsetDateTime next30Minutes = current.plusMinutes(30);

        var upcomingEvents = marketNewsService.onTask_RetrievingMarketNews().stream()
                .filter(event -> {
                    OffsetDateTime eventTime = event.getDate();

                    return !eventTime.isBefore(current)
                            && !eventTime.isAfter(next30Minutes);
                })
                .map(event -> """
                - %s at %s [%s]
                """.formatted(
                        event.getTitle(),
                        event.getDate().format(DateTimeFormatter.ofPattern("hh:mm a")),
                        marketNewsService.getImpactEmoji(event.getImpact())
                ))
                .collect(Collectors.joining());

        if (!upcomingEvents.isEmpty()) {
            var messageReminder = """
            <b>Upcoming events:</b>

            %s
            """.formatted(upcomingEvents);

            telegramService.sendMessage(messageReminder);
        }


    }

}
