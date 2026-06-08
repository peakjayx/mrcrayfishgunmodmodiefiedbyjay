package com.mrcrayfish.guns.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.guns.common.ThermalModeManager;
import com.mrcrayfish.guns.item.SwatHelmItem;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageSyncThermal;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class ThermalModeCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("thermalmode")
            .then(Commands.literal("enable")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                    if(!ThermalModeManager.isAllowed(player.getUUID())) {
                        ctx.getSource().sendFailure(new StringTextComponent("Not authorized for thermal mode"));
                        return 0;
                    }
                    ItemStack helm = player.getItemBySlot(EquipmentSlotType.HEAD);
                    if(!(helm.getItem() instanceof SwatHelmItem)) {
                        ctx.getSource().sendFailure(new StringTextComponent("No SWAT helm equipped"));
                        return 0;
                    }
                    SwatHelmItem.setThermalActive(helm, true);
                    PacketHandler.getPlayChannel().send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new MessageSyncThermal(true));
                    return 1;
                }))
            .then(Commands.literal("disable")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                    ItemStack helm = player.getItemBySlot(EquipmentSlotType.HEAD);
                    if(helm.getItem() instanceof SwatHelmItem) {
                        SwatHelmItem.setThermalActive(helm, false);
                        PacketHandler.getPlayChannel().send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new MessageSyncThermal(false));
                    }
                    return 1;
                }))
            .then(Commands.literal("allow")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayerEntity target = EntityArgument.getPlayer(ctx, "player");
                        ThermalModeManager.allow(target.getUUID());
                        ctx.getSource().sendSuccess(
                            new StringTextComponent("Thermal allowed: " + target.getName().getString()), false);
                        return 1;
                    })))
            .then(Commands.literal("disallow")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayerEntity target = EntityArgument.getPlayer(ctx, "player");
                        ThermalModeManager.disallow(target.getUUID());
                        ItemStack helm = target.getItemBySlot(EquipmentSlotType.HEAD);
                        if(helm.getItem() instanceof SwatHelmItem
                                && SwatHelmItem.isThermalActive(helm)) {
                            SwatHelmItem.setThermalActive(helm, false);
                            PacketHandler.getPlayChannel().send(
                                PacketDistributor.PLAYER.with(() -> target),
                                new MessageSyncThermal(false));
                        }
                        ctx.getSource().sendSuccess(
                            new StringTextComponent("Thermal disallowed: " + target.getName().getString()), false);
                        return 1;
                    })))
            .then(Commands.literal("list")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> {
                    java.util.Set<UUID> ids = ThermalModeManager.getAllowed();
                    if(ids.isEmpty()) {
                        ctx.getSource().sendSuccess(new StringTextComponent("Thermal allowed list: (empty)"), false);
                        return 1;
                    }
                    StringBuilder sb = new StringBuilder("Thermal allowed: ");
                    for(UUID id : ids) {
                        ServerPlayerEntity p = ctx.getSource().getServer()
                            .getPlayerList().getPlayer(id);
                        sb.append(p != null ? p.getName().getString() : id.toString())
                          .append(", ");
                    }
                    ctx.getSource().sendSuccess(
                        new StringTextComponent(sb.substring(0, sb.length() - 2)), false);
                    return 1;
                }))
        );
    }
}
