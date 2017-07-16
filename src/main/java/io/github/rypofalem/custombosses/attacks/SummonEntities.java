package io.github.rypofalem.custombosses.attacks;

import io.github.rypofalem.custombosses.boss.BossWatcher;
import org.bukkit.entity.Entity;


public class SummonEntities extends Attack{

	public SummonEntities(BossWatcher watcher, int duration, int cooldown, Class<Entity> type, int number) {
		super(watcher, duration, cooldown);
	}
}
