package com.salamanka.mod.item;

import com.salamanka.mod.registry.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Edible "coke". tier = 1, 3 or 5 stars. Tweak the effects in applyEffects() (ticks: 20 = 1 second).
 */
public class CokeItem extends Item {
    private final int tier;

    public CokeItem(int tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 24;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return tier >= 5;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            applyEffects(entity);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.PLAYER_BREATH, SoundSource.PLAYERS, 1.0F, 0.7F);
        }
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return stack;
        }
        stack.shrink(1);
        return stack;
    }

    private void applyEffects(LivingEntity e) {
        switch (tier) {
            case 1 -> {
                add(e, MobEffects.MOVEMENT_SPEED, 20 * 60, 1);   // Speed II, 60 s
                add(e, MobEffects.DIG_SPEED, 20 * 60, 0);        // Haste I, 60 s
            }
            case 3 -> {
                add(e, MobEffects.MOVEMENT_SPEED, 20 * 90, 3);   // Speed IV, 90 s
                add(e, MobEffects.DIG_SPEED, 20 * 90, 1);        // Haste II
                add(e, MobEffects.JUMP, 20 * 90, 1);             // Jump Boost II
                add(e, ModEffects.TRIP.get(), 20 * 60, 0);       // light hallucinations, 60 s
                add(e, MobEffects.CONFUSION, 20 * 8, 0);         // short wobble
            }
            default -> {
                add(e, MobEffects.MOVEMENT_SPEED, 20 * 90, 4);   // Speed V, 90 s
                add(e, MobEffects.JUMP, 20 * 90, 4);             // Jump Boost V
                add(e, MobEffects.DIG_SPEED, 20 * 90, 2);        // Haste III
                add(e, MobEffects.DAMAGE_BOOST, 20 * 90, 1);     // Strength II
                add(e, MobEffects.SLOW_FALLING, 20 * 90, 0);     // so the high jumps don't kill you
                add(e, MobEffects.NIGHT_VISION, 20 * 90, 0);
                add(e, MobEffects.CONFUSION, 20 * 40, 0);        // vanilla nausea wobble
                add(e, ModEffects.TRIP.get(), 20 * 90, 1);       // TOTAL TRIP, 90 s
            }
        }
    }

    private static void add(LivingEntity e, MobEffect effect, int ticks, int amplifier) {
        e.addEffect(new MobEffectInstance(effect, ticks, amplifier));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
}
