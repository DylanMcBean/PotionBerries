package com.pyrodeathadder.potionberries.objects.containers;

import com.pyrodeathadder.potionberries.PotionBerries;
import com.pyrodeathadder.potionberries.container.QuarryContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, PotionBerries.MOD_ID);

    public static final RegistryObject<ContainerType<QuarryContainer>> QUARRY_TYPE = CONTAINER_TYPES.register("quarry_type", () -> IForgeContainerType.create(QuarryContainer::new));
}
