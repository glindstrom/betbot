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
public enum SportsType {
    
    FOOTBALL(1),
    BASKETBALL(2);
    
    private final int id;

    private SportsType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
