package io.github.winx64.sse.player;

import io.github.winx64.sse.tool.ToolCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of SmartPlayer
 *
 * @author WinX64
 */
public final class SmartPlayerImpl implements SmartPlayer {

    private final Player player;

    private ToolCategory selectedCategory;

    private String lineBuffer;
    private List<String> signBuffer;

    public SmartPlayerImpl(Player player, ToolCategory selectedCategory) {
        this.player = player;

        this.selectedCategory = selectedCategory;

        this.lineBuffer = null;
        this.signBuffer = null;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull ToolCategory getSelectedToolCategory() { return selectedCategory; }

    @Override
    public void setSelectedToolCategory(@NotNull ToolCategory toolCategory) {
        this.selectedCategory = Objects.requireNonNull(toolCategory);
    }

    @Override
    public String getLineBuffer() {
        return lineBuffer;
    }

    @Override
    public void setLineBuffer(String lineBuffer) {
        this.lineBuffer = lineBuffer;
    }

    @Override
    public List<String> getSignBuffer() {
        return signBuffer;
    }

    @Override
    public void setSignBuffer(List<String> signBuffer) {
        if (signBuffer == null) {
            this.signBuffer = null;
            return;
        }
        if (signBuffer.size() != 4) {
            throw new IllegalArgumentException("signBuffer must contain 4 elements");
        }
        this.signBuffer = Collections.unmodifiableList(new ArrayList<>(signBuffer));
    }
}
