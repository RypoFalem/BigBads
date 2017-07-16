package io.github.rypofalem.custombosses.attacks;


import io.github.rypofalem.custombosses.Util;
import io.github.rypofalem.custombosses.bosses.BossWatcher;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class Attack {
	int cooldown;
	int MinDuration;
	int duration;
	long lastUsed = 0;
	double damage = 0;
	double hRange = 32;
	double vRange = 256;
	final BossWatcher watcher;

	public Attack(BossWatcher watcher, int duration, int cooldown){
		this.duration = 0;
		this.MinDuration = duration;
		this.watcher = watcher;
		this.cooldown = cooldown;
	}

	public void onTick(long tickCounter){
		duration++;
	}

	public LivingEntity getNewTarget(){
		Entity boss = watcher.getEntity();
		Map<LivingEntity, Integer> targetCandidates = new HashMap<>();
		for (Entity e : boss.getNearbyEntities(hRange, vRange, hRange)) {
			if (e instanceof Player)
				targetCandidates.put((Player) e, (int) (80 / boss.getLocation().distance(e.getLocation())));
		}
		LivingEntity target = Util.getWeightedRandomChoice(targetCandidates);
		watcher.setTarget(target);
		return target;
	}

	public boolean isCooledDown(){
		return System.currentTimeMillis() - lastUsed >= cooldown * 1000;
	}

	public boolean isValid(){return true;}

	public boolean isDone(){
		return duration >= MinDuration;
	}

	public void reset(){
		lastUsed = System.currentTimeMillis();
		duration = 0;
	}
}
