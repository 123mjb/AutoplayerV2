package com.chiefminingdad.autoplayer.types.arguments;

import com.chiefminingdad.autoplayer.types.Coord;
import com.chiefminingdad.autoplayer.types.Coords;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class BlockPosArgumentType implements ArgumentType<Coords> {

    public boolean isRelativeX = false;
    public boolean isRelativeY = false;
    public boolean isRelativeZ = false;

    public Coord x,y,z;

    @Override
    public Coords parse(StringReader reader) throws CommandSyntaxException {
        return Coords.parse(reader);
    }
}
