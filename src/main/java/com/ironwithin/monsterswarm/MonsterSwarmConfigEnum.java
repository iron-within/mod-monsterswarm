package com.ironwithin.monsterswarm;

public enum MonsterSwarmConfigEnum {
    ALWAYS(true, true),
    NEVER(false, false),
    FULLMOON(false, true);

    public final boolean fullmoon;

    public final boolean normal;

    MonsterSwarmConfigEnum(boolean nomral, boolean fullmoon) {
        this.fullmoon = fullmoon;
        this.normal = nomral;
    }

    public boolean isSwarm(boolean fmoon) {
        return (this.normal || (this.fullmoon && fmoon));
    }
}