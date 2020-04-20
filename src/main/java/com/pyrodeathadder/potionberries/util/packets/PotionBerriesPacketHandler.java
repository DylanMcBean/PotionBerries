package com.pyrodeathadder.potionberries.util.packets;

import com.pyrodeathadder.potionberries.PotionBerries;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PotionBerriesPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PotionBerries.MOD_ID,"main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    public static void RegisterPackets() {
        int id = 0;
        PotionBerriesPacketHandler.INSTANCE.registerMessage(id++, PotionBerriesWorldPacket.class, PotionBerriesWorldPacket::encode, PotionBerriesWorldPacket::decode, PotionBerriesWorldPacket::onRecieved);
    }

}
