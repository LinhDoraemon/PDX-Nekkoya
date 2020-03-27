package pdx.nekkoya48.fishing;

import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import pdx.nekkoya48.api.Hologram;
import pdx.nekkoya48.api.MachineStatus;
import pdx.nekkoya48.api.PRODUCE48;

public class Fisher extends PRODUCE48 {

	public static Material[] FISHES = { Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH };
	public static Material[] TREASURES = { Material.ENCHANTED_BOOK, Material.NAME_TAG, Material.NAUTILUS_SHELL,
			Material.SADDLE, Material.LILY_PAD };
	public static Material[] JUNKS = { Material.BOWL, Material.LEATHER, Material.ROTTEN_FLESH, Material.STICK,
			Material.STRING, Material.POTION, Material.BONE, Material.INK_SAC, Material.TRIPWIRE_HOOK };

	private int luckyChance = 5;
	private int junkChance = 20;

	private int rodSlot = 13;

	public Fisher(Location location) {
		super(location, 3, "§b§lMáy câu cá", MachineStatus.WAITING, 1, 5);

		this.setUpgradeCost(0);

		setSize(45);
		setColor(DyeColor.LIGHT_BLUE);
		setDemolish(33);
		setMachineInfo(31);
		setUpgrade(29);
		setInput(13);

		setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly" + "90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FjM"
				+ "mIxMTkzYzEyNDU5ZGEzMjc3N2E2NmU3N2MzM2ViYTgzODh" + "kOWNkNTE2ZDgzZjM2NTFiOWY3YmFmMCJ9fX0=");

	}

	@Override
	public void upgrade() {
		
	}

	@Override
	public boolean work(Location location) {
		int level = getLevel();

		if (getInventory().getItem(rodSlot) != null
				&& getInventory().getItem(rodSlot).getType() == Material.FISHING_ROD) {
			ItemStack rod = getInventory().getItem(rodSlot);

			if (level > 1) {
				this.luckyChance = luckyChance + level;
				this.junkChance = junkChance - level;
			}

			if (rod.hasItemMeta() && rod.getItemMeta().hasEnchants()) {
				if (rod.getItemMeta().hasEnchant(Enchantment.LURE)) {
					this.luckyChance = luckyChance + (rod.getEnchantmentLevel(Enchantment.LURE) * 2);
				}
			}

			int random = new Random().nextInt(100);
			Hologram.runSync(() -> {
				Chest chest = (Chest) location.getBlock().getRelative(BlockFace.UP).getState();
				Inventory inv = chest.getInventory();

				if (random < luckyChance) {
					Material mat = TREASURES[new Random().nextInt(TREASURES.length)];
					if (mat == Material.ENCHANTED_BOOK) {
						ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
						Enchantment enchant = Enchantment.values()[new Random().nextInt(Enchantment.values().length)];
						int enLevel = new Random().nextInt(enchant.getMaxLevel());
						if (enLevel < 1) {
							enLevel = 1;
						}
						EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
						meta.addStoredEnchant(enchant, enLevel, true);
						item.setItemMeta(meta);

						inv.addItem(item);
					} else {
						inv.addItem(new ItemStack(mat));
					}
				} else if (random < junkChance) {
					inv.addItem(new ItemStack(JUNKS[new Random().nextInt(JUNKS.length)]));
				} else {
					inv.addItem(new ItemStack(FISHES[new Random().nextInt(FISHES.length)]));
				}
			});

			location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.LIGHT_BLUE_CONCRETE_POWDER);

			if (rod.getDurability() == rod.getType().getMaxDurability()) {
				getInventory().setItem(rodSlot, null);
			} else {
				ItemMeta mt = rod.getItemMeta();
				((Damageable) mt).setDamage(rod.getDurability() + 1);
				rod.setItemMeta(mt);
			}
			setStatus(MachineStatus.FISHING);
			updateHolo(location);
			updateUI();
		} else {
			setStatus(MachineStatus.STOP_WORKING);
			updateHolo(location);
			updateUI();
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

}
