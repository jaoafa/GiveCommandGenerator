package com.jaoafa.GiveCommandGenerator;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.GiveCommandGenerator.Command.Cmd_MakeCmd;

public class GiveCommandGenerator extends JavaPlugin {
	public static FileConfiguration conf;
	public static JavaPlugin JavaPlugin;
	public static String pastebin_devkey = null;
	public static List<String> pastebin_devkeyList = null;
	/**
	 * プラグインが起動したときに呼び出し
	 * @author mine_book000
	 * @since 2018/08/14
	 */
	@Override
	public void onEnable() {
		JavaPlugin = this;

		// リスナーを設定
		Import_Listener();
		// コマンドを設定
		Import_Command_Executor();

		Load_Config(); // Config Load
	}

	/**
	 * コマンドの設定
	 * @author mine_book000
	 */
	private void Import_Command_Executor(){
		// 日付は制作完了(登録)の日付
		getCommand("makecmd").setExecutor(new Cmd_MakeCmd(this)); // 2018/08
	}
	/**
	 * リスナー設定
	 * @author mine_book000
	 */
	private void Import_Listener(){
		// 日付は制作完了(登録)の日付
	}

	/**
	 * リスナー設定の簡略化用
	 * @param listener Listener
	 */
	private void registEvent(Listener l) {
		getServer().getPluginManager().registerEvents(l, this);
	}

	/**
	 * プラグインが停止したときに呼び出し
	 * @author mine_book000
	 * @since 2018/08/14
	 */
	@Override
	public void onDisable() {

	}

	/**
	 * コンフィグ読み込み
	 * @author mine_book000
	 */
	private void Load_Config(){
		conf = getConfig();

		if(conf.contains("pastebin_devkey")){
			if(conf.isList("pastebin_devkey")){
				pastebin_devkeyList = conf.getStringList("pastebin_devkey");
				pastebin_devkey = pastebin_devkeyList.get(0);
			}else{
				pastebin_devkey = conf.getString("pastebin_devkey");
			}
		}else{
			getLogger().info("pastebinのdevKeyが取得できません。");
			getLogger().info("Disable GiveCommandGenerator...");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	/**
	 * CommandSenderに対してメッセージを送信します。
	 * @param sender CommandSender
	 * @param cmd Commandデータ
	 * @param message メッセージ
	 */
	public static void SendMessage(CommandSender sender, Command cmd, String message) {
		sender.sendMessage("[" + cmd.getName().toUpperCase() +"] " + ChatColor.GREEN + message);
	}

	public static JavaPlugin JavaPlugin(){
		if(GiveCommandGenerator.JavaPlugin == null){
			throw new NullPointerException("getJavaPlugin()が呼び出されましたが、JaoReputation.javapluginはnullでした。");
		}
		return GiveCommandGenerator.JavaPlugin;
	}
}
