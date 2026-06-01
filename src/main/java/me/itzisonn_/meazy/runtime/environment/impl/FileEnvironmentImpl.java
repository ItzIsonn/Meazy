package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.EvaluationException;
import me.itzisonn_.meazy.runtime.value.impl.ClassValueImpl;
import me.itzisonn_.meazy.runtime.value.impl.VariableValueImpl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.util.*;

@NullMarked
public class FileEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements FileEnvironment {
    @Getter
    private final String packageName;
    @Getter
    private final String className;
    private final Map<String, ClassDesc> imports;
    private final List<VariableValue> variables;
    private final Set<ClassValue> classes;

    public FileEnvironmentImpl(GlobalEnvironment parent, String packageName, String className) {
        super(parent, true);

        this.packageName = packageName;
        this.className = className;

        imports = new HashMap<>();
        variables = new ArrayList<>();
        classes = new HashSet<>();

        addImport("java.lang.String");
        addImport("java.lang.System");
        addImport("java.lang.IO");
        addImport(ConstantDescs.CD_Object, "Any");
        addImport(ConstantDescs.CD_Integer, "Int");
        addImport("java.lang.Long");
        addImport("java.lang.Float");
        addImport("java.lang.Double");
        addImport("java.lang.Boolean");
    }



    @Override
    public GlobalEnvironment getParent() {
        return (GlobalEnvironment) parent;
    }



    @Override
    public void addImport(ClassDesc classDesc, String name) {
        imports.put(name, classDesc);
    }

    @Override
    public void addImport(String fullName) {
        ClassDesc classDesc = ClassDesc.of(fullName);
        imports.put(classDesc.displayName(), classDesc);
    }

    @Override
    public Map<String, ClassDesc> getImports() {
        return new HashMap<>(imports);
    }



    @Override
    public VariableValue declareVariable(String id, DataType dataType, boolean isConstant, @Nullable Expression value) {
        VariableValue variableValue = new VariableValueImpl(id, dataType, isConstant, Set.of(), variables.size(), value, this);
        variables.add(variableValue);
        return variableValue;
    }

    @Override
    public List<VariableValue> getVariables() {
        return new ArrayList<>(variables);
    }



    @Override
    public ClassValue declareClass(ClassEnvironment classEnvironment) {
        ClassValue value = new ClassValueImpl(classEnvironment, List.of());

        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(value.getId())) {
                throw new EvaluationException(Text.translatable("meazy:runtime.class.already_exists", value.getId()));
            }
        }

        classes.add(value);
        return value;
    }

    @Override
    public Set<ClassValue> getClasses() {
        return new HashSet<>(classes);
    }
}