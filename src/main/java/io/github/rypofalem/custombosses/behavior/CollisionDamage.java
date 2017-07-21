package io.github.rypofalem.custombosses.behavior;


import io.github.rypofalem.custombosses.CustomBossesPlugin;
import io.github.rypofalem.custombosses.boss.BossWatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Getter
@Setter
@AllArgsConstructor
public class CollisionDamage implements TickableBehavior{
	protected final BossWatcher watcher;
	protected double damage;
	protected double range = 0;

	@Override
	public void onTick(long tickCounter){
		for(Entity e : watcher.getEntity().getNearbyEntities(range, range, range)){
			if(!(e instanceof Player)) continue;
			CustomBossesPlugin.damageEntityNotArmor(watcher.getEntity(), (Player) e, damage);
		}
	}
}
