package de.cheaterpaul.wallets.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.WalletItem;
import de.cheaterpaul.wallets.network.InputEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        this.addButton(new ImageButton(this.getGuiLeft() + 18, this.getGuiTop() + 37, 11, 7, 190, 0, 7, BACKGROUND, 256, 256, this::walletApplyPressed, new Button.ITooltip() {
            @Override
            public void onTooltip(@Nonnull Button button, @Nonnull MatrixStack stack, int mouseX, int mouseY) {
                int amount = 0;
                ItemStack itemstack = menu.slots.get(0).container.getItem(0);
                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() instanceof CoinItem) {
                        amount = itemstack.getCount() * ((CoinItem) itemstack.getItem()).getValue();
                    } else if (itemstack.getItem() instanceof WalletItem) {
                        amount = WalletItem.getCoinValue(itemstack);
                    }
                }
                renderTooltip(stack, new TranslationTextComponent("text.wallets.transfer", amount), mouseX, mouseY);
            }
        }, StringTextComponent.EMPTY));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 52, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, (button, stack, mouseX, mouseY) -> toolTip(button, stack, mouseX, mouseY, CoinItem.CoinValue.ONE), StringTextComponent.EMPTY, CoinItem.CoinValue.ONE));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 70, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, (button, stack, mouseX, mouseY) -> toolTip(button, stack, mouseX, mouseY, CoinItem.CoinValue.FIVE), StringTextComponent.EMPTY, CoinItem.CoinValue.FIVE));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 88, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, (button, stack, mouseX, mouseY) -> toolTip(button, stack, mouseX, mouseY, CoinItem.CoinValue.TWENTY), StringTextComponent.EMPTY, CoinItem.CoinValue.TWENTY));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 106, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, (button, stack, mouseX, mouseY) -> toolTip(button, stack, mouseX, mouseY, CoinItem.CoinValue.FIFTY), StringTextComponent.EMPTY, CoinItem.CoinValue.FIFTY));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 124, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, (button, stack, mouseX, mouseY) -> toolTip(button, stack, mouseX, mouseY, CoinItem.CoinValue.ONE_HUNDRED), StringTextComponent.EMPTY, CoinItem.CoinValue.ONE_HUNDRED));
        this.addButton(new AddWalletButton(this.getGuiLeft() + 142, this.getGuiTop() + 43, 14, 14, 176, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, (button, stack, mouseX, mouseY) -> toolTip(button, stack, mouseX, mouseY, CoinItem.CoinValue.FIVE_HUNDRED), StringTextComponent.EMPTY, CoinItem.CoinValue.FIVE_HUNDRED));
        this.addButton(new ImageButton(this.getGuiLeft() + 142, this.getGuiTop() + 16, 14, 14, 176, 0, 14, BACKGROUND, 256, 256, this::walletSumCoinsPressed, (button, stack, mouseX, mouseY) -> {
            try {
                int amount = sumField.getValue().equals("") ? 0 : Integer.parseInt(sumField.getValue());
                renderTooltip(stack, new TranslationTextComponent("text.wallets.take", amount), mouseX, mouseY);
            } catch (NumberFormatException ignored) {

            }
        }, StringTextComponent.EMPTY));
        this.sumField = new NumberOnlyTextFieldWidget(this.font, this.leftPos + 51, this.topPos + 18, 84, 9, new TranslationTextComponent("text.wallets.take_coin_sum"));
        this.sumField.setMaxLength(50);
        this.sumField.setValue("");
        this.sumField.setBordered(false);
        this.setInitialFocus(this.sumField);
        this.addWidget(this.sumField);
        this.walletSum = new NoEditTextFieldWidget(this.font, this.leftPos + 8, this.topPos + 63, 32, 10, new TranslationTextComponent("text.wallets.wallet_sum"));
        this.walletSum.setMaxLength(10);
        this.walletSum.setBordered(false);
        this.walletSum.setEditable(false);
        this.walletSum.setTextColorUneditable(14737632);
        this.walletSum.setValue(String.valueOf(this.menu.getWalletAmount()));
        this.addWidget(this.walletSum);
    }

    @Override
    public void resize(@Nonnull Minecraft minecraft, int xSize, int ySize) {
        String walletAmount = this.walletSum.getValue();
        String sumAmount = this.sumField.getValue();
        super.resize(minecraft, xSize, ySize);
        this.walletSum.setValue(walletAmount);
        this.sumField.setValue(sumAmount);
    }

    @Override
    public void tick() {
        super.tick();
        this.sumField.tick();
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    private void walletApplyPressed(Button b) {
        WalletsMod.dispatcher.sentToServer(new InputEventPacket(INSERT_COIN));
    }

    private void walletTakeCoinPressed(Button b) {
        AddWalletButton button = ((AddWalletButton) b);
        WalletsMod.dispatcher.sentToServer(new InputEventPacket(TAKE_COIN, button.getValue().toString()));

    }

    private void walletSumCoinsPressed(Button b) {
        int value = Integer.parseInt(this.sumField.getValue());
        if (value > this.menu.getWalletAmount()) {
            this.sumField.setTextColor(Color.parseColor("RED").getValue());
        } else {
            WalletsMod.dispatcher.sentToServer(new InputEventPacket(TAKE_COINS, this.sumField.getValue()));
        }
    }

    private void toolTip(Button button, MatrixStack stack, int mouseX, int mouseY, CoinItem.CoinValue amount) {
        renderTooltip(stack, new TranslationTextComponent("text.wallets.take", amount.getValue()), mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack stack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(stack, this.getGuiLeft(), this.getGuiTop(), this.getBlitOffset(), 0, 0, this.getXSize(), this.getYSize(), 256, 256);
    }

    @Override
    public void render(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.walletSum.setValue(String.valueOf(this.menu.getWalletAmount()));
        this.walletSum.render(stack, mouseX, mouseY, partialTicks);
        this.sumField.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    private static class AddWalletButton extends ImageButton {

        private final CoinItem.CoinValue value;

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, IPressable pressable, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, pressable);
            this.value = value;
        }

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, int textureWidth, int textureHeight, IPressable pressable, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, textureWidth, textureHeight, pressable);
            this.value = value;
        }

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, int textureWidth, int textureHeight, IPressable pressable, ITextComponent name, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, textureWidth, textureHeight, pressable, name);
            this.value = value;
        }

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, int textureWidth, int textureHeight, IPressable pressable, ITooltip tooltip, ITextComponent name, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, textureWidth, textureHeight, pressable, tooltip, name);
            this.value = value;
        }

        public CoinItem.CoinValue getValue() {
            return value;
        }
    }

    private static class NumberOnlyTextFieldWidget extends TextFieldWidget {

        public NumberOnlyTextFieldWidget(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_) {
            super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        }

        public NumberOnlyTextFieldWidget(FontRenderer p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable TextFieldWidget p_i232259_6_, ITextComponent p_i232259_7_) {
            super(p_i232259_1_, p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_6_, p_i232259_7_);
        }

        @Override
        public void insertText(@Nonnull String input) {
            try {
                this.setTextColor(14737632);
                int value = Integer.parseInt(input);
                super.insertText(input);
            } catch (NumberFormatException ignored) {

            }
        }
    }

    public static class NoEditTextFieldWidget extends TextFieldWidget {

        public NoEditTextFieldWidget(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_) {
            super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        }

        public NoEditTextFieldWidget(FontRenderer p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable TextFieldWidget p_i232259_6_, ITextComponent p_i232259_7_) {
            super(p_i232259_1_, p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_6_, p_i232259_7_);
        }

        @Override
        public boolean isFocused() {
            return false;
        }
    }
}
