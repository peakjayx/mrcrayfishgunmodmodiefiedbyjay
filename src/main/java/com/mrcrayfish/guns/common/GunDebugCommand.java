package com.mrcrayfish.guns.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GunDebugCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("gundebug")
                .requires(source -> source.hasPermission(2))
                .executes(GunDebugCommand::toggle));
    }

    private static int toggle(CommandContext<CommandSource> ctx)
    {
        KillHandler.debugMode = !KillHandler.debugMode;
        String state = KillHandler.debugMode ? "ENABLED" : "DISABLED";
        TextFormatting color = KillHandler.debugMode ? TextFormatting.GREEN : TextFormatting.RED;
        ctx.getSource().sendSuccess(new StringTextComponent(color + "[CGM Debug] Kill detection debug " + state), true);
        return 1;
    }
}
