package com.salamanka.mod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Instant (hitscan) Mauser shot used by both the player's gun and Salamanka. */
public final class MauserShot {
    private MauserShot() {}

    public static void fire(LivingEntity shooter, Vec3 from, Vec3 direction, float damage, double range) {
        Level level = shooter.level();
        Vec3 dir = direction.normalize();
        Vec3 to = from.add(dir.scale(range));

        BlockHitResult blockHit = level.clip(new ClipContext(from, to,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        Vec3 end = blockHit.getType() == HitResult.Type.MISS ? to : blockHit.getLocation();

        AABB box = new AABB(from, end).inflate(1.0D);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, shooter, from, end, box,
                e -> !e.isSpectator() && e.isPickable() && e != shooter);
        if (hit != null) {
            end = hit.getLocation();
            Entity victim = hit.getEntity();
            DamageSource source = shooter instanceof Player p
                    ? level.damageSources().playerAttack(p)
                    : level.damageSources().mobAttack(shooter);
            victim.hurt(source, damage);
        }

        if (level instanceof ServerLevel server) {
            double length = from.distanceTo(end);
            for (double d = 1.0D; d < length; d += 0.8D) {
                Vec3 p = from.add(dir.scale(d));
                server.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            Vec3 muzzle = from.add(dir.scale(0.9D));
            server.sendParticles(ParticleTypes.FLAME, muzzle.x, muzzle.y - 0.1D, muzzle.z, 4, 0.04D, 0.04D, 0.04D, 0.01D);
            server.sendParticles(ParticleTypes.SMOKE, muzzle.x, muzzle.y - 0.1D, muzzle.z, 5, 0.05D, 0.05D, 0.05D, 0.01D);
            server.sendParticles(ParticleTypes.SMOKE, end.x, end.y, end.z, 3, 0.05D, 0.05D, 0.05D, 0.01D);

            SoundSource src = shooter instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    SoundEvents.GENERIC_EXPLODE, src, 0.5F, 2.0F);
        }
    }
}
