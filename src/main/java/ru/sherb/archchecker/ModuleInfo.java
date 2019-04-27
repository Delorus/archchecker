package ru.sherb.archchecker;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class ModuleInfo {

    private final Module module;

    private double stability;

    public ModuleInfo(Module module) {
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
