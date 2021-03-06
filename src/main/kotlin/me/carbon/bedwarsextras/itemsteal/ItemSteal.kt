package me.carbon.bedwarsextras.itemsteal

import gg.essential.api.utils.get
import me.carbon.bedwarsextras.BedwarsExtras
import me.carbon.bedwarsextras.utils.getDistance
import me.carbon.bedwarsextras.utils.sendPrefixMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.play.client.C02PacketUseEntity

object ItemSteal {

    private var itemsToSteal: ArrayList<StealableItem> = ArrayList()

    private val mappedNames = HashMap<String, String>()

    var safe = true

    init {
        mappedNames["Blocks"] = "Block"
        mappedNames["Melee"] = "Gold Sword"
        mappedNames["Tools"] = "Iron Pickaxe"
        mappedNames["Ranged"] = "Bow"
        mappedNames["Potions"] = "Brewing Stand"
        mappedNames["Utility"] = "TNT"
    }

    var toggled = false

    enum class StealableItem(private val position: Int) {
        BLOCK(0),
        GOLD_SWORD(1),
        PICKAXE(2),
        BOW(3),
        BREWING_STAND(4),
        TNT(5);

        fun getPosition(): Int {
            return position
        }

        companion object {
            fun fromString(string: String): StealableItem? {
                return values().find { it.name.equals(string, ignoreCase = true) }
            }
        }
    }

    fun stealItem(amount: Int = 1) {
        val item = getCurrentItem()?:return
        val npc = Minecraft.getMinecraft().theWorld.loadedEntityList
            .find {
                it.displayName.formattedText.contains("ITEM SHOP") && (getDistance(
                    Minecraft.getMinecraft().thePlayer.position,
                    it.position
                ) < 4 || !safe)
            } ?: return sendPrefixMessage("&cNot in a position to steal item.")
        sendPrefixMessage(
            if(getCurrentItem() == null) "&aAttempting to steal &e$item"
            else "&aAdded &e$item&a to queue."
        )
        for(i in 0..amount) itemsToSteal.add(item)
        toggled = true
        val interactPacket = C02PacketUseEntity(npc, C02PacketUseEntity.Action.INTERACT)
        Minecraft.getMinecraft().netHandler.addToSendQueue(interactPacket)
    }

    fun finish() {
        toggled = false
        sendPrefixMessage("&aSuccessfully stole ${getCurrentItem()?.name}!")
        itemsToSteal.removeAt(0)
        stealItem()
    }

    fun getMappedName(displayName: String): String? {
        return ("${BedwarsExtras.PREFIX} \u00a7f" + (mappedNames[displayName.replace(Regex("\u00a7[0-9a-f]"), "")]
            ?: return null))
    }

    fun formatNbt(nbt: NBTTagCompound): NBTTagCompound {
        val oldDisplay = nbt.getCompoundTag("display")
        val mappedName = getMappedName(oldDisplay.getString("Name")) ?: return nbt
        val newDisplay = NBTTagCompound()
        val lore = NBTTagList()
        lore.appendTag(NBTTagString("\u00a7dMade by \u00a7cdeotime\u00a7d and \u00a7bs_a_d\u00a7d."))
        newDisplay.setString("Name", mappedName)
        newDisplay.setTag("Lore", lore)
        nbt.setTag("display", newDisplay)
        return nbt
    }

    fun isBwGui(item: ItemStack): Boolean {
        val gui = GuiEvent.lastGui
        if (gui !is GuiChest) return false
        val hotbarManager = gui.lowerChestInventory.name.contains("Hotbar Manager")
        if (!hotbarManager && !checkQuickBuy(gui.lowerChestInventory)) return false
        val itemIndex = gui.inventorySlots.inventory.indexOf(item)
        return if (hotbarManager && (itemIndex in 19..44)) true
        else itemIndex in 1..8
    }

    fun addToQueue(item: StealableItem) = itemsToSteal.add(item)

    fun getCurrentItem(): StealableItem? = if(itemsToSteal.isEmpty()) null else itemsToSteal[0]

    fun checkQuickBuy(inv: IInventory) =
        inv.getStackInSlot(0)?.tagCompound?.getCompoundTag("display")?.getString("Name")?.contains("Quick Buy") ?: false
}