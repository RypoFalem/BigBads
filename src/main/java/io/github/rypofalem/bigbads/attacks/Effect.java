package io.github.rypofalem.bigbads.attacks;


import io.github.rypofalem.bigbads.boss.BossWatcher;

public class Effect extends Attack{

	public Effect(BossWatcher watcher, int duration, int cooldown) {
		super(watcher, duration, cooldown);
	}
}
