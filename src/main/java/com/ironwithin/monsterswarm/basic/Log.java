package com.ironwithin.monsterswarm.basic;

import com.ironwithin.monsterswarm.MonsterSwarmMod;

public class Log {
    public static void log(CharSequence str) {
        MonsterSwarmMod.LOGGER.info((String) str);
    }
}