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
        if (args.isEmpty()) return sendHelpMessage()
        when (args[0]) {
            "steal" -> {
                if (args.size < 2) return sendPrefixMessage("&cInvalid item.")
                if (ItemSteal.toggled) return sendPrefixMessage("&cAnother item is already being stolen!")
                val stealItem = ItemSteal.StealableItem.fromString(args[1])
                stealItem?.run { sendPrefixMessage("&aAttempting to steal &e${stealItem}") }
                    ?: return sendPrefixMessage("&cInvalid item.")
                ItemSteal.stealItem(stealItem)
            }
            "togglesafe" -> {
                ItemSteal.safe = !ItemSteal.safe
                sendPrefixMessage("${if (ItemSteal.safe) "&aEnabled" else "&cDisabled"} safe mode.")
                if (!ItemSteal.safe) sendChatMessage("&c&lWARNING: This feature is use at your own risk!")
            }
            "items" -> {
                sendPrefixMessage("&aAll item arguments:")
                sendChatMessage("&c${ItemSteal.StealableItem.values().joinToString(separator = ",\n\u00a7c")}")
            }
            "stop" -> {
                sendPrefixMessage("&cStopped item steal.")
                ItemSteal.toggled = false
            }
            else -> {
                sendHelpMessage()
            }
        }
    }

    private fun sendHelpMessage() {
        sendPrefixMessage("&aAll ItemSteal commands:")
        sendChatMessage("&esteal <item>&f: Steals an item")
        sendChatMessage("&etogglesafe&f: Toggles safe mode &c(Dangerous)")
        sendChatMessage("&eitems&f: Lists all available items")
        sendChatMessage("&estop&f: Stops attempting to steal an item")
    }
}