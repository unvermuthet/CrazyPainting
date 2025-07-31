package com.github.omoflop.crazypainting.content;

import com.github.omoflop.crazypainting.CrazyPainting;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

import java.util.Optional;

public class CrazySounds {
    public static final SoundEvent COLOR_PICKER_USE = register("color_picker_use");
    public static final SoundEvent BRUSH_USE = register("brush_use");
    public static final SoundEvent UNDO = register("undo");

    public static void register() { }

    private static SoundEvent register(String name, Optional<Float> optional) {
        SoundEvent soundEvent = new SoundEvent(CrazyPainting.id(name), optional);
        Registry.register(Registries.SOUND_EVENT, soundEvent.id(), soundEvent);
        return soundEvent;
    }

    private static SoundEvent register(String name) {
        return register(name, Optional.empty());
    }

}
