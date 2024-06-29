package com.jackd.exchickens.entity.ai;

import java.util.EnumSet;

import com.jackd.exchickens.entity.EntityExplodingChicken;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

public class GoalAttackTamed extends TrackTargetGoal {

    private EntityExplodingChicken entity;
    private LivingEntity attackingEntity;
    private int lastAttackTime;

    public GoalAttackTamed(EntityExplodingChicken entity) {
        super(entity, false);
        this.entity = entity;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if(!this.entity.isTamed()) return false;

        LivingEntity owner = this.entity.getOwner();
        if(owner == null) return false;

        this.attackingEntity = owner.getAttacking();
        int ownerLastAttackTime = owner.getLastAttackTime();

        return this.lastAttackTime != ownerLastAttackTime &&
                this.canTrack(this.attackingEntity, TargetPredicate.DEFAULT);
    }

    @Override
    public void start() {
        this.entity.setTarget(this.attackingEntity);
        
        LivingEntity owner = this.entity.getOwner();
        if(owner != null) {
            this.lastAttackTime = owner.getLastAttackTime();
        }

        super.start();
    }


    
}
