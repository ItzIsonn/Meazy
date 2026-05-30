package me.itzisonn_.meazy.parser.ast.program.impl;

import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.program.ProgramFactory;
import me.itzisonn_.meazy.version.Version;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

@NullMarked
public class ProgramFactoryImpl implements ProgramFactory {
    @Override
    public ProgramImpl create(@Nullable File file, Version version, Map<String, @Nullable Version> requiredAddons, List<Statement> body) {
        return new ProgramImpl(file, version, requiredAddons, body);
    }

    @Override
    public ProgramImpl create(Version version, Map<String, @Nullable Version> requiredAddons, List<Statement> body) {
        return new ProgramImpl(null, version, requiredAddons, body);
    }
}
