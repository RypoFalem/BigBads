package io.github.rypofalem.custombosses.attacks;


import io.github.rypofalem.custombosses.Util;
import io.github.rypofalem.custombosses.bosses.BossWatcher;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
public class ChargeAttack extends Attack{
	@Setter public int maxCharges = 1;
	@Setter public int chargesLeft = maxCharges;
	@Setter public int maxAfterChargeDelay = 10;
	@Setter public double speed = .75;
	@Setter public double chargeLength = 25;

	protected int afterChargeDelay = 0;
	protected double distanceTraveled = 0;
	protected Location chargeDestination;

	public ChargeAttack(BossWatcher watcher, int cooldown) {
		super(watcher, 0, cooldown);
	}

	public void charge(){
		getNewTarget();
		if(watcher.getTarget() == null) return;
		if(watcher.getTarget().getWorld() != watcher.getEntity().getWorld()) return;
		if(chargeDestination == null){
			distanceTraveled = 0;
			chargeDestination = watcher.getTarget().getLocation().clone();
			chargeDestination.getWorld().playSound(chargeDestination, Sound.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 1, 1);
			Vector direction = Util.getDirectionTo(watcher.getEntity().getLocation(), chargeDestination).normalize();
			Vector relativeDestination = direction.clone().multiply(chargeLength);
			try{relativeDestination.checkFinite();}
			catch(IllegalArgumentException ex) {
				relativeDestination = new Vector(0,1,0);
				direction = new Vector(0,1,0);
			}
			chargeDestination = watcher.getEntity().getLocation().clone().add(relativeDestination);
			chargeDestination.setDirection(direction);
		}
		Entity boss = watcher.getEntity();
		Location location = boss.getLocation();
		if(distanceTraveled >= chargeLength + speed){
			location = chargeDestination;
			chargesLeft--;
			chargeDestination = null;
			afterChargeDelay = maxAfterChargeDelay;
		} else {
			location.setDirection(chargeDestination.getDirection());
			location = location.add(chargeDestination.getDirection().multiply(speed));
			distanceTraveled += speed;
		}
		try {
			boss.teleport(location);
		} catch (IllegalArgumentException exception){
			System.out.println(String.format("Could not teleport boss to location.\n" +
							"\tTeleport Location: %s\n" +
							"\tBoss Location: %s\n" +
							"\tCharge Destination:: %s"
					,location.toString(), boss.getLocation().toString(), chargeDestination.toString()));
			exception.printStackTrace();
		}
	}

	@Override
	public void onTick(long tickCounter){
		super.onTick(tickCounter);
		if(afterChargeDelay > 0) afterChargeDelay--;
		else charge();
	}

	@Override
	public boolean isDone(){
		return chargesLeft <= 0;
	}

	@Override
	public void reset(){
		super.reset();
		chargesLeft = maxCharges;
	}

	@Override
	public boolean isValid(){
		return getNewTarget() == null;
	}
}
