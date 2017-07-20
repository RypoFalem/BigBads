package io.github.rypofalem.custombosses.boss;

import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public abstract class Minion implements CustomEntity {

	@Getter @Setter
	public abstract class MinionWatcher implements EntityWatcher{
		final Entity entity;
		final CustomEntity customEntity;
		BossWatcher parent;

		public MinionWatcher(CustomEntity customEntity, Entity entity) {
			this.entity = entity;
			this.customEntity = customEntity;
		}
	}
}
