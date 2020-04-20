package com.pyrodeathadder.potionberries.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class GuiButton extends Button {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");


    public GuiButton(int widthIn, int heightIn, int width, int height, String text, IPressable onPress) {
        super(widthIn, heightIn, width, height, text, onPress);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered);
            //GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(mc, mouseX, mouseY);
            int color = getFGColor();

            if (this.isHovered && this.packedFGColor == Widget.UNSET_FG_COLOR)
                color = 0xFFFFA0; // Slightly Yellow

            String buttonText = this.getMessage();
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            this.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }

}
