package me.earth.phobos.features.modules.misc;

import java.util.Iterator;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiPackets extends Module {
   private Setting<AntiPackets.Mode> mode;
   private Setting<Integer> page;
   private Setting<Integer> pages;
   private Setting<Boolean> AdvancementInfo;
   private Setting<Boolean> Animation;
   private Setting<Boolean> BlockAction;
   private Setting<Boolean> BlockBreakAnim;
   private Setting<Boolean> BlockChange;
   private Setting<Boolean> Camera;
   private Setting<Boolean> ChangeGameState;
   private Setting<Boolean> Chat;
   private Setting<Boolean> ChunkData;
   private Setting<Boolean> CloseWindow;
   private Setting<Boolean> CollectItem;
   private Setting<Boolean> CombatEvent;
   private Setting<Boolean> ConfirmTransaction;
   private Setting<Boolean> Cooldown;
   private Setting<Boolean> CustomPayload;
   private Setting<Boolean> CustomSound;
   private Setting<Boolean> DestroyEntities;
   private Setting<Boolean> Disconnect;
   private Setting<Boolean> DisplayObjective;
   private Setting<Boolean> Effect;
   private Setting<Boolean> Entity;
   private Setting<Boolean> EntityAttach;
   private Setting<Boolean> EntityEffect;
   private Setting<Boolean> EntityEquipment;
   private Setting<Boolean> EntityHeadLook;
   private Setting<Boolean> EntityMetadata;
   private Setting<Boolean> EntityProperties;
   private Setting<Boolean> EntityStatus;
   private Setting<Boolean> EntityTeleport;
   private Setting<Boolean> EntityVelocity;
   private Setting<Boolean> Explosion;
   private Setting<Boolean> HeldItemChange;
   private Setting<Boolean> JoinGame;
   private Setting<Boolean> KeepAlive;
   private Setting<Boolean> Maps;
   private Setting<Boolean> MoveVehicle;
   private Setting<Boolean> MultiBlockChange;
   private Setting<Boolean> OpenWindow;
   private Setting<Boolean> Particles;
   private Setting<Boolean> PlaceGhostRecipe;
   private Setting<Boolean> PlayerAbilities;
   private Setting<Boolean> PlayerListHeaderFooter;
   private Setting<Boolean> PlayerListItem;
   private Setting<Boolean> PlayerPosLook;
   private Setting<Boolean> RecipeBook;
   private Setting<Boolean> RemoveEntityEffect;
   private Setting<Boolean> ResourcePackSend;
   private Setting<Boolean> Respawn;
   private Setting<Boolean> ScoreboardObjective;
   private Setting<Boolean> SelectAdvancementsTab;
   private Setting<Boolean> ServerDifficulty;
   private Setting<Boolean> SetExperience;
   private Setting<Boolean> SetPassengers;
   private Setting<Boolean> SetSlot;
   private Setting<Boolean> SignEditorOpen;
   private Setting<Boolean> SoundEffect;
   private Setting<Boolean> SpawnExperienceOrb;
   private Setting<Boolean> SpawnGlobalEntity;
   private Setting<Boolean> SpawnMob;
   private Setting<Boolean> SpawnObject;
   private Setting<Boolean> SpawnPainting;
   private Setting<Boolean> SpawnPlayer;
   private Setting<Boolean> SpawnPosition;
   private Setting<Boolean> Statistics;
   private Setting<Boolean> TabComplete;
   private Setting<Boolean> Teams;
   private Setting<Boolean> TimeUpdate;
   private Setting<Boolean> Title;
   private Setting<Boolean> UnloadChunk;
   private Setting<Boolean> UpdateBossInfo;
   private Setting<Boolean> UpdateHealth;
   private Setting<Boolean> UpdateScore;
   private Setting<Boolean> UpdateTileEntity;
   private Setting<Boolean> UseBed;
   private Setting<Boolean> WindowItems;
   private Setting<Boolean> WindowProperty;
   private Setting<Boolean> WorldBorder;
   private Setting<Boolean> Animations;
   private Setting<Boolean> ChatMessage;
   private Setting<Boolean> ClickWindow;
   private Setting<Boolean> ClientSettings;
   private Setting<Boolean> ClientStatus;
   private Setting<Boolean> CloseWindows;
   private Setting<Boolean> ConfirmTeleport;
   private Setting<Boolean> ConfirmTransactions;
   private Setting<Boolean> CreativeInventoryAction;
   private Setting<Boolean> CustomPayloads;
   private Setting<Boolean> EnchantItem;
   private Setting<Boolean> EntityAction;
   private Setting<Boolean> HeldItemChanges;
   private Setting<Boolean> Input;
   private Setting<Boolean> KeepAlives;
   private Setting<Boolean> PlaceRecipe;
   private Setting<Boolean> Player;
   private Setting<Boolean> PlayerAbility;
   private Setting<Boolean> PlayerDigging;
   private Setting<Boolean> PlayerTryUseItem;
   private Setting<Boolean> PlayerTryUseItemOnBlock;
   private Setting<Boolean> RecipeInfo;
   private Setting<Boolean> ResourcePackStatus;
   private Setting<Boolean> SeenAdvancements;
   private Setting<Boolean> PlayerPackets;
   private Setting<Boolean> Spectate;
   private Setting<Boolean> SteerBoat;
   private Setting<Boolean> TabCompletion;
   private Setting<Boolean> UpdateSign;
   private Setting<Boolean> UseEntity;
   private Setting<Boolean> VehicleMove;
   private int hudAmount;

   public AntiPackets() {
      super("AntiPackets", "Blocks Packets", Module.Category.MISC, true, false, false);
      this.mode = this.register(new Setting("Packets", AntiPackets.Mode.CLIENT));
      this.page = this.register(new Setting("SPackets", 1, 1, 10, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER;
      }));
      this.pages = this.register(new Setting("CPackets", 1, 1, 4, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT;
      }));
      this.AdvancementInfo = this.register(new Setting("AdvancementInfo", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.Animation = this.register(new Setting("Animation", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.BlockAction = this.register(new Setting("BlockAction", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.BlockBreakAnim = this.register(new Setting("BlockBreakAnim", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.BlockChange = this.register(new Setting("BlockChange", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.Camera = this.register(new Setting("Camera", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.ChangeGameState = this.register(new Setting("ChangeGameState", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.Chat = this.register(new Setting("Chat", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 1;
      }));
      this.ChunkData = this.register(new Setting("ChunkData", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.CloseWindow = this.register(new Setting("CloseWindow", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.CollectItem = this.register(new Setting("CollectItem", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.CombatEvent = this.register(new Setting("Combatevent", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.ConfirmTransaction = this.register(new Setting("ConfirmTransaction", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.Cooldown = this.register(new Setting("Cooldown", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.CustomPayload = this.register(new Setting("CustomPayload", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.CustomSound = this.register(new Setting("CustomSound", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 2;
      }));
      this.DestroyEntities = this.register(new Setting("DestroyEntities", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.Disconnect = this.register(new Setting("Disconnect", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.DisplayObjective = this.register(new Setting("DisplayObjective", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.Effect = this.register(new Setting("Effect", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.Entity = this.register(new Setting("Entity", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.EntityAttach = this.register(new Setting("EntityAttach", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.EntityEffect = this.register(new Setting("EntityEffect", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.EntityEquipment = this.register(new Setting("EntityEquipment", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 3;
      }));
      this.EntityHeadLook = this.register(new Setting("EntityHeadLook", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.EntityMetadata = this.register(new Setting("EntityMetadata", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.EntityProperties = this.register(new Setting("EntityProperties", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.EntityStatus = this.register(new Setting("EntityStatus", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.EntityTeleport = this.register(new Setting("EntityTeleport", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.EntityVelocity = this.register(new Setting("EntityVelocity", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.Explosion = this.register(new Setting("Explosion", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.HeldItemChange = this.register(new Setting("HeldItemChange", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 4;
      }));
      this.JoinGame = this.register(new Setting("JoinGame", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.KeepAlive = this.register(new Setting("KeepAlive", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.Maps = this.register(new Setting("Maps", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.MoveVehicle = this.register(new Setting("MoveVehicle", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.MultiBlockChange = this.register(new Setting("MultiBlockChange", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.OpenWindow = this.register(new Setting("OpenWindow", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.Particles = this.register(new Setting("Particles", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.PlaceGhostRecipe = this.register(new Setting("PlaceGhostRecipe", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 5;
      }));
      this.PlayerAbilities = this.register(new Setting("PlayerAbilities", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.PlayerListHeaderFooter = this.register(new Setting("PlayerListHeaderFooter", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.PlayerListItem = this.register(new Setting("PlayerListItem", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.PlayerPosLook = this.register(new Setting("PlayerPosLook", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.RecipeBook = this.register(new Setting("RecipeBook", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.RemoveEntityEffect = this.register(new Setting("RemoveEntityEffect", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.ResourcePackSend = this.register(new Setting("ResourcePackSend", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.Respawn = this.register(new Setting("Respawn", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 6;
      }));
      this.ScoreboardObjective = this.register(new Setting("ScoreboardObjective", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.SelectAdvancementsTab = this.register(new Setting("SelectAdvancementsTab", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.ServerDifficulty = this.register(new Setting("ServerDifficulty", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.SetExperience = this.register(new Setting("SetExperience", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.SetPassengers = this.register(new Setting("SetPassengers", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.SetSlot = this.register(new Setting("SetSlot", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.SignEditorOpen = this.register(new Setting("SignEditorOpen", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.SoundEffect = this.register(new Setting("SoundEffect", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 7;
      }));
      this.SpawnExperienceOrb = this.register(new Setting("SpawnExperienceOrb", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.SpawnGlobalEntity = this.register(new Setting("SpawnGlobalEntity", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.SpawnMob = this.register(new Setting("SpawnMob", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.SpawnObject = this.register(new Setting("SpawnObject", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.SpawnPainting = this.register(new Setting("SpawnPainting", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.SpawnPlayer = this.register(new Setting("SpawnPlayer", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.SpawnPosition = this.register(new Setting("SpawnPosition", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.Statistics = this.register(new Setting("Statistics", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 8;
      }));
      this.TabComplete = this.register(new Setting("TabComplete", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.Teams = this.register(new Setting("Teams", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.TimeUpdate = this.register(new Setting("TimeUpdate", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.Title = this.register(new Setting("Title", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.UnloadChunk = this.register(new Setting("UnloadChunk", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.UpdateBossInfo = this.register(new Setting("UpdateBossInfo", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.UpdateHealth = this.register(new Setting("UpdateHealth", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.UpdateScore = this.register(new Setting("UpdateScore", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 9;
      }));
      this.UpdateTileEntity = this.register(new Setting("UpdateTileEntity", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 10;
      }));
      this.UseBed = this.register(new Setting("UseBed", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 10;
      }));
      this.WindowItems = this.register(new Setting("WindowItems", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 10;
      }));
      this.WindowProperty = this.register(new Setting("WindowProperty", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 10;
      }));
      this.WorldBorder = this.register(new Setting("WorldBorder", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.SERVER && (Integer)this.page.getValue() == 10;
      }));
      this.Animations = this.register(new Setting("Animations", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.ChatMessage = this.register(new Setting("ChatMessage", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.ClickWindow = this.register(new Setting("ClickWindow", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.ClientSettings = this.register(new Setting("ClientSettings", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.ClientStatus = this.register(new Setting("ClientStatus", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.CloseWindows = this.register(new Setting("CloseWindows", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.ConfirmTeleport = this.register(new Setting("ConfirmTeleport", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.ConfirmTransactions = this.register(new Setting("ConfirmTransactions", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 1;
      }));
      this.CreativeInventoryAction = this.register(new Setting("CreativeInventoryAction", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.CustomPayloads = this.register(new Setting("CustomPayloads", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.EnchantItem = this.register(new Setting("EnchantItem", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.EntityAction = this.register(new Setting("EntityAction", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.HeldItemChanges = this.register(new Setting("HeldItemChanges", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.Input = this.register(new Setting("Input", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.KeepAlives = this.register(new Setting("KeepAlives", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.PlaceRecipe = this.register(new Setting("PlaceRecipe", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 2;
      }));
      this.Player = this.register(new Setting("Player", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 3;
      }));
      this.PlayerAbility = this.register(new Setting("PlayerAbility", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 3;
      }));
      this.PlayerDigging = this.register(new Setting("PlayerDigging", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.page.getValue() == 3;
      }));
      this.PlayerTryUseItem = this.register(new Setting("PlayerTryUseItem", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 3;
      }));
      this.PlayerTryUseItemOnBlock = this.register(new Setting("TryUseItemOnBlock", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 3;
      }));
      this.RecipeInfo = this.register(new Setting("RecipeInfo", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 3;
      }));
      this.ResourcePackStatus = this.register(new Setting("ResourcePackStatus", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 3;
      }));
      this.SeenAdvancements = this.register(new Setting("SeenAdvancements", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 3;
      }));
      this.PlayerPackets = this.register(new Setting("PlayerPackets", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 4;
      }));
      this.Spectate = this.register(new Setting("Spectate", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 4;
      }));
      this.SteerBoat = this.register(new Setting("SteerBoat", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 4;
      }));
      this.TabCompletion = this.register(new Setting("TabCompletion", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 4;
      }));
      this.UpdateSign = this.register(new Setting("UpdateSign", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 4;
      }));
      this.UseEntity = this.register(new Setting("UseEntity", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 4;
      }));
      this.VehicleMove = this.register(new Setting("VehicleMove", false, (v) -> {
         return this.mode.getValue() == AntiPackets.Mode.CLIENT && (Integer)this.pages.getValue() == 4;
      }));
      this.hudAmount = 0;
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (this.isEnabled()) {
         if (event.getPacket() instanceof CPacketAnimation && (Boolean)this.Animations.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketChatMessage && (Boolean)this.ChatMessage.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketClickWindow && (Boolean)this.ClickWindow.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketClientSettings && (Boolean)this.ClientSettings.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketClientStatus && (Boolean)this.ClientStatus.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketCloseWindow && (Boolean)this.CloseWindows.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketConfirmTeleport && (Boolean)this.ConfirmTeleport.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketConfirmTransaction && (Boolean)this.ConfirmTransactions.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketCreativeInventoryAction && (Boolean)this.CreativeInventoryAction.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketCustomPayload && (Boolean)this.CustomPayloads.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketEnchantItem && (Boolean)this.EnchantItem.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketEntityAction && (Boolean)this.EntityAction.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketHeldItemChange && (Boolean)this.HeldItemChanges.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketInput && (Boolean)this.Input.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketKeepAlive && (Boolean)this.KeepAlives.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketPlaceRecipe && (Boolean)this.PlaceRecipe.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketPlayer && (Boolean)this.Player.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketPlayerAbilities && (Boolean)this.PlayerAbility.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketPlayerDigging && (Boolean)this.PlayerDigging.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketPlayerTryUseItem && (Boolean)this.PlayerTryUseItem.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && (Boolean)this.PlayerTryUseItemOnBlock.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketRecipeInfo && (Boolean)this.RecipeInfo.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketResourcePackStatus && (Boolean)this.ResourcePackStatus.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketSeenAdvancements && (Boolean)this.SeenAdvancements.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketSpectate && (Boolean)this.Spectate.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketSteerBoat && (Boolean)this.SteerBoat.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketTabComplete && (Boolean)this.TabCompletion.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketUpdateSign && (Boolean)this.UpdateSign.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketUseEntity && (Boolean)this.UseEntity.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof CPacketVehicleMove && (Boolean)this.VehicleMove.getValue()) {
            event.setCanceled(true);
         }

      }
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (this.isEnabled()) {
         if (event.getPacket() instanceof SPacketAdvancementInfo && (Boolean)this.AdvancementInfo.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketAnimation && (Boolean)this.Animation.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketBlockAction && (Boolean)this.BlockAction.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketBlockBreakAnim && (Boolean)this.BlockBreakAnim.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketBlockChange && (Boolean)this.BlockChange.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCamera && (Boolean)this.Camera.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketChangeGameState && (Boolean)this.ChangeGameState.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketChat && (Boolean)this.Chat.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketChunkData && (Boolean)this.ChunkData.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCloseWindow && (Boolean)this.CloseWindow.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCollectItem && (Boolean)this.CollectItem.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCombatEvent && (Boolean)this.CombatEvent.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketConfirmTransaction && (Boolean)this.ConfirmTransaction.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCooldown && (Boolean)this.Cooldown.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCustomPayload && (Boolean)this.CustomPayload.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCustomSound && (Boolean)this.CustomSound.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketDestroyEntities && (Boolean)this.DestroyEntities.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketDisconnect && (Boolean)this.Disconnect.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketChunkData && (Boolean)this.ChunkData.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCloseWindow && (Boolean)this.CloseWindow.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketCollectItem && (Boolean)this.CollectItem.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketDisplayObjective && (Boolean)this.DisplayObjective.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEffect && (Boolean)this.Effect.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntity && (Boolean)this.Entity.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityAttach && (Boolean)this.EntityAttach.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityEffect && (Boolean)this.EntityEffect.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityEquipment && (Boolean)this.EntityEquipment.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityHeadLook && (Boolean)this.EntityHeadLook.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityMetadata && (Boolean)this.EntityMetadata.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityProperties && (Boolean)this.EntityProperties.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityStatus && (Boolean)this.EntityStatus.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityTeleport && (Boolean)this.EntityTeleport.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketEntityVelocity && (Boolean)this.EntityVelocity.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketExplosion && (Boolean)this.Explosion.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketHeldItemChange && (Boolean)this.HeldItemChange.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketJoinGame && (Boolean)this.JoinGame.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketKeepAlive && (Boolean)this.KeepAlive.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketMaps && (Boolean)this.Maps.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketMoveVehicle && (Boolean)this.MoveVehicle.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketMultiBlockChange && (Boolean)this.MultiBlockChange.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketOpenWindow && (Boolean)this.OpenWindow.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketParticles && (Boolean)this.Particles.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketPlaceGhostRecipe && (Boolean)this.PlaceGhostRecipe.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketPlayerAbilities && (Boolean)this.PlayerAbilities.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketPlayerListHeaderFooter && (Boolean)this.PlayerListHeaderFooter.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketPlayerListItem && (Boolean)this.PlayerListItem.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketPlayerPosLook && (Boolean)this.PlayerPosLook.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketRecipeBook && (Boolean)this.RecipeBook.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketRemoveEntityEffect && (Boolean)this.RemoveEntityEffect.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketResourcePackSend && (Boolean)this.ResourcePackSend.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketRespawn && (Boolean)this.Respawn.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketScoreboardObjective && (Boolean)this.ScoreboardObjective.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSelectAdvancementsTab && (Boolean)this.SelectAdvancementsTab.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketServerDifficulty && (Boolean)this.ServerDifficulty.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSetExperience && (Boolean)this.SetExperience.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSetPassengers && (Boolean)this.SetPassengers.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSetSlot && (Boolean)this.SetSlot.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSignEditorOpen && (Boolean)this.SignEditorOpen.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSoundEffect && (Boolean)this.SoundEffect.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSpawnExperienceOrb && (Boolean)this.SpawnExperienceOrb.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSpawnGlobalEntity && (Boolean)this.SpawnGlobalEntity.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSpawnMob && (Boolean)this.SpawnMob.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSpawnObject && (Boolean)this.SpawnObject.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSpawnPainting && (Boolean)this.SpawnPainting.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSpawnPlayer && (Boolean)this.SpawnPlayer.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketSpawnPosition && (Boolean)this.SpawnPosition.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketStatistics && (Boolean)this.Statistics.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketTabComplete && (Boolean)this.TabComplete.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketTeams && (Boolean)this.Teams.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketTimeUpdate && (Boolean)this.TimeUpdate.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketTitle && (Boolean)this.Title.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketUnloadChunk && (Boolean)this.UnloadChunk.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketUpdateBossInfo && (Boolean)this.UpdateBossInfo.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketUpdateHealth && (Boolean)this.UpdateHealth.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketUpdateScore && (Boolean)this.UpdateScore.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketUpdateTileEntity && (Boolean)this.UpdateTileEntity.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketUseBed && (Boolean)this.UseBed.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketWindowItems && (Boolean)this.WindowItems.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketWindowProperty && (Boolean)this.WindowProperty.getValue()) {
            event.setCanceled(true);
         }

         if (event.getPacket() instanceof SPacketWorldBorder && (Boolean)this.WorldBorder.getValue()) {
            event.setCanceled(true);
         }

      }
   }

   public void onEnable() {
      String standart = "§aAntiPackets On!§f Cancelled Packets: ";
      StringBuilder text = new StringBuilder(standart);
      if (!this.settings.isEmpty()) {
         Iterator var3 = this.settings.iterator();

         while(var3.hasNext()) {
            Setting setting = (Setting)var3.next();
            if (setting.getValue() instanceof Boolean && (Boolean)setting.getValue() && !setting.getName().equalsIgnoreCase("Enabled") && !setting.getName().equalsIgnoreCase("drawn")) {
               String name = setting.getName();
               text.append(name).append(", ");
            }
         }
      }

      if (text.toString().equals(standart)) {
         Command.sendMessage("§aAntiPackets On!§f Currently not cancelling any Packets.");
      } else {
         String output = this.removeLastChar(this.removeLastChar(text.toString()));
         Command.sendMessage(output);
      }

   }

   public void onUpdate() {
      int amount = 0;
      if (!this.settings.isEmpty()) {
         Iterator var2 = this.settings.iterator();

         while(var2.hasNext()) {
            Setting setting = (Setting)var2.next();
            if (setting.getValue() instanceof Boolean && (Boolean)setting.getValue() && !setting.getName().equalsIgnoreCase("Enabled") && !setting.getName().equalsIgnoreCase("drawn")) {
               ++amount;
            }
         }
      }

      this.hudAmount = amount;
   }

   public String getDisplayInfo() {
      return this.hudAmount == 0 ? "" : this.hudAmount + "";
   }

   public String removeLastChar(String str) {
      if (str != null && str.length() > 0) {
         str = str.substring(0, str.length() - 1);
      }

      return str;
   }

   public static enum Mode {
      CLIENT,
      SERVER;
   }
}
