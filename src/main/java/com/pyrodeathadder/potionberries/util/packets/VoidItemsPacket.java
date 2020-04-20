package com.pyrodeathadder.potionberries.util.packets;

import com.pyrodeathadder.potionberries.container.QuarryContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Supplier;

public class VoidItemsPacket {
    private boolean updateItems;

    public VoidItemsPacket(boolean update) {
        this.updateItems = update;
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeBoolean(updateItems);
    }

    public static VoidItemsPacket decode(PacketBuffer buffer) {
        return new VoidItemsPacket(buffer.readBoolean());
    }

    public void onRecieved(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
        });
        context.get().setPacketHandled(true);
    }
}
