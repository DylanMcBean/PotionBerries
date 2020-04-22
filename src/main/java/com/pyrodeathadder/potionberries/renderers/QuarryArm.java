package com.pyrodeathadder.potionberries.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pyrodeathadder.potionberries.init.BlockInit;
import com.pyrodeathadder.potionberries.tileentity.QuarryTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.EmptyModelData;

import static com.pyrodeathadder.potionberries.objects.blocks.QuarryFrame.SECTION;

public class QuarryArm extends TileEntityRenderer<QuarryTileEntity> {
    private ItemRenderer renderer;

    public QuarryArm(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    private void add(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v) {
        renderer.pos(stack.getLast().getMatrix(),x,y,z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .tex(u,v)
                .overlay(0, 15)
                .lightmap(0,240)
                .normal(1,0,0)
                .endVertex();
    }

    @Override
    public void render(QuarryTileEntity quarryTileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        if(quarryTileEntity.initialised) {
            TextureAtlasSprite tex = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("potionberries:blocks/pipe_connector"));

            IVertexBuilder vb = iRenderTypeBuffer.getBuffer(RenderType.getEntityTranslucent(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
            BlockPos relativePos = new BlockPos(quarryTileEntity.xpos,quarryTileEntity.ypos,quarryTileEntity.zpos).add(-quarryTileEntity.getPos().getX(),-quarryTileEntity.getPos().getY(), -quarryTileEntity.getPos().getZ());

            switch (quarryTileEntity.facingPositon) {
                case "south":relativePos = new BlockPos(quarryTileEntity.xpos,quarryTileEntity.ypos,quarryTileEntity.zpos).add(-quarryTileEntity.getPos().getX(),-quarryTileEntity.getPos().getY(), -quarryTileEntity.getPos().getZ());
                    break;
                case "west":relativePos = new BlockPos(quarryTileEntity.zpos,quarryTileEntity.ypos,-quarryTileEntity.xpos).add(-quarryTileEntity.getPos().getZ(),-quarryTileEntity.getPos().getY(), quarryTileEntity.getPos().getX());
                    break;
                case "north":relativePos = new BlockPos(-quarryTileEntity.xpos,quarryTileEntity.ypos,-quarryTileEntity.zpos).add(quarryTileEntity.getPos().getX(),-quarryTileEntity.getPos().getY(), quarryTileEntity.getPos().getZ());
                    break;
                case "east":relativePos = new BlockPos(-quarryTileEntity.zpos,quarryTileEntity.ypos,quarryTileEntity.xpos).add(quarryTileEntity.getPos().getZ(),-quarryTileEntity.getPos().getY(), -quarryTileEntity.getPos().getX());
                    break;
            }

            matrixStack.push();
            BlockRendererDispatcher blockRendererDispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockState state = BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, 2);
            relativeTranslate(matrixStack, quarryTileEntity,relativePos.getX()+quarryTileEntity.xoffset,relativePos.getY()+5,relativePos.getZ()+quarryTileEntity.zoffset);
            blockRendererDispatcher.renderBlock(state, matrixStack,iRenderTypeBuffer,240,i1, EmptyModelData.INSTANCE);
            matrixStack.pop();

            matrixStack.push();
            relativeTranslate(matrixStack, quarryTileEntity,relativePos.getX(),5,relativePos.getZ() + quarryTileEntity.zoffset);
            state = BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, quarryTileEntity.xAligned? 1:0);
            for (int x = 0; x < quarryTileEntity.width; x++) {
                if(x != quarryTileEntity.xoffset) {
                    blockRendererDispatcher.renderBlock(state, matrixStack, iRenderTypeBuffer, 240, i1, EmptyModelData.INSTANCE);
                }
                relativeTranslate(matrixStack, quarryTileEntity,1,0,0);
            }
            matrixStack.pop();


            matrixStack.push();
            relativeTranslate(matrixStack, quarryTileEntity,relativePos.getX()+quarryTileEntity.xoffset,5,relativePos.getZ());
            state = BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, quarryTileEntity.xAligned? 0:1);
            for (int z = 0; z < quarryTileEntity.length; z++) {
                if(z != quarryTileEntity.zoffset) {
                    blockRendererDispatcher.renderBlock(state, matrixStack, iRenderTypeBuffer, 240, i1, EmptyModelData.INSTANCE);
                }
                relativeTranslate(matrixStack, quarryTileEntity,0,0,1);
            }
            matrixStack.pop();


            matrixStack.push();
            relativeTranslate(matrixStack, quarryTileEntity,relativePos.getX()+quarryTileEntity.xoffset,relativePos.getY()+4,relativePos.getZ()+quarryTileEntity.zoffset);
            state = BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, 3);
            for (int y = (int)quarryTileEntity.yoffset+2; y < 5; y++) {
                blockRendererDispatcher.renderBlock(state, matrixStack, iRenderTypeBuffer, 240, i1, EmptyModelData.INSTANCE);
                relativeTranslate(matrixStack, quarryTileEntity,0,-1,0);
            }
            matrixStack.pop();

            relativeTranslate(matrixStack, quarryTileEntity,relativePos.getX()+quarryTileEntity.xoffset,relativePos.getY()+quarryTileEntity.yoffset +1,relativePos.getZ()+quarryTileEntity.zoffset);
            state = BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, 4);
            blockRendererDispatcher.renderBlock(state, matrixStack, iRenderTypeBuffer, 240, i1, EmptyModelData.INSTANCE);

        }
    }

    @Override
    public boolean isGlobalRenderer(QuarryTileEntity te) {
        return true;
    }

    public MatrixStack relativeTranslate(MatrixStack m, QuarryTileEntity q, float x, float y, float z) {
        switch (q.facingPositon) {
            case "south":
                m.translate(x,y,z);
                break;
            case "west":
                m.translate(-z,y,x);
                break;
            case "north":
                m.translate(-x,y,-z);
                break;
            case "east":
                m.translate(z,y,-x);
                break;
        }
        return m;
    }
}
