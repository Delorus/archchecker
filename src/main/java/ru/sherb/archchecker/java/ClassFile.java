package ru.sherb.archchecker.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class ClassFile {

    private final ModuleFile module;
    private final QualifiedName pkg;
    private final String name;

    private final Set<QualifiedName> imports;

    private ClassFile(ModuleFile module, QualifiedName pkg, String name, Set<QualifiedName> imports) {
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

    public List<QualifiedName> findImportsFrom(List<ClassFile> classes) {
        return classes.stream()
                      .map(ClassFile::fullName)
                      .filter(imports::contains)
                      .collect(Collectors.toList());
    }

    public static final class ClassBuilder {
        private ModuleFile module;
        private QualifiedName pkg;
        private String name;
        private Set<QualifiedName> imports = new HashSet<>();

        private ClassBuilder() {
        }

        public ClassBuilder module(ModuleFile module) {
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

        public ClassFile build() {
            validate();
            return new ClassFile(module, pkg, name, imports);
        }

        private void validate() {
            if (module == null || pkg == null || name == null) {
                throw new IllegalArgumentException(validateMsg());
            }
        }

        private String validateMsg() {
            List<String> msgs = new ArrayList<>();

            msgs.add("class does not contain the necessary data:");

            if (name == null) {
                msgs.add("name is null!");
            } else {
                msgs.add("name: " + name);
            }

            if (pkg == null) {
                msgs.add("package is null!");
            } else {
                msgs.add("package: " + pkg);
            }

            if (module == null) {
                msgs.add("module is null!");
            } else {
                msgs.add("module: " + module);
            }

            return String.join(" ", msgs);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassFile cls = (ClassFile) o;
        return pkg.equals(cls.pkg) &&
                name.equals(cls.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, name);
    }

    @Override
    public String toString() {
        return "Class{"
                + "module=" + module.name()
                + ", pkg=" + pkg
                + ", name='" + name + '\''
                + ", imports=[" + imports.stream()
                                         .map(QualifiedName::toString)
                                         .collect(Collectors.joining("; "))
                + "]}";
    }

    static final class SerializeAgent {
        String fullName;
        List<String> imports;

        static SerializeAgent from(ClassFile cls) {
            var agent = new SerializeAgent();

            agent.fullName = cls.fullName().toString();
            agent.imports = cls.imports
                    .stream()
                    .map(QualifiedName::toString)
                    .collect(Collectors.toList());

            return agent;
        }

        ClassFile toClass(ModuleFile module) {
            var fullName = new QualifiedName(this.fullName);
            var imports = this.imports
                    .stream()
                    .map(QualifiedName::new)
                    .collect(Collectors.toSet());

            return new ClassFile(module, fullName.pkg(), fullName.simpleName(), imports);
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
