package com.pyrodeathadder.potionberries.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pyrodeathadder.potionberries.PotionBerries;
import com.pyrodeathadder.potionberries.container.GuiButton;
import com.pyrodeathadder.potionberries.container.QuarryContainer;
import com.pyrodeathadder.potionberries.util.helpers.KeyboardHelper;
import com.pyrodeathadder.potionberries.util.packets.PotionBerriesPacketHandler;
import com.pyrodeathadder.potionberries.util.packets.PotionBerriesWorldPacket;
import com.pyrodeathadder.potionberries.util.packets.VoidItemsPacket;
import com.sun.org.apache.xpath.internal.objects.XObject;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class Quarry_Screen extends ContainerScreen<QuarryContainer> {

    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(PotionBerries.MOD_ID, "textures/gui/quarry_gui.png");

    QuarryContainer container = this.getContainer();
    static int xoff = 0,zoff = 1,quarry_width = 0,quarry_length = 0;

    public Quarry_Screen(QuarryContainer screenContainer, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(screenContainer, playerInventory, titleIn);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = 175; //size of the gui main window
        this.ySize = 221; //--
    }

    @Override
    public void render(final int mouseX,final int mouseY, final float partialTicks) {
        this.renderBackground();
        super.render(mouseX,mouseY,partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.font.drawString(this.title.getFormattedText(),8.0f, 6.0f, 421752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, 124.0f, 421752);


        //Draw Width
        this.font.drawString(""+this.quarry_width, 135.0f, 31.0f, 000);

        //Draw length
        this.font.drawString(""+this.quarry_length, 135.0f, 60.0f, 000);

        //Xoff
        this.font.drawString(""+this.xoff, 135.0f, 80.0f, 000);

        //Zoff
        this.font.drawString(""+this.zoff, 135.0f, 100.0f, 000);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(6.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE_LOCATION);

        int x = (this.width - this.xSize)/2;
        int y = (this.height - this.ySize)/2;
        this.blit(x,y,0,0,this.xSize, this.ySize);
        //Quarry Chunk Based
        if(mouseOver(mouseX, mouseY,x+7,y+96,24,24)) { //active
            this.blit(x + 7, y + 96, 210, 8, 24, 24);
        } else {
            this.blit(x + 7, y + 96, 184, 8, 24, 24);
        }


        //Quarry Whole
        if(mouseOver(mouseX, mouseY,x+34,y+96,24,24)) { //active
            this.blit(x + 34, y + 96, 210, 34, 24, 24);
        }else {
            this.blit(x + 34, y + 96, 184, 34, 24, 24);
        }

        //Mine Button
        if(mouseOver(mouseX, mouseY,x+64,y+100,26,16)) { //active
            this.blit(x + 64, y + 100, 184, 158, 26, 16);
        } else {
            this.blit(x + 64, y + 100, 184, 140, 26, 16);
        }

        //Width

        this.blit(x+129, y+27, 184,60,29,16);
        //+
        if(mouseOver(mouseX, mouseY,x+160,y+30,7,11)) { //active
            this.blit(x + 160, y + 30, 184, 110, 7, 11);
        } else {
            this.blit(x + 160, y + 30, 184, 94, 7, 11);
        }
        //-
        if(mouseOver(mouseX, mouseY,x+120,y+30,7,11)) { //active
            this.blit(x + 120, y + 30, 196, 110, 7, 11);
        } else {
            this.blit(x + 120, y + 30, 196, 94, 7, 11);
        }

        //length
        this.blit(x+129, y+56, 184,60,29,16);
        //+
        if(mouseOver(mouseX, mouseY,x+160,y+59,7,11)) { //active
            this.blit(x + 160, y + 59, 184, 110, 7, 11);
        } else {
            this.blit(x + 160, y + 59, 184, 94, 7, 11);
        }
        //-
        if(mouseOver(mouseX, mouseY,x+120,y+59,7,11)) { //active
            this.blit(x + 120, y + 59, 196, 110, 7, 11);
        } else {
            this.blit(x + 120, y + 59, 196, 94, 7, 11);
        }


        //Xoff
        this.blit(x+129, y+76, 184,60,29,16);
        //+
        if(mouseOver(mouseX, mouseY,x+160,y+77,9,6)) { //active
            this.blit(x + 160, y + 77, 215, 87, 9, 6);
        } else {
            this.blit(x + 160, y + 77, 215, 69, 9, 6);
        }
        //-
        if(mouseOver(mouseX, mouseY,x+160,y+85,9,6)) { //active
            this.blit(x + 160, y + 85, 215, 78, 9, 6);
        } else {
            this.blit(x + 160, y + 85, 215, 60, 9, 6);
        }


        //ZOff
        this.blit(x+129, y+96, 184,60,29,16);
        //+
        if(mouseOver(mouseX, mouseY,x+160,y+97,9,6)) { //active
            this.blit(x+160, y+97, 215,87,9,6);
        } else {
            this.blit(x+160, y+97, 215,69,9,6);
        }
        //-
        if(mouseOver(mouseX, mouseY,x+160,y+105,9,6)) { //active
            this.blit(x + 160, y + 105, 215, 78, 9, 6);
        } else {
            this.blit(x + 160, y + 105, 215, 60, 9, 6);
        }

    }

    public boolean mouseOver(int mouseX, int mouseY, int xStart, int yStart, int xWidth, int yHeight) {
        return (mouseX >= xStart && mouseX <= xStart + xWidth) && (mouseY >= yStart && mouseY <= yStart + yHeight);
    }

    @Override
    public QuarryContainer getContainer() {
        return super.getContainer();
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.xSize)/2;
        int y = (this.height - this.ySize)/2;
        addButton(new GuiButton(x+64,y+100,26,16,"", test -> {
            sendPacket();
            this.xoff = 0;
            this.zoff = 1;
            this.quarry_width = 0;
            this.quarry_length = 0;
        }));

        addButton(new GuiButton(x+160,y+30,7,11,"", widthplus -> ChangeValue("width",1)));
        addButton(new GuiButton(x+120,y+30,7,11,"", widthminus -> ChangeValue("width",-1)));

        addButton(new GuiButton(x+160,y+59,7,11,"", widthplus -> ChangeValue("length",1)));
        addButton(new GuiButton(x+120,y+59,7,11,"", widthminus -> ChangeValue("length",-1)));

        addButton(new GuiButton(x+160,y+77,9,6,"", widthplus -> ChangeValue("xoff",1)));
        addButton(new GuiButton(x+160,y+85,6,6,"", widthminus -> ChangeValue("xoff",-1)));

        addButton(new GuiButton(x+160,y+97,9,6,"", widthplus -> ChangeValue("zoff",1)));
        addButton(new GuiButton(x+160,y+105,6,6,"", widthminus -> ChangeValue("zoff",-1)));
    }

    public void sendPacket() {
        PotionBerriesPacketHandler.INSTANCE.sendToServer(new PotionBerriesWorldPacket(this.zoff, this.xoff, this.quarry_width, this.quarry_length));
    }

    public void ChangeValue(String value, int change){
        change *= KeyboardHelper.isHoldingControl()? 10:1;
        switch(value){
            case "xoff":
                this.xoff = Math.min(this.quarry_width,Math.max(0,this.xoff + change));
                break;
            case "zoff":
                this.zoff = Math.min(10,Math.max(1,this.zoff + change));
                break;
            case "width":
                this.quarry_width = Math.min(256,Math.max(0,this.quarry_width + change));
                break;
            case "length":
                this.quarry_length = Math.min(256,Math.max(0,this.quarry_length + change));
                break;
        }
    }
}
