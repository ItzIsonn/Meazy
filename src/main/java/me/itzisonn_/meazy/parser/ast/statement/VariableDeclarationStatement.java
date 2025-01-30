package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class VariableDeclarationStatement implements Statement {
    private final Set<AccessModifier> accessModifiers;
    private final boolean isConstant;
    private final List<VariableDeclarationInfo> declarationInfos;

    public VariableDeclarationStatement(Set<AccessModifier> accessModifiers, boolean isConstant, List<VariableDeclarationInfo> declarationInfos) {
        this.accessModifiers = accessModifiers;
        this.isConstant = isConstant;
        this.declarationInfos = declarationInfos;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder accessModifiersBuilder = new StringBuilder();
        for (AccessModifier accessModifier : accessModifiers) {
            accessModifiersBuilder.append(accessModifier.getId()).append(" ");
        }

        String keywordString = isConstant ? "val" : "var";

        String declarationString = declarationInfos.stream().map(variableDeclarationInfo -> {
            String value = variableDeclarationInfo.getValue() == null ? "" : " = " + variableDeclarationInfo.getValue().toCodeString(0);
            return variableDeclarationInfo.getId() + ":" + variableDeclarationInfo.getDataType() + value;
        }).collect(Collectors.joining(", "));

        return accessModifiersBuilder + keywordString + " " + declarationString;
    }

    @Getter
    public static class VariableDeclarationInfo {
        private final String id;
        private final String dataType;
        private final Expression value;

        public VariableDeclarationInfo(String id, String dataType, Expression value) {
            this.id = id;
            this.dataType = dataType;
            this.value = value;
        }
    }
}
