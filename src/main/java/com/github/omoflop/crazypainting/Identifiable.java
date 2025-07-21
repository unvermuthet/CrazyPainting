package com.github.omoflop.crazypainting;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface Identifiable {
    Identifier getId();
    static RegistryKey<Item> key(String registryName) {
        return RegistryKey.of(RegistryKeys.ITEM, CrazyPainting.id(registryName));
    }
}
