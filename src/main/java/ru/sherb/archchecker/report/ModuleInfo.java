package ru.sherb.archchecker.report;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class ModuleInfo {

    private final String name;
    private double stability;

    public ModuleInfo(String name) {
        this.name = name;
    }

    public void setStability(double stability) {
        this.stability = stability;
    }

    public String name() {
        return name;
    }

    public double stability() {
        return stability;
    }
}
