package io.github.rypofalem.custombosses.boss;

import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;

public interface Minion extends CustomEntity {

	interface MinionWatcher extends EntityWatcher{
		void setParent(BossWatcher parent);
		BossWatcher getParent();
	}
}
