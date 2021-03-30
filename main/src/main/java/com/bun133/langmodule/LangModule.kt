package com.bun133.langmodule

import com.github.bun133.flyframe.FlyModulePlugin
import com.github.bun133.flyframe.Module
import com.github.bun133.flyframe.ModuleEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.HashMap


class LangModulePlugin : FlyModulePlugin() {
    val module = LangModule(this)
    override fun onEnable() {
        // Plugin startup logic
    }

    override fun getModule(): Module {
        return module
    }

    override fun onDisable() {
        // Plugin shutdown logic
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
        try {
            println("[LangModule]")
            println(
                LangFile.JA_JP.get(plugin,"addServer.add")
            )
            println(
                LangFile.EN_US.get(plugin,"addServer.add")
            )
        }catch (e:Exception){
            println(e.message)
        }
    }

    fun getLangList(): Array<LangFile> {
        return LangFile.values()
    }
}

enum class LangFile(val friendlyName: String, val path: String) {
    JA_JP("1.15.2_ja_jp","lang/1.15.2/ja_jp.json"),
    EN_US("1.15.2_en_us","lang/1.15.2/en_us.json");

    var json: Map<String, String>? = null

    fun get(plugin:LangModulePlugin,id: String): String? {
        if(json == null) load(plugin)
        return json!![id]
    }

    fun load(plugin: LangModulePlugin){
        val listType: Type = object : TypeToken<HashMap<String?, String?>?>() {}.type
        val gson = Gson()
        json = gson.fromJson(JsonReader(InputStreamReader(plugin.getResource(path)!!,"UTF8")),listType)
    }
}