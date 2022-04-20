package me.carbon.bedwarsextras.utils

import me.carbon.bedwarsextras.BedwarsExtras
import net.minecraft.client.Minecraft
import net.minecraft.inventory.Container
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import kotlin.math.abs
import kotlin.math.sqrt

fun sendChatMessage(message: String) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(message.color()))
}

fun sendPrefixMessage(message: String) {
    sendChatMessage("${BedwarsExtras.PREFIX}&r $message")
}

fun String.color(): String {
    return this.replace("&", "\u00a7")
}

fun clickContainer(container: Container, slot: Int) {
    val player = Minecraft.getMinecraft().thePlayer
    val transactionID = container.getNextTransactionID(player.inventory)
    val packet = C0EPacketClickWindow(container.windowId, slot, 0, 0, null, transactionID)
    Minecraft.getMinecraft().netHandler.addToSendQueue(packet)
}

fun getDistance(pos1: BlockPos, pos2: BlockPos): Double {
    val x = abs(pos2.x - pos1.x)
    val y = abs(pos2.y - pos1.y)
    val z = abs(pos2.z - pos1.z)
    return sqrt(((x * x) + (y * y) + (z * z)).toDouble())
}