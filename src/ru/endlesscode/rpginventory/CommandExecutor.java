package ru.endlesscode.rpginventory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import ru.endlesscode.rpginventory.api.InventoryAPI;
import ru.endlesscode.rpginventory.inventory.InventoryManager;
import ru.endlesscode.rpginventory.inventory.ResourcePackManager;
import ru.endlesscode.rpginventory.inventory.backpack.BackpackManager;
import ru.endlesscode.rpginventory.item.ItemManager;
import ru.endlesscode.rpginventory.pet.PetManager;
import ru.endlesscode.rpginventory.utils.StringUtils;

import java.util.List;

/**
 * Created by OsipXD on 28.08.2015
 * It is part of the RpgInventory.
 * All rights reserved 2014 - 2015 © «EndlessCode Group»
 */
@SuppressWarnings("deprecation")
class CommandExecutor {
    public static void givePet(CommandSender sender, String playerName, String petId) {
        if (validatePlayer(sender, playerName)) {
            Player player = RPGInventory.getInstance().getServer().getPlayer(playerName);
            ItemStack petItem = PetManager.getPetItem(petId);

            if (petItem != null) {
                player.getInventory().addItem(petItem);
                return;
            } else {
                sender.sendMessage(StringUtils.coloredLine("&cPet '" + petId + "' not found!"));
            }
        }

        sender.sendMessage(StringUtils.coloredLine("&3Use &6/rpginv pet [&eplayer&6] [&epetId&6]"));
    }

    public static void giveFood(CommandSender sender, String playerName, String foodId, String stringAmount) {
        if (validatePlayer(sender, playerName)) {
            Player player = RPGInventory.getInstance().getServer().getPlayer(playerName);
            ItemStack foodItem = PetManager.getFoodItem(foodId);

            if (foodItem != null) {
                try {
                    int amount = Integer.parseInt(stringAmount);
                    foodItem.setAmount(amount);
                    player.getInventory().addItem(foodItem);
                    return;
                } catch (NumberFormatException e) {
                    sender.sendMessage(StringUtils.coloredLine("&cThe amount must be a number!"));
                }
            } else {
                sender.sendMessage(StringUtils.coloredLine("&cFood '" + foodId + "' not found!"));
            }
        }

        sender.sendMessage(StringUtils.coloredLine("&3Use &6/rpginv food [&eplayer&6] [&efoodId&6] (&eamount&6)"));
    }

    public static void giveItem(CommandSender sender, String playerName, String itemId) {
        if (validatePlayer(sender, playerName)) {
            Player player = RPGInventory.getInstance().getServer().getPlayer(playerName);
            ItemStack petItem = ItemManager.getItem(itemId);

            if (petItem != null) {
                player.getInventory().addItem(petItem);
                return;
            } else {
                sender.sendMessage(StringUtils.coloredLine("&cItem '" + itemId + "' not found!"));
            }
        }

        sender.sendMessage(StringUtils.coloredLine("&3Use &6/rpginv item [&eplayer&6] [&eitemId&6]"));
    }

    public static void giveBackpack(CommandSender sender, String playerName, String id) {
        if (validatePlayer(sender, playerName)) {
            Player player = RPGInventory.getInstance().getServer().getPlayer(playerName);
            ItemStack bpItem = BackpackManager.getItem(id);

            if (bpItem != null) {
                player.getInventory().addItem(bpItem);
                return;
            } else {
                sender.sendMessage(StringUtils.coloredLine("&cBackpack '" + id + "' not found!"));
            }
        }

        sender.sendMessage(StringUtils.coloredLine("&3Use &6/rpginv bp [&eplayer&6] [&eitemId&6]"));
    }

    public static void printHelp(CommandSender sender) {
        sender.sendMessage(StringUtils.coloredLine("&3===================&b[&eRPGInventory&b]&3====================="));
        sender.sendMessage(StringUtils.coloredLine("&8[] &7Required, &8() &7Optional"));

        if (RPGInventory.getPermissions().has(sender, "rpginventory.open.others")) {
            sender.sendMessage(StringUtils.coloredLine("&6rpginv open (&eplayer&6) &7- open inventory"));
        } else if (RPGInventory.getPermissions().has(sender, "rpginventory.open")) {
            sender.sendMessage(StringUtils.coloredLine("&6rpginv open &7- open inventory"));
        }

        if (RPGInventory.getPermissions().has(sender, "rpginventory.textures.others")) {
            sender.sendMessage(StringUtils.coloredLine("&6rpginv textures (&eplayer&6) [&eenable&6|&edisable&6] &7- use textures or not"));
        } else if (RPGInventory.getPermissions().has(sender, "rpginventory.textures")) {
            sender.sendMessage(StringUtils.coloredLine("&6rpginv textures [&eenable&6|&edisable&6] &7- use textures or not"));
        }

        if (RPGInventory.getPermissions().has(sender, "rpginventory.admin")) {
            sender.sendMessage(StringUtils.coloredLine("&6rpginv reload &7- reload config"));
            sender.sendMessage(StringUtils.coloredLine("&6rpginv list [&etype&6] &7- show list of pets, food or items"));
            sender.sendMessage(StringUtils.coloredLine("&6rpginv food [&eplayer&6] [&efoodId&6] (&eamount&6) &7- gives food to player"));
            sender.sendMessage(StringUtils.coloredLine("&6rpginv pet [&eplayer&6] [&epetId&6] &7- gives pet to player"));
            sender.sendMessage(StringUtils.coloredLine("&6rpginv item [&eplayer&6] [&eitemId&6] &7- gives item to player"));
            sender.sendMessage(StringUtils.coloredLine("&6rpginv bp [&eplayer&6] [&ebackpackId&6] &7- gives backpack to player"));
        }

        sender.sendMessage(StringUtils.coloredLine("&3====================================================="));
    }

    public static void printList(CommandSender sender, String type) {
        switch (type) {
            case "pet":
            case "pets":
                List<String> petList = PetManager.getPetList();
                sender.sendMessage(StringUtils.coloredLine(petList.size() == 0 ? "&cPets not found..." : "&3Pets list: &6" + petList));
                break;
            case "food":
                List<String> foodList = PetManager.getFoodList();
                sender.sendMessage(StringUtils.coloredLine(foodList.size() == 0 ? "&cFood not found..." : "&3Food list: &6" + foodList));
                break;
            case "item":
            case "items":
                List<String> itemList = ItemManager.getItemList();
                sender.sendMessage(StringUtils.coloredLine(itemList.size() == 0 ? "&cItems not found..." : "&3Items list: &6" + itemList));
                break;
            case "bp":
            case "backpack":
            case "backpacks":
                List<String> bpList = BackpackManager.getBackpackList();
                sender.sendMessage(StringUtils.coloredLine(bpList.size() == 0 ? "&cBackpacks not found..." : "&3Backpacks list: &6" + bpList));
                break;
            default:
                sender.sendMessage(StringUtils.coloredLine("&3Use &6/rpginv list [&epets&6|&efood&6|&eitems&6|&ebackpacks&6]"));
                break;
        }
    }

    public static void reloadPlugin(CommandSender sender) {
        PluginManager pm = RPGInventory.getInstance().getServer().getPluginManager();
        pm.disablePlugin(RPGInventory.getInstance());
        pm.enablePlugin(RPGInventory.getInstance());
        sender.sendMessage(StringUtils.coloredLine("&e[RPGInventory] Plugin successfully reloaded!"));
    }

    public static void openInventory(CommandSender sender) {
        if (!validatePlayer(sender)) {
            return;
        }

        Player player = ((Player) sender).getPlayer();
        if (InventoryAPI.isRPGInventory(player.getOpenInventory().getTopInventory())) {
            return;
        }

        InventoryManager.get(player).openInventory();
    }

    public static void openInventory(CommandSender sender, String playerName) {
        if (!validatePlayer(sender) || !validatePlayer(sender, playerName)) {
            return;
        }

        Player player = RPGInventory.getInstance().getServer().getPlayer(playerName);
        ((Player) sender).openInventory(InventoryManager.get(player).getInventory());
    }

    public static void updateTextures(CommandSender sender, String status) {
        if (!validatePlayer(sender)) {
            return;
        }

        Player player = (Player) sender;
        updateTextures(sender, player.getName(), status);
    }

    public static void updateTextures(CommandSender sender, String playerName, String status) {
        if (validatePlayer(sender, playerName)) {
            final Player player = RPGInventory.getInstance().getServer().getPlayer(playerName);
            boolean flag = status.startsWith("e");

            if (flag == ResourcePackManager.isLoadedResourcePack(player)) {
                sender.sendMessage(String.format(RPGInventory.getLanguage().getCaption("error.rp.already"), RPGInventory.getLanguage().getCaption("rp." + (flag ? "enabled" : "disabled"))));
                return;
            }

            if (flag) {
                if (ResourcePackManager.getMode() == ResourcePackManager.Mode.DISABLED) {
                    sender.sendMessage(RPGInventory.getLanguage().getCaption("error.rp.disabled"));
                    return;
                }

                ResourcePackManager.wontResourcePack(player, true);
            } else {
                if (ResourcePackManager.getMode() == ResourcePackManager.Mode.FORCE) {
                    sender.sendMessage(RPGInventory.getLanguage().getCaption("error.rp.force"));
                    return;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setResourcePack("https://dl.dropboxusercontent.com/u/105899524/RPGInventory/Empty.zip");
                    }
                }.runTaskLater(RPGInventory.getInstance(), 5);
                ResourcePackManager.wontResourcePack(player, false);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    InventoryManager.unloadPlayerInventory(player);
                    InventoryManager.loadPlayerInventory(player);
                }
            }.runTaskLater(RPGInventory.getInstance(), 5);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean validatePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtils.coloredLine("&cThis command not allowed from console."));
            return false;
        }

        return validatePlayer(sender, (Player) sender);
    }

    private static boolean validatePlayer(CommandSender sender, String playerName) {
        Player player = RPGInventory.getInstance().getServer().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(StringUtils.coloredLine("&cPlayer '" + playerName + "' not found!"));
        }

        return validatePlayer(sender, player);
    }

    private static boolean validatePlayer(CommandSender sender, Player player) {
        if (!InventoryManager.playerIsLoaded(player)) {
            sender.sendMessage(StringUtils.coloredLine("&cThis command not allowed here."));
            return false;
        }

        return true;
    }
}
