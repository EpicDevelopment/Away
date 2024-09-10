package me.yirf.afk.thread;

import me.yirf.afk.Afk;

public class Tasks {
    public static void sync(Runnable runnable) {
        Afk.instance.getServer().getScheduler().runTask(Afk.instance, runnable);
    }
}
