module ru.sherb.archckecker {
    requires fastjson;
    requires java.sql;
    requires info.picocli;

    opens ru.sherb.archchecker.args to info.picocli;
    opens ru.sherb.archchecker.java to fastjson;
}