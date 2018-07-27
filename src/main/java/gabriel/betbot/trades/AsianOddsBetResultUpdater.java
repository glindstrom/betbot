package gabriel.betbot.trades;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gabriel.betbot.dtos.bethistorysummary.BetHistorySummaryDto;
import gabriel.betbot.dtos.bethistorysummary.BetSummary;
import gabriel.betbot.repositories.BetRepository;
import gabriel.betbot.service.AsianOddsClient;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gabriel
 */
@Named
public class AsianOddsBetResultUpdater {

    private static final Logger LOG = LogManager.getLogger(AsianOddsBetResultUpdater.class.getName());
    private static final ImmutableMap<String, BetStatus> STRING_TO_BET_STATUS = new ImmutableMap.Builder<String, BetStatus>()
            .put("8", BetStatus.REJECTED)
            .put("V", BetStatus.VOIDED)
            .put("E", BetStatus.FAIL)
            .put("2", BetStatus.SETTLED)
            .build();
    private static final ImmutableMap<String, PnlStatus> STRING_TO_PNL_STATUS = new ImmutableMap.Builder<String, PnlStatus>()
            .put("Won", PnlStatus.WON)
            .put("Half lost", PnlStatus.HALF_LOST)
            .put("Half won", PnlStatus.HALF_WON)
            .put("Lost", PnlStatus.LOST)
            .put("Stake returned", PnlStatus.STAKE_RETURNED)
            .build();

    private static final ImmutableList<String> EXCLUDES_STATUSES = ImmutableList.of("S", "0", "1");

    private final AsianOddsClient asianOddsClient;
    private final BetRepository betRepository;

    @Inject
    public AsianOddsBetResultUpdater(final AsianOddsClient asianOddsClient, final BetRepository betRepository) {
        this.asianOddsClient = asianOddsClient;
        this.betRepository = betRepository;
    }

    public void updateResults() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        updateResults(yesterday);
    }

    public void updateResults(final LocalDate date) {
        BetHistorySummaryDto dto = asianOddsClient.getBetHistorySummaryDto(date);
        if (hasSummary(dto)) {
            List<Bet> betsToUpdate = dto.result.betSummaries.stream()
                    .filter(bs -> !EXCLUDES_STATUSES.contains(bs.status))
                    .map(this::updateBetsWithResult)
                    .filter(bet -> Objects.nonNull(bet))
                    .collect(Collectors.toList());
            LOG.info("Updating {} bets", betsToUpdate.size());
            betsToUpdate.stream()
                    .map(betRepository::saveAndGet)
                    .forEach(System.out::println);

        }
    }

    private boolean hasSummary(final BetHistorySummaryDto dto) {
        return dto.result != null && dto.result.betSummaries != null;
    }

    private Bet updateBetsWithResult(final BetSummary betSummary) {
        Bet bet = betRepository.findByBetPlacementReferece(betSummary.betPlacementReference);
        if (bet == null) {
            LOG.warn("Could not find bet with betPLacementReference {}, bet summary: {}", betSummary.betPlacementReference, betSummary);
            return null;
        }
        BetStatus betStatus = STRING_TO_BET_STATUS.get(betSummary.status);
        if (bet.getStatus() == betStatus) {
            return null;
        }
        Bet.Builder betBuilder = new Bet.Builder(bet)
                .withStatus(betStatus)
                .withBetPlacementMessage(betSummary.betPlacementMessage);

        if (betStatus == BetStatus.SETTLED) {
            betBuilder = betBuilder
                    .withPnlStatus(STRING_TO_PNL_STATUS.get(betSummary.pnlInfo))
                    .withPnl(betSummary.pnl)
                    .withActualStake(betSummary.stake);
        }
        LOG.info("Updating bet {}, result: {}", bet.getId(), betSummary.pnl);
        return betBuilder.build();
    }

}
