package com.github.bun133.langmodule

import com.github.bun133.flyframe.FlyCommandProxy
import com.github.bun133.flyframe.FlyModulePlugin
import com.github.bun133.flyframe.Module
import com.github.bun133.flyframe.ModuleEvent
import com.github.bun133.flyframe.flylib.SmartTabCompleter
import com.github.bun133.flyframe.flylib.TabChain
import com.github.bun133.flyframe.flylib.TabObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.HashMap


class LangModulePlugin : FlyModulePlugin() {
    val module = LangModule(this)
    override fun onEnable() {
        // Plugin startup logic
    }

    override fun getCommands(): MutableList<FlyCommandProxy> = mutableListOf(
        FlyCommandProxy(this, "lang", LangCommand(this), LangCommand.gen())
    )

    override fun getModule(): Module {
        return module
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}

class LangCommand(val plugin: LangModulePlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (sender.isOp) {
                return run(sender, command, label, args)
            } else {
                return false
            }
        } else {
            return run(sender, command, label, args)
        }
    }

    fun run(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (args.size) {
            0 -> {
                if (sender is Player) {
                    sender.sendMessage(
                        "${sender.inventory.itemInMainHand.type.name}:${safeNull(
                            plugin.module.getLangList()[0].get(
                                plugin,
                                sender.inventory.itemInMainHand.type
                            )
                        )}"
                    )
                } else {
                    sender.sendMessage("This Command Can be used by player")
                }

                return true
            }

            1 -> {
                when (args[0]) {
                    "list" -> {
                        plugin.module.getLangList().forEach {
                            if (it.json == null) it.load(plugin)
                            sender.sendMessage("${it.name} Size:${it.json!!.size}")
                        }
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
            2 -> {
                when (args[0]) {
                    "get" -> {
                        plugin.module.getLangList().forEach {
                            try {
                                sender.sendMessage("${it.name} : ${it.get(plugin, args[1])}")
                            } catch (e: NullPointerException) {
                                sender.sendMessage("${it.name} : null")
                            }
                        }
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
            else -> return false
        }
    }

    fun safeNull(s: String?): String {
        return s ?: "null"
    }

    companion object {
        fun gen(): SmartTabCompleter {
            return SmartTabCompleter(
                TabChain(
                    TabObject(
                        "list"
                    )
                ),
                TabChain(
                    TabObject(
                        "get"
                    )
                )
            )
        }
    }
}

class LangModule(val plugin: LangModulePlugin) : Module {
    override var authorName: String = "Bun133"
    override var moduleName: String = "LangModule"
    override var version: String = "1.0"
    override fun onEvent(e: ModuleEvent) {
    }

    override fun onModuleDisable() {
    }

    override fun onModuleEnable() {
//        try {
//            println("[LangModule]")
//            println(
//                LangFile.JA_JP.get(plugin,"addServer.add")
//            )
//            println(
//                LangFile.EN_US.get(plugin,"addServer.add")
//            )
//        }catch (e:Exception){
//            println(e.message)
//        }
    }

    fun getLangList(): Array<LangFile> {
        return LangFile.values()
    }
}

enum class LangFile(val friendlyName: String, val path: String) {
    JA_JP("1.15.2_ja_jp", "lang/1.15.2/ja_jp.json"),
    EN_US("1.15.2_en_us", "lang/1.15.2/en_us.json");

    var json: Map<String, String>? = null

    fun get(plugin: LangModulePlugin, id: String): String? {
        if (json == null) load(plugin)
        return json!![id]
    }

    fun load(plugin: LangModulePlugin) {
        val listType: Type = object : TypeToken<HashMap<String?, String?>?>() {}.type
        val gson = Gson()
        json = gson.fromJson(JsonReader(InputStreamReader(plugin.getResource(path)!!, "UTF8")), listType)
    }

    /**
     * 大半はいける
     */
    fun get(plugin: LangModulePlugin, material: Material): String? {
        var id = ""
        if (material.isBlock || material.isAir) {
            id += "block.minecraft.${material.name.toLowerCase()}"
        } else {
            id += "item.minecraft.${material.name.toLowerCase()}"
        }
        return get(plugin, id)
    }

    /**
     * これ怪しい
     */
    fun get(plugin: LangModulePlugin, effect: PotionEffectType): String? {
        return get(plugin, "effect.minecraft." + effect.name.toLowerCase())
    }

    /**
     * これはたぶん行ける
     */
    fun get(plugin: LangModulePlugin, entity: EntityType): String? {
        return get(plugin, "entity.minecraft." + entity.getName())
    }
}