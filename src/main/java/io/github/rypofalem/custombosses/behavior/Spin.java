package io.github.rypofalem.custombosses.behavior;

import io.github.rypofalem.custombosses.boss.BossWatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
public class Spin implements TickableBehavior{
	protected final BossWatcher watcher;
	protected float speed;
	protected float minSpeed;
	protected float maxSpeed;
	protected float acceleration;

	@Override
	public void onTick(long tickCounter) {
		speed += acceleration;
		speed = Math.max(minSpeed, Math.min(speed, maxSpeed));
		Location location = watcher.getEntity().getLocation().clone();
		location.setYaw(location.getYaw() - speed);
		watcher.getEntity().teleport(location);
	}
}
