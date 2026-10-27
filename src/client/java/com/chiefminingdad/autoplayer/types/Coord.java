package com.chiefminingdad.autoplayer.types;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public record Coord(boolean relative, double value) {
    public double get(double position){
        return relative?position+value:value;
    }


    public static Coord parseDouble(StringReader reader)throws CommandSyntaxException {
        boolean isRelative = false;
        double coordinate = 0d;
        if(reader.canRead()&&reader.peek()=='~'){ isRelative = true;reader.skip();}
        if(!(isRelative && reader.peek()==' ')) {
            coordinate = reader.readDouble();
        }
        return new Coord(isRelative,coordinate);
    }
}
