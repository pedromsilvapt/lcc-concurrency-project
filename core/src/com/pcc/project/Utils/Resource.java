package com.pcc.project.Utils;

public class Resource {
    // Represents the maximum amount of this resource we can hold
    public float capacity;

    // The current amount of resource available
    public float amount;

    public float drainAmount;

    public float rechargeAmount;

    public Resource ( float capacity, float amount, float drainAmount, float rechargeAmount ) {
        this.capacity = capacity;
        this.amount = amount;
        this.drainAmount = drainAmount;
        this.rechargeAmount = rechargeAmount;
    }

    public float getCapacity () {
        return this.capacity;
    }

    public float getAmount () {
        return amount;
    }

    public float getDrainAmount () {
        return drainAmount;
    }

    public float getRechargeAmount () {
        return rechargeAmount;
    }

    public boolean isFull (  ) {
        return this.amount >= this.capacity;
    }

    public boolean isEmpty () {
        return this.amount <= 0;
    }

    public void refill () {
        this.amount = this.capacity;
    }

    public void drain ( float time ) {
        this.amount = Math.max( 0, this.amount - ( time * this.drainAmount ) );
    }

    public void recharge ( float time ) {
        this.amount = Math.min( this.capacity, this.amount + ( time * this.rechargeAmount ) );
    }

    @Override
    public String toString () {
        return String.format( "%f/%f", this.amount, this.capacity );
    }
}
