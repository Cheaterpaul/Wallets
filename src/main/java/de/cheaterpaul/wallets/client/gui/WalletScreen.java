package de.cheaterpaul.wallets.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.config.Config;
import de.cheaterpaul.wallets.inventory.ICoinChangeListener;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.ICoinContainer;
import de.cheaterpaul.wallets.network.InputEventPacket;
import de.cheaterpaul.wallets.network.TakeCoinPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static de.cheaterpaul.wallets.network.InputEventPacket.*;

public class WalletScreen extends AbstractContainerScreen<WalletContainer> implements ICoinChangeListener {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MOD_ID, "textures/gui/wallet.png");

    private EditBox sumField;
    private EditBox walletSum;

    public WalletScreen(WalletContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageHeight = 177;
        this.imageWidth = 188;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 8 + 6;
        this.menu.listen(this);
    }

    @Override
    protected void init() {
        super.init();
        ImageButton button = new ImageButton(this.getGuiLeft() + 18, this.getGuiTop() + 37, 11, 7, 202, 0, 7, BACKGROUND, 256, 256, this::walletApplyPressed);
        this.addRenderableWidget(button);
        ItemStack itemstack = menu.slots.get(0).container.getItem(0);
        int amount = 0;
        if (itemstack.getItem() instanceof ICoinContainer) {
            amount = ((ICoinContainer) itemstack.getItem()).getCoins(itemstack);
        }
        Component text = Component.translatable("text.wallets.transfer", amount);
        if (menu.getWalletAmount() + amount > 999999999) {
            text = Component.translatable("text.wallets.wallet_full").withStyle(ChatFormatting.RED);
        }
        button.setTooltip(Tooltip.create(text));

        var takeOne = this.addRenderableWidget(new AddWalletButton(this.getGuiLeft() + 52 + 20, this.getGuiTop() + 43, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, Component.empty(), CoinItem.CoinValue.ONE));
        takeOne.setTooltip(Tooltip.create(Component.translatable("text.wallets.take_coin", Component.translatable(CoinItem.CoinValue.ONE.getTranslation()))));
        var takeFive = this.addRenderableWidget(new AddWalletButton(this.getGuiLeft() + 70 + 20, this.getGuiTop() + 43, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, Component.empty(), CoinItem.CoinValue.FIVE));
        takeFive.setTooltip(Tooltip.create(Component.translatable("text.wallets.take_coin", Component.translatable(CoinItem.CoinValue.FIVE.getTranslation()))));
        var takeTen = this.addRenderableWidget(new AddWalletButton(this.getGuiLeft() + 88 + 20, this.getGuiTop() + 43, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, Component.empty(), CoinItem.CoinValue.TEN));
        takeTen.setTooltip(Tooltip.create(Component.translatable("text.wallets.take_coin", Component.translatable(CoinItem.CoinValue.TEN.getTranslation()))));
        var takeTwenty = this.addRenderableWidget(new AddWalletButton(this.getGuiLeft() + 106 + 20, this.getGuiTop() + 43, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, Component.empty(), CoinItem.CoinValue.TWENTY));
        takeTwenty.setTooltip(Tooltip.create(Component.translatable("text.wallets.take_coin", Component.translatable(CoinItem.CoinValue.TWENTY.getTranslation()))));
        var takeFifty = this.addRenderableWidget(new AddWalletButton(this.getGuiLeft() + 124 + 20, this.getGuiTop() + 43, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, Component.empty(), CoinItem.CoinValue.FIFTY));
        takeFifty.setTooltip(Tooltip.create(Component.translatable("text.wallets.take_coin", Component.translatable(CoinItem.CoinValue.FIFTY.getTranslation()))));
        var takeHundred = this.addRenderableWidget(new AddWalletButton(this.getGuiLeft() + 142 + 20, this.getGuiTop() + 43, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, Component.empty(), CoinItem.CoinValue.ONE_HUNDRED));
        takeHundred.setTooltip(Tooltip.create(Component.translatable("text.wallets.take_coin", Component.translatable(CoinItem.CoinValue.ONE_HUNDRED.getTranslation()))));
        var takeFiveHundred = this.addRenderableWidget(new AddWalletButton(this.getGuiLeft() + 160 + 20, this.getGuiTop() + 43, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletTakeCoinPressed, Component.empty(), CoinItem.CoinValue.FIVE_HUNDRED));
        takeFiveHundred.setTooltip(Tooltip.create(Component.translatable("text.wallets.take_coin", Component.translatable(CoinItem.CoinValue.FIVE_HUNDRED.getTranslation()))));

        this.addRenderableWidget(new TakeButton(this.getGuiLeft() + 142 + 2, this.getGuiTop() + 16, 14, 14, 188, 0, 14, BACKGROUND, 256, 256, this::walletSumCoinsPressed, Component.empty()));
        if (!Config.CONFIG.disableCoinPouch.get()) {
            this.addRenderableWidget(new PouchButton(this.getGuiLeft() + 162, this.getGuiTop() + 16, 14, 14, 213, 0, 14, BACKGROUND, 256, 256, this::walletCreateCoinPoach, Component.empty()));
        }
        this.sumField = new NumberOnlyTextFieldWidget(this.font, this.leftPos + 52, this.topPos + 19, 84, 9, Component.translatable("text.wallets.take_coin_sum"));
        this.sumField.setMaxLength(13);
        this.sumField.setValue("");
        this.sumField.setBordered(false);
        this.setInitialFocus(this.sumField);
        this.addWidget(this.sumField);
        this.walletSum = new NoEditTextFieldWidget(this.font, this.leftPos + 8, this.topPos + 63, 55, 10, Component.translatable("text.wallets.wallet_sum"));
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
    protected void containerTick() {
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
        WalletsMod.dispatcher.sentToServer(new TakeCoinPacket(button.getValue(), hasShiftDown() ? 64 : 1));
    }

    private void walletSumCoinsPressed(Button b) {
        getSum().ifPresent(value -> {
            if (value <= this.menu.getWalletAmount()) {
                WalletsMod.dispatcher.sentToServer(new InputEventPacket(TAKE_COINS, value.toString()));
            }
        });
    }

    @Override
    public void renderSlot(@Nonnull GuiGraphics graphics, @Nonnull Slot slot) {
        if (slot instanceof WalletContainer.TakeOnlySlot) {
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            graphics.setColor(1, 1, 1, 0.2f);
            RenderSystem.colorMask(true, true, true, true);
            TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(((WalletContainer.TakeOnlySlot) slot).getTexture());
            graphics.blit(slot.x, slot.y, 0, 16, 16, sprite);
            graphics.setColor(1, 1, 1, 1f);

        }
        super.renderSlot(graphics, slot);
    }

    private void walletCreateCoinPoach(Button b) {
        getSum().ifPresent(value -> {
            if (value <= this.menu.getWalletAmount()) {
                WalletsMod.dispatcher.sentToServer(new InputEventPacket(CREATE_POUCH, value.toString()));
            }
        });

    }

    private Optional<Long> getSum() {
        try {
            String val = this.sumField.getValue();
            long value = val.equals("") ? 0 : Long.parseLong(val);
            return Optional.of(value);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        graphics.blit(BACKGROUND, this.getGuiLeft(), this.getGuiTop(), 0, 0, 0, this.getXSize(), this.getYSize(), 256, 256);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.walletSum.setValue(String.valueOf(this.menu.getWalletAmount()));
        this.walletSum.render(graphics, mouseX, mouseY, partialTicks);
        this.sumField.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void coinsChanged() {
        this.walletSum.setValue(String.valueOf(this.menu.getWalletAmount()));
    }

    private static class AddWalletButton extends ImageButton {

        private final CoinItem.CoinValue value;

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, OnPress pressable, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, pressable);
            this.value = value;
            this.setTooltip(Tooltip.create(Component.translatable("text.wallets.take", value.getValue())));
        }

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, int textureWidth, int textureHeight, OnPress pressable, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, textureWidth, textureHeight, pressable);
            this.value = value;
        }

        public AddWalletButton(int xPos, int yPos, int xSize, int ySize, int xTexPos, int yTexPos, int yTexDiff, ResourceLocation texture, int textureWidth, int textureHeight, OnPress pressable, Component name, CoinItem.CoinValue value) {
            super(xPos, yPos, xSize, ySize, xTexPos, yTexPos, yTexDiff, texture, textureWidth, textureHeight, pressable, name);
            this.value = value;
        }

        public CoinItem.CoinValue getValue() {
            return value;
        }
    }

    public static class NoEditTextFieldWidget extends EditBox {

        public NoEditTextFieldWidget(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, Component p_i232260_6_) {
            super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        }

        public NoEditTextFieldWidget(Font p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable EditBox p_i232259_6_, Component p_i232259_7_) {
            super(p_i232259_1_, p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_6_, p_i232259_7_);
        }

        @Override
        public boolean isFocused() {
            return false;
        }
    }

    private class PouchButton extends ImageButton {

        public PouchButton(int p_94256_, int p_94257_, int p_94258_, int p_94259_, int p_94260_, int p_94261_, int p_94262_, ResourceLocation p_94263_, int p_94264_, int p_94265_, OnPress p_94266_, Component p_94267_) {
            super(p_94256_, p_94257_, p_94258_, p_94259_, p_94260_, p_94261_, p_94262_, p_94263_, p_94264_, p_94265_, p_94266_, p_94267_);
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics graphics, int p_267992_, int p_267950_, float p_268076_) {
            super.renderWidget(graphics, p_267992_, p_267950_, p_268076_);
            getSum().ifPresent(value -> {
                this.setTooltip(Tooltip.create(Component.translatable("text.wallets.create_pouch", value)));
            });
        }
    }

    private class TakeButton extends ImageButton {

        public TakeButton(int p_94256_, int p_94257_, int p_94258_, int p_94259_, int p_94260_, int p_94261_, int p_94262_, ResourceLocation p_94263_, int p_94264_, int p_94265_, OnPress p_94266_, Component p_94267_) {
            super(p_94256_, p_94257_, p_94258_, p_94259_, p_94260_, p_94261_, p_94262_, p_94263_, p_94264_, p_94265_, p_94266_, p_94267_);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int p_267992_, int p_267950_, float p_268076_) {
            super.renderWidget(graphics, p_267992_, p_267950_, p_268076_);
                getSum().ifPresent(value -> {
                    this.setTooltip(Tooltip.create(Component.translatable("text.wallets.take", value)));
                });
        }
    }

    private class NumberOnlyTextFieldWidget extends EditBox {

        public NumberOnlyTextFieldWidget(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, Component p_i232260_6_) {
            super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        }

        public NumberOnlyTextFieldWidget(Font p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable EditBox p_i232259_6_, Component p_i232259_7_) {
            super(p_i232259_1_, p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_6_, p_i232259_7_);
        }

        @Override
        public void insertText(@Nonnull String input) {
            try {
                int value = Integer.parseInt(input);
                super.insertText(input);
                if (Long.parseLong(this.getValue()) > menu.getWalletAmount()) {
                    this.setTextColor(16733525);
                } else {
                    this.setTextColor(14737632);
                }
            } catch (NumberFormatException ignored) {

            }
        }

        @Override
        public void deleteChars(int p_146175_1_) {
            super.deleteChars(p_146175_1_);
            try {
                if (Long.parseLong(this.getValue()) > menu.getWalletAmount()) {
                    this.setTextColor(16733525);
                } else {
                    this.setTextColor(14737632);
                }
            } catch (NumberFormatException ignored) {
                this.setTextColor(14737632);
            }
        }

        @Override
        public void deleteWords(int p_146177_1_) {
            super.deleteWords(p_146177_1_);
            try {
                if (Long.parseLong(this.getValue()) > menu.getWalletAmount()) {
                    this.setTextColor(16733525);
                } else {
                    this.setTextColor(14737632);
                }
            } catch (NumberFormatException ignored) {
                this.setTextColor(14737632);
            }
        }
    }
}
