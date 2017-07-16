package io.github.rypofalem.custombosses.attacks;


import io.github.rypofalem.custombosses.Util;
import io.github.rypofalem.custombosses.bosses.BossWatcher;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class FloatAttack extends Attack {
	public static final float fullCircle = 360f;
	@Getter @Setter public float maxSpeed = .35f;
	@Getter @Setter public float rotationSpeed = .1f * 360f / 20f;
	@Getter @Setter public float minPitch = -60f;
	@Getter @Setter public float maxPitch = 0f;
	@Getter @Setter public float acceleration = .015f;
	@Getter @Setter public double targetDistance = 2;

	protected final Vector speed = new Vector(0,0,0);
	protected float pitch = 0;
	protected float yaw = 0;
	protected boolean goingUp = true;

	public FloatAttack(BossWatcher watcher, int duration, int cooldown){
		super(watcher, duration, cooldown);
	}

	@Override
	public void onTick(long tickcounter){
		super.onTick(tickcounter);
		if(tickcounter % (20 * 5) == 0) getNewTarget();
		if(watcher.getTarget() == null && tickcounter % (20*10) == 0) getNewTarget();
		move();
	}

	private void move(){
		Entity boss = watcher.getEntity();
		LivingEntity target = watcher.getTarget();
		if(target == null) return;
		if(target.getWorld() != boss.getWorld()) return;

		if(goingUp){
			pitch -= rotationSpeed;
			if(pitch < minPitch){
				goingUp = false;
			}
		} else {
			pitch += rotationSpeed;
			if(pitch > maxPitch){
				goingUp = true;
			}
		}
		yaw += rotationSpeed;
		while(yaw > fullCircle) yaw -= fullCircle;

		Location location = target.getEyeLocation().clone();
		location.setPitch(pitch);
		location.setYaw(yaw);
		location.add(location.getDirection().multiply(targetDistance));
		//location is now a point around the target
		Vector direction = Util.getDirectionTo(boss.getLocation(), location).normalize();
		speed.add(direction.multiply(acceleration));
		if(speed.length() > maxSpeed) speed.normalize().multiply(maxSpeed);
		location = boss.getLocation().add(speed);
		//location is now the boss's old location but moved towards the selected target point
		location.setDirection(target.getEyeLocation().toVector().subtract(location.toVector()));
		boss.teleport(location);
	}

	@Override
	public boolean isValid(){
		return getNewTarget() == null;
	}

}
