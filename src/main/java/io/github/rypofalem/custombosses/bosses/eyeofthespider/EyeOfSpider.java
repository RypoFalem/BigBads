package io.github.rypofalem.custombosses.bosses.eyeofthespider;

import com.winthier.custom.entity.EntityContext;
import com.winthier.custom.entity.EntityWatcher;
import io.github.rypofalem.custombosses.attacks.ChargeAttack;
import io.github.rypofalem.custombosses.attacks.FloatAttack;
import io.github.rypofalem.custombosses.behavior.CollisionDamage;
import io.github.rypofalem.custombosses.bosses.Boss;
import io.github.rypofalem.custombosses.bosses.BossWatcher;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;


public class EyeOfSpider extends Boss {
	private static final String ID = "custombosses:eyeofthespider";
	private static final String NAME =  "Eye of the Spider";
	private static final Class type = ElderGuardian.class;
	private static final double MAXHP = 300;

	public EyeOfSpider(){
		immunities = Arrays.asList(DamageCause.LAVA, DamageCause.HOT_FLOOR, DamageCause.CRAMMING, DamageCause.CONTACT,
				DamageCause.SUFFOCATION, DamageCause.DROWNING);
	}

	@Override
	public String getCustomId() {
		return ID;
	}

	@Override
	public Entity spawnEntity(Location location) {
		return location.getWorld().spawn(location, type, e -> {
			ElderGuardian boss = (ElderGuardian)e;
			boss.setAI(false);
			boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(MAXHP);
			boss.setHealth(MAXHP);
			boss.setCustomName(NAME);
		});
	}

	@Override
	public EntityWatcher createEntityWatcher(Entity boss) {
		return new Watcher(this, boss);
	}


	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDamage(EntityDamageByEntityEvent event, EntityContext context){
		if(!event.getDamager().equals(context.getEntity())) return;
		if(event.getCause() == DamageCause.THORNS) event.setCancelled(true);
	}

	public class Watcher extends BossWatcher{
		private ChargeAttack charge;

		public Watcher(EyeOfSpider eye, Entity boss){
			super(eye, boss);

			FloatAttack blindFloat = new FloatAttack(this, 20 * 9, 15){
				@Override
				public void onTick(long tickCounter){
					super.onTick(tickCounter);
					if(tickCounter%10 == 0){
						for(Entity e : entity.getNearbyEntities(getHRange(), 256, getHRange())){
							if(e instanceof LivingEntity){
								if(e instanceof Player)
									((LivingEntity) e).addPotionEffect(
											new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, false, false), true);
								else
									((LivingEntity) e).addPotionEffect(
											new PotionEffect(PotionEffectType.GLOWING, 80, 1, false, false), true);
							}
						}
						((LivingEntity)entity).addPotionEffect(
								new PotionEffect(PotionEffectType.GLOWING, 80, 1, false, false), true);
					}
				}
			};
			blindFloat.setHRange(100);
			charge = new ChargeAttack(this, 1);
			charge.setSpeed(.5);
			charge.setMaxCharges(2);
			charge.setHRange(100);
			FloatAttack floatAttack = new FloatAttack(this, 100,0);
			floatAttack.setHRange(100);
			attackList.add(blindFloat);
			attackList.add(charge);
			attackList.add(floatAttack);

			behaviors.add(new CollisionDamage(this, 30, 0));
		}

		@Override
		public void onTick(){
			super.onTick();
			if(tickcounter % 100 == 0) entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.HOSTILE, 1f, 1f);
		}

		//set charges in a range of 2 to 7, scaling linearly with health lost
		//set charge speed in a range of .5 to 1, scaling linearly with health lost
		public void onHealthChange(){
			super.onHealthChange();
			double hpRatio = ((LivingEntity)entity).getHealth() /
					((LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			charge.setMaxCharges(7 - (int)(5 * hpRatio));
			charge.setSpeed(1 - .5 * hpRatio);
		}
	}
}
