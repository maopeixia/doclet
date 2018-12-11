package com.microsoft.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class MetadataFileItem {

    private String uid;
    private List<String> children = new ArrayList<>();
    private String parent;

    private String id;
    private String alias;
    private String name;
    private String fullName;
    private String overload;
    private String type;
    private String url;

    private Map<String, String> extraProperties = new HashMap<>();

    // Not present in spec
    private String href;
    private String nameWithType;
    private String packageName;
    private String summary;
    private String content;
    private List<TypeParameter> typeParameters = new ArrayList<>();
    private List<TypeParameter> parameters = new ArrayList<>();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getExtraProperties() {
        return extraProperties;
    }

    public void setExtraProperties(Map<String, String> extraProperties) {
        this.extraProperties = extraProperties;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getNameWithType() {
        return nameWithType;
    }

    public void setNameWithType(String nameWithType) {
        this.nameWithType = nameWithType;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(List<TypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public List<TypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<TypeParameter> parameters) {
        this.parameters = parameters;
    }

    public String toItemString() {
        String result = "- uid: " + uid + "\n"
            + "  id: " + id + "\n";
        if (parent != null) {
            result += "  parent: " + parent + "\n";
        }
        if (!children.isEmpty()) {
            result += "  children:\n";
            for (String child : children) {
                result += "  - " + child + "\n";
            }
        }
        result += "  href: " + href + "\n"
            + "  langs:\n"
            + "  - java\n"
            + "  name: " + name + "\n"
            + "  nameWithType: " + nameWithType + "\n"
            + "  fullName: " + fullName + "\n";

        if (StringUtils.isNotEmpty(overload)) {
            result += "  overload: " + overload + "\n";
        }

        result += "  type: " + type + "\n";
        if (StringUtils.isNotEmpty(packageName)) {
            result += "  package: " + packageName + "\n";
        }
        result += "  summary: " + summary + "\n"
            + "  syntax:\n"
            + "    content: " + content + "\n";

        if (!typeParameters.isEmpty()) {
            result += "    typeParameters:\n";
            for (TypeParameter typeParameter : typeParameters) {
                result += "    - id: " + typeParameter.getId() + "\n"
                    + "      type: " + typeParameter.getType() + "\n";
            }
        }
        if (!parameters.isEmpty()) {
            result += "    parameters:\n";
            for (TypeParameter parameter : parameters) {
                result += "    - id: " + parameter.getId() + "\n"
                    + "      type: " + parameter.getType() + "\n"
                    + "      description: " + parameter.getDescription() + "\n";
            }
        }
        return result;
    }

    public String toReferenceString() {
        String result = "- uid: " + uid + "\n"
            + "  parent: " + parent + "\n"
            + "  href: " + href + "\n"
            + "  name: " + name + "\n"
            + "  nameWithType: " + nameWithType + "\n"
            + "  fullName: " + fullName + "\n"
            + "  type: " + type + "\n"
            + "  summary: " + summary + "\n"
            + "  syntax:\n";
        result += "    content: " + content + "\n";

        if (typeParameters.isEmpty()) {
            return result;
        }

        result += "    typeParameters:\n";
        for (TypeParameter typeParameter : typeParameters) {
            result += "    - id: " + typeParameter.getId() + "\n"
                + "      type: " + typeParameter.getType() + "\n";
        }
        return result;
    }
}
