package ru.endlesscode.rpginventory.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.rpginventory.RPGInventory;
import ru.endlesscode.rpginventory.inventory.backpack.BackpackManager;
import ru.endlesscode.rpginventory.inventory.backpack.BackpackType;
import ru.endlesscode.rpginventory.item.CustomItem;
import ru.endlesscode.rpginventory.item.ItemManager;
import ru.endlesscode.rpginventory.nms.VersionHandler;
import ru.endlesscode.rpginventory.pet.PetFood;
import ru.endlesscode.rpginventory.pet.PetManager;
import ru.endlesscode.rpginventory.pet.PetType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by OsipXD on 28.08.2015
 * It is part of the RpgInventory.
 * All rights reserved 2014 - 2016 © «EndlessCode Group»
 */
public class ItemUtils {
    public static final String UNBREAKABLE_TAG = "Unbreakable";
    public static final String HIDE_FLAGS_TAG = "HideFlags";
    public static final String BACKPACK_UID_TAG = "backpack.uid";
    public static final String BACKPACK_TAG = "backpack.id";
    public static final String ITEM_TAG = "rpginv.id";
    public static final String FOOD_TAG = "food.id";
    public static final String PET_TAG = "pet.id";

    private static final List<Material> itemsWithDurability = Arrays.asList(
            Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.WOOD_HOE, Material.WOOD_SWORD,
            Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_HOE, Material.STONE_SWORD,
            Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_HOE, Material.IRON_SWORD,
            Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.GOLD_HOE, Material.GOLD_SWORD,
            Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE, Material.DIAMOND_SWORD,
            Material.BOW, Material.FLINT_AND_STEEL, Material.SHEARS, Material.FISHING_ROD
    );

    public static ItemStack setTag(@NotNull ItemStack item, String tag, String value) {
        item = toBukkitItemStack(item);
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(item));

        if (!nbt.containsKey(tag)) {
            if (UNBREAKABLE_TAG.equals(tag) || HIDE_FLAGS_TAG.equals(tag)) {
                nbt.put(tag, Integer.valueOf(value));
            } else {
                nbt.put(tag, value);
            }
        }
        NbtFactory.setItemTag(item, nbt);

        return item;
    }

    public static String getTag(@NotNull ItemStack item, String tag) {
        return getTag(item, tag, null);
    }

    @SuppressWarnings("WeakerAccess")
    public static String getTag(@NotNull ItemStack item, String tag, String defaultValue) {
        item = toBukkitItemStack(item);
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(item));

        if (!nbt.containsKey(tag)) {
            return defaultValue;
        }

        return nbt.getString(tag);
    }

    public static boolean hasTag(@NotNull ItemStack originalItem, String tag) {
        if (!originalItem.hasItemMeta()) {
            return false;
        }

        ItemStack item = toBukkitItemStack(originalItem.clone());
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
        return nbt.containsKey(tag);
    }

    @NotNull
    public static ItemStack getTexturedItem(@NotNull String texture) {
        String[] textures = texture.split(":");

        if (Material.getMaterial(textures[0]) == null) {
            RPGInventory.getPluginLogger().warning("Material " + textures[0] + " not found");
            return new ItemStack(Material.AIR);
        }

        ItemStack item = new ItemStack(Material.getMaterial(textures[0]));

        if (textures.length == 2) {
            if (VersionHandler.isHigher1_9() && item.getType() == Material.MONSTER_EGG) {
                item = toBukkitItemStack(item);
                NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
                nbt.put("EntityTag", NbtFactory.ofCompound("temp").put("id", textures[1]));
            } else {
                item.setDurability(Byte.parseByte(textures[1]));

                if (isItemHasDurability(item)) {
                    item = setTag(item, UNBREAKABLE_TAG, "1");
                    item = setTag(item, HIDE_FLAGS_TAG, "63");
                }
            }
        }

        return item;
    }

    private static boolean isItemHasDurability(ItemStack item) {
        return itemsWithDurability.contains(item.getType());
    }

    public static NbtCompound itemStackToNBT(@NotNull ItemStack originalItem, String name) {
        NbtCompound nbt = NbtFactory.ofCompound(name);

        nbt.put("material", originalItem.getType().name());
        nbt.put("amount", originalItem.getAmount());
        nbt.put("data", originalItem.getDurability());

        ItemStack item = toBukkitItemStack(originalItem.clone());
        NbtCompound tag = ItemUtils.isEmpty(item) ? null : NbtFactory.asCompound(NbtFactory.fromItemTag(item));
        if (tag != null) {
            nbt.put("tag", tag);
        }

        return nbt;
    }

    public static ItemStack nbtToItemStack(@NotNull NbtCompound nbt) {
        ItemStack item = new ItemStack(Material.valueOf(nbt.getString("material")));

        if (!ItemUtils.isEmpty(item)) {
            item.setAmount(nbt.getInteger("amount"));
            item.setDurability(nbt.getShort("data"));

            if (nbt.containsKey("tag")) {
                item = toBukkitItemStack(item);
                NbtFactory.setItemTag(item, nbt.getCompound("tag"));
            }
        }

        return item;
    }

    public static ItemStack[] syncItems(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            items[i] = ItemUtils.syncItem(items[i]);
        }

        return items;
    }

    @Contract("null -> !null")
    private static ItemStack syncItem(ItemStack item) {
        if (ItemUtils.isEmpty(item)) {
            return new ItemStack(Material.AIR);
        }

        short durability = item.getDurability();
        if (CustomItem.isCustomItem(item)) {
            item = ItemManager.getItem(ItemUtils.getTag(item, ItemUtils.ITEM_TAG));
            item.setDurability(durability);
        } else if (BackpackManager.isBackpack(item)) {
            String bpUID = ItemUtils.getTag(item, ItemUtils.BACKPACK_UID_TAG);
            BackpackType type = BackpackManager.getBackpackType(ItemUtils.getTag(item, ItemUtils.BACKPACK_TAG));

            if (type == null) {
                return new ItemStack(Material.AIR);
            }

            item = type.getItem();
            if (bpUID != null) {
                ItemUtils.setTag(item, ItemUtils.BACKPACK_UID_TAG, bpUID);
            }
            item.setDurability(durability);
        } else if (PetType.isPetItem(item)) {
            PetType petType = PetManager.getPetFromItem(item);
            if (petType == null) {
                return new ItemStack(Material.AIR);
            }

            int cooldown = PetManager.getCooldown(item);
            double health = PetManager.getHealth(item, petType.getHealth());

            item = petType.getSpawnItem();
            item.setDurability(durability);
            PetManager.setCooldown(item, cooldown);
            PetManager.saveHealth(item, health);
        } else if (PetFood.isFoodItem(item)) {
            PetFood food = PetManager.getFoodFromItem(item);
            int amount = item.getAmount();
            if (food == null) {
                return new ItemStack(Material.AIR);
            }

            item = food.getFoodItem();
            item.setAmount(amount);
            item.setDurability(durability);
        }

        return item;
    }

    @Contract("null -> true")
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    private static ItemStack toBukkitItemStack(ItemStack item) {
        return !item.getClass().getName().endsWith("CraftItemStack") ? MinecraftReflection.getBukkitItemStack(item) : item;
    }
}
