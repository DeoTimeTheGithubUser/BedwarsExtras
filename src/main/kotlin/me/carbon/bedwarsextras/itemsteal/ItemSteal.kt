package me.carbon.bedwarsextras.itemsteal

import me.carbon.bedwarsextras.BedwarsExtras
import me.carbon.bedwarsextras.utils.getDistance
import me.carbon.bedwarsextras.utils.sendPrefixMessage
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.play.client.C02PacketUseEntity

object ItemSteal {

    val bwGuis = ArrayList<ItemStack>()                         // this entire system is really bad and
    val mappedNbt = HashMap<NBTTagCompound, NBTTagCompound>()   // causes a ton of performance problems but
    val mappedNames = HashMap<String, String>()                 // i do not really want to bother fixing it

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
    var stealItem: StealableItem? = null

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

    fun stealItem(item: StealableItem) {
        val npc = Minecraft.getMinecraft().theWorld.loadedEntityList
            .find {
                it.displayName.formattedText.contains("ITEM SHOP") && (getDistance(
                    Minecraft.getMinecraft().thePlayer.position,
                    it.position
                ) < 4 && safe)
            } ?: return sendPrefixMessage("&cNot in a position to steal item.")
        stealItem = item
        toggled = true
        val interactPacket = C02PacketUseEntity(npc, C02PacketUseEntity.Action.INTERACT)
        Minecraft.getMinecraft().netHandler.addToSendQueue(interactPacket)
    }

    fun finish() {
        toggled = false
        sendPrefixMessage("&aSuccessfully stole ${stealItem?.name}!")
        stealItem = null
    }

    fun getMappedName(item: ItemStack): String? {
        return getMappedName(item.displayName)
    }

    fun getMappedName(displayName: String): String? {
        return ("${BedwarsExtras.PREFIX} \u00a7f" + (mappedNames[displayName.replace(Regex("\u00a7[0-9a-f]"), "")]
            ?: return null))
    }

    fun findBestSlot(): Int {
        val slot = Minecraft.getMinecraft().thePlayer.inventoryContainer.inventorySlots
            .filter { !it.hasStack }
            .sortedBy { it.slotIndex }[0]
        return slot.slotIndex
    }

    fun formatNbt(nbt: NBTTagCompound): NBTTagCompound {
        val cloned = nbt.copy() as NBTTagCompound
        val oldDisplay = nbt.getCompoundTag("display")
        val mappedName = getMappedName(oldDisplay.getString("Name")) ?: return nbt
        val newDisplay = NBTTagCompound()
        val lore = NBTTagList()
        lore.appendTag(NBTTagString("\u00a7dMade by deotime and s_a_d"))
        newDisplay.setString("Name", mappedName)
        newDisplay.setTag("Lore", lore)
        nbt.setTag("display", newDisplay)
        mappedNbt[nbt] = cloned
        return nbt
    }

    fun setBwGui(item: ItemStack) {
        bwGuis.add(item)
        item.tagCompound = mappedNbt[item.tagCompound] ?: return
    }

    fun isBwGui(item: ItemStack): Boolean {
        return bwGuis.contains(item)
    }
}