package io.github.rypofalem.custombosses.boss;


import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;
import io.github.rypofalem.custombosses.attacks.Attack;
import io.github.rypofalem.custombosses.behavior.TickableBehavior;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class BossWatcher implements EntityWatcher {
	protected final CustomEntity customEntity;
	protected final Entity entity;
	protected final List<Attack> attacks = new ArrayList<>();
	protected final List<TickableBehavior> behaviors = new ArrayList<>();
	protected BossBar bossBar;
	protected Attack currentAttack;
	protected long tickcounter = 0;
	@Setter protected LivingEntity target;

	protected BossWatcher(CustomEntity customEntity, Entity entity){
		this.customEntity = customEntity;
		this.entity = entity;
		createBossBar();
		updateBossBar();
		updateBossBarPlayers();
	}

	protected void onTick(){
		tickcounter++;
		for(TickableBehavior behavior : behaviors){
			behavior.onTick(tickcounter);
		}
		if(tickcounter % 20 == 0) updateBossBarPlayers();
		if(setAttack() != null) currentAttack.onTick(tickcounter);
	}

	protected void onHealthChange(){
		updateBossBar();
	}

	protected BossBar createBossBar(){
		if(bossBar != null) removeBossBar();
		bossBar = Bukkit.createBossBar(entity.getCustomName(),
				BarColor.RED,
				BarStyle.SEGMENTED_6);
		bossBar.setVisible(true);
		return bossBar;
	}

	public void updateBossBar(){
		if(entity.isDead()){
			removeBossBar();
			return;
		}
		if(entity instanceof LivingEntity){
			bossBar.setProgress(((LivingEntity)entity).getHealth() /
					((LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		}
	}

	public void updateBossBarPlayers(){
		bossBar.removeAll();
		for(Entity e : entity.getNearbyEntities(128, 256, 128)){
			if(!(e instanceof Player)) continue;
			Player player = (Player)e;
			bossBar.addPlayer(player);
		}
	}

	public void removeBossBar(){
		bossBar.setVisible(false);
		bossBar.removeAll();
	}

	public List<Attack> getAttacks(){
		return Collections.unmodifiableList(attacks);
	}

	protected Attack setAttack(){
		if(currentAttack != null && !currentAttack.isDone()){
			return currentAttack;
		}
		if(currentAttack != null) currentAttack.reset();
		Collections.shuffle(attacks);
		for(Attack att : attacks){
			if(att.isCooledDown()){
				return currentAttack = att;
			}
		}
		return currentAttack;
	}
}
