package pdx.nekkoya48.farming;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import pdx.nekkoya48.api.Hologram;
import pdx.nekkoya48.api.MachineStatus;
import pdx.nekkoya48.api.PRODUCE48;

public class Farmer extends PRODUCE48 {

	public Farmer(Location loc) {
		super(loc, 1, "§a§lMáy nông sản", MachineStatus.WAITING, 1, 10);

		this.setUpgradeCost(0);

		setSize(45);
		setColor(DyeColor.LIME);
		setDemolish(33);
		setMachineInfo(31);
		setUpgrade(29);
		setInput(15, 14, 13, 12, 11);

		setTexture(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90" + "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQ0Mjk2Y"
						+ "jMzM2QyNTMxOWFmM2YzMzA1MTc5N2Y5ZTZkODIxY2QxOWEwM" + "TRmYjcxMzdiZWI4NmE0ZTllOTYifX19");
	}

	@Override
	public void upgrade() {

	}

	@Override
	public boolean work(Location location) {
		int radius = getLevel();

		for (double x = location.getX() - radius; x <= location.getX() + radius; x++) {
			for (double z = location.getZ() - radius; z <= location.getZ() + radius; z++) {
				Location loc = new Location(location.getWorld(), x, location.getY(), z);
				Block b = loc.getBlock();
				Block farmland = loc.getBlock().getRelative(BlockFace.DOWN);

				if (farmland.getBlockData().matches(Material.FARMLAND.createBlockData("[moisture=7]")) == false) {
					Hologram.runSync(() -> {
						farmland.setBlockData(Material.FARMLAND.createBlockData("[moisture=7]"));
					});
					loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.BLUE_WOOL);
				}

				BlockData bdata = b.getBlockData();
				if (bdata instanceof Ageable) {
					Ageable age = (Ageable) bdata;
					if (age.getAge() == age.getMaximumAge()) {
						loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.HAY_BLOCK);

						setStatus(MachineStatus.HARVESTING);
						updateHolo(location);
						updateUI();
						
						new BukkitRunnable() {
							public void run() {
								Chest chest = (Chest) location.getBlock().getRelative(BlockFace.UP).getState();
								Inventory inv = chest.getInventory();
								ItemStack[] drop = new ItemStack[b.getDrops().size()];
								drop = b.getDrops().toArray(drop);
								inv.addItem(drop);
							}
						}.runTask(Bukkit.getPluginManager().getPlugin("PDXNekkoya"));

						setType(b, Material.AIR);
						return true;
					}
				}
				
				if (b.getType() == Material.AIR && b.getRelative(BlockFace.DOWN).getType() == Material.FARMLAND) {
					setStatus(MachineStatus.PLANTING);
					updateHolo(location);
					updateUI();
					Material[] cropsItem = { Material.WHEAT_SEEDS, Material.POTATO, Material.CARROT,
							Material.BEETROOT_SEEDS, Material.SWEET_BERRIES };
					Material[] cropsSeed = { Material.WHEAT, Material.POTATOES, Material.CARROTS,
							Material.BEETROOTS, Material.SWEET_BERRY_BUSH };
					for (int so = 0; so < cropsItem.length; ++so) {
						if (getInventory().containsAtLeast(new ItemStack(cropsItem[so]), 1)) {
							setType(b, cropsSeed[so]);
							getInventory().removeItem(new ItemStack(cropsItem[so], 1));
							return true;
						}
					}
				}
			}
		}

		setStatus(MachineStatus.WAITING);
		updateHolo(location);
		updateUI();
		return true;
	}

	@Override
	public boolean demolish(Location location) {
		int radius = getLevel();

		for (double x = location.getX() - radius; x <= location.getX() + radius; x++) {
			for (double z = location.getZ() - radius; z <= location.getZ() + radius; z++) {
				Location loc = new Location(location.getWorld(), x, location.getY(), z);
				Block b = loc.getBlock();
				Block farmland = loc.getBlock().getRelative(BlockFace.DOWN);

				setType(b, Material.AIR);
				setType(farmland, Material.GRASS_BLOCK);
			}
		}

		setType(location.getBlock(), Material.AIR);
		setType(location.getBlock().getRelative(BlockFace.UP), Material.AIR);
		interrupt(location);
		return true;
	}

}
