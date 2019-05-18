package ru.sherb.archchecker.report;

import ru.sherb.archchecker.analysis.Module;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 18.05.19
 */
final class ModuleInfos implements Iterable<ModuleInfo> {

    private final Map<String, ModuleInfo> infos;
    private final Map<String, Module> modules;

    ModuleInfos(List<Module> modules, List<ModuleInfo> infos) {
        assert modules.size() == infos.size();

        this.modules = mapNameToModule(modules);
        this.infos = mapNameToInfo(infos);
    }

    private Map<String, Module> mapNameToModule(List<Module> modules) {
        return modules.stream()
                      .collect(Collectors.toMap(Module::name, Function.identity()));
    }

    private Map<String, ModuleInfo> mapNameToInfo(List<ModuleInfo> infos) {
        return infos.stream()
                    .collect(Collectors.toMap(ModuleInfo::name, Function.identity()));
    }

    Module getModule(ModuleInfo info) {
        assert info != null && getModuleByName(info.name()) != null;

        return getModuleByName(info.name());
    }

    Module getModuleByName(String name) {
        assert name != null && modules.get(name) != null;

        return modules.get(name);
    }

    ModuleInfo getInfo(Module module) {
        assert module != null && getInfoByName(module.name()) != null;

        return getInfoByName(module.name());
    }

    ModuleInfo getInfoByName(String name) {
        assert name != null && infos.get(name) != null;

        return infos.get(name);
    }

    @Override
    public Iterator<ModuleInfo> iterator() {
        return infos.values().iterator();
    }
}
