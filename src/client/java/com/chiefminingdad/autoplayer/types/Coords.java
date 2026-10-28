package com.chiefminingdad.autoplayer.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public record Coords(Coord x, Coord y, Coord z) {
    public Vec3 getPosition(FabricClientCommandSource sender){
        Vec3 pos = sender.getPosition();
        return new Vec3(this.x.get(pos.x()), this.y.get(pos.y()), this.z.get(pos.z()));
    }

    public Vec3i getPositioni(FabricClientCommandSource sender){
        Vec3 pos = sender.getPosition();
        return new Vec3i((int) this.x.get(pos.x()), (int) this.y.get(pos.y()), (int) this.z.get(pos.z()));
    }

    public static Coords parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        Coord x = Coord.parseDouble(reader);
        if(reader.canRead()&&reader.peek() == ' '){
            reader.skip();
            Coord y = Coord.parseDouble(reader);
            if(reader.canRead()&&reader.peek() == ' '){
                reader.skip();
                Coord z = Coord.parseDouble(reader);
                return new Coords(x,y,z);
            }else {
                reader.setCursor(start);
                throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
            }
        }else {
            reader.setCursor(start);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
        }
    }
}
