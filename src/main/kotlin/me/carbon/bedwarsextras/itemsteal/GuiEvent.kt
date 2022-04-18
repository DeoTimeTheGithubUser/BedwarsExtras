package me.carbon.bedwarsextras.itemsteal

import me.carbon.bedwarsextras.utils.clickContainer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class GuiEvent {

    @SubscribeEvent
    fun guiPost(event: GuiScreenEvent.InitGuiEvent.Post) {
        lastGui = event.gui
        val gui = event.gui
        if (ItemSteal.toggled) {
            if (gui !is GuiChest) return
            val guiName = gui.lowerChestInventory.name
            Minecraft.getMinecraft().currentScreen = null
            Minecraft.getMinecraft().inGameHasFocus = true
            Minecraft.getMinecraft().mouseHelper.grabMouseCursor()
            if (guiName.contains("Quick Buy")) {
                Thread {
                    Thread.sleep(50)
                    clickContainer(gui.inventorySlots, 53)
                }.start()
            }
            if (guiName.contains("Hotbar Manager")) {
                Thread {
                    Thread.sleep(50)
                    clickContainer(gui.inventorySlots, 19 + ItemSteal.getCurrentItem()!!.getPosition())
                    Thread.sleep(50)
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/report TimeDeo")
                }.start()
            }
        }
    }

    companion object {
        var lastGui: GuiScreen? = null
    }
}