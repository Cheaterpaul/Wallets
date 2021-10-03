package de.cheaterpaul.wallets.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.network.InputEventPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

import static de.cheaterpaul.wallets.network.InputEventPacket.*;

public class WalletScreen extends ContainerScreen<WalletContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MOD_ID, "textures/gui/wallet.png");

    private TextFieldWidget sumField;
    private TextFieldWidget walletSum;

    public WalletScreen(WalletContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageHeight = 177;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ImageButton(this.getGuiLeft() + 18, this.getGuiTop() + 37, 11, 7, 190, 0, 7, BACKGROUND, this::walletApplyPressed));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 52, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, this::walletTakeCoinPressed, CoinItem.CoinValue.ONE));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 70, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, this::walletTakeCoinPressed, CoinItem.CoinValue.FIVE));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 88, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, this::walletTakeCoinPressed, CoinItem.CoinValue.TWENTY));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 106, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, this::walletTakeCoinPressed, CoinItem.CoinValue.FIFTY));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 124, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, this::walletTakeCoinPressed, CoinItem.CoinValue.ONE_HUNDRED));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 142, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, this::walletTakeCoinPressed, CoinItem.CoinValue.FIVE_HUNDRED));
        this.addButton(new ImageButton(this.getGuiLeft() + 142, this.getGuiTop() + 16, 14, 14, 176, 0, 14, BACKGROUND, this::walletSumCoinsPressed));
        this.sumField = new TextFieldWidget(this.font, this.leftPos + 51, this.topPos + 17, 80, 9, new TranslationTextComponent("text.wallets.take_coin_sum"));
        this.sumField.setMaxLength(50);
        this.addWidget(this.sumField);
        this.walletSum = new TextFieldWidget(this.font, this.leftPos + 8, this.topPos + 61, 32, 9, new TranslationTextComponent("text.wallets.wallet_sum"));
        this.walletSum.setMaxLength(10);
        this.addWidget(this.walletSum);
    }

    private void walletApplyPressed(Button b) {
        WalletsMod.dispatcher.sentToServer(new InputEventPacket(INSERT_COIN));
    }

    private void walletTakeCoinPressed(Button b) {
        AddWalletButton button = ((AddWalletButton) b);
        WalletsMod.dispatcher.sentToServer(new InputEventPacket(TAKE_COIN, button.getValue().toString()));

    }

    private void walletSumCoinsPressed(Button b) {
        WalletsMod.dispatcher.sentToServer(new InputEventPacket(TAKE_COINS, String.valueOf(3)));
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack stack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(stack, this.getGuiLeft(), this.getGuiTop(), this.getBlitOffset(), 0, 0, this.getXSize(), this.getYSize(), 256, 256);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    private static class AddWalletButton extends ImageButton {

        private final CoinItem.CoinValue value;

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, IPressable pressable, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, pressable);
            this.value = value;
        }

        public AddWalletButton(int p_i51135_1_, int p_i51135_2_, int p_i51135_3_, int p_i51135_4_, int p_i51135_5_, int p_i51135_6_, int p_i51135_7_, ResourceLocation p_i51135_8_, int p_i51135_9_, int p_i51135_10_, IPressable p_i51135_11_, CoinItem.CoinValue value) {
            super(p_i51135_1_, p_i51135_2_, p_i51135_3_, p_i51135_4_, p_i51135_5_, p_i51135_6_, p_i51135_7_, p_i51135_8_, p_i51135_9_, p_i51135_10_, p_i51135_11_);
            this.value = value;
        }

        public AddWalletButton(int p_i232261_1_, int p_i232261_2_, int p_i232261_3_, int p_i232261_4_, int p_i232261_5_, int p_i232261_6_, int p_i232261_7_, ResourceLocation p_i232261_8_, int p_i232261_9_, int p_i232261_10_, IPressable p_i232261_11_, ITextComponent p_i232261_12_, CoinItem.CoinValue value) {
            super(p_i232261_1_, p_i232261_2_, p_i232261_3_, p_i232261_4_, p_i232261_5_, p_i232261_6_, p_i232261_7_, p_i232261_8_, p_i232261_9_, p_i232261_10_, p_i232261_11_, p_i232261_12_);
            this.value = value;
        }

        public AddWalletButton(int p_i244513_1_, int p_i244513_2_, int p_i244513_3_, int p_i244513_4_, int p_i244513_5_, int p_i244513_6_, int p_i244513_7_, ResourceLocation p_i244513_8_, int p_i244513_9_, int p_i244513_10_, IPressable p_i244513_11_, ITooltip p_i244513_12_, ITextComponent p_i244513_13_, CoinItem.CoinValue value) {
            super(p_i244513_1_, p_i244513_2_, p_i244513_3_, p_i244513_4_, p_i244513_5_, p_i244513_6_, p_i244513_7_, p_i244513_8_, p_i244513_9_, p_i244513_10_, p_i244513_11_, p_i244513_12_, p_i244513_13_);
            this.value = value;
        }

        public CoinItem.CoinValue getValue() {
            return value;
        }
    }
}
