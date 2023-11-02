package dev.cleusgamer201.visibilitytoggle;

import dev.cleusgamer201.visibilitytoggle.api.PlayerVisibilityChangedEvent;
import dev.cleusgamer201.visibilitytoggle.api.PlayerVisibilityUpdatedEvent;
import dev.cleusgamer201.visibilitytoggle.api.Visibility;
import dev.cleusgamer201.visibilitytoggle.database.Cache;
import dev.cleusgamer201.visibilitytoggle.database.CacheLoadEvent;
import dev.cleusgamer201.visibilitytoggle.database.DBManager;
import dev.cleusgamer201.visibilitytoggle.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    private static String prefix;
    public static String getPrefix() {
        return prefix;
    }

    private Config config;
    private DBManager dbManager;

    private DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private double toggleDelay;
    private int itemSlot;
    private boolean clearInvOnJoin, joinToggleMessage, showToggleMessages, showToggleTitles, toggleSound, blockItemDrop, giveItemOnRespawn,
            clearInvOnWorldChange, blockItemMove, toggleItem;
    private String toggleDelayMessage, toggleShowMessage, toggleRankMessage, toggleHideMessage, toggleShowTitle, toggleShowSubtitle,
            toggleRankTitle, toggleRankSubtitle, toggleOffTitle, toggleOffSubtitle;
    private Sound sound;
    private float soundVol, soundPitch;
    private ItemStack onItem, rankItem, offItem;
    private List<String> enabledWorlds;

    @Override
    public void onEnable() {
        instance = this;
        prefix = Utils.color("&bVisibilityToggle&7>> &r");
        config = new Config("Config");
        loadConfigValues();
        dbManager = new DBManager(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("visibilitytoggle").setExecutor(new Commands(this));
        Utils.log("&aPlugin Enabled!");
        prefix = Utils.color(config.getString("Prefix"));
    }

    @Override
    public void onDisable() {
        prefix = Utils.color("&bVisibilityToggle&7>> &r");
        dbManager.shutdown();
        Utils.log("&cPlugin Disabled!");
    }

    public void loadConfigValues() {
        toggleDelay = config.getDouble("Settings.Delay");
        itemSlot = config.getInt("Settings.Slot") - 1;
        clearInvOnJoin = config.getBoolean("Settings.ClearInvOnJoin");
        joinToggleMessage = config.getBoolean("Settings.JoinToggleMessage");
        showToggleMessages = config.getBoolean("Settings.ShowToggleMessages");
        showToggleTitles = config.getBoolean("Settings.ShowToggleTitles");
        toggleSound = config.getBoolean("Settings.ToggleSound.Enabled");
        blockItemDrop = config.getBoolean("Settings.BlockDrop");
        giveItemOnRespawn = config.getBoolean("Settings.GiveOnRespawn");
        clearInvOnWorldChange = config.getBoolean("Settings.ClearInvOnWorldChange");
        blockItemMove = config.getBoolean("Settings.BlockMovement");
        toggleItem = config.getBoolean("Settings.ToggleItem");
        toggleDelayMessage = Utils.color(config.getString("Messages.Delay"));
        toggleShowMessage = Utils.color(config.getString("Messages.Show"));
        toggleRankMessage = Utils.color(config.getString("Messages.Rank"));
        toggleHideMessage = Utils.color(config.getString("Messages.Hide"));
        toggleShowTitle = Utils.color(config.getString("Titles.Show.Title"));
        toggleShowSubtitle = Utils.color(config.getString("Titles.Show.Subtitle"));
        toggleRankTitle = Utils.color(config.getString("Titles.Rank.Title"));
        toggleRankSubtitle = Utils.color(config.getString("Titles.Rank.Subtitle"));
        toggleOffTitle = Utils.color(config.getString("Titles.Hide.Title"));
        toggleOffSubtitle = Utils.color(config.getString("Titles.Hide.Subtitle"));
        enabledWorlds = config.getStringList("Settings.EnabledWorlds");
        enabledWorlds.replaceAll(String::toLowerCase);
        sound = Sounds.valueOf(config.getString("Settings.ToggleSound.Sound")).bukkitSound();
        soundVol = (float) config.getDouble("Settings.ToggleSound.Vol");
        soundPitch = (float) config.getDouble("Settings.ToggleSound.Pitch");
        onItem = buildItem("Show").toItemStack();
        rankItem = buildItem("Rank").toItemStack();
        offItem = buildItem("Hide").toItemStack();
    }

    private ItemBuilder buildItem(String s) {
        return new ItemBuilder(config.getString("Items." + s + ".Item")).setName(config.getString("Items." + s + ".Name")).setLore(config.getStringList("Items." + s + ".Lore"));
    }


    public Config getConfig() {
        return config;
    }


    public DBManager getDbManager() {
        return dbManager;
    }

    public double getToggleDelay() {
        return toggleDelay;
    }

    public void setToggleDelay(double delay) {
        this.toggleDelay = delay;
    }

    public boolean isInEnabledWorld(Player player) {
        String worldName = player.getWorld().getName();
        return enabledWorlds.contains(worldName.toLowerCase());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (clearInvOnJoin) {
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onCacheLoad(CacheLoadEvent event) {
        Player player = event.getPlayer();
        Cache cache = event.getCache();
        updateVisibility(player);
        if (joinToggleMessage) {
            switch (cache.getVisibility()) {
                case SHOW_ALL:
                    player.sendMessage(toggleShowMessage);
                    break;
                case RANK_ONLY:
                    player.sendMessage(toggleRankMessage);
                    break;
                case HIDE_ALL:
                    player.sendMessage(toggleHideMessage);
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (blockItemDrop) {
            ItemStack item = e.getItemDrop().getItemStack();
            if (item.hasItemMeta() && isInEnabledWorld(player)) {
                if (item.isSimilar(onItem) || item.isSimilar(rankItem) || item.isSimilar(offItem)) {
                    e.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (giveItemOnRespawn) {
            Bukkit.getScheduler().runTaskLater(this, () -> updateVisibility(player), 5L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (clearInvOnWorldChange) {
            player.getInventory().clear();
        }
        Bukkit.getScheduler().runTaskLater(this, () -> updateVisibility(player), 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (blockItemMove) {
            ItemStack item = e.getCurrentItem();
            if (item != null && item.hasItemMeta()) {
                if (item.isSimilar(onItem) || item.isSimilar(rankItem) || item.isSimilar(offItem)) {
                    e.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = e.getItem();
            if (item != null && item.hasItemMeta()) {
                if (item.isSimilar(onItem) || item.isSimilar(rankItem) || item.isSimilar(offItem)) {
                    e.setCancelled(true);
                    if (isInEnabledWorld(player)) {
                        Bukkit.getScheduler().runTaskLater(this, () -> toggleVisibility(player), 3L);
                    }
                }
            }
        }
    }

    /*@EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        ItemStack offHand = e.getOffHandItem(), mainHand = e.getMainHandItem();
        if (offHand != null && offHand.hasItemMeta() && (offHand.isSimilar(onItem) || offHand.isSimilar(rankItem) || offHand.isSimilar(offItem))) {
            e.setCancelled(true);
        } else if (mainHand != null && mainHand.hasItemMeta() && (mainHand.isSimilar(onItem) || mainHand.isSimilar(rankItem) || mainHand.isSimilar(offItem))) {
            e.setCancelled(true);
        }
    }*/

    public void showHide(Player p, Player a) {
        showHide(dbManager.getCache(p).getVisibility(), p, a);
    }

    protected void showHide(Visibility visibility, Player p, Player a) {
        if (a.hasMetadata("NPC") || p.hasMetadata("NPC")) {
            return;
        }

        Cache ca = dbManager.getCache(a);
        switch (visibility) {
            case SHOW_ALL:
                if (!p.canSee(a)) {
                    p.showPlayer(a);
                }
                break;
            case RANK_ONLY:
                if (a.hasPermission("visibilitytoggle.rank")) {
                    if (!p.canSee(a)) {
                        p.showPlayer(a);
                    }
                    break;
                }
                if (p.canSee(a)) {
                    p.hidePlayer(a);
                }
                break;
            case HIDE_ALL:
                if (p.canSee(a)) {
                    p.hidePlayer(a);
                }
                break;
        }
        switch (ca.getVisibility()) {
            case SHOW_ALL:
                if (!a.canSee(p)) {
                    a.showPlayer(p);
                }
                break;
            case RANK_ONLY:
                if (p.hasPermission("visibilitytoggle.rank")) {
                    if (!a.canSee(p)) {
                        a.showPlayer(p);
                    }
                    break;
                }
                if (a.canSee(p)) {
                    a.hidePlayer(p);
                }
                break;
            case HIDE_ALL:
                if (a.canSee(p)) {
                    a.hidePlayer(p);
                }
                break;
        }
    }

    public void updateVisibility(Player player) {
        Cache cache = dbManager.getCache(player);
        Visibility visibility = cache.getVisibility();
        if (isInEnabledWorld(player)) {
            if (toggleItem) {
                switch (visibility) {
                    case SHOW_ALL:
                        player.getInventory().setItem(itemSlot, onItem);
                        break;
                    case RANK_ONLY:
                        player.getInventory().setItem(itemSlot, rankItem);
                        break;
                    case HIDE_ALL:
                        player.getInventory().setItem(itemSlot, offItem);
                        break;
                }
                player.updateInventory();
            }
            if (toggleSound) {
                player.playSound(player.getLocation(), sound, soundVol, soundPitch);
            }
            for (Player a : player.getWorld().getPlayers()) {
                if (player != a) {
                    showHide(visibility, player, a);
                }
            }
            Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new PlayerVisibilityUpdatedEvent(player, visibility)));
        } else {
            for (Player a : player.getWorld().getPlayers()) {
                if (player != a && !player.canSee(a)) {
                    player.showPlayer(a);
                    a.showPlayer(player);
                }
            }
            Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new PlayerVisibilityUpdatedEvent(player, Visibility.SHOW_ALL)));
            if (toggleItem) {
                ItemStack item = player.getInventory().getItem(itemSlot);
                if (item != null && (item.isSimilar(onItem) || item.isSimilar(rankItem) || item.isSimilar(offItem))) {
                    player.getInventory().clear(itemSlot);
                    player.updateInventory();
                }
            }
        }
    }

    public void toggleVisibility(Player player) {
        switch (dbManager.getCache(player).getVisibility()) {
            case SHOW_ALL:
                toggleVisibility(player, Visibility.RANK_ONLY);
                break;
            case RANK_ONLY:
                toggleVisibility(player, Visibility.HIDE_ALL);
                break;
            case HIDE_ALL:
                toggleVisibility(player, Visibility.SHOW_ALL);
                break;
        }
    }

    public void toggleVisibility(Player player, Visibility newVisibility) {
        toggleVisibility(player, newVisibility, true);
    }

    public void toggleVisibility(Player player, Visibility newVisibility, boolean delayCheck) {
        Cache cache = dbManager.getCache(player);
        if (delayCheck) {
            double last = cache.getLastUse(), now = (TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) / 1000.0);
            if (last > now && !player.hasPermission("visibility.admin") && !player.hasPermission("visibility.bypass")) {
                player.sendMessage(toggleDelayMessage.replace("{cooldown}", decimalFormat.format(last - now)));
                return;
            }
        }
        if (showToggleMessages) {
            switch (newVisibility) {
                case SHOW_ALL:
                    player.sendMessage(toggleShowMessage);
                    break;
                case RANK_ONLY:
                    player.sendMessage(toggleRankMessage);
                    break;
                case HIDE_ALL:
                    player.sendMessage(toggleHideMessage);
                    break;
            }
        }
        if (showToggleTitles) {
            switch (newVisibility) {
                case SHOW_ALL:
                    TitleAPI.sendTitle(player, toggleShowTitle);
                    TitleAPI.sendSubTitle(player, toggleShowSubtitle);
                    break;
                case RANK_ONLY:
                    TitleAPI.sendTitle(player, toggleRankTitle);
                    TitleAPI.sendSubTitle(player, toggleRankSubtitle);
                    break;
                case HIDE_ALL:
                    TitleAPI.sendTitle(player, toggleOffTitle);
                    TitleAPI.sendSubTitle(player, toggleOffSubtitle);
                    break;
            }
        }

        Visibility oldVisibility = cache.getVisibility();
        cache.setVisbility(newVisibility);
        cache.setLastUse(toggleDelay);
        updateVisibility(player);
        Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new PlayerVisibilityChangedEvent(player, oldVisibility, newVisibility)));
    }

    public void forceToggleVisibility(Player player) {
        switch (dbManager.getCache(player).getVisibility()) {
            case SHOW_ALL:
                forceToggleVisibility(player, Visibility.RANK_ONLY);
                break;
            case RANK_ONLY:
                forceToggleVisibility(player, Visibility.HIDE_ALL);
                break;
            case HIDE_ALL:
                forceToggleVisibility(player, Visibility.SHOW_ALL);
                break;
        }
    }

    public void forceToggleVisibility(Player player, Visibility newVisibility) {
        if (showToggleMessages) {
            switch (newVisibility) {
                case SHOW_ALL:
                    player.sendMessage(toggleShowMessage);
                    break;
                case RANK_ONLY:
                    player.sendMessage(toggleRankMessage);
                    break;
                case HIDE_ALL:
                    player.sendMessage(toggleHideMessage);
                    break;
            }
        }
        if (showToggleTitles) {
            switch (newVisibility) {
                case SHOW_ALL:
                    TitleAPI.sendTitle(player, toggleShowTitle);
                    TitleAPI.sendSubTitle(player, toggleShowSubtitle);
                    break;
                case RANK_ONLY:
                    TitleAPI.sendTitle(player, toggleRankTitle);
                    TitleAPI.sendSubTitle(player, toggleRankSubtitle);
                    break;
                case HIDE_ALL:
                    TitleAPI.sendTitle(player, toggleOffTitle);
                    TitleAPI.sendSubTitle(player, toggleOffSubtitle);
                    break;
            }
        }

        Cache cache = dbManager.getCache(player);
        Visibility oldVisibility = cache.getVisibility();

        cache.setVisbility(newVisibility);
        cache.setLastUse(System.currentTimeMillis());
        updateVisibility(player);
        Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new PlayerVisibilityChangedEvent(player, oldVisibility, newVisibility)));
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    public int getItemSlot() {
        return itemSlot;
    }

    public void setItemSlot(int itemSlot) {
        this.itemSlot = itemSlot;
    }

    public boolean isClearInvOnJoin() {
        return clearInvOnJoin;
    }

    public void setClearInvOnJoin(boolean clearInvOnJoin) {
        this.clearInvOnJoin = clearInvOnJoin;
    }

    public boolean isJoinToggleMessage() {
        return joinToggleMessage;
    }

    public void setJoinToggleMessage(boolean joinToggleMessage) {
        this.joinToggleMessage = joinToggleMessage;
    }

    public boolean isShowToggleMessages() {
        return showToggleMessages;
    }

    public void setShowToggleMessages(boolean showToggleMessages) {
        this.showToggleMessages = showToggleMessages;
    }

    public boolean isToggleSound() {
        return toggleSound;
    }

    public void setToggleSound(boolean toggleSound) {
        this.toggleSound = toggleSound;
    }

    public boolean isBlockItemDrop() {
        return blockItemDrop;
    }

    public void setBlockItemDrop(boolean blockItemDrop) {
        this.blockItemDrop = blockItemDrop;
    }

    public boolean isGiveItemOnRespawn() {
        return giveItemOnRespawn;
    }

    public void setGiveItemOnRespawn(boolean giveItemOnRespawn) {
        this.giveItemOnRespawn = giveItemOnRespawn;
    }

    public boolean isClearInvOnWorldChange() {
        return clearInvOnWorldChange;
    }

    public void setClearInvOnWorldChange(boolean clearInvOnWorldChange) {
        this.clearInvOnWorldChange = clearInvOnWorldChange;
    }

    public boolean isBlockItemMove() {
        return blockItemMove;
    }

    public void setBlockItemMove(boolean blockItemMove) {
        this.blockItemMove = blockItemMove;
    }

    public boolean isToggleItem() {
        return toggleItem;
    }

    public void setToggleItem(boolean toggleItem) {
        this.toggleItem = toggleItem;
    }

    public boolean isShowToggleTitles() {
        return showToggleTitles;
    }

    public void setShowToggleTitles(boolean showToggleTitles) {
        this.showToggleTitles = showToggleTitles;
    }

    public String getToggleDelayMessage() {
        return toggleDelayMessage;
    }

    public void setToggleDelayMessage(String toggleDelayMessage) {
        this.toggleDelayMessage = toggleDelayMessage;
    }

    public String getToggleShowMessage() {
        return toggleShowMessage;
    }

    public void setToggleShowMessage(String toggleShowMessage) {
        this.toggleShowMessage = toggleShowMessage;
    }

    public String getToggleRankMessage() {
        return toggleRankMessage;
    }

    public void setToggleRankMessage(String toggleRankMessage) {
        this.toggleRankMessage = toggleRankMessage;
    }

    public String getToggleHideMessage() {
        return toggleHideMessage;
    }

    public void setToggleHideMessage(String toggleHideMessage) {
        this.toggleHideMessage = toggleHideMessage;
    }

    public String getToggleShowTitle() {
        return toggleShowTitle;
    }

    public void setToggleShowTitle(String toggleShowTitle) {
        this.toggleShowTitle = toggleShowTitle;
    }

    public String getToggleShowSubtitle() {
        return toggleShowSubtitle;
    }

    public void setToggleShowSubtitle(String toggleShowSubtitle) {
        this.toggleShowSubtitle = toggleShowSubtitle;
    }

    public String getToggleRankTitle() {
        return toggleRankTitle;
    }

    public void setToggleRankTitle(String toggleRankTitle) {
        this.toggleRankTitle = toggleRankTitle;
    }

    public String getToggleRankSubtitle() {
        return toggleRankSubtitle;
    }

    public void setToggleRankSubtitle(String toggleRankSubtitle) {
        this.toggleRankSubtitle = toggleRankSubtitle;
    }

    public String getToggleOffTitle() {
        return toggleOffTitle;
    }

    public void setToggleOffTitle(String toggleOffTitle) {
        this.toggleOffTitle = toggleOffTitle;
    }

    public String getToggleOffSubtitle() {
        return toggleOffSubtitle;
    }

    public void setToggleOffSubtitle(String toggleOffSubtitle) {
        this.toggleOffSubtitle = toggleOffSubtitle;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public float getSoundVol() {
        return soundVol;
    }

    public void setSoundVol(float soundVol) {
        this.soundVol = soundVol;
    }

    public float getSoundPitch() {
        return soundPitch;
    }

    public void setSoundPitch(float soundPitch) {
        this.soundPitch = soundPitch;
    }

    public ItemStack getOnItem() {
        return onItem;
    }

    public void setOnItem(ItemStack onItem) {
        this.onItem = onItem;
    }

    public ItemStack getRankItem() {
        return rankItem;
    }

    public void setRankItem(ItemStack rankItem) {
        this.rankItem = rankItem;
    }

    public ItemStack getOffItem() {
        return offItem;
    }

    public void setOffItem(ItemStack offItem) {
        this.offItem = offItem;
    }

    public List<String> getEnabledWorlds() {
        return enabledWorlds;
    }

    public void setEnabledWorlds(List<String> enabledWorlds) {
        this.enabledWorlds = enabledWorlds;
    }
}
