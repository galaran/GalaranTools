package me.galaran.bukkitutils.gtools.nms;

import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemStackWithNBT implements ConfigurationSerializable {

    private final net.minecraft.server.ItemStack nmsStack;

    /**
     * Only wraps bukkit stack, no clonning
     */
    public ItemStackWithNBT(ItemStack bukkitStack) {
        nmsStack = CraftItemStack.createNMSItemStack(bukkitStack);
    }

    public ItemStack getBukkitStack() {
        return new CraftItemStack(nmsStack);
    }

    public ItemStack getBukkitStackCopy() {
        return new CraftItemStack(nmsStack.cloneItemStack());
    }

    /**
     * @return stack name or null if none
     */
    public String getName() {
        NBTTagCompound displayTag = getDisplayTag(false);
        if (displayTag != null && displayTag.hasKey("Name")) {
            return displayTag.getString("Name");
        }
        return null;
    }

    /**
     * @param newName new stack name or null to clear
     */
    public void setName(String newName) {
        if (newName == null) {
            NBTTagCompound displayTag = getDisplayTag(false);
            if (displayTag != null) {
                displayTag.remove("Name");
            }
        } else {
            getDisplayTag(true).setString("Name", newName);
        }
    }

    /**
     * @return stack lore or null if none
     */
    public String[] getLore() {
        NBTTagCompound displayTag = getDisplayTag(false);
        if (displayTag != null && displayTag.hasKey("Lore")) {
            NBTTagList loreList = displayTag.getList("Lore");

            String[] result = new String[loreList.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((NBTTagString) loreList.get(i)).data;
            }
            return result;
        }
        return null;
    }

    /**
     * @param newLore new stack lore or null to clear
     */
    public void setLore(Iterable<String> newLore) {
        if (newLore == null) {
            NBTTagCompound displayTag = getDisplayTag(false);
            if (displayTag != null) {
                displayTag.remove("Lore");
            }
        } else {
            NBTTagList newLoreList = new NBTTagList("Lore");
            for (String loreLine : newLore) {
                newLoreList.add(new NBTTagString("LoreLine", loreLine));
            }
            getDisplayTag(true).set("Lore", newLoreList);
        }
    }

    /**
     * @param loreLine new lore line (not null)
     */
    public void addLore(String loreLine) {
        Validate.notNull(loreLine);

        NBTTagCompound displayTag = getDisplayTag(true);
        NBTTagList loreList;
        if (displayTag.hasKey("Lore")) {
            loreList = displayTag.getList("Lore");
        } else {
            loreList = new NBTTagList("Lore");
            displayTag.set("Lore", loreList);
        }
        loreList.add(new NBTTagString("LoreLine", loreLine));
    }

    private NBTTagCompound getDisplayTag(boolean create) {
        NBTTagCompound tag = nmsStack.tag;
        if (create) {
            if (tag == null) {
                tag = new NBTTagCompound();
                nmsStack.tag = tag;
            }

            if (!tag.hasKey("display")) {
                tag.setCompound("display", new NBTTagCompound());
            }
            return tag.getCompound("display");
        } else {
            if (tag != null && tag.hasKey("display")) {
                return tag.getCompound("display");
            }
            return null;
        }
    }

    /**
     * Get stack's NBT tag as Base64 string or null, if no tag
     */
    public String getTagBase64() {
        if (nmsStack.tag == null) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTBase.a(nmsStack.tag, new DataOutputStream(baos));
        return DatatypeConverter.printBase64Binary(baos.toByteArray());
    }

    /**
     * Set stack's NBT tag to Base64 string. Null will set tag to null
     */
    public void setTagBase64(String tagBase64) {
        if (tagBase64 == null) {
            nmsStack.tag = null;
        } else {
            byte[] nbtData = DatatypeConverter.parseBase64Binary(tagBase64);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(nbtData));
            nmsStack.tag = (NBTTagCompound) NBTBase.b(dis);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("type", Material.getMaterial(nmsStack.id).name());

        if (nmsStack.getData() != 0) {
            result.put("damage", nmsStack.getData());
        }

        if (nmsStack.count != 1) {
            result.put("amount", nmsStack.count);
        }

        String tagBase64 = getTagBase64();
        if (tagBase64 != null) {
            result.put("tag-base64", tagBase64);
        }

        return result;
    }

    public static ItemStackWithNBT deserialize(Map<String, Object> map) {
        Material type = Material.getMaterial((String) map.get("type"));
        short damage = 0;
        int amount = 1;

        if (map.containsKey("damage")) {
            damage = ((Number) map.get("damage")).shortValue();
        }

        if (map.containsKey("amount")) {
            amount = ((Number) map.get("amount")).intValue();
        }

        ItemStackWithNBT result = new ItemStackWithNBT(new ItemStack(type, amount, damage));
        result.setTagBase64((String) map.get("tag-base64"));

        return result;
    }
}
