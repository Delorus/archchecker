package ru.sherb.archchecker.analysis;

import ru.sherb.archchecker.java.ModuleFile;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class ModuleInfo {

    private final ModuleFile module;

    private double stability;

    public ModuleInfo(ModuleFile module) {
        this.module = module;
    }

    public void setStability(double stability) {
        this.stability = stability;
    }

    public String name() {
        return module.name();
    }

    public double stability() {
        return stability;
    }
}
