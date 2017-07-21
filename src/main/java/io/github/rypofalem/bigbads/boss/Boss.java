package io.github.rypofalem.bigbads.boss;


import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityContext;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.entity.TickableEntity;
import io.github.rypofalem.bigbads.BigBadsPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Boss implements TickableEntity, CustomEntity {
	@Getter protected List<EntityDamageEvent.DamageCause> immunities = new ArrayList<>();
	@Getter protected List<String> allowedWorlds = new ArrayList<>();

	public Boss(){
		allowedWorlds.add("resource");
		allowedWorlds.add("world");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBossDamage(EntityDamageEvent event, EntityContext context){
		for(EntityDamageEvent.DamageCause immunity : immunities){
			if(event.getCause() == immunity){
				event.setDamage(0);
				event.setCancelled(true);
				break;
			}
		}
		Bukkit.getScheduler().runTaskLater(
				BigBadsPlugin.instance,
				() -> ((BossWatcher)context.getEntityWatcher()).onHealthChange(),
				1);
	}

	@Override
	public void onTick(EntityWatcher entityWatcher) {
		if(!allowedWorlds.contains(entityWatcher.getEntity().getWorld().getName())) entityWatcher.getEntity().remove();
		((BossWatcher)entityWatcher).onTick();
	}

	@Override
	public void entityWatcherWillUnregister(EntityWatcher watcher) {
		((BossWatcher)watcher).removeBossBar();
	}

}
