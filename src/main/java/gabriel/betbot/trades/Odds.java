package gabriel.betbot.trades;

import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gabriel
 */
public class Odds {

    private final BigDecimal odds1;
    private final BigDecimal odds2;
    private final BigDecimal oddsX;
    private final BigDecimal edge1;
    private final BigDecimal edge2;
    private final BigDecimal edgeX;
    private final OddsType oddsType;
    private final String bookie;

    private Odds(final Builder builder) {
        this.odds1 = builder.odds1;
        this.odds2 = builder.odds2;
        this.oddsX = builder.oddsX;
        this.oddsType = builder.oddsType;
        this.bookie = builder.bookie;
        this.edge1 = builder.edge1;
        this.edge2 = builder.edge2;
        this.edgeX = builder.edgeX;
    }

    @Override
    public String toString() {
        String s = "odds1: " + odds1;
        s += ", odds2: " + odds2;
        if (oddsType == OddsType.ONE_X_TWO) {
            s += ", oddsX: " + oddsX;
        }
        s += ", edge1: " + edge1;
        s += ", edge2: " + edge2;
        if (oddsType == OddsType.ONE_X_TWO) {
            s += ", edgeX: " + edgeX;
        }
        
        return s;
    }

    public BigDecimal getHomeOdds() {
        return odds1;
    }

    public BigDecimal getAwayOdds() {
        return odds2;
    }

    public BigDecimal getDrawOdds() {
        return oddsX;
    }

    public BigDecimal getOverOdds() {
        return odds1;
    }

    public BigDecimal getUnderOdds() {
        return odds2;
    }

    public BigDecimal getOdds1() {
        return odds1;
    }

    public BigDecimal getOdds2() {
        return odds2;
    }

    public BigDecimal getOddsX() {
        return oddsX;
    }

    public OddsType getOddsType() {
        return oddsType;
    }

    public String getBookie() {
        return bookie;
    }

    public BigDecimal getEdge1() {
        return edge1;
    }

    public BigDecimal getEdge2() {
        return edge2;
    }

    public BigDecimal getEdgeX() {
        return edgeX;
    }

    public List<BigDecimal> getEdges() {
        List<BigDecimal> edges = new ArrayList();
        edges.add(edge1);
        edges.add(edge2);
        if (oddsType == OddsType.ONE_X_TWO) {
            edges.add(edgeX);
        }
        return ImmutableList.copyOf(edges);
    }

    public static class Builder {

        private BigDecimal odds1;
        private BigDecimal odds2;
        private BigDecimal oddsX;
        private BigDecimal edge1;
        private BigDecimal edge2;
        private BigDecimal edgeX;
        private OddsType oddsType;
        private String bookie;

        public Builder() {
            this.edge1 = BigDecimal.ZERO;
            this.edge2 = BigDecimal.ZERO;
            this.edgeX = BigDecimal.ZERO;
        }

        public Builder(final Odds source) {
            this.odds1 = source.odds1;
            this.odds2 = source.odds2;
            this.oddsX = source.oddsX;
            this.edge1 = source.edge1;
            this.edge2 = source.edge2;
            this.edgeX = source.edge2;
            this.oddsType = source.oddsType;
            this.bookie = source.bookie;
        }

        public Builder withHomeOdds(final BigDecimal homeOdds) {
            this.odds1 = homeOdds;
            return this;
        }

        public Builder withAwayOdds(final BigDecimal awayOdds) {
            this.odds2 = awayOdds;
            return this;
        }

        public Builder withOdds1(final BigDecimal odds1) {
            this.odds1 = odds1;
            return this;
        }

        public Builder withOdds2(final BigDecimal odds2) {
            this.odds2 = odds2;
            return this;
        }

        public Builder withEdge1(final BigDecimal edge1) {
            this.edge1 = edge1;
            return this;
        }

        public Builder withEdge2(final BigDecimal edge2) {
            this.edge2 = edge2;
            return this;
        }

        public Builder withEdgeX(final BigDecimal edgeX) {
            this.edgeX = edgeX;
            return this;
        }

        public Builder withDrawOdds(final BigDecimal drawOdds) {
            this.oddsX = drawOdds;
            return this;
        }

        public Builder withOverOdds(final BigDecimal overOdds) {
            this.odds1 = overOdds;
            return this;
        }

        public Builder withUnderOdds(final BigDecimal underOdds) {
            this.odds2 = underOdds;
            return this;
        }

        public Builder withOddsType(final OddsType oddsType) {
            this.oddsType = oddsType;
            return this;
        }

        public Builder withBookie(final String bookie) {
            this.bookie = bookie;
            return this;
        }

        public Odds build() {
            return new Odds(this);
        }
    }

}
