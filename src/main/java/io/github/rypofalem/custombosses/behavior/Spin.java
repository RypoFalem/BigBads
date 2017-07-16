package io.github.rypofalem.custombosses.behavior;

import io.github.rypofalem.custombosses.bosses.BossWatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
public class Spin implements TickableBehavior{
	final BossWatcher watcher;
	protected double minSpeed;
	protected double maxSpeed;
	protected double acceleration;
	protected float yaw;

	@Override
	public void onTick(long tickCounter) {
		Location location = watcher.getEntity().getLocation().clone();
		location.setYaw(yaw);
		watcher.getEntity().teleport(location);
	}
}
