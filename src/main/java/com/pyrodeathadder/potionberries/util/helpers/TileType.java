package com.pyrodeathadder.potionberries.util.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.*;

public class TileType{
    TileEntity tileEntity;
    Object tileEntityObject;

    public TileType(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    public TileType(Object tileEntity) {
        this.tileEntityObject = tileEntity;
    }

    public int getSizeInventory() {
        switch(tileEntity.getClass().getName()){
            case "net.minecraft.tileentity.ChestTileEntity": return ((ChestTileEntity) tileEntity).getSizeInventory();
            case "net.minecraft.tileentity.HopperTileEntity": return ((HopperTileEntity) tileEntity).getSizeInventory();
            case "net.minecraft.tileentity.ShulkerBoxTileEntity": return ((ShulkerBoxTileEntity) tileEntity).getSizeInventory();
            case "net.minecraft.tileentity.BarrelTileEntity": return ((BarrelTileEntity) tileEntity).getSizeInventory();
        }
        return 0;
    }

    public ItemStack getStackInSlot(int slot) {
        switch(tileEntity.getClass().getName()){
            case "net.minecraft.tileentity.ChestTileEntity": return ((ChestTileEntity) tileEntity).getStackInSlot(slot);
            case "net.minecraft.tileentity.HopperTileEntity": return ((HopperTileEntity) tileEntity).getStackInSlot(slot);
            case "net.minecraft.tileentity.ShulkerBoxTileEntity": return ((ShulkerBoxTileEntity) tileEntity).getStackInSlot(slot);
            case "net.minecraft.tileentity.BarrelTileEntity": return ((BarrelTileEntity) tileEntity).getStackInSlot(slot);
        }
        return null;
    }

    public Object copy() {
        switch(tileEntity.getClass().getName()){
            case "net.minecraft.tileentity.ChestTileEntity": return ((ChestTileEntity) tileEntity);
            case "net.minecraft.tileentity.HopperTileEntity": return ((HopperTileEntity) tileEntity);
            case "net.minecraft.tileentity.ShulkerBoxTileEntity": return ((ShulkerBoxTileEntity) tileEntity);
            case "net.minecraft.tileentity.BarrelTileEntity": return ((BarrelTileEntity) tileEntity);
        }
        return null;
    }

    public void setInventorySlotContents(int slot, ItemStack stack) {
        switch(tileEntity.getClass().getName()){
            case "net.minecraft.tileentity.ChestTileEntity": ((ChestTileEntity) tileEntity).setInventorySlotContents(slot,stack); break;
            case "net.minecraft.tileentity.HopperTileEntity": ((HopperTileEntity) tileEntity).setInventorySlotContents(slot,stack); break;
            case "net.minecraft.tileentity.ShulkerBoxTileEntity": ((ShulkerBoxTileEntity) tileEntity).setInventorySlotContents(slot,stack); break;
            case "net.minecraft.tileentity.BarrelTileEntity": ((BarrelTileEntity) tileEntity).setInventorySlotContents(slot,stack); break;
        }
    }
}
