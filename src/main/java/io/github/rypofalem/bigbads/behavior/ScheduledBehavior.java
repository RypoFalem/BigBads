package io.github.rypofalem.bigbads.behavior;

import com.winthier.custom.entity.EntityWatcher;
import io.github.rypofalem.bigbads.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.function.Consumer;

public class ScheduledBehavior implements TickableBehavior{
	@Getter @Setter protected Consumer<EntityWatcher> behavior;
	@Getter @Setter EntityWatcher watcher;
	@Getter @Setter protected int frequency;
	@Getter @Setter protected int variance;
	protected int schedule;

	ScheduledBehavior(EntityWatcher watcher, Consumer<EntityWatcher> behavior, int frequency, int variance){
		this.watcher = watcher;
		this.behavior = behavior;
		this.frequency = frequency;
		this.variance = variance;
		scheduleNextEvent(0);
	}

	@Override
	public void onTick(long tickCounter) {
		if(schedule > tickCounter) return;
		scheduleNextEvent(tickCounter);
		behavior.accept(watcher);
	}

	protected int scheduleNextEvent(long tickCounter){
		return schedule = Util.random.nextInt(variance*2 + 1) - variance + frequency + (int)tickCounter;
	}

	public static ScheduledBehavior createScheduledNoise(EntityWatcher watcher, int frequency, int variance, Sound sound, SoundCategory category, float volume, float pitch){
		return new ScheduledBehavior(watcher,
				futureWatcher -> futureWatcher.getEntity().getLocation().getWorld().playSound(futureWatcher.getEntity().getLocation(), sound, category, 1, 1),
				frequency, variance);
	}
}
