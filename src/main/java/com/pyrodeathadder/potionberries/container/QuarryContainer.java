package com.pyrodeathadder.potionberries.container;

import com.pyrodeathadder.potionberries.init.BlockInit;
import com.pyrodeathadder.potionberries.objects.containers.ModContainerTypes;
import com.pyrodeathadder.potionberries.tileentity.QuarryTileEntity;
import com.pyrodeathadder.potionberries.util.packets.PotionBerriesPacketHandler;
import com.pyrodeathadder.potionberries.util.packets.VoidItemsPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class QuarryContainer extends Container {

    public final QuarryTileEntity tileEntity;
    private final IWorldPosCallable canInteractWithCallable;


    public QuarryContainer(final int windowId, final PlayerInventory playerInventory, final QuarryTileEntity tileEntity) {
        super(ModContainerTypes.QUARRY_TYPE.get(), windowId);
        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        //Main Inventory
        int startX = 8;
        int startY = 28;
        int slotSizePlus2 = 18;
        int id = 0;
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 4; column++){
                this.addSlot(new SlotItemHandler(tileEntity.inventory, id, startX+(column * slotSizePlus2), startY+(row * slotSizePlus2)));
                id ++;
            }
        }

        //Player Main Inventory
        int startPlayerInY = startY * 5;
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                this.addSlot(new Slot(playerInventory,9+ (row*9)+column, startX+(column * slotSizePlus2), startPlayerInY+(row * slotSizePlus2)));
            }
        }


        //Player Hotbar
        int hotbarY = startPlayerInY + 58;
        for(int column = 0; column < 9; ++column){
            this.addSlot(new Slot(playerInventory,(column), startX+(column * slotSizePlus2), hotbarY));
        }
    }


    private static QuarryTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "Player Inventory cannot be null");
        Objects.requireNonNull(data, "Data cannot be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if(tileAtPos instanceof QuarryTileEntity){
            return (QuarryTileEntity)tileAtPos;
        } else {
            throw new IllegalStateException("TileEntity is not correct; " + tileAtPos);
        }
    }

    public QuarryContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }


    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(canInteractWithCallable, playerIn, BlockInit.QUARRY.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()){
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();
            if(index < 12) {
                if(!this.mergeItemStack(itemStack1,12, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                } else if(!this.mergeItemStack(itemStack1,0,36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if(itemStack1 == ItemStack.EMPTY){
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemStack;
    }
}
