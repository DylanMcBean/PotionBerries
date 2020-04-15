package com.pyrodeathadder.potionberries.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pyrodeathadder.potionberries.tileentity.QuarryTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class QuarryArm extends TileEntityRenderer<QuarryTileEntity> {

    private ItemRenderer renderer;


    public QuarryArm(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    private void add(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v) {
        renderer.pos(stack.getLast().getMatrix(),x,y,z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .tex(u,v)
                .lightmap(0,240)
                .normal(1,0,0)
                .endVertex();
    }

    private static float diffFunction(long time, long delta, float scale) {
        long dt = time % (delta*2);
        if(dt > delta){
            dt = 2*delta - dt;
        }
        return dt * scale;
    }

    @Override
    public void render(QuarryTileEntity quarryTileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        matrixStack.clear();
        matrixStack.push();
    }
}
