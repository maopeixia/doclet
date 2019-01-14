package com.microsoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"uid", "id", "parent", "children", "href", "langs", "name", "nameWithType", "fullName", "overload",
    "type", "package", "summary", "syntax", "inheritance", "exceptions", "spec.java"})
public class MetadataFileItem {

    private final String uid;
    private String id;
    private String parent;
    private List<String> children = new ArrayList<>();
    private String href;
    private String[] langs;
    private String name;
    private String nameWithType;
    private String fullName;
    private String overload;
    private String type;
    @JsonProperty("package")
    private String packageName;
    private String summary;
    private Syntax syntax;
    @JsonProperty("inheritance")
    private String[] superclass;
    private List<ExceptionItem> exceptions;
    @JsonProperty("spec.java")
    private SpecJava specJava;

    public MetadataFileItem(String[] langs, String uid) {
        this(uid);
        this.langs = langs;
    }

    public MetadataFileItem(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String[] getLangs() {
        return langs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameWithType() {
        return nameWithType;
    }

    public void setNameWithType(String nameWithType) {
        this.nameWithType = nameWithType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOverload() {
        return overload;
    }

    public void setOverload(String overload) {
        this.overload = overload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Syntax getSyntax() {
        return syntax;
    }

    public void setSyntax(Syntax syntax) {
        this.syntax = syntax;
    }

    public String[] getSuperclass() {
        return superclass;
    }

    public void setSuperclass(String superclass) {
        this.superclass = new String[]{superclass};
    }

    public List<ExceptionItem> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<ExceptionItem> exceptions) {
        this.exceptions = exceptions;
    }

    public SpecJava getSpecJava() {
        return specJava;
    }

    public void setSpecJava(SpecJava specJava) {
        this.specJava = specJava;
    }

    public void setContent(String content) {
        if (syntax == null) {
            syntax = new Syntax();
        }
        syntax.setContent(content);
    }

    public void setTypeParameters(List<TypeParameter> typeParameters) {
        if (syntax == null) {
            syntax = new Syntax();
        }
        syntax.setTypeParameters(typeParameters);
    }

    public void setParameters(List<MethodParameter> parameters) {
        if (syntax == null) {
            syntax = new Syntax();
        }
        syntax.setParameters(parameters);
    }

    public void setReturn(Return returnValue) {
        if (syntax == null) {
            syntax = new Syntax();
        }
        syntax.setReturnValue(returnValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetadataFileItem that = (MetadataFileItem) o;

        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
