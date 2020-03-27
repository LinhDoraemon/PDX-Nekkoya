package pdx.nekkoya48.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pdx.nekkoya48.api.Hologram;
import pdx.nekkoya48.api.MachineHelper;
import pdx.nekkoya48.api.MachineStatus;
import pdx.nekkoya48.api.PRODUCE48;

public class VoidQuarry extends PRODUCE48 {

	private boolean isProcessing;
	private int timeLeft;
	private int totalTime;

	public VoidQuarry(Location location) {
		super(location, 2, "§9§lMáy đào quặng", MachineStatus.WAITING, 1, 7);

		this.setUpgradeCost(0);

		setSize(45);
		setColor(DyeColor.BLUE);
		setDemolish(33);
		setMachineInfo(31);
		setUpgrade(29);
		setInput(15, 14, 13, 12, 11);

		setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6L" + "y90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
				+ "OGQ4NjIwMjQxMDhjNzg1YmMwZWY3MTk5ZWM3N2M0MDJ" + "kYmJmY2M2MjRlOWY0MWY4M2Q4YWVkOGIzOWZkMTMifX19");
	}

	@Override
	public void upgrade() {
	}

	@Override
	public boolean work(Location location) {
		int rareLevel = getLevel();
		List<ItemStack> ore = getOresByLevel(rareLevel);
		ItemStack item = ore.get(new Random().nextInt(ore.size()));
		int index = ore.indexOf(item);

		HashMap<Material, Integer> map = getFuel();

		if (isProcessing) {
			Hologram.runSync(() -> {
				Chest chest = (Chest) location.getBlock().getRelative(BlockFace.UP).getState();
				Inventory inv = chest.getInventory();
				if (new Random().nextInt(130) < (130 - (index * 10))) {
					inv.addItem(item);
				} else {
					inv.addItem(ore.get(0));
				}
			});

			location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.POLISHED_ANDESITE);

			timeLeft--;
			MachineHelper.updateProgressbar(getInventory(), 22, timeLeft, totalTime,
					new ItemStack(Material.FLINT_AND_STEEL));

			if (timeLeft == 0)
				isProcessing = false;

			setStatus(MachineStatus.MINING);
			updateHolo(location);
			updateUI();
		} else {
			if (map != null) {
				this.isProcessing = true;
				map.keySet().forEach(k -> {
					this.timeLeft = map.get(k) / 20;
					this.totalTime = map.get(k) / 20;
					getInventory().removeItem(new ItemStack(k));
				});
			} else {
				setStatus(MachineStatus.STOP_WORKING);
				updateHolo(location);
				updateUI();
			}
		}
		return true;
	}

	@Override
	public boolean demolish(Location location) {
		setType(location.getBlock(), Material.AIR);
		setType(location.getBlock().getRelative(BlockFace.UP), Material.AIR);
		interrupt(location);
		return true;
	}

	public HashMap<Material, Integer> getFuel() {
		HashMap<Material, Integer> map = new HashMap<>();
		Material[] fuels = { Material.COAL, Material.CHARCOAL, Material.LAVA_BUCKET, Material.COAL_BLOCK };
		int[] fuelsTime = { 200, 200, 600, 400 };
		for (int so = 0; so < fuels.length; so++) {
			if (getInventory().containsAtLeast(new ItemStack(fuels[so]), 1)) {
				map.put(fuels[so], fuelsTime[so]);
				return map;
			}
		}
		return null;
	}

	public List<ItemStack> getOresByLevel(int level) {
		List<ItemStack> list = new ArrayList<>();
		list.add(new ItemStack(Material.COBBLESTONE));
		list.add(new ItemStack(Material.COAL));
		list.add(new ItemStack(Material.IRON_NUGGET));
		list.add(new ItemStack(Material.GOLD_NUGGET));
		switch (level) {
		case 1:
			break;
		case 2:
			list.add(new ItemStack(Material.IRON_INGOT));
			list.add(new ItemStack(Material.GOLD_INGOT));
			break;
		case 3:
			list.add(new ItemStack(Material.IRON_INGOT));
			list.add(new ItemStack(Material.GOLD_INGOT));
			list.add(new ItemStack(Material.LAPIS_LAZULI));
			break;
		case 4:
			list.add(new ItemStack(Material.IRON_INGOT));
			list.add(new ItemStack(Material.GOLD_INGOT));
			list.add(new ItemStack(Material.LAPIS_LAZULI));
			list.add(new ItemStack(Material.REDSTONE));
			break;
		case 5:
			list.add(new ItemStack(Material.IRON_INGOT));
			list.add(new ItemStack(Material.GOLD_INGOT));
			list.add(new ItemStack(Material.LAPIS_LAZULI));
			list.add(new ItemStack(Material.REDSTONE));
			list.add(new ItemStack(Material.QUARTZ));
			break;
		case 6:
			list.add(new ItemStack(Material.IRON_INGOT));
			list.add(new ItemStack(Material.GOLD_INGOT));
			list.add(new ItemStack(Material.LAPIS_LAZULI));
			list.add(new ItemStack(Material.REDSTONE));
			list.add(new ItemStack(Material.QUARTZ));
			list.add(new ItemStack(Material.EMERALD));
			break;
		default:
			list.add(new ItemStack(Material.IRON_INGOT));
			list.add(new ItemStack(Material.GOLD_INGOT));
			list.add(new ItemStack(Material.LAPIS_LAZULI));
			list.add(new ItemStack(Material.REDSTONE));
			list.add(new ItemStack(Material.QUARTZ));
			list.add(new ItemStack(Material.EMERALD));
			list.add(new ItemStack(Material.DIAMOND));
			break;
		}

		return list;
	}

}
