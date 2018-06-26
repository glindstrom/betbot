/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gabriel.betbot.trades;

/**
 *
 * @author gabriel
 */
public enum OddsName {
    HOME_ODDS("HomeOdds"),
    AWAY_ODDS("AwayOdds"),
    DRAW_ODDS("DrawOdds");
    
    private final String name;

    private OddsName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
       return getName();
    }
    
    
    
    
}
