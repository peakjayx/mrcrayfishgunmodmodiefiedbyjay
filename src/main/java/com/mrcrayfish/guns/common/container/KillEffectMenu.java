package com.mrcrayfish.guns.common.container;

import com.mrcrayfish.guns.common.KillEffectListener;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Set;
import java.util.UUID;

public class KillEffectMenu
{
    public static void open(ServerPlayerEntity player)
    {
        player.openMenu(new INamedContainerProvider()
        {
            @Override
            public ITextComponent getDisplayName()
            {
                return new StringTextComponent("§8Kill Effects");
            }

            @Override
            public Container createMenu(int id, PlayerInventory inv, PlayerEntity p)
            {
                UUID uuid = p.getUUID();
                Inventory gui = new Inventory(9);

                ItemStack ace = make(Items.WHITE_WOOL,   "§f§lAce Kill Effect",       uuid, KillEffectListener.aceEffectPlayers);
                ItemStack mario = make(Items.YELLOW_WOOL, "§e§lMario Kill Effect",     uuid, KillEffectListener.marioEffectPlayers);
                ItemStack trash = make(Items.BARRIER,     "§8§lYou Are Trash Effect",  uuid, KillEffectListener.trashEffectPlayers);
                ItemStack rocket = make(Items.FIREWORK_ROCKET, "§c§lRocket Kill Effect", uuid, KillEffectListener.rocketEffectPlayers);
                ItemStack creeper = make(Items.CREEPER_SPAWN_EGG, "§2§lCreeper Kill Effect", uuid, KillEffectListener.creeperEffectPlayers);
                ItemStack tnt = make(Items.TNT,           "§6§lExplosion Kill Effect", uuid, KillEffectListener.explosionEffectPlayers);
                ItemStack totem = make(Items.TOTEM_OF_UNDYING, "§6§lTotem Kill Effect", uuid, KillEffectListener.totemEffectPlayers);
                ItemStack mexico = make(Items.CACTUS,     "§a§lMexico Kill Effect",    uuid, KillEffectListener.mexicoEffectPlayers);
                ItemStack water = make(Items.WATER_BUCKET,"§b§lWater Kill Effect",     uuid, KillEffectListener.waterEffectPlayers);

                gui.setItem(0, ace);
                gui.setItem(1, mario);
                gui.setItem(2, trash);
                gui.setItem(3, rocket);
                gui.setItem(4, creeper);
                gui.setItem(5, tnt);
                gui.setItem(6, totem);
                gui.setItem(7, mexico);
                gui.setItem(8, water);

                return new KillEffectContainer(id, inv, gui);
            }
        });
    }

    private static ItemStack make(net.minecraft.item.Item item, String name, UUID uuid, Set<UUID> effectSet)
    {
        ItemStack stack = new ItemStack(item);
        stack.setHoverName(new StringTextComponent(name));
        if(effectSet.contains(uuid))
        {
            stack.enchant(Enchantments.UNBREAKING, 1);
            stack.getOrCreateTag().putInt("HideFlags", 1);
        }
        return stack;
    }
}
