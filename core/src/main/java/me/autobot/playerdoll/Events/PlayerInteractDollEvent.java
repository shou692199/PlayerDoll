package me.autobot.playerdoll.Events;


import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractDollEvent implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            return;
        }
        Entity doll = event.getRightClicked();
        if (!PlayerDoll.dollManagerMap.containsKey(doll.getName())) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking() || !player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {return;}

        player.chat("/doll menu "+doll.getName());
        //new Menu(player,doll.getName());

    }
}