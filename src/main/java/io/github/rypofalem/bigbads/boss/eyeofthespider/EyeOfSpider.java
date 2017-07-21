package io.github.rypofalem.bigbads.boss.eyeofthespider;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.EntityContext;
import com.winthier.custom.entity.EntityWatcher;
import io.github.rypofalem.bigbads.attacks.ChargeAttack;
import io.github.rypofalem.bigbads.attacks.FloatAttack;
import io.github.rypofalem.bigbads.attacks.SummonEntities;
import io.github.rypofalem.bigbads.behavior.CollisionDamage;
import io.github.rypofalem.bigbads.boss.Boss;
import io.github.rypofalem.bigbads.boss.BossWatcher;
import io.github.rypofalem.bigbads.boss.Minion;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;


public class EyeOfSpider extends Boss {
	private static final String ID = "bigbads:eyeofthespider";
	private static final String NAME = "Eye of the Spider";
	private static final Class type = ElderGuardian.class;
	private static final double MAXHP = 500;

	public EyeOfSpider() {
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
			ElderGuardian boss = (ElderGuardian) e;
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
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event, EntityContext context) {
		if (event.getDamager().equals(context.getEntity())) {
			if (event.getCause() == DamageCause.THORNS) event.setCancelled(true);
		}
	}

	public class Watcher extends BossWatcher {
		ChargeAttack charge;
		BlindFloat blindFloat;
		FloatAttack floatAttack;
		SummonEyeMinion summon;

		public Watcher(EyeOfSpider eye, Entity boss) {
			super(eye, boss);

			blindFloat = new BlindFloat(this, 20 * 10, 15);
			blindFloat.setHRange(100);
			charge = new ChargeAttack(this, 1).setSpeed(.5).setMaxCharges(2);
			charge.setHRange(100);
			floatAttack = new FloatAttack(this, 60, 0);
			floatAttack.setHRange(100);
			summon = new SummonEyeMinion(this, 1, 120, 4);
			attacks.add(summon);
			//attacks.add(blindFloat);
			attacks.add(charge);
			attacks.add(floatAttack);

			behaviors.add(new CollisionDamage(this, 50, 0));
			this.setTarget(floatAttack.getNewTarget());
		}

		@Override
		public void onTick() {
			super.onTick();
			if (tickcounter % 100 == 0)
				entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.HOSTILE, 1f, 1f);
		}

		//set charges in a range of 2 to 7, scaling linearly with health lost
		//set charge speed in a range of .5 to 1, scaling linearly with health lost
		public void onHealthChange() {
			super.onHealthChange();
			double hpRatio = ((LivingEntity) entity).getHealth() /
					((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			charge.setMaxCharges(7 - (int) (5 * hpRatio));
			charge.setSpeed(1 - .5 * hpRatio);
		}

		class BlindFloat extends FloatAttack {
			public BlindFloat(Watcher watcher, int duration, int cooldown) {
				super(watcher, duration, cooldown);
			}

			@Override
			public void onTick(long tickCounter) {
				super.onTick(tickCounter);
				if (tickCounter % 20 != 0) return;
				for (Entity e : entity.getNearbyEntities(getHRange(), 256, getHRange())) {
					if(e instanceof Player){
						Player p = (Player) e;
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false), true);
					}
					if(e instanceof LivingEntity){
						LivingEntity le = (LivingEntity)e;
						le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, false, false), true);
					}
				}
			}
		}

		class SummonEyeMinion extends SummonEntities{
			public SummonEyeMinion(BossWatcher watcher, int duration, int cooldown, int number) {
				super(watcher, duration, cooldown, (EyeMinion)CustomPlugin.getInstance().getEntityManager().getCustomEntity(EyeMinion.CUSTOMID), number);
			}

			@Override
			public void onTick(long tickCounter){
				super.onTick(tickCounter);
				float offset = 0;
				float incr = 360f / getSummons().length;
				for(Minion.MinionWatcher minion : getSummons()){
					((EyeMinion.Watcher)minion).setOffSet(offset);
					offset += incr;
				}
			}
		}
	}
}
