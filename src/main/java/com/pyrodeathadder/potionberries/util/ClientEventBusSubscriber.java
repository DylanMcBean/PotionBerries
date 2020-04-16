package com.pyrodeathadder.potionberries.util;

import com.pyrodeathadder.potionberries.PotionBerries;
import com.pyrodeathadder.potionberries.gui.Quarry_Screen;
import com.pyrodeathadder.potionberries.objects.containers.ModContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PotionBerries.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainerTypes.QUARRY_TYPE.get(), Quarry_Screen::new);
    }
}
