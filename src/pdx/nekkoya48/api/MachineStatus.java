package pdx.nekkoya48.api;

public enum MachineStatus {

	HARVESTING("§2§lĐang thu hoạch ..."), PLANTING("§b§lĐang trồng lại ..."), WAITING("§e§lĐang chờ ..."), STOP_WORKING(
			"§c§lKhông hoạt động ..."), FEEDING("§d§lĐang cho ăn ..."), MINING("§b§lĐang khai thác ..."), FISHING("§6§lĐang câu ...");
	
	private String display;

	MachineStatus(String display){
		this.display = display;
	}
	
	public String getDisplay() {
		return display;
	}

}
