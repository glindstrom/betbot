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
public enum OddsType {
    
    HANDICAP("Asian Handicap", "H"),
    OVER_UNDER("OverUnder", "O"),
    ONE_X_TWO("1X2", "X");
    
    private final String name;
    private final String code;
    
    private OddsType(final String name, final String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
    
}
