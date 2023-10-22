package net.qf.impl.entity.anim;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.math.AffineTransformation;
import org.jetbrains.annotations.NotNull;

public class AnimationContainer {
    @NotNull
    private final Object2ObjectOpenHashMap<String, Int2ObjectMap<AffineTransformation>> container;
    public final int endTick;

    public AnimationContainer(@NotNull Object2ObjectOpenHashMap<String, Int2ObjectMap<AffineTransformation>> container, int endTick) {
        this.container = container;
        this.endTick = endTick;
    }

    public Int2ObjectMap<AffineTransformation> getContainerByName(String s) {
        return container.get(s);
    }

    public AffineTransformation getContainerTick(String s, int i) {
        return container.get(s).get(i);
    }
}
