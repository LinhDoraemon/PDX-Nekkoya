package pdx.nekkoya48.event;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import pdx.core101.skull.SkullBlock;
import pdx.core101.string.StringUtils;
import pdx.nekkoya48.api.PRODUCE48;
import pdx.nekkoya48.multithread.MachineThread;

public class PDXEvent implements Listener {

	@EventHandler
	public void PDX_MACHINE_PLACE_EVENT(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();

		if (b == null) {
			return;
		}

		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}

		if (b.getType() != Material.PLAYER_HEAD)
			return;

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			PRODUCE48 machine = MachineThread.MACHINES.get(StringUtils.toString(b.getLocation()));
			if (machine != null && machine.getInventory() != null) {
				p.playSound(p.getEyeLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 3);
				p.openInventory(machine.getInventory());
				return;
			} else {
				for (PRODUCE48 m : PRODUCE48.REGISTERED_MACHINES) {
					try {
						if (SkullBlock.getTexture(b) != null
								&& SkullBlock.getTexture(b).equalsIgnoreCase(m.getTexture())) {
							PRODUCE48 clone = (PRODUCE48) m.clone();
							clone.create(b.getLocation(), true);
							p.playSound(p.getEyeLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 3);
							p.sendMessage("§aThiết lập máy thành công ! Nhấn lần nữa để mở giao diện !");
							if(b.getRelative(BlockFace.UP).getType() != Material.CHEST) {
								b.getRelative(BlockFace.UP).setType(Material.CHEST);
							}
							break;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

}
