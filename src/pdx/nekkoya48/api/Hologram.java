package pdx.nekkoya48.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

public final class Hologram {

	public static void update(Block b, String name) {
		runSync(() -> {
			ArmorStand hologram = getArmorStand(b, true);
			hologram.setCustomName(name);
		});
	}

	public static void remove(Block b) {
		runSync(() -> {
			ArmorStand hologram = getArmorStand(b, false);
			if (hologram != null)
				hologram.remove();
		});
	}

	public static ArmorStand getArmorStand(Block b, boolean createIfNoneExists) {
		Location l = new Location(b.getWorld(), b.getX() + 0.5, b.getY() + 0.3F, b.getZ() + 0.5);

		for (Entity n : l.getChunk().getEntities()) {
			if (n instanceof ArmorStand && n.getCustomName() != null && l.distanceSquared(n.getLocation()) < 0.4D) {
				return (ArmorStand) n;
			}
		}

		if (!createIfNoneExists)
			return null;
		else
			return create(l);
	}

	public static ArmorStand create(Location l) {
		ArmorStand armorStand = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		armorStand.setVisible(false);
		armorStand.setSilent(true);
		armorStand.setMarker(true);
		armorStand.setGravity(false);
		armorStand.setBasePlate(false);
		armorStand.setCustomName("Default");
		armorStand.setCustomNameVisible(true);
		armorStand.setRemoveWhenFarAway(false);
		return armorStand;
	}

	public static BukkitTask runSync(Runnable r) {
		return Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("PDXNekkoya"), r);
	}

}