package me.martin.main.Guns;

import de.tr7zw.nbtapi.NBTItem;
import me.martin.main.Main;
import me.martin.main.Utils.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;

public class PinkPrecision implements Listener {

    Main main;

    public PinkPrecision(Main main){

        this.main = main;

    }

    HashSet<Player> shootCooldown = new HashSet<>();
    HashSet<Player> reload = new HashSet<>();
    HashSet<Player> scope = new HashSet<>();

    HashSet<Player> comboDelay = new HashSet<>();

    HashMap<Player, ItemStack> scopeHelmet = new HashMap<>();

    @EventHandler
    public void shoot(PlayerInteractEvent e) {

        Player shooter = e.getPlayer();

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (shooter.getInventory().getItemInHand().getType().equals(Material.STAINED_CLAY) && shooter.getItemInHand().getData().getData() == (byte) 10) {

                ItemStack barrett = e.getItem();
                NBTItem gun = new NBTItem(barrett);

                int currentAmmo;

                currentAmmo = gun.getInteger("ammo");

                if (!comboDelay.contains(shooter)) {
                    if (gun.hasKey("ammo")) {
                        if (gun.getInteger("ammo") > 0) {
                            if (!shootCooldown.contains(shooter)) {
                                if (!reload.contains(shooter)) {

                                    Projectile bullet = shooter.launchProjectile(Arrow.class);
                                    bullet.setShooter(shooter);
                                    bullet.setVelocity(shooter.getEyeLocation().getDirection().multiply(50.0));

                                    shooter.getWorld().playSound(shooter.getLocation(), Sound.BLAZE_DEATH, 1, 1);

                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {

                                        PacketPlayOutEntityDestroy invisBullet = new PacketPlayOutEntityDestroy(bullet.getEntityId());
                                        ((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(invisBullet);
                                    }

                                    for (double i = 2; i < 30; i += 3) {

                                        Location bulletLocation = shooter.getEyeLocation();
                                        Vector direction = bulletLocation.getDirection();
                                        bulletLocation.add(direction.multiply(i));

                                        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {

                                            PacketPlayOutWorldParticles smoke = new PacketPlayOutWorldParticles(EnumParticle.SMOKE_NORMAL, false, (float) bulletLocation.getX(), (float) bulletLocation.getY(), (float) bulletLocation.getZ(), 0, 0, 0, 0, 2, 0, 0, 0);
                                            ((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(smoke);

                                        }
                                    }

                                    for(int i = 3; i < 4; i++){

                                        Location bulletLocation = shooter.getEyeLocation();
                                        Vector direction = bulletLocation.getDirection();
                                        bulletLocation.add(direction.multiply(i));

                                        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {

                                            PacketPlayOutWorldParticles smoke = new PacketPlayOutWorldParticles(EnumParticle.FIREWORKS_SPARK, false, (float) bulletLocation.getX(), (float) bulletLocation.getY(), (float) bulletLocation.getZ(), 0, 0, 0, 0, 2, 0, 0, 0);
                                            ((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(smoke);
                                            PacketPlayOutWorldParticles smoke2 = new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_ANGRY, false, (float) bulletLocation.getX(), (float) bulletLocation.getY(), (float) bulletLocation.getZ(), 0, 0, 0, 0, 2, 0, 0, 0);
                                            ((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(smoke2);

                                        }

                                    }

                                    currentAmmo--;
                                    gun.setInteger("ammo", currentAmmo);

                                    shooter.setItemInHand(gun.getItem());
                                    shooter.updateInventory();

                                    Utils.actionBarMessage(shooter, ("§e" + currentAmmo + "⑤"));

                                    shootCooldown.add(shooter);

                                    new BukkitRunnable() {


                                        @Override
                                        public void run() {

                                            shootCooldown.remove(shooter);

                                        }
                                    }.runTaskLater(main, 22);


                                }else{

                                    reload.remove(shooter);

                                }


                            }

                        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR) && shooter.getItemInHand().getType().equals(Material.STAINED_CLAY)  && shooter.getItemInHand().getData().getData() == (byte) 4 && currentAmmo == 0 && !(shooter.getInventory().contains(Material.SLIME_BALL))) {

                            PacketPlayOutChat noammo = new PacketPlayOutChat(new ChatComponentText("§e" + "0" + "⑤"), (byte) 2);
                            ((CraftPlayer) shooter).getHandle().playerConnection.sendPacket(noammo);

                            shooter.playSound(shooter.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
                        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR) && shooter.getItemInHand().getType().equals(Material.STAINED_CLAY)  && shooter.getItemInHand().getData().getData() == (byte) 4 && currentAmmo == 0 && !reload.contains(shooter)) {

                            reload.add(shooter);

                            if (reload.contains(shooter)) {


                            }

                            shooter.playSound(shooter.getLocation(), Sound.IRONGOLEM_DEATH, 1, 1);

                            new BukkitRunnable() {

                                double reloadTime = 2.0;

                                @Override
                                public void run() {

                                    if (reload.contains(shooter) && shooter.getItemInHand().getType().equals(Material.STAINED_CLAY)  && shooter.getItemInHand().getData().getData() == (byte) 10) {
                                        if (reloadTime <= 0.1) {

                                            gun.setInteger("ammo", 7);
                                            shooter.setItemInHand(gun.getItem());

                                            PacketPlayOutChat actionBarAmmo = new PacketPlayOutChat(new ChatComponentText("§e" + gun.getInteger("ammo") + "⑤"), (byte) 2);
                                            ((CraftPlayer) shooter).getHandle().playerConnection.sendPacket(actionBarAmmo);

                                            reload.remove(shooter);

                                            this.cancel();
                                        } else {

                                            reloadTime -= 0.1;
                                            PacketPlayOutChat reloading = new PacketPlayOutChat(new ChatComponentText("§7§oReloading... " + String.format("%.1f", reloadTime) + "§7§os "), (byte) 2);
                                            ((CraftPlayer) shooter).getHandle().playerConnection.sendPacket(reloading);

                                        }
                                    } else {

                                        this.cancel();

                                    }
                                }
                            }.runTaskTimer(main, 0, 2);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void reload(PlayerDropItemEvent e){

        Player player = e.getPlayer();

        if(reload.contains(player)){
            e.setCancelled(true);
        }

        if(e.getItemDrop().getItemStack().getType().equals(Material.STAINED_CLAY)  && e.getItemDrop().getItemStack().getData().getData() == (byte) 10){

            NBTItem gun = new NBTItem(e.getItemDrop().getItemStack());

            if(gun.hasKey("ammo")){
                if(gun.getInteger("ammo") < 7){
                    if(!reload.contains(player)){

                        reload.add(player);

                        e.setCancelled(true);

                        player.playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 1, 1);

                        new BukkitRunnable(){

                            double reloadTime = 2.0;

                            @Override
                            public void run() {

                                if(reload.contains(player) && player.getItemInHand().getType().equals(Material.STAINED_CLAY)  && player.getItemInHand().getData().getData() == (byte) 10){
                                    if(reloadTime <= 0.1){

                                        gun.setInteger("ammo", 7);
                                        player.setItemInHand(gun.getItem());

                                        PacketPlayOutChat actionBarAmmo = new PacketPlayOutChat(new ChatComponentText("§e" + gun.getInteger("ammo") + "⑤"), (byte) 2);
                                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionBarAmmo);

                                        reload.remove(player);

                                        this.cancel();

                                    }else{

                                        reloadTime -= 0.1;
                                        PacketPlayOutChat reloading = new PacketPlayOutChat(new ChatComponentText("§7§oReloading... " + String.format("%.1f", reloadTime) + "§7§os "), (byte) 2);
                                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(reloading);

                                    }
                                }else{

                                    this.cancel();

                                }

                            }
                        }.runTaskTimer(main, 0, 2);

                    }

                }else{

                    e.setCancelled(true);

                }
            }

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void damage(EntityDamageByEntityEvent e){

        double damage = main.getConfig().getDouble("Guns.PinkPrecision.Damage");

        double damagehs = main.getConfig().getDouble("Guns.PinkPrecision.DamageHS");

        if(e.getDamager() instanceof Arrow){

            Arrow bullet = (Arrow)e.getDamager();

            if(bullet.getShooter() instanceof Player){

            Player shooter = (Player) bullet.getShooter();

            if(e.getEntity() instanceof Player) {

                Player victim = (Player) e.getEntity();

                if (bullet.getShooter() instanceof Player) {
                    if (shooter.getItemInHand().getType().equals(Material.STAINED_CLAY) && shooter.getItemInHand().getData().getData() == (byte) 10) {
                        if (shooter.getLocation().getPitch() > -0.5) {

                            e.setDamage(damage);

                            victim.setMaximumNoDamageTicks(10);

                            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));

                            shooter.playSound(shooter.getLocation(), Sound.NOTE_BASS_DRUM, 1, 1);

                            for(Player onlinePlayers : Bukkit.getOnlinePlayers()){

                                ((CraftPlayer)onlinePlayers).getHandle().getDataWatcher().watch(9 ,(byte) 0);

                            }

                            new BukkitRunnable() {


                                @Override
                                public void run() {

                                    victim.setVelocity(victim.getVelocity().multiply(0.8));
                                    victim.setMaximumNoDamageTicks(20);

                                }
                            }.runTaskLater(main, 1);

                        }else{

                            e.setDamage(damagehs);

                            victim.setMaximumNoDamageTicks(10);

                            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));

                            shooter.playSound(shooter.getLocation(), Sound.NOTE_BASS, 1, 1);

                            new BukkitRunnable() {


                                @Override
                                public void run() {

                                    victim.setVelocity(victim.getVelocity().multiply(0.8));
                                    victim.setMaximumNoDamageTicks(20);

                                }
                            }.runTaskLater(main, 1);

                        }
                    }
                }

            }

            }

        }

    }

    @EventHandler
    public void scope(PlayerToggleSneakEvent e){

        Player player = e.getPlayer();

        if(player.getItemInHand().getType().equals(Material.STAINED_CLAY)  && player.getItemInHand().getData().getData() == (byte) 10 && !player.isSneaking()){

            scopeHelmet.put(player, player.getInventory().getHelmet());
            scope.add(player);

            player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
            player.setWalkSpeed(-0.25f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8888, 4));

        }

        if(player.getItemInHand().getType().equals(Material.STAINED_CLAY)  && player.getItemInHand().getData().getData() == (byte) 10 && player.isSneaking() && scope.contains(player)){

            player.getInventory().setHelmet(scopeHelmet.get(player));
            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.SLOW);
            scope.remove(player);

        }

    }

    @EventHandler
    public void inScopeGunChange(PlayerItemHeldEvent e){

        Player player = e.getPlayer();

        if(scope.contains(player)){

            player.getInventory().setHelmet(scopeHelmet.get(player));
            player.setWalkSpeed(0.2f);
            scope.remove(player);

        }

    }

    @EventHandler
    public void displayGunAmmoOnEquip(PlayerItemHeldEvent e){

        Player player = e.getPlayer();

        reload.remove(player);

        if(player.getInventory().getItem(e.getNewSlot()) != null) {

            NBTItem gun = new NBTItem(player.getInventory().getItem(e.getNewSlot()));

            if (player.getInventory().getItem(e.getNewSlot()).getType().equals(Material.STAINED_CLAY)  && player.getInventory().getItem(e.getNewSlot()).getData().getData() == (byte) 10) {
                if (gun.hasKey("ammo")) {

                    comboDelay.add(player);

                    PacketPlayOutChat actionBarAmmo1 = new PacketPlayOutChat(new ChatComponentText("§e" + gun.getInteger("ammo") + "⑤"), (byte) 2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionBarAmmo1);
                    player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1.0F, 1.0F);

                    new BukkitRunnable() {


                        @Override
                        public void run() {

                            comboDelay.remove(player);

                        }
                    }.runTaskLater(main, 5L);

                }
            }
        }
    }

}
