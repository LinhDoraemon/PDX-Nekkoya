package pdx.nekkoya48.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.pdx101.data.LevelCache;

import pdx.core101.inventory.UI;
import pdx.core101.inventory.UIItemStack;
import pdx.core101.item.ItemBuilderUtils;
import pdx.core101.string.StringUtils;
import pdx.nekkoya48.multithread.MachineThread;

public abstract class PRODUCE48 implements Cloneable {

	public static List<PRODUCE48> REGISTERED_MACHINES = new ArrayList<>();

	private int ID;
	private String display;
	private MachineStatus status;
	private int level = 1;
	private int maxLevel;
	private UI inventory;

	private int[] input;
	private int upgrade;
	private int demolish;
	private int machineInfo;

	public void register() {
		REGISTERED_MACHINES.add(this);
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	private String texture;

	private int size;
	private DyeColor color;

	public int getSize() {
		return size;
	}

	public DyeColor getColor() {
		return color;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setColor(DyeColor color) {
		this.color = color;
	}

	private int upgradeCost = 0;

	private List<String> infoParameters = new ArrayList<>();

	public PRODUCE48(Location location, int ID, String display, MachineStatus status, int level, int maxLevel) {
		this.ID = ID;
		this.display = display;
		this.status = status;
		this.level = level;
		this.maxLevel = maxLevel;
	}

	public void start(Location loc) {
		if (MachineThread.MACHINES.get(StringUtils.toString(loc)) == null) {
			MachineThread.MACHINES.put(StringUtils.toString(loc), this);
		}
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public void setStatus(MachineStatus status) {
		this.status = status;
	}

	public int getUpgradeCost() {
		return upgradeCost;
	}

	public void setUpgradeCost(int upgradeCost) {
		this.upgradeCost = upgradeCost;
	}

	public int getID() {
		return ID;
	}

	public String getDisplay() {
		return display;
	}

	public MachineStatus getStatus() {
		return status;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public Inventory getInventory() {
		return inventory.getInventory();
	}

	public int getDemolish() {
		return demolish;
	}

	public int[] getInput() {
		return input;
	}

	public int getUpgrade() {
		return upgrade;
	}

	public int getMachineInfo() {
		return machineInfo;
	}

	public UI buildInventory(Location location) {
		UI ui = new UI(size, getDisplay());
		for (int pane = 0; pane < size; pane++) {
			ui.setItem(pane, new UIItemStack(Material.valueOf(color.toString() + "_STAINED_GLASS_PANE")) {
				@Override
				public void onClick(InventoryClickEvent e) {
					e.setCancelled(true);
				}
			});
		}

		for (int in : input) {
			ui.setItem(in, new UIItemStack(Material.AIR));
		}

		ItemStack upgradeItem = new ItemBuilderUtils(Material.LIME_CONCRETE, "§a§lNâng cấp",
				"§fNhấn để nâng cấp " + getDisplay() + " §ftừ cấp",
				"§fđộ §e§l" + getLevel() + " §flên cấp độ §e§l" + (getLevel() + 1),
				"§bGiá : §6§l" + getUpgradeCost() * (getLevel() + 1));
		ui.setItem(upgrade, new UIItemStack(upgradeItem) {

			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				Player p = (Player) e.getWhoClicked();
				LevelCache cache = new LevelCache(p);
				if (cache.getGold() >= (getUpgradeCost() * (getLevel() + 1))) {
					upgrade();
				} else {
					p.closeInventory();
					p.playSound(p.getEyeLocation(), Sound.ENTITY_BLAZE_DEATH, 1, 1);
					p.sendMessage("§cBạn không có đủ tiền !");
				}
			}
		});

		String[] defaultIn4 = { "§aCấp độ : §e" + getLevel(), "§aCấp độ tối đa : §c" + getMaxLevel(),
				"§aTrạng thái : " + getStatus().getDisplay(), "" };
		String[] infomation = new String[infoParameters.size()];
		infomation = infoParameters.toArray(infomation);
		String[] both = ArrayUtils.addAll(defaultIn4, infomation);

		ItemStack infoItem = new ItemBuilderUtils(Material.OAK_SIGN, "§6§lThông số", both);
		ui.setItem(machineInfo, new UIItemStack(infoItem) {
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
			}
		});

		ItemStack demolishItem = new ItemBuilderUtils(Material.BARRIER, "§c§lXóa bỏ", "§fNhấn để xóa bỏ máy này. Chú ý",
				"§fdọn hết đồ ở rương thành phẩm nếu không", "§fbạn sẽ mất hết.");
		ui.setItem(demolish, new UIItemStack(demolishItem) {
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				demolish(location);
				e.getWhoClicked().getWorld().playSound(e.getWhoClicked().getEyeLocation(), Sound.ENTITY_ENDERMAN_DEATH,
						1, 2);
			}
		});

		this.inventory = ui;
		return ui;
	}

	public void updateUI() {
		String[] defaultIn4 = { "§aCấp độ : §e" + getLevel(), "§aCấp độ tối đa : §c" + getMaxLevel(),
				"§aTrạng thái : " + getStatus().getDisplay(), "" };
		String[] infomation = new String[infoParameters.size()];
		infomation = infoParameters.toArray(infomation);
		String[] both = ArrayUtils.addAll(defaultIn4, infomation);

		ItemStack infoItem = new ItemBuilderUtils(Material.OAK_SIGN, "§6§lThông số", both);
		inventory.setItem(machineInfo, new UIItemStack(infoItem) {
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
			}
		});
	}

	public ItemStack getItem() {
		return new ItemBuilderUtils(Material.PLAYER_HEAD, getDisplay(), "").setTexture(getTexture());
	}

	public void setType(final Block b, final Material m) {
		new BukkitRunnable() {
			public void run() {
				b.setType(m);
			}
		}.runTask(Bukkit.getPluginManager().getPlugin("PDXNekkoya"));
	}

	public void updateHolo(Location location) {
		Hologram.runSync(() -> {
			Hologram.getArmorStand(location.getBlock(), true);
			Hologram.update(location.getBlock(), "§fTrạng thái : " + getStatus().getDisplay());
		});
	}

	public void create(Location location, boolean start) {
		buildInventory(location);
		updateHolo(location);
		if (start)
			start(location);
	}

	public List<String> getInfoParameters() {
		return infoParameters;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public void setInventory(UI inventory) {
		this.inventory = inventory;
	}

	public void setInput(int... input) {
		this.input = input;
	}

	public void setUpgrade(int upgrade) {
		this.upgrade = upgrade;
	}

	public void setDemolish(int demolish) {
		this.demolish = demolish;
	}

	public void setMachineInfo(int machineInfo) {
		this.machineInfo = machineInfo;
	}

	public void setInfoParameters(List<String> infoParameters) {
		this.infoParameters = infoParameters;
	}

	public void interrupt(Location loc) {
		if (MachineThread.MACHINES.get(StringUtils.toString(loc)) != null)
			MachineThread.MACHINES.remove(StringUtils.toString(loc), this);
		Hologram.remove(loc.getBlock());
	}

	public abstract void upgrade();

	public abstract boolean work(Location location);

	public abstract boolean demolish(Location location);

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
