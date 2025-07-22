package com.github.omoflop.crazypainting.network;

import com.github.omoflop.crazypainting.network.types.ChangeKey;
import com.github.omoflop.crazypainting.network.types.PaintingId;

import java.util.Objects;

public record ChangeRecord(ChangeKey key, PaintingId id) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChangeRecord that = (ChangeRecord) o;
        return Objects.equals(id, that.id) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, id);
    }
}
