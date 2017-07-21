package io.github.rypofalem.bigbads.boss;

import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;

public interface Minion extends CustomEntity {

	interface MinionWatcher extends EntityWatcher{
		void setParent(BossWatcher parent);
		BossWatcher getParent();
	}
}
