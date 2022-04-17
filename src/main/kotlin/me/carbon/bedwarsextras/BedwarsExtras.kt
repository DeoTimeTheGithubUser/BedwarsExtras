package me.carbon.bedwarsextras

import me.carbon.bedwarsextras.itemsteal.CommandItemSteal
import me.carbon.bedwarsextras.itemsteal.GuiEvent
import me.carbon.bedwarsextras.utils.color
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.LogManager

@SideOnly(Side.CLIENT)
@Mod(modid = BedwarsExtras.MOD_ID)
class BedwarsExtras {
    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(GuiEvent())
        ClientCommandHandler.instance.registerCommand(CommandItemSteal())
        initialized = true
    }

    companion object {
        const val MOD_ID = "BedwarsExtras"
        val PREFIX = "&e[BedwarsExtras]".color()
        var initialized = false
    }
}
