package com.pyrodeathadder.potionberries.util.packets;

import com.pyrodeathadder.potionberries.container.QuarryContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PotionBerriesWorldPacket{
    private int xoff, zoff, width, length;

    public PotionBerriesWorldPacket(int xoffset, int zoffset, int width, int length) {
        this.xoff = xoffset;
        this.zoff = zoffset;
        this.width = width;
        this.length  = length;
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(this.xoff);
        packetBuffer.writeInt(this.zoff);
        packetBuffer.writeInt(this.width);
        packetBuffer.writeInt(this.length);
    }

    public static PotionBerriesWorldPacket decode(PacketBuffer buffer) {
        return new PotionBerriesWorldPacket(buffer.readInt(),buffer.readInt(),buffer.readInt(),buffer.readInt());
    }

    public void onRecieved(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if(context.get().getSender().openContainer instanceof QuarryContainer) {
                QuarryContainer quarryContainer = (QuarryContainer) context.get().getSender().openContainer;
                quarryContainer.tileEntity.width = this.width;
                quarryContainer.tileEntity.length = this.length;
                quarryContainer.tileEntity.init(this.xoff, this.zoff);
            }
        });
        context.get().setPacketHandled(true);
    }
}
