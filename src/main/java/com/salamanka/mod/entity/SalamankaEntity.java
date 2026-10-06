package com.salamanka.mod.entity;

import com.salamanka.mod.registry.ModItems;
import com.salamanka.mod.registry.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Salamanka: a peaceful trader (like a villager). If someone hits him he pulls out the Mauser and shoots.
 */
public class SalamankaEntity extends PathfinderMob implements Merchant, RangedAttackMob {
    private static final int ANGER_TICKS = 20 * 45;
    private static final int RESTOCK_TICKS = 12000;

    @Nullable
    private Player tradingPlayer;
    @Nullable
    private MerchantOffers offers;
    private int angerTicks = 0;
    private int restockTimer = 0;

    public SalamankaEntity(EntityType<? extends SalamankaEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F); // the shown pistol never drops
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StopWhileTradingGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 28, 24.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    // ------------------------------------------------------------------ combat

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !this.level().isClientSide
                && source.getEntity() instanceof LivingEntity attacker
                && attacker != this
                && !attacker.isSpectator()) {
            Player trader = this.tradingPlayer;
            if (trader != null) {
                this.setTradingPlayer(null);
                trader.closeContainer();
            }
            this.setTarget(attacker);
            this.angerTicks = ANGER_TICKS;
        }
        return result;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        boolean armed = target != null;
        this.setAggressive(armed);
        this.setItemSlot(EquipmentSlot.MAINHAND, armed ? new ItemStack(ModItems.MAUSER.get()) : ItemStack.EMPTY);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        Vec3 from = this.getEyePosition();
        Vec3 to = new Vec3(target.getX(), target.getY(0.6D), target.getZ());
        double spread = 0.035D;
        Vec3 dir = to.subtract(from).normalize().add(
                (this.random.nextDouble() - 0.5D) * spread * 2.0D,
                (this.random.nextDouble() - 0.5D) * spread * 2.0D,
                (this.random.nextDouble() - 0.5D) * spread * 2.0D);
        float damage = 7.0F + this.random.nextInt(2); // 7-8 damage, same as the player's Mauser
        this.swing(InteractionHand.MAIN_HAND);
        MauserShot.fire(this, from, dir, damage, 40.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.tradingPlayer != null
                && (!this.tradingPlayer.isAlive() || !(this.tradingPlayer.containerMenu instanceof MerchantMenu))) {
            this.setTradingPlayer(null);
        }
        LivingEntity target = this.getTarget();
        if (target != null) {
            if (--this.angerTicks <= 0 || !target.isAlive() || target.isSpectator()
                    || this.distanceToSqr(target) > 48.0D * 48.0D) {
                this.setTarget(null);
            }
        }
        if (++this.restockTimer >= RESTOCK_TICKS) {
            this.restockTimer = 0;
            if (this.offers != null) {
                for (MerchantOffer offer : this.offers) {
                    offer.resetUses();
                }
            }
        }
    }

    // ------------------------------------------------------------------ trading

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.NAME_TAG)) {
            return super.mobInteract(player, hand);
        }
        if (this.isAlive() && this.tradingPlayer == null && this.getTarget() == null) {
            if (!this.level().isClientSide) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private void fillOffers(MerchantOffers list) {
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 4),
                new ItemStack(ModItems.COKE_1.get()), 24, 0, 0.0F));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 14),
                new ItemStack(ModItems.COKE_3.get()), 12, 0, 0.0F));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 40),
                new ItemStack(ModItems.COKE_5.get()), 5, 0, 0.0F));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 24), new ItemStack(Items.IRON_INGOT, 8),
                new ItemStack(ModItems.MAUSER.get()), 3, 0, 0.0F));
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.fillOffers(this.offers);
        }
        return this.offers;
    }

    @Override
    public void overrideOffers(@Nullable MerchantOffers newOffers) {
        this.offers = newOffers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        if (!this.level().isClientSide) {
            this.playSound(SoundEvents.VILLAGER_YES, 0.6F, 0.8F);
        }
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int xp) {
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    // ------------------------------------------------------------------ misc

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        if (reason != MobSpawnType.STRUCTURE && reason != MobSpawnType.CHUNK_GENERATION) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.SPAWN.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        return result;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.tradingPlayer == null && this.getTarget() == null ? ModSounds.AMBIENT.get() : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Offers", this.getOffers().createTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Offers", 10)) {
            this.offers = new MerchantOffers(tag.getCompound("Offers"));
        }
    }

    /** Stands still and looks at the customer while the trade window is open. */
    static class StopWhileTradingGoal extends Goal {
        private final SalamankaEntity mob;

        StopWhileTradingGoal(SalamankaEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTradingPlayer() != null;
        }

        @Override
        public void start() {
            this.mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            Player p = this.mob.getTradingPlayer();
            if (p != null) {
                this.mob.getLookControl().setLookAt(p, 30.0F, 30.0F);
            }
        }
    }
}
