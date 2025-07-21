package com.github.omoflop.crazypainting.client;

import com.github.omoflop.crazypainting.client.datagen.CrazyModelProvider;
import com.github.omoflop.crazypainting.client.datagen.CrazyRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CrazyPaintingDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(CrazyModelProvider::new);
        pack.addProvider(CrazyRecipeProvider::new);
    }

}
