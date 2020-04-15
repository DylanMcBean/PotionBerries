package com.pyrodeathadder.potionberries.objects.blocks;

import com.pyrodeathadder.potionberries.util.helpers.StringHelpers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class Berries extends SweetBerryBushBlock {
    public final int tier;
    public final Properties properties;
    public Berries(Properties properties, int tier) {
        super(properties);
        this.properties = properties;
        this.tier = tier;
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(this.asItem());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayIn) {
        int i = state.get(AGE);
        boolean flag = i == 3;
        if(!flag && player.getHeldItem(handIn).getItem() == Items.BONE_MEAL) {
            if (worldIn.rand.nextInt(10) == 0) {
                return ActionResultType.PASS;
            }
        } else if (i > 1) {
            int j = (i - 3) + worldIn.rand.nextInt(tier);
            spawnAsEntity(worldIn, pos, new ItemStack(this.asItem(), j + (flag ? 1 : 0)));
            worldIn.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS,1f,0.8f + worldIn.rand.nextFloat() * 0.4f);
            worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(0)),0);
            return ActionResultType.SUCCESS;
        } else {
            return super.onBlockActivated(state, worldIn, pos, player,handIn, rayIn);
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        for(int i = 0; i < this.asItem().getFood().getEffects().size(); i ++){
            String effect = this.asItem().getFood().getEffects().get(i).getKey().getPotion().delegate.name().getPath();
            int amplifier = this.asItem().getFood().getEffects().get(i).getKey().getAmplifier();
            TextFormatting color = this.asItem().getFood().getEffects().get(i).getKey().getPotion().getEffectType().getColor();
            int duration = this.asItem().getFood().getEffects().get(i).getKey().getDuration();

            final String[] AMPLIFIERNUMBERS = new String[]{"", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
            if (amplifier < 11) {
                tooltip.add(new StringTextComponent(color + StringHelpers.titleCaseConversion(effect, "_") + AMPLIFIERNUMBERS[amplifier - 1] + " (" + StringHelpers.timeChanger(duration) + ")"));
            } else {
                tooltip.add(new StringTextComponent(color + StringHelpers.titleCaseConversion(effect, "_") + " " + amplifier + " (" + StringHelpers.timeChanger(duration) + ")"));
            }
        }
        tooltip.add(new StringTextComponent("Tier " + tier));
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        int i = state.get(AGE);
        if (i < 3 && worldIn.getLightSubtracted(pos.up(), 0) >= 9 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt(20) == 0)) {
            worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
        }

    }
}
