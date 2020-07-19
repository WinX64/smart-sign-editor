package io.github.winx64.sse.configuration;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class SignConfiguration extends BaseConfiguration {

    private static final int CONFIG_VERSION = 3;

    private ItemStack toolItem;
    private boolean matchName;
    private boolean matchLore;

    private boolean usingExtendedTool;
    private int extendedToolReach;

    private Set<String> specialSigns;

    private ToolUsage editToolUsage;

    private ToolUsage signCopyToolUsage;
    private ToolUsage lineCopyToolUsage;

    private ToolUsage signPasteToolUsage;
    private ToolUsage linePasteToolUsage;

    private ToolUsage signEraseToolUsage;
    private ToolUsage lineEraseToolUsage;

    public SignConfiguration(SmartSignEditor plugin) {
        super(plugin, "Config", "config.yml", "config-version", CONFIG_VERSION);

        this.toolItem = new ItemStack(Material.FEATHER);
        this.matchName = false;
        this.matchLore = false;

        this.usingExtendedTool = true;
        this.extendedToolReach = 10;

        this.specialSigns = new HashSet<>();

        this.editToolUsage = ToolUsage.RIGHT_CLICK;

        this.signCopyToolUsage = ToolUsage.NO_SHIFT_RIGHT_CLICK;
        this.lineCopyToolUsage = ToolUsage.SHIFT_RIGHT_CLICK;

        this.signPasteToolUsage = ToolUsage.NO_SHIFT_RIGHT_CLICK;
        this.linePasteToolUsage = ToolUsage.SHIFT_RIGHT_CLICK;

        this.signEraseToolUsage = ToolUsage.NO_SHIFT_RIGHT_CLICK;
        this.lineEraseToolUsage = ToolUsage.SHIFT_RIGHT_CLICK;
    }

    public ItemStack getToolItem() {
        return toolItem.clone();
    }

    public boolean matchesItem(ItemStack target) {
        if (target.getType() != this.toolItem.getType()) {
            return false;
        }

        if (matchName) {
            String targetName = target.getItemMeta().getDisplayName();
            String toolItemName = this.toolItem.getItemMeta().getDisplayName();

            if (targetName != null && toolItemName != null && !targetName.equalsIgnoreCase(toolItemName)) {
                return false;
            }
            if ((targetName == null) ^ (toolItemName == null)) {
                return false;
            }
        }
        if (matchLore) {
            List<String> targetLore = target.getItemMeta().getLore();
            List<String> toolItemLore = this.toolItem.getItemMeta().getLore();

            if ((targetLore != null && toolItemLore != null && !targetLore.equals(toolItemLore))) {
                return false;
            }
            return (targetLore == null) == (toolItemLore == null);
        }
        return true;
    }

    public boolean isUsingExtendedTool() {
        return usingExtendedTool;
    }

    public int getExtendedToolReach() {
        return extendedToolReach;
    }

    public boolean isSpecialSign(String text) {
        return specialSigns.contains(text.toLowerCase());
    }

    public ToolUsage getEditToolUsage() {
        return editToolUsage;
    }

    public ToolUsage getSignCopyToolUsage() {
        return signCopyToolUsage;
    }

    public ToolUsage getLineCopyToolUsage() {
        return lineCopyToolUsage;
    }

    public ToolUsage getSignPasteToolUsage() {
        return signPasteToolUsage;
    }

    public ToolUsage getLinePasteToolUsage() {
        return linePasteToolUsage;
    }

    public ToolUsage getSignEraseToolUsage() {
        return signEraseToolUsage;
    }

    public ToolUsage getLineEraseToolUsage() {
        return lineEraseToolUsage;
    }

    private void loadToolItem(ConfigurationSection config) {
        if (config == null) {
            plugin.log(Level.WARNING, "[Config] SubTool item section does not exist!");
            return;
        }

        ItemStack toolItem = config.getItemStack("tool", null);
        if (toolItem == null) {
            plugin.log(Level.WARNING, "[Config] Couldn't find or parse the tool item. Using default FEATHER");
        } else if (toolItem.getType() == Material.AIR) {
            plugin.log(Level.WARNING, "[Config] AIR is not a valid item for the tool item. Using default FEATHER");
        } else {
            this.toolItem = toolItem;
        }

        this.matchName = config.getBoolean("match-name", false);
        this.matchLore = config.getBoolean("match-lore", false);
    }

    private void loadExtendedTool(ConfigurationSection config) {
        if (config == null) {
            plugin.log(Level.WARNING, "[Config] Extended tool section does not exist!");
            return;
        }

        this.usingExtendedTool = config.getBoolean("enabled", true);
        this.extendedToolReach = config.getInt("reach", 10);
        if (extendedToolReach < 1) {
            plugin.log(Level.WARNING, "[Config] The extended tool reach must be of at least 1. Defaulting it to 10");
            this.extendedToolReach = 10;
        }
    }

    private void loadToolUsages(ConfigurationSection config) {
        if (config == null || config.getKeys(false).isEmpty()) {
            plugin.log(Level.WARNING, "[Config] SubTool usage section does not exist.");
            return;
        }

        this.editToolUsage = loadToolUsage(config, "edit.sign-edit", ToolUsage.RIGHT_CLICK);

        this.signCopyToolUsage = loadToolUsage(config, "copy.sign-copy", ToolUsage.NO_SHIFT_RIGHT_CLICK);
        this.lineCopyToolUsage = loadToolUsage(config, "copy.line-copy", ToolUsage.SHIFT_RIGHT_CLICK);

        this.signPasteToolUsage = loadToolUsage(config, "paste.sign-paste", ToolUsage.NO_SHIFT_RIGHT_CLICK);
        this.linePasteToolUsage = loadToolUsage(config, "paste.line-paste", ToolUsage.SHIFT_RIGHT_CLICK);

        this.signEraseToolUsage = loadToolUsage(config, "erase.sign-erase", ToolUsage.NO_SHIFT_RIGHT_CLICK);
        this.lineEraseToolUsage = loadToolUsage(config, "erase.line-erase", ToolUsage.SHIFT_RIGHT_CLICK);
    }

    private ToolUsage loadToolUsage(ConfigurationSection config, String path, ToolUsage defaultValue) {
        String usageValue = config.getString(path);
        if (usageValue == null) {
            plugin.log(Level.WARNING, "[Config] Tool usage \"%s\" not found. Using default value %s", path,
                    ToolUsage.NO_SHIFT_RIGHT_CLICK);
            usageValue = defaultValue.name();
        }

        ToolUsage usage = ToolUsage.getToolUsage(usageValue);
        if (usage == null) {
            plugin.log(Level.WARNING, "[Config] Invalid tool usage \"%s\". Using default value %s", usageValue,
                    ToolUsage.NO_SHIFT_RIGHT_CLICK);
            usage = defaultValue;
        }

        return usage;
    }

    @Override
    protected void prepareConfiguration() {
        loadToolItem(config.getConfigurationSection("tool-item"));

        loadToolUsages(config.getConfigurationSection("tool-usages"));

        loadExtendedTool(config.getConfigurationSection("extended-tool"));

        List<String> specialSigns = config.getStringList("special-signs");
        if (specialSigns.isEmpty()) {
            plugin.log(Level.WARNING, "[Config] No special signs detected!");
        } else {
            for (String sign : specialSigns) {
                this.specialSigns.add(sign.toLowerCase());
            }
        }
    }
}
