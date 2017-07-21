package io.github.rypofalem.custombosses.boss.eyeofthespider;

import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.entity.TickableEntity;
import io.github.rypofalem.custombosses.Util;
import io.github.rypofalem.custombosses.attacks.Attack;
import io.github.rypofalem.custombosses.boss.BossWatcher;
import io.github.rypofalem.custombosses.boss.Minion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class EyeMinion implements TickableEntity, Minion {
	final Class type = Guardian.class;
	final int MAXHP = 15;
	final String NAME = "Mini Eye";
	public final static String CUSTOMID = "custombosses:minieye";
	static final EntityDamageEvent.DamageCause[] IMMUNITIES = {EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.HOT_FLOOR, EntityDamageEvent.DamageCause.CRAMMING, EntityDamageEvent.DamageCause.CONTACT,
	EntityDamageEvent.DamageCause.SUFFOCATION, EntityDamageEvent.DamageCause.DROWNING};


	@Override
	public String getCustomId() {
		return CUSTOMID;
	}

	@Override
	public Entity spawnEntity(Location location) {
		return location.getWorld().spawn(location, type, e -> {
			Guardian guard = (Guardian) e;
			guard.setAI(false);
			guard.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(MAXHP);
			guard.setHealth(MAXHP);
			guard.setCustomName(NAME);
		});
	}

	public EntityWatcher createEntityWatcher(final Entity entity) {
		return new Watcher(this, entity);
	}

	@Override
	public void onTick(EntityWatcher entityWatcher) {
		((Watcher)entityWatcher).onTick();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event){
		for(EntityDamageEvent.DamageCause immunity: IMMUNITIES){
			if(immunity == event.getCause()) event.setCancelled(true);
		}
	}

	@Getter
	public class Watcher implements MinionWatcher {
		protected final List<Attack> attacks = new ArrayList<>();
		protected final float speed = 1f;
		protected float yaw = 0;
		protected float pitch = 0;
		@Setter	float offSet = 0;
		protected double tickCount = 0;
		protected double schedule;
		protected Entity target;
		protected Entity entity;
		protected CustomEntity customEntity;
		protected BossWatcher parent = null;

		public Watcher(CustomEntity customEntity, Entity entity) {
			this.customEntity = customEntity;
			this.entity = entity;
			scheduleNextShot();
			getNewTarget();
		}

		public void onTick(){
			if(getParent() == null || getParent().getEntity() == null || !getParent().getEntity().isValid()){
				this.getEntity().remove();
				return;
			}
			yaw += speed;
			pitch += speed;
			while(yaw > 360) yaw -= 360;
			while(pitch > 360) pitch -=360;
			Location location = getParent().getEntity().getLocation().clone();
			location.setYaw(yaw + offSet);
			location.setPitch(pitch + offSet);
			location = location.add(location.getDirection().multiply(5));
			if(target != null) location.setDirection(Util.getDirectionTo(getEntity().getLocation(), target.getLocation()));
			if(tickCount >= schedule){
				scheduleNextShot();
				getNewTarget();
				if(target != null ){
					location.setDirection(Util.getDirectionTo(getEntity().getLocation(), target.getLocation()));
					location.getWorld().spawn(
							getEntity().getLocation().clone().add(getEntity().getLocation().getDirection()),
							ShulkerBullet.class,
							shulkerBullet -> shulkerBullet.setTarget(target));
				}
			}
			getEntity().teleport(location);
			tickCount++;
		}

		private void scheduleNextShot(){
			schedule = tickCount + 100 + Util.random.nextInt(2*60 + 1) - 30;
		}

		private Entity getNewTarget(){
			Map<Entity, Integer> targets = new HashMap<>();
			for(Entity e : getEntity().getNearbyEntities(32, 32, 32)){
				if(e instanceof Player)  targets.put(e,1);
			}
			return target = Util.getWeightedRandomChoice(targets);
		}

		public void setParent(BossWatcher bossWatcher){
			this.parent = bossWatcher;
		}
	}
}
