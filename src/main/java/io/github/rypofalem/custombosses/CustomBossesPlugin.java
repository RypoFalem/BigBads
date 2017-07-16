package io.github.rypofalem.custombosses;


import com.winthier.custom.event.CustomRegisterEvent;
import io.github.rypofalem.custombosses.boss.eyeofthespider.EyeOfSpider;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomBossesPlugin extends JavaPlugin implements Listener {
	public static CustomBossesPlugin instance;

	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		instance = this;
	}

	@EventHandler
	public void onCustomRegister(CustomRegisterEvent event) {
		event.addEntity(new EyeOfSpider());
	}

}
