package com.jaoafa.GiveCommandGenerator.Command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.GiveCommandGenerator.GiveCommandGenerator;
import com.jaoafa.GiveCommandGenerator.Lib.MySQL;
import com.jaoafa.GiveCommandGenerator.Lib.Pastebin;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class Cmd_MakeCmd implements CommandExecutor {
	JavaPlugin plugin;
	public Cmd_MakeCmd(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			GiveCommandGenerator.SendMessage(sender, cmd, "このコマンドはプレイヤーからのみ実行できます。");
			return true;
		}
		Player player = (Player) sender;
		PlayerInventory inv = player.getInventory();
		ItemStack main = inv.getItemInMainHand();

		if(main == null){
			GiveCommandGenerator.SendMessage(sender, cmd, "手にアイテムを持ってください。");
			return true;
		}else if(main.getType() == Material.AIR){
			GiveCommandGenerator.SendMessage(sender, cmd, "手にアイテムを持ってください。");
			return true;
		}

		if(args.length == 1 && args[0].equalsIgnoreCase("debug")){
			YamlConfiguration yaml = new YamlConfiguration();

			yaml.set("data", main);

			String code = yaml.saveToString();
			String name = "GiveCommandGenerator Debug Mode : " + main.getType().name();
			String type = "1"; // Unlisted
			String expire = "1W"; // 1週間
			String format = "yaml"; // Yaml
			try{
				Pastebin pastebin = new Pastebin(code, name, type, expire, format);
				String url = pastebin.Send();

				GiveCommandGenerator.SendMessage(sender, cmd, "デバックデータの発行に成功しました。" + url);
				return true;
			}catch(Pastebin.BadRequestException e){
				GiveCommandGenerator.SendMessage(sender, cmd, "デバックデータの発行に失敗しました。(" + e.getMessage() + ")");
				return true;
			}catch(NullPointerException e){
				GiveCommandGenerator.SendMessage(sender, cmd, "デバックデータの発行に失敗しました。");
				return true;
			}
		}
		StringBuilder builder = new StringBuilder();
		builder.append("/give"); // "/give"
		builder.append(" "); // "/give "
		builder.append("@p"); // "/give <プレイヤー>"
		builder.append(" "); // "/give <プレイヤー> "
		String itemName = main.getType().name().toLowerCase();

		itemName = replaceMaterialToGiveName(itemName);

		builder.append(itemName); // "/give @p <アイテム>"
		builder.append(" "); // "/give @p <アイテム> "
		builder.append(main.getAmount()); // "/give @p <アイテム> [個数]"
		builder.append(" "); // "/give @p <アイテム> [個数] "
		builder.append(main.getDurability()); // "/give @p <アイテム> [個数] [データ]"
		net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(main);
		NBTTagCompound nbttag = nmsItem.getTag();
		if(nbttag != null){
			builder.append(" "); // "/give @p <アイテム> [個数] [データ] "
			builder.append(nbttag.toString()); // "/give @p <アイテム> [個数] [データ] [データタグ]"
		}

		String code = builder.toString();
		String name = "GiveCommandGenerator : " + main.getType().name();
		try {
			PreparedStatement statement = MySQL.getNewPreparedStatement("INSERT INTO cmd (player, uuid, title, command) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, player.getName());
			statement.setString(2, player.getUniqueId().toString());
			statement.setString(3, name);
			statement.setString(4, code);

			statement.executeUpdate();
			ResultSet res = statement.getGeneratedKeys();
			if(res == null || !res.next()){
				throw new IllegalStateException();
			}
			int id = res.getInt(1);
			GiveCommandGenerator.SendMessage(sender, cmd, "コマンドの発行に成功しました。" + "https://jaoafa.com/cmd/" + id);
			return true;
		}catch(SQLException | ClassNotFoundException e){
			GiveCommandGenerator.SendMessage(sender, cmd, "コマンドの発行に失敗しました。(" + e.getMessage() + ")");
			return true;
		}

	}
	/**
	 * Materialとgive時のアイテム名が違う場合に置き換える
	 * @param MaterialName　マテリアル名(Material.name())
	 * @return 必要に応じて置き換えたGive時のアイテム名
	 */
	private String replaceMaterialToGiveName(String MaterialName){
		if(MaterialName.equalsIgnoreCase("note_block")) MaterialName = "noteblock";
		if(MaterialName.equalsIgnoreCase("dead_bush")) MaterialName = "deadbush";
		if(MaterialName.equalsIgnoreCase("red_rose")) MaterialName = "red_flower";
		if(MaterialName.equalsIgnoreCase("workbench")) MaterialName = "crafting_table";
		if(MaterialName.equalsIgnoreCase("glowing_redstone_ore")) MaterialName = "lit_redstone_ore";
		if(MaterialName.equalsIgnoreCase("snow_block")) MaterialName = "snow";
		if(MaterialName.equalsIgnoreCase("iron_fence")) MaterialName = "iron_bars";
		if(MaterialName.equalsIgnoreCase("thin_glass")) MaterialName = "glass_pane";
		if(MaterialName.equalsIgnoreCase("mycel")) MaterialName = "mycelium";
		if(MaterialName.equalsIgnoreCase("water_lily")) MaterialName = "waterlily";
		if(MaterialName.equalsIgnoreCase("nether_fence")) MaterialName = "nether_brick_fence";
		if(MaterialName.equalsIgnoreCase("enchantment_table")) MaterialName = "enchanting_table";
		if(MaterialName.equalsIgnoreCase("ender_portal")) MaterialName = "end_portal";
		if(MaterialName.equalsIgnoreCase("ender_portal_frame")) MaterialName = "end_portal_frame";
		if(MaterialName.equalsIgnoreCase("ender_stone")) MaterialName = "end_stone";
		if(MaterialName.equalsIgnoreCase("redstone_lamp_off")) MaterialName = "redstone_lamp";
		if(MaterialName.equalsIgnoreCase("redstone_lamp_on")) MaterialName = "lit_redstone_lamp";
		if(MaterialName.equalsIgnoreCase("cobble_wall")) MaterialName = "cobblestone_wall";
		if(MaterialName.equalsIgnoreCase("gold_plate")) MaterialName = "light_weighted_pressure_plate";
		if(MaterialName.equalsIgnoreCase("iron_plate")) MaterialName = "heavy_weighted_pressure_plate";
		if(MaterialName.equalsIgnoreCase("stained_clay")) MaterialName = "stained_hardened_clay";
		if(MaterialName.equalsIgnoreCase("slime_block")) MaterialName = "slime";
		if(MaterialName.equalsIgnoreCase("hard_clay")) MaterialName = "hardened_clay";
		if(MaterialName.equalsIgnoreCase("silver_glazed_terracotta")) MaterialName = "light_gray_glazed_terracotta";
		if(MaterialName.equalsIgnoreCase("sulphur")) MaterialName = "gunpowder";
		if(MaterialName.equalsIgnoreCase("seeds")) MaterialName = "wheat_seeds";
		if(MaterialName.equalsIgnoreCase("pork")) MaterialName = "porkchop";
		if(MaterialName.equalsIgnoreCase("grilled_pork")) MaterialName = "cooked_porkchop";
		if(MaterialName.equalsIgnoreCase("clay_brick")) MaterialName = "brick";
		if(MaterialName.equalsIgnoreCase("sugar_cane")) MaterialName = "reeds";
		if(MaterialName.equalsIgnoreCase("watch")) MaterialName = "clock";
		if(MaterialName.equalsIgnoreCase("raw_fish")) MaterialName = "fish";
		if(MaterialName.equalsIgnoreCase("diode")) MaterialName = "repeater";
		if(MaterialName.equalsIgnoreCase("raw_beef")) MaterialName = "beef";
		if(MaterialName.equalsIgnoreCase("raw_chicken")) MaterialName = "chicken";
		if(MaterialName.equalsIgnoreCase("nether_stalk")) MaterialName = "nether_wart";
		if(MaterialName.equalsIgnoreCase("brewing_stand_item")) MaterialName = "brewing_stand";
		if(MaterialName.equalsIgnoreCase("cauldron_item")) MaterialName = "cauldron";
		if(MaterialName.equalsIgnoreCase("eye_of_ender")) MaterialName = "ender_eye";
		if(MaterialName.equalsIgnoreCase("flower_pot_item")) MaterialName = "flower_pot";
		if(MaterialName.equalsIgnoreCase("carrot_item")) MaterialName = "carrot";
		if(MaterialName.equalsIgnoreCase("potato_item")) MaterialName = "potato";
		if(MaterialName.equalsIgnoreCase("empty_map")) MaterialName = "map";
		if(MaterialName.equalsIgnoreCase("skull_item")) MaterialName = "skull";
		if(MaterialName.equalsIgnoreCase("firework")) MaterialName = "fireworks";
		if(MaterialName.equalsIgnoreCase("redstone_comparator")) MaterialName = "comparator";
		if(MaterialName.equalsIgnoreCase("nether_brick_item")) MaterialName = "netherbrick";
		if(MaterialName.equalsIgnoreCase("leash")) MaterialName = "lead";
		if(MaterialName.equalsIgnoreCase("spruce_door_item")) MaterialName = "spruce_door";
		if(MaterialName.equalsIgnoreCase("birch_door_item")) MaterialName = "birch_door";
		if(MaterialName.equalsIgnoreCase("jungle_door_item")) MaterialName = "jungle_door";
		if(MaterialName.equalsIgnoreCase("acacia_door_item")) MaterialName = "acacia_door";
		if(MaterialName.equalsIgnoreCase("dark_oak_door_item")) MaterialName = "dark_oak_door";
		if(MaterialName.equalsIgnoreCase("chorus_fruit_popped")) MaterialName = "popped_chorus_fruit";
		if(MaterialName.equalsIgnoreCase("dragons_breath")) MaterialName = "dragon_breath";
		if(MaterialName.equalsIgnoreCase("mushroom_soup")) MaterialName = "mushroom_stew";
		if(MaterialName.equalsIgnoreCase("wood_door")) MaterialName = "wooden_door";
		if(MaterialName.equalsIgnoreCase("snow_ball")) MaterialName = "snowball";
		if(MaterialName.equalsIgnoreCase("storage_minecart")) MaterialName = "chest_minecart";
		if(MaterialName.equalsIgnoreCase("powered_minecart")) MaterialName = "furnace_minecart";
		if(MaterialName.equalsIgnoreCase("exp_bottle")) MaterialName = "experience_bottle";
		if(MaterialName.equalsIgnoreCase("fireball")) MaterialName = "fire_charge";
		if(MaterialName.equalsIgnoreCase("book_and_quill")) MaterialName = "writable_book";
		if(MaterialName.equalsIgnoreCase("explosive_minecart")) MaterialName = "tnt_minecart";
		if(MaterialName.equalsIgnoreCase("iron_barding")) MaterialName = "iron_horse_armor";
		if(MaterialName.equalsIgnoreCase("gold_barding")) MaterialName = "golden_horse_armor";
		if(MaterialName.equalsIgnoreCase("diamond_barding")) MaterialName = "diamond_horse_armor";
		if(MaterialName.equalsIgnoreCase("command_minecart")) MaterialName = "command_block_minecart";
		if(MaterialName.equalsIgnoreCase("boat_spruce")) MaterialName = "spruce_boat";
		if(MaterialName.equalsIgnoreCase("boat_birch")) MaterialName = "birch_boat";
		if(MaterialName.equalsIgnoreCase("boat_jungle")) MaterialName = "jungle_boat";
		if(MaterialName.equalsIgnoreCase("boat_acacia")) MaterialName = "acacia_boat";
		if(MaterialName.equalsIgnoreCase("boat_dark_oak")) MaterialName = "dark_oak_boat";
		if(MaterialName.equalsIgnoreCase("totem")) MaterialName = "totem_of_undying";
		if(MaterialName.equalsIgnoreCase("gold_record")) MaterialName = "record_13";
		if(MaterialName.equalsIgnoreCase("green_record")) MaterialName = "record_cat";
		if(MaterialName.equalsIgnoreCase("record_3")) MaterialName = "record_blocks";
		if(MaterialName.equalsIgnoreCase("record_4")) MaterialName = "record_chirp";
		if(MaterialName.equalsIgnoreCase("record_5")) MaterialName = "record_far";
		if(MaterialName.equalsIgnoreCase("record_6")) MaterialName = "record_mall";
		if(MaterialName.equalsIgnoreCase("record_7")) MaterialName = "record_mellohi";
		if(MaterialName.equalsIgnoreCase("record_8")) MaterialName = "record_stal";
		if(MaterialName.equalsIgnoreCase("record_9")) MaterialName = "record_strad";
		if(MaterialName.equalsIgnoreCase("record_10")) MaterialName = "record_ward";
		if(MaterialName.equalsIgnoreCase("record_12")) MaterialName = "record_wait";
		if(MaterialName.equalsIgnoreCase("sign_post")) MaterialName = "standing_sign";
		if(MaterialName.equalsIgnoreCase("cake_block")) MaterialName = "cake";
		if(MaterialName.equalsIgnoreCase("wood")) MaterialName = "planks";
		if(MaterialName.equalsIgnoreCase("stationary_water")) MaterialName = "water";
		if(MaterialName.equalsIgnoreCase("stationary_lava")) MaterialName = "lava";
		if(MaterialName.equalsIgnoreCase("bed_block")) MaterialName = "bed";
		if(MaterialName.equalsIgnoreCase("powered_rail")) MaterialName = "golden_rail";
		if(MaterialName.equalsIgnoreCase("piston_sticky_base")) MaterialName = "sticky_piston";
		if(MaterialName.equalsIgnoreCase("long_grass")) MaterialName = "tallgrass";
		if(MaterialName.equalsIgnoreCase("piston_base")) MaterialName = "piston";
		if(MaterialName.equalsIgnoreCase("piston_extension")) MaterialName = "piston_head";
		if(MaterialName.equalsIgnoreCase("double_step")) MaterialName = "double_stone_slab";
		if(MaterialName.equalsIgnoreCase("step")) MaterialName = "stone_slab";
		if(MaterialName.equalsIgnoreCase("wood_stairs")) MaterialName = "oak_stairs";
		if(MaterialName.equalsIgnoreCase("crops")) MaterialName = "wheat";
		if(MaterialName.equalsIgnoreCase("soil")) MaterialName = "farmland";
		if(MaterialName.equalsIgnoreCase("burning_furnace")) MaterialName = "lit_furnace";
		if(MaterialName.equalsIgnoreCase("rails")) MaterialName = "rail";
		if(MaterialName.equalsIgnoreCase("cobblestone_stairs")) MaterialName = "stone_stairs";
		if(MaterialName.equalsIgnoreCase("stone_plate")) MaterialName = "stone_pressure_plate";
		if(MaterialName.equalsIgnoreCase("iron_door_block")) MaterialName = "iron_door";
		if(MaterialName.equalsIgnoreCase("wood_plate")) MaterialName = "wooden_pressure_plate";
		if(MaterialName.equalsIgnoreCase("redstone_torch_off")) MaterialName = "unlit_redstone_torch";
		if(MaterialName.equalsIgnoreCase("redstone_torch_on")) MaterialName = "redstone_torch";
		if(MaterialName.equalsIgnoreCase("sugar_cane_block")) MaterialName = "reeds";
		if(MaterialName.equalsIgnoreCase("jack_o_lantern")) MaterialName = "lit_pumpkin";
		if(MaterialName.equalsIgnoreCase("diode_block_off")) MaterialName = "unpowered_repeater";
		if(MaterialName.equalsIgnoreCase("diode_block_on")) MaterialName = "powered_repeater";
		if(MaterialName.equalsIgnoreCase("trap_door")) MaterialName = "trapdoor";
		if(MaterialName.equalsIgnoreCase("monster_eggs")) MaterialName = "monster_egg";
		if(MaterialName.equalsIgnoreCase("smooth_brick")) MaterialName = "stonebrick";
		if(MaterialName.equalsIgnoreCase("huge_mushroom_1")) MaterialName = "brown_mushroom_block";
		if(MaterialName.equalsIgnoreCase("huge_mushroom_2")) MaterialName = "red_mushroom_block";
		if(MaterialName.equalsIgnoreCase("smooth_stairs")) MaterialName = "stone_brick_stairs";
		if(MaterialName.equalsIgnoreCase("nether_warts")) MaterialName = "nether_wart";
		if(MaterialName.equalsIgnoreCase("wood_double_step")) MaterialName = "double_wooden_slab";
		if(MaterialName.equalsIgnoreCase("wood_step")) MaterialName = "wooden_slab";
		if(MaterialName.equalsIgnoreCase("tripwire")) MaterialName = "tripwire_hook";
		if(MaterialName.equalsIgnoreCase("spruce_wood_stairs")) MaterialName = "spruce_stairs";
		if(MaterialName.equalsIgnoreCase("birch_wood_stairs")) MaterialName = "birch_stairs";
		if(MaterialName.equalsIgnoreCase("jungle_wood_stairs")) MaterialName = "jungle_stairs";
		if(MaterialName.equalsIgnoreCase("command")) MaterialName = "command_block";
		if(MaterialName.equalsIgnoreCase("wood_button")) MaterialName = "wooden_button";
		if(MaterialName.equalsIgnoreCase("redstone_comparator_off")) MaterialName = "unpowered_comparator";
		if(MaterialName.equalsIgnoreCase("redstone_comparator_on")) MaterialName = "powered_comparator";
		if(MaterialName.equalsIgnoreCase("leaves_2")) MaterialName = "leaves2";
		if(MaterialName.equalsIgnoreCase("log_2")) MaterialName = "log2";
		if(MaterialName.equalsIgnoreCase("beetroot_block")) MaterialName = "beetroots";
		if(MaterialName.equalsIgnoreCase("command_repeating")) MaterialName = "repeating_command_block";
		if(MaterialName.equalsIgnoreCase("command_chain")) MaterialName = "chain_command_block";
		if(MaterialName.equalsIgnoreCase("ink_sack")) MaterialName = "dye";

		if(MaterialName.equalsIgnoreCase("iron_spade")) MaterialName = "iron_shovel";
		if(MaterialName.equalsIgnoreCase("wood_sword")) MaterialName = "wooden_sword";
		if(MaterialName.equalsIgnoreCase("wood_spade")) MaterialName = "wooden_shovel";
		if(MaterialName.equalsIgnoreCase("wood_pickaxe")) MaterialName = "wooden_pickaxe";
		if(MaterialName.equalsIgnoreCase("wood_axe")) MaterialName = "wooden_axe";
		if(MaterialName.equalsIgnoreCase("stone_spade")) MaterialName = "stone_shovel";
		if(MaterialName.equalsIgnoreCase("diamond_spade")) MaterialName = "diamond_shovel";
		if(MaterialName.equalsIgnoreCase("gold_sword")) MaterialName = "golden_sword";
		if(MaterialName.equalsIgnoreCase("gold_spade")) MaterialName = "golden_shovel";
		if(MaterialName.equalsIgnoreCase("gold_pickaxe")) MaterialName = "golden_pickaxe";
		if(MaterialName.equalsIgnoreCase("gold_axe")) MaterialName = "golden_axe";
		if(MaterialName.equalsIgnoreCase("wood_hoe")) MaterialName = "wooden_hoe";
		if(MaterialName.equalsIgnoreCase("gold_hoe")) MaterialName = "golden_hoe";
		if(MaterialName.equalsIgnoreCase("gold_helmet")) MaterialName = "golden_helmet";
		if(MaterialName.equalsIgnoreCase("gold_chestplate")) MaterialName = "golden_chestplate";
		if(MaterialName.equalsIgnoreCase("gold_leggings")) MaterialName = "golden_leggings";
		if(MaterialName.equalsIgnoreCase("gold_boots")) MaterialName = "golden_boots";
		if(MaterialName.equalsIgnoreCase("carrot_stick")) MaterialName = "carrot_on_a_stick";
		return MaterialName;
	}
}
