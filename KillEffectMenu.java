package de.junior.untitled.core.gui;

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

public class KillEffectMenu {

    public static void open(ServerPlayerEntity player) {
        player.openMenu(new INamedContainerProvider() {

            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("§8Kill Effects");
            }

            @Override
            public Container createMenu(int id, PlayerInventory inv, PlayerEntity p) {
                Inventory gui = new Inventory(9);

                // Slot 0 – Ace
                ItemStack ace = new ItemStack(Items.WHITE_WOOL);
                ace.setHoverName(new StringTextComponent("§f§lAce Kill Effect"));
                gui.setItem(0, ace);

                // Slot 1 – Mario
                ItemStack mario = new ItemStack(Items.YELLOW_WOOL);
                mario.setHoverName(new StringTextComponent("§e§lMario Kill Effect"));
                gui.setItem(1, mario);

                // Slot 2 – Trash
                ItemStack trash = new ItemStack(Items.BARRIER);
                trash.setHoverName(new StringTextComponent("§8§lYou Are Trash Effect"));
                gui.setItem(2, trash);

                // Slot 3 – Rocket
                ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
                rocket.setHoverName(new StringTextComponent("§c§lRocket Kill Effect"));
                gui.setItem(3, rocket);

                // Slot 4 – Creeper
                ItemStack creeper = new ItemStack(Items.CREEPER_SPAWN_EGG);
                creeper.setHoverName(new StringTextComponent("§2§lCreeper Kill Effect"));
                gui.setItem(4, creeper);

                // Slot 5 – Explosion
                ItemStack tnt = new ItemStack(Items.TNT);
                tnt.setHoverName(new StringTextComponent("§6§lExplosion Kill Effect"));
                gui.setItem(5, tnt);

                // Slot 6 – Totem
                ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
                totem.setHoverName(new StringTextComponent("§6§lTotem Kill Effect"));
                gui.setItem(6, totem);

                // Slot 7 – Mexico
                ItemStack mexico = new ItemStack(Items.CACTUS);
                mexico.setHoverName(new StringTextComponent("§a§lMexico Kill Effect"));
                gui.setItem(7, mexico);

                // Slot 8 – Water
                ItemStack water = new ItemStack(Items.WATER_BUCKET);
                water.setHoverName(new StringTextComponent("§b§lWater Kill Effect"));
                gui.setItem(8, water);

                return new KillEffectContainer(id, inv, gui);
            }
        });
    }
}