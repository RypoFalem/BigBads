package io.github.rypofalem.custombosses.attacks;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.EntityWatcher;
import io.github.rypofalem.custombosses.boss.BossWatcher;
import io.github.rypofalem.custombosses.boss.Minion;
import io.github.rypofalem.custombosses.boss.eyeofthespider.EyeMinion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

@Getter
public class SummonEntities extends Attack{
	protected final Minion type;
	protected final Minion.MinionWatcher[] summons;

	public SummonEntities(BossWatcher watcher, int duration, int cooldown, Minion type, int number) {
		super(watcher, duration, cooldown);
		this.summons = new Minion.MinionWatcher[number];
		this.type = type;
	}

	@Override
	public void onTick(long tickCounter){
		super.onTick(tickCounter);
		for (int i = 0; i < getSummons().length; i++) {
			Minion.MinionWatcher minion = getSummons()[i];
			if(minion == null || !minion.getEntity().isValid()){
				minion = (Minion.MinionWatcher) CustomPlugin.getInstance().getEntityManager().spawnEntity(watcher.getEntity().getLocation(), getType().getCustomId());
				minion.setParent(watcher);
				getSummons()[i] = minion;
			}
		}
	}


}
