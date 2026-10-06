package com.salamanka.mod.client;

import com.salamanka.mod.Salamanka;
import com.salamanka.mod.registry.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** All client-side hallucinations: camera sway, FOV pulsing, rainbow screen tint, notes and creepy sounds. */
@Mod.EventBusSubscriber(modid = Salamanka.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TripClientEvents {
    private static final SoundEvent[] CREEPY = {
            SoundEvents.ENDERMAN_STARE, SoundEvents.WARDEN_HEARTBEAT, SoundEvents.GHAST_AMBIENT,
            SoundEvents.ENDERMAN_SCREAM, SoundEvents.ENDERMAN_TELEPORT
    };

    /** 0 = no trip, ~0.3 = light (3 stars), 1 = TOTAL TRIP (5 stars). Fades out during the last 4 seconds. */
    private static float intensity() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return 0.0F;
        }
        MobEffectInstance effect = player.getEffect(ModEffects.TRIP.get());
        if (effect == null) {
            return 0.0F;
        }
        float base = effect.getAmplifier() >= 1 ? 1.0F : 0.3F;
        float fade = Math.min(1.0F, effect.getDuration() / 80.0F);
        return base * fade;
    }

    private static double time(double partial) {
        ClientLevel level = Minecraft.getInstance().level;
        return level == null ? 0.0D : level.getGameTime() + partial;
    }

    @SubscribeEvent
    public static void onCamera(ViewportEvent.ComputeCameraAngles event) {
        float i = intensity();
        if (i <= 0.0F) {
            return;
        }
        double t = time(event.getPartialTick());
        event.setRoll((float) (event.getRoll() + Math.sin(t * 0.07D) * 14.0D * i));
        event.setPitch((float) (event.getPitch() + Math.sin(t * 0.11D) * 4.0D * i));
        event.setYaw((float) (event.getYaw() + Math.cos(t * 0.09D) * 4.0D * i));
    }

    @SubscribeEvent
    public static void onFov(ViewportEvent.ComputeFov event) {
        float i = intensity();
        if (i <= 0.0F) {
            return;
        }
        double t = time(event.getPartialTick());
        event.setFOV(event.getFOV() + Math.sin(t * 0.08D) * 22.0D * i);
    }

    @SubscribeEvent
    public static void onGui(RenderGuiEvent.Pre event) {
        float i = intensity();
        if (i <= 0.0F) {
            return;
        }
        double t = time(event.getPartialTick());
        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();
        int alpha = Mth.clamp((int) ((40.0D + 30.0D * Math.sin(t * 0.12D)) * i * 1.4D), 0, 140);
        int top = (alpha << 24) | Mth.hsvToRgb((float) ((t * 0.007D) % 1.0D), 0.9F, 1.0F);
        int bottom = (alpha << 24) | Mth.hsvToRgb((float) ((t * 0.007D + 0.5D) % 1.0D), 0.9F, 1.0F);
        event.getGuiGraphics().fillGradient(0, 0, w, h, top, bottom);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        float i = intensity();
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (i < 0.2F || player == null || level == null) {
            return;
        }
        RandomSource r = level.random;
        if (r.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.NOTE,
                    player.getX() + (r.nextDouble() - 0.5D) * 6.0D,
                    player.getY() + r.nextDouble() * 3.0D,
                    player.getZ() + (r.nextDouble() - 0.5D) * 6.0D,
                    r.nextDouble(), 0.0D, 0.0D);
        }
        if (i > 0.9F && r.nextInt(100) == 0) {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    CREEPY[r.nextInt(CREEPY.length)], SoundSource.AMBIENT,
                    0.8F, 0.5F + r.nextFloat(), false);
        }
    }
}
