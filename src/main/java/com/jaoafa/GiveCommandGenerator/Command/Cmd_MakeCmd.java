package com.jaoafa.GiveCommandGenerator.Command;

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
		if(itemName.equalsIgnoreCase("potato_item")) itemName = "potato";
		//if(itemName.equalsIgnoreCase("potato_item")) itemName = "potato";
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
		String type = "1"; // Unlisted
		String expire = "1W"; // 1週間
		String format = "text"; // None

		try{
			Pastebin pastebin = new Pastebin(code, name, type, expire, format);
			String url = pastebin.Send();

			GiveCommandGenerator.SendMessage(sender, cmd, "コマンドの発行に成功しました。" + url);
			return true;
		}catch(Pastebin.BadRequestException e){
			GiveCommandGenerator.SendMessage(sender, cmd, "コマンドの発行に失敗しました。(" + e.getMessage() + ")");
			return true;
		}catch(NullPointerException e){
			GiveCommandGenerator.SendMessage(sender, cmd, "コマンドの発行に失敗しました。");
			return true;
		}

	}
}
