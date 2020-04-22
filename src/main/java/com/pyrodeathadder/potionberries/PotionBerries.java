package com.pyrodeathadder.potionberries;

import com.pyrodeathadder.potionberries.container.QuarryContainer;
import com.pyrodeathadder.potionberries.init.BlockInit;
import com.pyrodeathadder.potionberries.init.ItemInit;
import com.pyrodeathadder.potionberries.init.ModTileEntityType;
import com.pyrodeathadder.potionberries.objects.blocks.Berries;
import com.pyrodeathadder.potionberries.objects.containers.ModContainerTypes;
import com.pyrodeathadder.potionberries.renderers.QuarryArm;
import com.pyrodeathadder.potionberries.tileentity.QuarryTileEntity;
import com.pyrodeathadder.potionberries.util.packets.PotionBerriesPacketHandler;
import com.pyrodeathadder.potionberries.util.packets.PotionBerriesWorldPacket;
import com.pyrodeathadder.potionberries.world.gen.OreGen;
import mcjty.lib.setup.Registration;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("potionberries")
@Mod.EventBusSubscriber(modid = PotionBerries.MOD_ID, bus = Bus.MOD)
public class PotionBerries
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "potionberries";
    public static PotionBerries instance;

    public PotionBerries() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        ItemInit.ITEMS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        ModTileEntityType.TILE_ENTITY_TYPE.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        PotionBerriesPacketHandler.RegisterPackets();
        //QuarryArm.register();

        instance = this;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        BlockInit.BLOCKS.getEntries().stream().filter(block -> !(block.get() instanceof Berries))
                .map(RegistryObject::get).forEach(block -> {
                final Item.Properties properties = new Item.Properties().group(PotionBerriesItemGroup.instance);
                final BlockItem blockItem = new BlockItem(block, properties);
                blockItem.setRegistryName(block.getRegistryName());
                registry.register(blockItem);
        });

        LOGGER.debug("Registered BlockItems!");
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        BrewingRecipeRegistry.addRecipe(Ingredient.fromItems(ItemInit.CHRYSOCOLLA_INGOT.get()), Ingredient.fromItems(Items.DIAMOND), new ItemStack(ItemInit.CHRYSOCOLLA_AXE.get()));
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ClientRegistry.bindTileEntityRenderer(ModTileEntityType.QUARRY.get(),QuarryArm::new);

        RenderTypeLookup.setRenderLayer(BlockInit.NIGHT_VISION_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_NIGHT_VISION_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.INVISIBILITY_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_INVISIBILITY_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LEAPING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_LEAPING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_LEAPING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.FIRE_RESISTANCE_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_FIRE_RESISTANCE_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SPEED_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SPEED_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SPEED_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOWNESS_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOWNESS_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SLOWNESS_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.TURTLE_MASTER_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_TURTLE_MASTER_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_TURTLE_MASTER_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WATER_BREATHING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WATER_BREATHING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HEALING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HEALING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HARMING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HARMING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.POISON_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_POISON_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_POISON_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.REGENERATION_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_REGENERATION_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_REGENERATION_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRENGTH_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_STRENGTH_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_STRENGTH_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WEAKNESS_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WEAKNESS_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOW_FALLING_BERRY_BUSH_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOW_FALLING_BERRY_BUSH_1.get(), RenderType.getCutout());

        RenderTypeLookup.setRenderLayer(BlockInit.NIGHT_VISION_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_NIGHT_VISION_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.INVISIBILITY_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_INVISIBILITY_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LEAPING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_LEAPING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_LEAPING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.FIRE_RESISTANCE_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_FIRE_RESISTANCE_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SPEED_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SPEED_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SPEED_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOWNESS_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOWNESS_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SLOWNESS_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.TURTLE_MASTER_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_TURTLE_MASTER_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_TURTLE_MASTER_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WATER_BREATHING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WATER_BREATHING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HEALING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HEALING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HARMING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HARMING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.POISON_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_POISON_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_POISON_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.REGENERATION_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_REGENERATION_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_REGENERATION_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRENGTH_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_STRENGTH_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_STRENGTH_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WEAKNESS_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WEAKNESS_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOW_FALLING_BERRY_BUSH_2.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOW_FALLING_BERRY_BUSH_2.get(), RenderType.getCutout());

        RenderTypeLookup.setRenderLayer(BlockInit.NIGHT_VISION_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_NIGHT_VISION_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.INVISIBILITY_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_INVISIBILITY_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LEAPING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_LEAPING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_LEAPING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.FIRE_RESISTANCE_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_FIRE_RESISTANCE_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SPEED_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SPEED_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SPEED_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOWNESS_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOWNESS_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SLOWNESS_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.TURTLE_MASTER_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_TURTLE_MASTER_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_TURTLE_MASTER_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WATER_BREATHING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WATER_BREATHING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HEALING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HEALING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HARMING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HARMING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.POISON_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_POISON_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_POISON_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.REGENERATION_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_REGENERATION_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_REGENERATION_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRENGTH_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_STRENGTH_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_STRENGTH_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WEAKNESS_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WEAKNESS_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOW_FALLING_BERRY_BUSH_3.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOW_FALLING_BERRY_BUSH_3.get(), RenderType.getCutout());

        RenderTypeLookup.setRenderLayer(BlockInit.NIGHT_VISION_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_NIGHT_VISION_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.INVISIBILITY_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_INVISIBILITY_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LEAPING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_LEAPING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_LEAPING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.FIRE_RESISTANCE_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_FIRE_RESISTANCE_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SPEED_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SPEED_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SPEED_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOWNESS_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOWNESS_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SLOWNESS_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.TURTLE_MASTER_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_TURTLE_MASTER_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_TURTLE_MASTER_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WATER_BREATHING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WATER_BREATHING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HEALING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HEALING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HARMING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HARMING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.POISON_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_POISON_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_POISON_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.REGENERATION_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_REGENERATION_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_REGENERATION_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRENGTH_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_STRENGTH_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_STRENGTH_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WEAKNESS_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WEAKNESS_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOW_FALLING_BERRY_BUSH_4.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOW_FALLING_BERRY_BUSH_4.get(), RenderType.getCutout());

        RenderTypeLookup.setRenderLayer(BlockInit.NIGHT_VISION_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_NIGHT_VISION_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.INVISIBILITY_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_INVISIBILITY_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LEAPING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_LEAPING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_LEAPING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.FIRE_RESISTANCE_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_FIRE_RESISTANCE_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SPEED_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SPEED_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SPEED_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOWNESS_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOWNESS_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_SLOWNESS_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.TURTLE_MASTER_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_TURTLE_MASTER_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_TURTLE_MASTER_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WATER_BREATHING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WATER_BREATHING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HEALING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HEALING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.HARMING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_HARMING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.POISON_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_POISON_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_POISON_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.REGENERATION_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_REGENERATION_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_REGENERATION_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRENGTH_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_STRENGTH_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.STRONG_STRENGTH_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.WEAKNESS_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_WEAKNESS_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.SLOW_FALLING_BERRY_BUSH_5.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockInit.LONG_SLOW_FALLING_BERRY_BUSH_5.get(), RenderType.getCutout());

        RenderTypeLookup.setRenderLayer(BlockInit.QUARRYFRAME.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlockInit.QUARRY_STRUCTURE_FRAME.get(), RenderType.getTranslucent());

    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {

    }

    @SubscribeEvent
    public static void LoadCompleteEvent(FMLLoadCompleteEvent event) {
        OreGen.generateOre();
    }

    public static class PotionBerriesItemGroup extends ItemGroup
    {
        public static final PotionBerriesItemGroup instance = new PotionBerriesItemGroup(ItemGroup.GROUPS.length, "potionberries");
        private PotionBerriesItemGroup(int index, String label)
        {
            super(index, label);
        }

        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(BlockInit.CHRYSOCOLLA_BLOCK.get());
        }
    }
}
