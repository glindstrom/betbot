/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gabriel.betbot.trades;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gabriel
 */
public enum MarketType {
    LIVE(0),
    TODAY(1),
    EARLY(2);
    
    private final int id;
    private final static Map<Integer, MarketType> idToMarketTypeMap = initMap();
    
    private MarketType(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public static MarketType getById(int id) {
        return idToMarketTypeMap.get(id);
    }
    
    private static Map<Integer, MarketType> initMap() {
        Map<Integer, MarketType> map = new HashMap();
        Arrays.stream(MarketType.values())
                .forEach(type -> map.put(type.id, type));
        return ImmutableMap.copyOf(map);
    }
}
