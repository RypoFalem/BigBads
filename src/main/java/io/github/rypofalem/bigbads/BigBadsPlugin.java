package io.github.rypofalem.bigbads;


import com.winthier.custom.event.CustomRegisterEvent;
import io.github.rypofalem.bigbads.boss.eyeofthespider.EyeMinion;
import io.github.rypofalem.bigbads.boss.eyeofthespider.EyeOfSpider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BigBadsPlugin extends JavaPlugin implements Listener {
	public static BigBadsPlugin instance;
	public static boolean dontDamageArmor = false;

	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		instance = this;
	}

	@EventHandler
	public void onCustomRegister(CustomRegisterEvent event) {
		event.addEntity(new EyeOfSpider());
		event.addEntity(new EyeMinion());
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onItemDamage(PlayerItemDamageEvent event){
		if(!dontDamageArmor) return;
		event.setCancelled(true);
	}

	public static void damageEntityNotArmor(LivingEntity damagee, double damage){
		try {
			dontDamageArmor = true;
			damagee.damage(damage);
		} finally {dontDamageArmor = false;}
	}

	public static void damageEntityNotArmor(Entity damager, LivingEntity damagee, double damage){
		try {
			dontDamageArmor = true;
			damagee.damage(damage, damager);
		} finally {dontDamageArmor = false;}
	}

}
