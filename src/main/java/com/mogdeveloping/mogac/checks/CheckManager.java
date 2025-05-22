package com.mogdeveloping.mogac.checks;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.combat.KillAura;
import com.mogdeveloping.mogac.checks.combat.Reach;
import com.mogdeveloping.mogac.checks.movement.Speed;
import com.mogdeveloping.mogac.checks.movement.Spider;
import com.mogdeveloping.mogac.checks.movement.Timer;
import com.mogdeveloping.mogac.checks.packet.PacketSpammer;
import com.mogdeveloping.mogac.checks.world.Destroyer;
import com.mogdeveloping.mogac.checks.world.Scaffold;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {

    private final MOGAC plugin;
    private final List<Check> checks = new ArrayList<>();

    public CheckManager(MOGAC plugin) {
        this.plugin = plugin;
        registerChecks();
    }

    private void registerChecks() {
        checks.add(new KillAura(plugin));
        checks.add(new Reach(plugin));
        checks.add(new Speed(plugin));
        checks.add(new Spider(plugin));
        checks.add(new Timer(plugin));
        checks.add(new Scaffold(plugin));
        checks.add(new Destroyer(plugin));
        checks.add(new PacketSpammer(plugin));
    }

    public List<Check> getChecks() {
        return checks;
    }

    public List<Check> getChecksForType(CheckType type) {
        List<Check> result = new ArrayList<>();
        
        for (Check check : checks) {
            if (check.getType() == type) {
                result.add(check);
            }
        }
        
        return result;
    }
}