package org.dantesys.reliquiasNexus.util;

import org.bukkit.entity.Player;

public class TpaRequest {
    private final Player request;
    private final Player target;
    private final long timestamp;
    public TpaRequest(Player request, Player target){
        this.request=request;
        this.target=target;
        this.timestamp=System.currentTimeMillis();
    }
    public Player getRequest(){
        return this.request;
    }
    public Player getTarget(){
        return this.target;
    }
    public long getTimestamp(){
        return this.timestamp;
    }
}
