package com.mrcrayfish.guns.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.guns.common.container.KillEffectMenu;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class KillEffectCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("killeffect")
                .executes(ctx -> {
                    CommandSource source = ctx.getSource();
                    try
                    {
                        ServerPlayerEntity player = source.getPlayerOrException();
                        KillEffectMenu.open(player);
                    }
                    catch(Exception ignored) {}
                    return 1;
                }));
    }
}
