package ru.sherb.archchecker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class Class {

    private final Module module;
    private final QualifiedName pkg;
    private final String name; //TODO никогда не заполняется

    private final Set<QualifiedName> imports;

    private Class(Module module, QualifiedName pkg, String name, Set<QualifiedName> imports) {
        this.module = module;
        this.imports = imports;
        this.pkg = pkg;
        this.name = name;
    }

    public static ClassBuilder builder() {
        return new ClassBuilder();
    }

    public QualifiedName fullName() {
        return pkg.newWith(name);
    }

    public List<QualifiedName> findImportsFrom(List<Class> classes) {
        return classes.stream()
                      .map(Class::fullName)
                      .filter(imports::contains)
                      .collect(Collectors.toList());
    }

    public static final class ClassBuilder {
        private Module module;
        private QualifiedName pkg;
        private String name;
        private Set<QualifiedName> imports = new HashSet<>();

        private ClassBuilder() {
        }

        public ClassBuilder module(Module module) {
            this.module = module;
            return this;
        }

        public ClassBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ClassBuilder pkg(String pkg) {
            this.pkg = new QualifiedName(pkg);
            return this;
        }

        public ClassBuilder addImport(String impt) {
            this.imports.add(new QualifiedName(impt));
            return this;
        }

        public Class build() {
            return new Class(module, pkg, name, imports);
        }
    }

    static final class SerializeAgent {
        String fullName;
        List<String> imports;

        static SerializeAgent from(Class cls) {
            var agent = new SerializeAgent();

            agent.fullName = cls.fullName().toString();
            agent.imports = cls.imports
                    .stream()
                    .map(QualifiedName::toString)
                    .collect(Collectors.toList());

            return agent;
        }

        Class toClass(Module module) {
            var fullName = new QualifiedName(this.fullName);
            var imports = this.imports
                    .stream()
                    .map(QualifiedName::new)
                    .collect(Collectors.toSet());

            return new Class(module, fullName.pkg(), fullName.simpleName(), imports);
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public List<String> getImports() {
            return imports;
        }

        public void setImports(List<String> imports) {
            this.imports = imports;
        }
    }
}
