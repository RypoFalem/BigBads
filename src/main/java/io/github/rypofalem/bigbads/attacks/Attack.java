package io.github.rypofalem.bigbads.attacks;


import io.github.rypofalem.bigbads.Util;
import io.github.rypofalem.bigbads.boss.BossWatcher;
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
	protected int cooldown;
	protected int MinDuration;
	protected int duration;
	protected long lastUsed = 0;
	protected double damage = 0;
	protected double hRange = 32;
	protected double vRange = 256;
	protected final BossWatcher watcher;

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
