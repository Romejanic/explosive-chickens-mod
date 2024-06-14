package com.jackd.exchickens.items;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public abstract class ItemFake extends Item {

    private Identifier fakingId;
    private String translationKey;

    public ItemFake(Settings settings, Identifier fakingId) {
        super(settings);
        this.fakingId = fakingId;
        this.translationKey = Util.createTranslationKey("item", fakingId);
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public Identifier getOriginal() {
        return this.fakingId;
    }

}
