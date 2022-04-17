package me.carbon.bedwarsextras.itemsteal

import me.carbon.bedwarsextras.utils.sendChatMessage
import me.carbon.bedwarsextras.utils.sendPrefixMessage
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class CommandItemSteal : CommandBase() {
    override fun getCommandName(): String {
        return "itemsteal"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/itemsteal"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>) {
        if (args.isEmpty()) return
        when (args[0]) {
            "steal" -> {
                val stealItem = ItemSteal.StealableItem.fromString(args[1])
                stealItem?.run { sendPrefixMessage("&aAttempting to steal &e${stealItem}") }
                    ?: return sendPrefixMessage("&cInvalid item.")
                ItemSteal.stealItem(stealItem)
            }
            "togglesafe" -> {
                ItemSteal.safe = !ItemSteal.safe
                sendPrefixMessage("${if(ItemSteal.safe) "&aEnabled" else "&cDisabled"} safe mode.")
                if(!ItemSteal.safe) sendChatMessage("&c&lWARNING: This feature is use at your own risk!")
            }
            "help" -> {
                sendPrefixMessage("&aAll item arguments:")
                sendChatMessage("&c${ItemSteal.StealableItem.values().joinToString(separator = ",\n\u00a7c")}")
            }
            "stop" -> {
                sendPrefixMessage("&cStopped item steal.")
                ItemSteal.toggled = false
            }
        }

    }
}