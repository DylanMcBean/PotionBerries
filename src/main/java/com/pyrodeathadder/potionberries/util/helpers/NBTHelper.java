package com.pyrodeathadder.potionberries.util.helpers;

import com.pyrodeathadder.potionberries.tileentity.QuarryTileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl;

import javax.annotation.Nullable;

public class NBTHelper {
    public static CompoundNBT toNBT(Object o) {
        if (o instanceof ItemStack) {
            return writeItemStack((ItemStack)o);
        }
        if(o instanceof QuarryTileEntity) {
            return writeQuarry((QuarryTileEntity)o);
        }
        return null;
    }


    private static CompoundNBT writeQuarry(QuarryTileEntity o) {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("xpos", o.xpos);
        compound.putInt("ypos", o.ypos);
        compound.putInt("zpos", o.zpos);
        compound.putInt("xoff", o.xoffset);
        compound.putInt("yoff", o.yoffset);
        compound.putInt("zoff", o.zoffset);
        compound.putInt("tick", o.tick);
        compound.putBoolean("running", o.running);
        compound.putBoolean("positiveX", o.positiveX);
        compound.putBoolean("positiveZ", o.positiveZ);
        compound.putInt("previousX",o.previousBlockBreak.getX());
        compound.putInt("previousZ", o.previousBlockBreak.getZ());
        compound.putInt("Width", o.width);
        compound.putInt("Length", o.length);
        compound.putBoolean("xAligned", o.xAligned);
        compound.putString("FacingPositon", o.facingPositon);

        return compound;
    }


    private static CompoundNBT writeItemStack(ItemStack i) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("count", i.getCount());
        nbt.putString("item", i.getItem().getRegistryName().toString());
        nbt.putByte("type", (byte)0);
        return nbt;
    }

    @Nullable
    public static Object fromNBT(@Nullable CompoundNBT compound) {
        switch (compound.getByte("type")) {
            case 0:
                return readItemStack(compound);
            default:
                return null;
        }
    }

    private static ItemStack readItemStack(CompoundNBT compound) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("item")));
        int count = compound.getInt("count");
        return new ItemStack(item, count);
    }
}
