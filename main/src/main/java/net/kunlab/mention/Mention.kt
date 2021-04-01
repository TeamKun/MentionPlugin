package net.kunlab.mention

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Mention : JavaPlugin(), Listener {
    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    fun onMessage(e: AsyncChatEvent) {
        val message = e.message()
        if (message is TextComponent) {
            var str = message.content()
            val matched = str.substringBetweenAll('@', ' ')
            if (matched.isEmpty()) {
                server.sendMessage(Component.text("Non Matched!"))
            } else {
//                str.indexAll('@')
//                    .map { Pair(it, str.next(' ', it)) }
//                    .filter { it.second != -1 }
//                    .forEachIndexed { index, pair ->
//
//                    }
                str = str.replace("@", ChatColor.AQUA.toString() + "@")
                str = str.replace(" ", ChatColor.RESET.toString() + " ")
                matched
                    .map { it.replace("@", "") }
                    .mapNotNull { server.getPlayer(it) }
                    .forEach {
                        it.playSound(Sound.sound(Key.key(org.bukkit.Sound.ENTITY_ITEM_PICKUP.key.namespace,org.bukkit.Sound.ENTITY_ITEM_PICKUP.key.key),Sound.Source.MASTER,30.0F,1.0F))
                    }

                e.isCancelled = true
                server.sendMessage(Component.text("<${(e.player.displayName() as TextComponent).content()}> $str"))
            }
        } else {
            println("ERROR")
        }
    }
}

fun String.indexAll(c: Char): MutableList<Int> {
    val indexes = mutableListOf<Int>()
    this.forEachIndexed { index, c_ -> if (c_ == c) indexes.add(index) }
    return indexes
}

fun String.next(c: Char, start: Int): Int {
    this.filterIndexed { index, _ -> index > start }
        .forEachIndexed { index, c_ -> if (c_ == c) return index + start + 1 }
    return -1
}

fun String.substringNext(c: Char, start: Int): String {
    val next = this.next(c, start)
    if (next == -1) return ""
    return this.substring(start, start + next)
}

fun String.substringBetween(startIndex: Int, start: Char, end: Char): String {
    val startIndex = this.indexOf(start, startIndex)
    if (startIndex == -1) return ""
    val endIndex = this.indexOf(end, startIndex)
    if (endIndex == -1) return ""
    return this.substring(startIndex, endIndex)
}

fun String.substringBetweenAll(start: Char, end: Char): MutableList<String> {
    val indexes = this.indexAll(start)
    if (indexes.isEmpty()) return mutableListOf()
    val list = mutableListOf<String>()
    indexes.forEach { list.add(this.substringBetween(it, start, end)) }
    return list
}

fun main() {
    "@bun133 aaa @ ".substringBetweenAll('@', ' ').forEach { println(it) }
    println("@bun133 ".substringBetween(0, '@', ' '))
    println("@bun133 @bun133".indexAll('@'))
    println("@bun133 ".indexAll(' '))
    println("@bun133 @aaa".indexOf('@'))
    println("@bun133 @aaa ".next(' ', "@bun133 @aaa".indexAll('@')[1]))
}