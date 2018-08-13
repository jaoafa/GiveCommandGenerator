package com.jaoafa.GiveCommandGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.GiveCommandGenerator.Command.Cmd_MakeCmd;
import com.jaoafa.GiveCommandGenerator.Lib.Discord;
import com.jaoafa.GiveCommandGenerator.Lib.PermissionsManager;

public class GiveCommandGenerator extends JavaPlugin {
	public static FileConfiguration conf;
	public static JavaPlugin JavaPlugin;
	public static String pastebin_devkey = null;
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

		if(conf.contains("discordtoken")){
			Discord.start(this, conf.getString("discordtoken"));
		}else{
			getLogger().info("Discordへの接続に失敗しました。 [conf NotFound]");
			getLogger().info("Disable GiveCommandGenerator...");
			getServer().getPluginManager().disablePlugin(this);
		}

		if(conf.contains("pastebin_devkey")){
			pastebin_devkey = conf.getString("pastebin_devkey");
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

	static String bugreport_folder = null;
	/**
	 * バグリポーター<br>
	 * Discordにメッセージ送ったり、管理部・モデレーターに通知したりする。
	 * @param exception
	 */
	public static void BugReporter(Throwable exception){
		// フォルダ作成
		if(bugreport_folder == null){
			String Path = JavaPlugin().getDataFolder() + File.separator + "bugreport" + File.separator;
			File folder = new File(Path);
			if(folder.exists()){
				bugreport_folder = Path;
			}else{
				if(folder.mkdir()){
					JavaPlugin().getLogger().info("BugReportのリポートディレクトリの作成に成功しました。");
					bugreport_folder = Path;
				}else{
					JavaPlugin().getLogger().info("BugReportのリポートディレクトリの作成に失敗しました。");
				}
			}
		}

		// ID作成
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		String id = sdf.format(new Date());

		String filepath = bugreport_folder + id + ".json";
		File file = new File(filepath);

		// バグ記録ファイル作成・記録
		try {
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			exception.printStackTrace(pw);
			fw.write(pw.toString());
			fw.close();
			JavaPlugin().getLogger().info("Bugreport: ファイル書き込みに成功");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			JavaPlugin().getLogger().info("Bugreport: ファイル書き込みに失敗");
			e.printStackTrace();
		}

		// 管理部・モデレーターにメッセージを送信
		for(Player p: Bukkit.getServer().getOnlinePlayers()) {
			String group = PermissionsManager.getPermissionMainGroup(p);
			if(group.equalsIgnoreCase("Admin") || group.equalsIgnoreCase("Moderator")) {
				p.sendMessage("[MyMaid] " + ChatColor.GREEN + "jaoReputationのシステム障害が発生しました。");
				p.sendMessage("[MyMaid] " + ChatColor.GREEN + "エラー: " + exception.getMessage());
			}
		}

		// Discordへ送信
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		boolean res = Discord.send("293856671799967744", "jaoReputationでエラーが発生しました。" + "\n"
				+ "```" + sw.toString() + "```\n"
				+ "Cause: `" + exception.getCause() + "`\n"
				+ "報告ID: `" + id + "`");
		if(res){
			JavaPlugin().getLogger().info("Bugreport: Discord送信に成功");
		}else{
			JavaPlugin().getLogger().info("Bugreport: Discord送信に失敗");
		}

		// スタックトレース出力とか
		JavaPlugin().getLogger().info("Bugreport: エラー発生。報告ID: 「" + id + "」");
		exception.printStackTrace();
	}
	public static JavaPlugin JavaPlugin(){
		if(GiveCommandGenerator.JavaPlugin == null){
			throw new NullPointerException("getJavaPlugin()が呼び出されましたが、JaoReputation.javapluginはnullでした。");
		}
		return GiveCommandGenerator.JavaPlugin;
	}
}
