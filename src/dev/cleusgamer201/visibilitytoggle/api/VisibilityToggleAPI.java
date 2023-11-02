package dev.cleusgamer201.visibilitytoggle.api;

import java.text.DecimalFormat;
import java.util.List;

import dev.cleusgamer201.visibilitytoggle.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VisibilityToggleAPI {

    private final Main plugin;

    private VisibilityToggleAPI() {
        this.plugin = Main.getInstance();
    }

    private static VisibilityToggleAPI instance;

    public static VisibilityToggleAPI getInstance() {
        if (instance == null) {
            instance = new VisibilityToggleAPI();
        }
        return instance;
    }

    public void reloadConfigAndVisibility() {
        if (plugin.isToggleItem()) {
            int slot = plugin.getItemSlot();
            for (Player all : Bukkit.getOnlinePlayers()) {
                ItemStack item = all.getInventory().getItem(slot);
                if (item != null && (item.isSimilar(plugin.getOnItem()) || item.isSimilar(plugin.getRankItem())) || item.isSimilar(plugin.getOffItem())) {
                    all.getInventory().clear(slot);
                }
            }
        }
        plugin.loadConfigValues();
        Bukkit.getOnlinePlayers().forEach(plugin::updateVisibility);
    }

    public void refreshVisibility(Player player) {
        plugin.updateVisibility(player);
    }

    public void updateVisibilityFor(Player player, Player anotherPlayer) {
        plugin.showHide(player, anotherPlayer);
    }

    public void toggleVisibility(Player player) {
        plugin.toggleVisibility(player);
    }

    public void toggleVisibility(Player player, Visibility visibility) {
        plugin.toggleVisibility(player, visibility);
    }

    public void toggleVisibility(Player player, Visibility visibility, boolean ignoreDelay) {
        plugin.toggleVisibility(player, visibility, ignoreDelay);
    }

    public void forceToggleVisibility(Player player) {
        plugin.toggleVisibility(player);
    }

    public void forceToggleVisibility(Player player, Visibility visibility) {
        plugin.toggleVisibility(player, visibility);
    }

    public Visibility getVisibilityOf(Player player) {
        return plugin.getDbManager().getCache(player).getVisibility();
    }

    public boolean isInEnableWorld(Player player) {
        return plugin.isInEnabledWorld(player);
    }

    public DecimalFormat getDecimalFormat() {
        return plugin.getDecimalFormat();
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        plugin.setDecimalFormat(decimalFormat);
    }

    public double getToggleDelay() {
        return plugin.getToggleDelay();
    }

    public void setToggleDelay(double delay) {
        plugin.setToggleDelay(delay);
    }

    public int getItemSlot() {
        return plugin.getItemSlot();
    }

    public void setItemSlot(int itemSlot) {
        plugin.setItemSlot(itemSlot);
    }

    public boolean isClearInvOnJoin() {
        return plugin.isClearInvOnJoin();
    }

    public void setClearInvOnJoin(boolean clearInvOnJoin) {
        plugin.setClearInvOnJoin(clearInvOnJoin);
    }

    public boolean isJoinToggleMessage() {
        return plugin.isJoinToggleMessage();
    }

    public void setJoinToggleMessage(boolean joinToggleMessage) {
        plugin.setJoinToggleMessage(joinToggleMessage);
    }

    public boolean isShowToggleMessages() {
        return plugin.isShowToggleMessages();
    }

    public void setShowToggleMessages(boolean showToggleMessages) {
        plugin.setShowToggleMessages(showToggleMessages);
    }

    public boolean isToggleSound() {
        return plugin.isToggleSound();
    }

    public void setToggleSound(boolean toggleSound) {
        plugin.setToggleSound(toggleSound);
    }

    public boolean isBlockItemDrop() {
        return plugin.isBlockItemDrop();
    }

    public void setBlockItemDrop(boolean blockItemDrop) {
        plugin.setBlockItemDrop(blockItemDrop);
    }

    public boolean isGiveItemOnRespawn() {
        return plugin.isGiveItemOnRespawn();
    }

    public void setGiveItemOnRespawn(boolean giveItemOnRespawn) {
        plugin.setGiveItemOnRespawn(giveItemOnRespawn);
    }

    public boolean isClearInvOnWorldChange() {
        return plugin.isClearInvOnWorldChange();
    }

    public void setClearInvOnWorldChange(boolean clearInvOnWorldChange) {
        plugin.setClearInvOnWorldChange(clearInvOnWorldChange);
    }

    public boolean isBlockItemMove() {
        return plugin.isBlockItemMove();
    }

    public void setBlockItemMove(boolean blockItemMove) {
        plugin.setBlockItemMove(blockItemMove);
    }

    public boolean isToggleItem() {
        return plugin.isToggleItem();
    }

    public void setToggleItem(boolean toggleItem) {
        plugin.setToggleItem(toggleItem);
    }

    public boolean isShowToggleTitles() {
        return plugin.isShowToggleTitles();
    }

    public void setShowToggleTitles(boolean showToggleTitles) {
        plugin.setShowToggleTitles(showToggleTitles);
    }

    public String getToggleDelayMessage() {
        return plugin.getToggleDelayMessage();
    }

    public void setToggleDelayMessage(String toggleDelayMessage) {
        plugin.setToggleDelayMessage(toggleDelayMessage);
    }

    public String getToggleShowMessage() {
        return plugin.getToggleShowMessage();
    }

    public void setToggleShowMessage(String toggleShowMessage) {
        plugin.setToggleShowMessage(toggleShowMessage);
    }

    public String getToggleRankMessage() {
        return plugin.getToggleRankMessage();
    }

    public void setToggleRankMessage(String toggleRankMessage) {
        plugin.setToggleRankMessage(toggleRankMessage);
    }

    public String getToggleHideMessage() {
        return plugin.getToggleHideMessage();
    }

    public void setToggleHideMessage(String toggleHideMessage) {
        plugin.setToggleHideMessage(toggleHideMessage);
    }

    public String getToggleShowTitle() {
        return plugin.getToggleShowTitle();
    }

    public void setToggleShowTitle(String toggleShowTitle) {
        plugin.setToggleShowTitle(toggleShowTitle);
    }

    public String getToggleShowSubtitle() {
        return plugin.getToggleShowSubtitle();
    }

    public void setToggleShowSubtitle(String toggleShowSubtitle) {
        plugin.setToggleShowSubtitle(toggleShowSubtitle);
    }

    public String getToggleRankTitle() {
        return plugin.getToggleRankTitle();
    }

    public void setToggleRankTitle(String toggleRankTitle) {
        plugin.setToggleRankTitle(toggleRankTitle);
    }

    public String getToggleRankSubtitle() {
        return plugin.getToggleRankSubtitle();
    }

    public void setToggleRankSubtitle(String toggleRankSubtitle) {
        plugin.setToggleRankSubtitle(toggleRankSubtitle);
    }

    public String getToggleOffTitle() {
        return plugin.getToggleOffTitle();
    }

    public void setToggleOffTitle(String toggleOffTitle) {
        plugin.setToggleOffTitle(toggleOffTitle);
    }

    public String getToggleOffSubtitle() {
        return plugin.getToggleOffSubtitle();
    }

    public void setToggleOffSubtitle(String toggleOffSubtitle) {
        plugin.setToggleOffSubtitle(toggleOffSubtitle);
    }

    public Sound getSound() {
        return plugin.getSound();
    }

    public void setSound(Sound sound) {
        plugin.setSound(sound);
    }

    public float getSoundVol() {
        return plugin.getSoundVol();
    }

    public void setSoundVol(float soundVol) {
        plugin.setSoundVol(soundVol);
    }

    public float getSoundPitch() {
        return plugin.getSoundPitch();
    }

    public void setSoundPitch(float soundPitch) {
        plugin.setSoundPitch(soundPitch);
    }

    public ItemStack getOnItem() {
        return plugin.getOnItem();
    }

    public void setOnItem(ItemStack onItem) {
        plugin.setOnItem(onItem);
    }

    public ItemStack getRankItem() {
        return plugin.getRankItem();
    }

    public void setRankItem(ItemStack rankItem) {
        plugin.setRankItem(rankItem);
    }

    public ItemStack getOffItem() {
        return plugin.getOffItem();
    }

    public void setOffItem(ItemStack offItem) {
        plugin.setOffItem(offItem);
    }

    public List<String> getEnabledWorlds() {
        return plugin.getEnabledWorlds();
    }

    public void setEnabledWorlds(List<String> enabledWorlds) {
        plugin.setEnabledWorlds(enabledWorlds);
    }
}
