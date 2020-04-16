package com.pyrodeathadder.potionberries.init;

import com.pyrodeathadder.potionberries.PotionBerries;
import com.pyrodeathadder.potionberries.tileentity.QuarryTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityType {
   public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPE = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, PotionBerries.MOD_ID);

   public static final RegistryObject<TileEntityType<QuarryTileEntity>> QUARRY = TILE_ENTITY_TYPE.register("quarry", () -> TileEntityType.Builder.create(QuarryTileEntity::new, BlockInit.QUARRY.get()).build(null));
}
