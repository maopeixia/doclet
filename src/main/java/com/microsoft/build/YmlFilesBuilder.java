package com.microsoft.build;

import com.microsoft.lookup.BaseLookup;
import com.microsoft.lookup.ClassItemsLookup;
import com.microsoft.lookup.ClassLookup;
import com.microsoft.lookup.PackageLookup;
import com.microsoft.model.MetadataFile;
import com.microsoft.model.MetadataFileItem;
import com.microsoft.model.SpecViewModel;
import com.microsoft.model.TocFile;
import com.microsoft.model.TocItem;
import com.microsoft.util.ElementUtil;
import com.microsoft.util.FileUtil;
import com.microsoft.util.YamlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;


public class YmlFilesBuilder {

    private final static String[] LANGS = {"java"};
    private final Pattern XREF_LINK_PATTERN = Pattern.compile("<xref uid=\".*?\" .*?>.*?</xref>");
    private final Pattern XREF_LINK_CONTENT_PATTERN = Pattern.compile("(?<=<xref uid=\").*?(?=\" .*?>.*?</xref>)");

    private DocletEnvironment environment;
    private String outputPath;
    private ElementUtil elementUtil;
    private PackageLookup packageLookup;
    private ClassItemsLookup classItemsLookup;
    private ClassLookup classLookup;
    private List<MetadataFileItem> classRefList =  new ArrayList<>();

    public YmlFilesBuilder(DocletEnvironment environment, String outputPath,
        String[] excludePackages, String[] excludeClasses) {
        this.environment = environment;
        this.outputPath = outputPath;
        this.elementUtil = new ElementUtil(excludePackages, excludeClasses);
        this.packageLookup = new PackageLookup(environment);
        this.classItemsLookup = new ClassItemsLookup(environment);
        this.classLookup = new ClassLookup(environment);
    }

    public boolean build() {
        List<MetadataFile> packageMetadataFiles = new ArrayList<>();
        List<MetadataFile> classMetadataFiles = new ArrayList<>();

        TocFile tocFile = new TocFile(outputPath);
        
        for (PackageElement packageElement : elementUtil.extractPackageElements(environment.getIncludedElements())) {
        	setClassinfoTocache(packageElement);
        }
        
        for (PackageElement packageElement : elementUtil.extractPackageElements(environment.getIncludedElements())) {
            String uid = packageLookup.extractUid(packageElement);
            packageMetadataFiles.add(buildPackageMetadataFile(packageElement));

            TocItem packageTocItem = new TocItem(uid, uid);
            buildFilesForInnerClasses(packageElement, packageTocItem.getItems(), classMetadataFiles);
            tocFile.addTocItem(packageTocItem);
        }

        for (MetadataFile packageFile : packageMetadataFiles) {
            String packageFileName = packageFile.getFileName();
            for (MetadataFile classFile : classMetadataFiles) {
                String classFileName = classFile.getFileName();
                if (packageFileName.equalsIgnoreCase(classFileName)) {
                    packageFile.setFileName(packageFileName.replaceAll("\\.yml$", "(package).yml"));
                    classFile.setFileName(classFileName.replaceAll("\\.yml$", "(class).yml"));
                    break;
                }
            }
        }
                
        populateUidValues(packageMetadataFiles, classMetadataFiles);

        packageMetadataFiles.forEach(FileUtil::dumpToFile);
        classMetadataFiles.forEach(FileUtil::dumpToFile);
        FileUtil.dumpToFile(tocFile);

        return true;
    }
    
    void setClassinfoTocache(Element element)
    {
    	 for (TypeElement classElement : elementUtil.extractSortedElements(element)) {
    		 MetadataFile classitem = addCacheClassInfo(classElement);
    		 addMethodsInfoTocache(classElement,classitem);
    		 classRefList.addAll(classitem.getItems());
    	 }
    	 
    	/* for (MetadataFileItem a : classRefList )
    	 {
    		 
    		 String s="";
    	 }*/
    }
      
    void buildFilesForInnerClasses(Element element, List<TocItem> listToAddItems, List<MetadataFile> container) {
        for (TypeElement classElement : elementUtil.extractSortedElements(element)) {
            String uid = classLookup.extractUid(classElement);
            String name = classLookup.extractTocName(classElement);

            listToAddItems.add(new TocItem(uid, name));
            
            container.add(buildClassYmlFile(classElement));
            buildFilesForInnerClasses(classElement, listToAddItems, container);
        }
    }

    MetadataFile buildPackageMetadataFile(PackageElement packageElement) {
        String fileName = packageLookup.extractHref(packageElement);
        MetadataFile packageMetadataFile = new MetadataFile(outputPath, fileName);
        MetadataFileItem packageItem = new MetadataFileItem(LANGS, packageLookup.extractUid(packageElement));
        packageItem.setId(packageLookup.extractId(packageElement));
        addChildrenReferences(packageElement, packageItem.getChildren(), packageMetadataFile.getReferences());
        populateItemFields(packageItem, packageLookup, packageElement);
        packageMetadataFile.getItems().add(packageItem);
        return packageMetadataFile;
    }

    void addChildrenReferences(Element element, List<String> packageChildren,
        Set<MetadataFileItem> referencesCollector) {
        for (TypeElement classElement : elementUtil.extractSortedElements(element)) {
            referencesCollector.add(buildClassReference(classElement));

            packageChildren.add(classLookup.extractUid(classElement));
            addChildrenReferences(classElement, packageChildren, referencesCollector);
        }
    }

    MetadataFileItem buildClassReference(TypeElement classElement) {
        MetadataFileItem referenceItem = new MetadataFileItem(classLookup.extractUid(classElement));
        referenceItem.setName(classLookup.extractName(classElement));
        referenceItem.setNameWithType(classLookup.extractNameWithType(classElement));
        referenceItem.setFullName(classLookup.extractFullName(classElement));
        return referenceItem;
    }

    <T extends Element> void populateItemFields(MetadataFileItem item, BaseLookup<T> lookup, T element) {
        item.setName(lookup.extractName(element));
        item.setNameWithType(lookup.extractNameWithType(element));
        item.setFullName(lookup.extractFullName(element));
        item.setType(lookup.extractType(element));
        item.setSummary(lookup.extractSummary(element));
        item.setContent(lookup.extractContent(element));
    }

    MetadataFile buildClassYmlFile(TypeElement classElement) {
        String fileName = classLookup.extractHref(classElement);
        MetadataFile classMetadataFile = new MetadataFile(outputPath, fileName);
        addClassInfo(classElement, classMetadataFile);
        addConstructorsInfo(classElement, classMetadataFile);
        addMethodsInfo(classElement, classMetadataFile);
        addFieldsInfo(classElement, classMetadataFile);
        addReferencesInfo(classElement, classMetadataFile);
        applyPostProcessing(classMetadataFile);
        return classMetadataFile;
    }
    
    MetadataFile addCacheClassInfo(TypeElement classElement) {
    	  String fileName = classLookup.extractHref(classElement);
          MetadataFile classMetadataFile = new MetadataFile(outputPath, fileName);  
          addClassCacheItem(classElement, classMetadataFile);
          return classMetadataFile;
    }
    
    void addClassCacheItem(TypeElement classElement, MetadataFile classMetadataFile)
    {
    	MetadataFileItem classItem = new MetadataFileItem(LANGS, classLookup.extractUid(classElement));
        populateItemFields(classItem, classLookup, classElement);
        classItem.setId(classLookup.extractId(classElement));
        classItem.setParent(classLookup.extractParent(classElement));
        
        var superclass = (TypeElement) environment.getTypeUtils().asElement(classElement.getSuperclass());
        Optional.ofNullable(superclass).ifPresent(param->{
        classItem.setInheritance(Arrays.asList(superclass.getQualifiedName().toString()));   
        });
  
        classMetadataFile.getItems().add(classItem);
    }
    
    void addMethodsInfoTocache(TypeElement classElement, MetadataFile classMetadataFile)
    {
    	 ElementFilter.methodsIn(classElement.getEnclosedElements()).stream()
         .filter(methodElement -> !methodElement.getModifiers().contains(Modifier.PRIVATE))
         .forEach(methodElement -> {
             MetadataFileItem methodItem = buildMetadataFileItem(methodElement);
             classMetadataFile.getItems().add(methodItem);
         });
    }

    void addClassInfo(TypeElement classElement, MetadataFile classMetadataFile) {
        MetadataFileItem classItem = new MetadataFileItem(LANGS, classLookup.extractUid(classElement));
        classItem.setId(classLookup.extractId(classElement));
        classItem.setParent(classLookup.extractParent(classElement));
        addChildren(classElement, classItem.getChildren());
        populateItemFields(classItem, classLookup, classElement);
        classItem.setPackageName(classLookup.extractPackageName(classElement));
        classItem.setTypeParameters(classLookup.extractTypeParameters(classElement));
        
        List<String> inherits = new ArrayList<>();
        List<String> inheritsmember = new ArrayList<>();
        var superclass = (TypeElement) environment.getTypeUtils().asElement(classElement.getSuperclass());
        Optional.ofNullable(superclass).ifPresent(param->{
        	            String singleInherintance=param.getQualifiedName().toString();
        	            inherits.add(singleInherintance);
        	            List<String> inheritances = GetInheritance(singleInherintance,inherits);
        	            Collections.reverse(inheritances);
        	            classItem.setInheritance(inheritances);
        	            
        	            List<String> inheritancesmembers=GetInheritanceMethod(singleInherintance,inheritsmember);
        	            classItem.setInheritedMembers(inheritancesmembers);       	            
                       });
        
             
        classItem.setInterfaces(classLookup.extractInterfaces(classElement));
        classMetadataFile.getItems().add(classItem);
    }
    
    void addChildren(TypeElement classElement, List<String> children) {
        collect(classElement, children, ElementFilter::constructorsIn, classItemsLookup::extractUid);
        collect(classElement, children, ElementFilter::methodsIn, classItemsLookup::extractUid);
        collect(classElement, children, ElementFilter::fieldsIn, classItemsLookup::extractUid);
        collect(classElement, children, ElementFilter::typesIn, String::valueOf);
    }

    List<? extends Element> filterPrivateElements(List<? extends Element> elements) {
        return elements.stream()
            .filter(element -> !element.getModifiers().contains(Modifier.PRIVATE)).collect(Collectors.toList());
    }

    void collect(TypeElement classElement, List<String> children,
        Function<Iterable<? extends Element>, List<? extends Element>> selectFunc,
        Function<? super Element, String> mapFunc) {

        List<? extends Element> elements = selectFunc.apply(classElement.getEnclosedElements());
        children.addAll(filterPrivateElements(elements).stream()
            .map(mapFunc).collect(Collectors.toList()));
    }

    void addConstructorsInfo(TypeElement classElement, MetadataFile classMetadataFile) {
        for (ExecutableElement constructorElement : ElementFilter.constructorsIn(classElement.getEnclosedElements())) {
            MetadataFileItem constructorItem = buildMetadataFileItem(constructorElement);
            constructorItem.setOverload(classItemsLookup.extractOverload(constructorElement));
            constructorItem.setContent(classItemsLookup.extractConstructorContent(constructorElement));
            constructorItem.setParameters(classItemsLookup.extractParameters(constructorElement));
            classMetadataFile.getItems().add(constructorItem);

            addParameterReferences(constructorItem, classMetadataFile);
            addOverloadReferences(constructorItem, classMetadataFile);
        }
    }

    void addMethodsInfo(TypeElement classElement, MetadataFile classMetadataFile) {
        ElementFilter.methodsIn(classElement.getEnclosedElements()).stream()
            .filter(methodElement -> !methodElement.getModifiers().contains(Modifier.PRIVATE))
            .forEach(methodElement -> {
                MetadataFileItem methodItem = buildMetadataFileItem(methodElement);
                methodItem.setOverload(classItemsLookup.extractOverload(methodElement));
                methodItem.setContent(classItemsLookup.extractMethodContent(methodElement));
                methodItem.setExceptions(classItemsLookup.extractExceptions(methodElement));
                methodItem.setParameters(classItemsLookup.extractParameters(methodElement));
                methodItem.setReturn(classItemsLookup.extractReturn(methodElement));

                classMetadataFile.getItems().add(methodItem);
                addExceptionReferences(methodItem, classMetadataFile);
                addParameterReferences(methodItem, classMetadataFile);
                addReturnReferences(methodItem, classMetadataFile);
                addOverloadReferences(methodItem, classMetadataFile);
            });
    }

    void addFieldsInfo(TypeElement classElement, MetadataFile classMetadataFile) {
        ElementFilter.fieldsIn(classElement.getEnclosedElements()).stream()
            .filter(fieldElement -> !fieldElement.getModifiers().contains(Modifier.PRIVATE))
            .forEach(fieldElement -> {
                MetadataFileItem fieldItem = buildMetadataFileItem(fieldElement);
                fieldItem.setContent(classItemsLookup.extractFieldContent(fieldElement));
                fieldItem.setReturn(classItemsLookup.extractReturn(fieldElement));
                classMetadataFile.getItems().add(fieldItem);
                addReturnReferences(fieldItem, classMetadataFile);
            });
    }

    void addReferencesInfo(TypeElement classElement, MetadataFile classMetadataFile) {
        MetadataFileItem classReference = new MetadataFileItem(classLookup.extractUid(classElement));
        classReference.setParent(classLookup.extractParent(classElement));
        populateItemFields(classReference, classLookup, classElement);
        classReference.setTypeParameters(classLookup.extractTypeParameters(classElement));
       
        addTypeParameterReferences(classReference, classMetadataFile);
        addSuperclassAndInterfacesReferences(classElement, classMetadataFile);
        addInnerClassesReferences(classElement, classMetadataFile);
        addInheritanceExtendReferences(classElement,classMetadataFile);    
    }

    MetadataFileItem buildMetadataFileItem(Element element) {
        return new MetadataFileItem(LANGS, classItemsLookup.extractUid(element)) {{
            setId(classItemsLookup.extractId(element));
            setParent(classItemsLookup.extractParent(element));
            setName(classItemsLookup.extractName(element));
            setNameWithType(classItemsLookup.extractNameWithType(element));
            setFullName(classItemsLookup.extractFullName(element));
            setType(classItemsLookup.extractType(element));
            setPackageName(classItemsLookup.extractPackageName(element));
            setSummary(classItemsLookup.extractSummary(element));
        }};
    }

    void addParameterReferences(MetadataFileItem methodItem, MetadataFile classMetadataFile) {
        classMetadataFile.getReferences().addAll(
            methodItem.getSyntax().getParameters().stream()
                .map(parameter -> buildRefItem(parameter.getType()))
                .filter(o -> !classMetadataFile.getItems().contains(o))
                .collect(Collectors.toList()));
    }

    void addReturnReferences(MetadataFileItem methodItem, MetadataFile classMetadataFile) {
        classMetadataFile.getReferences().addAll(
            Stream.of(methodItem.getSyntax().getReturnValue())
                .filter(Objects::nonNull)
                .map(returnValue -> buildRefItem(returnValue.getReturnType()))
                .filter(o -> !classMetadataFile.getItems().contains(o))
                .collect(Collectors.toList()));
    }

    void addExceptionReferences(MetadataFileItem methodItem, MetadataFile classMetadataFile) {
        classMetadataFile.getReferences().addAll(
            methodItem.getExceptions().stream()
                .map(exceptionItem -> buildRefItem(exceptionItem.getType()))
                .filter(o -> !classMetadataFile.getItems().contains(o))
                .collect(Collectors.toList()));
    }

    void addTypeParameterReferences(MetadataFileItem methodItem, MetadataFile classMetadataFile) {
        classMetadataFile.getReferences().addAll(
            methodItem.getSyntax().getTypeParameters().stream()
                .map(typeParameter -> {
                    String id = typeParameter.getId();
                    return new MetadataFileItem(id, id, false);
                }).collect(Collectors.toList()));
    }

    void addSuperclassAndInterfacesReferences(TypeElement classElement, MetadataFile classMetadataFile) {
        classMetadataFile.getReferences().addAll(classLookup.extractReferences(classElement));
    }

    void addInnerClassesReferences(TypeElement classElement, MetadataFile classMetadataFile) {
        classMetadataFile.getReferences().addAll(
            ElementFilter.typesIn(classElement.getEnclosedElements()).stream()
                .map(this::buildClassReference)
                .collect(Collectors.toList()));
    }

    void addOverloadReferences(MetadataFileItem item, MetadataFile classMetadataFile) {
        MetadataFileItem overloadRefItem = new MetadataFileItem(item.getOverload()) {{
            setName(RegExUtils.removeAll(item.getName(), "\\(.*\\)$"));
            setNameWithType(RegExUtils.removeAll(item.getNameWithType(), "\\(.*\\)$"));
            setFullName(RegExUtils.removeAll(item.getFullName(), "\\(.*\\)$"));
            setPackageName(item.getPackageName());
        }};
        classMetadataFile.getReferences().add(overloadRefItem);
    }
    
    void addInheritanceExtendReferences(TypeElement classElement, MetadataFile classMetadataFile) {
    	var superclass = (TypeElement) environment.getTypeUtils().asElement(classElement.getSuperclass());
    	
    	 Optional.ofNullable(superclass).ifPresent(param->{
                       String singleInherintance=param.getQualifiedName().toString();
                       List<MetadataFileItem> upperclasslist = new ArrayList<>(); 
                       List<MetadataFileItem> upperclassmemberlist = new ArrayList<>();
                       
                       classMetadataFile.getReferences().addAll(
                    		   GetInheritanceList(singleInherintance,upperclasslist).stream()
                               .map(Item -> buildRefItem(Item.getUid()))
                               .filter(o -> !classMetadataFile.getItems().contains(o))
                               .collect(Collectors.toList()));
                       
                      classMetadataFile.getReferences().addAll(
                    		  GetInheritanceMethodList(singleInherintance,upperclassmemberlist).stream()
                               .map(Item -> buildRefItem(Item.getUid()))
                               .filter(o -> !classMetadataFile.getItems().contains(o))
                               .collect(Collectors.toList()));
            });
    }

    void applyPostProcessing(MetadataFile classMetadataFile) {
        expandComplexGenericsInReferences(classMetadataFile);
    }

    /**
     * Replace one record in 'references' with several records in this way:
     * <pre>
     * a.b.c.List<df.mn.ClassOne<tr.T>> ->
     *     - a.b.c.List
     *     - df.mn.ClassOne
     *     - tr.T
     * </pre>
     */
    void expandComplexGenericsInReferences(MetadataFile classMetadataFile) {
        Set<MetadataFileItem> additionalItems = new LinkedHashSet<>();
        Iterator<MetadataFileItem> iterator = classMetadataFile.getReferences().iterator();
        while (iterator.hasNext()) {
            MetadataFileItem item = iterator.next();
            String uid = item.getUid();
            if (!uid.endsWith("*") && uid.contains("<")) {            	
	                List<String> classNames = splitUidWithGenericsIntoClassNames(uid);
	                additionalItems.addAll(classNames.stream()
	                    .map(s -> new MetadataFileItem(s, classLookup.makeTypeShort(s), true))
	                    .collect(Collectors.toSet()));           	
            }
        }
        // Remove items which already exist in 'items' section (compared by 'uid' field)
        additionalItems.removeAll(classMetadataFile.getItems());

        classMetadataFile.getReferences().addAll(additionalItems);
    }

    void populateUidValues(List<MetadataFile> packageMetadataFiles, List<MetadataFile> classMetadataFiles) {
        Lookup lookup = new Lookup(packageMetadataFiles, classMetadataFiles);

        classMetadataFiles.forEach(classMetadataFile -> {
            LookupContext lookupContext = lookup.buildContext(classMetadataFile);

            for (MetadataFileItem item : classMetadataFile.getItems()) {
                item.setSummary(YamlUtil.convertHtmlToMarkdown(
                    populateUidValues(item.getSummary(), lookupContext)
                ));

                Optional.ofNullable(item.getSyntax()).ifPresent(syntax -> {
                        Optional.ofNullable(syntax.getParameters()).ifPresent(
                            methodParams -> methodParams.forEach(
                                param -> {
                                    param.setDescription(populateUidValues(param.getDescription(), lookupContext));
                                })
                        );
                        Optional.ofNullable(syntax.getReturnValue()).ifPresent(returnValue ->
                            returnValue.setReturnDescription(
                                populateUidValues(syntax.getReturnValue().getReturnDescription(), lookupContext)
                            )
                        );
                    }
                );
            }
        });
    }

    String populateUidValues(String text, LookupContext lookupContext) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        Matcher linkMatcher = XREF_LINK_PATTERN.matcher(text);
        while (linkMatcher.find()) {
            String link = linkMatcher.group();
            Matcher linkContentMatcher = XREF_LINK_CONTENT_PATTERN.matcher(link);
            if (!linkContentMatcher.find()) {
                continue;
            }

            String linkContent = linkContentMatcher.group();
            String uid = resolveUidByLookup(linkContent, lookupContext);
            String updatedLink = linkContentMatcher.replaceAll(uid);
            text = StringUtils.replace(text, link, updatedLink);
        }
        return text;
    }

    String resolveUidByLookup(String linkContent, LookupContext lookupContext) {
        if (StringUtils.isBlank(linkContent)) {
            return "";
        }

        linkContent = linkContent.trim();
        if (linkContent.startsWith("#")) {
            String firstKey = lookupContext.getOwnerUid();
            linkContent = firstKey + linkContent;
        }
        linkContent = linkContent.replace("#", ".");
        return lookupContext.containsKey(linkContent) ? lookupContext.resolve(linkContent) : "";
    }

    List<String> splitUidWithGenericsIntoClassNames(String uid) {
        uid = RegExUtils.removeAll(uid, "[>]+$");
        return Arrays.asList(StringUtils.split(uid, "<"));
    }

    MetadataFileItem buildRefItem(String value) {
        value = RegExUtils.removeAll(value, "\\[\\]$");
        if (!value.endsWith("*") && value.contains("<")) {
        	return new MetadataFileItem(value,getJavaSpec(replaceUidAndSplit(value)));
        }
        else
        return new MetadataFileItem(value, classLookup.makeTypeShort(value), true);
    }
    
    List<String> replaceUidAndSplit(String value)
    {
    	String retValue= RegExUtils.replaceAll(value,"\\<",",<,");
    	retValue = RegExUtils.replaceAll(retValue,"\\>",",>,");
    	return  Arrays.asList(StringUtils.split(retValue, ",")); 
    }
    
    List<SpecViewModel> getJavaSpec(List<String> value)
    {
    	List<SpecViewModel> returnList = new ArrayList<>();
    
    	   Optional.ofNullable(value).ifPresent(
                   Params -> Params.forEach(
                       param -> {
                         if(param.equalsIgnoreCase("<") || param.equalsIgnoreCase(">"))
                         returnList.add(new SpecViewModel(param,param));
                         else if (param!="")
                         returnList.add(new SpecViewModel(param,param,param));
                       })
               );
    	   
        return returnList;
    }
         
    MetadataFileItem getClassElement(String uid)
    {      	
    	Optional<MetadataFileItem> item = classRefList.stream().filter(i -> i.getUid().equalsIgnoreCase(uid)).findFirst();
    	
    	if(item.isEmpty())
    		return new MetadataFileItem("");
    	else
         	return item.get();
    
    }
    
    List<String> GetInheritance(String uid,List<String> listValue)
    {
    	List<String> upperclass = getClassElement(uid).getInheritance();
    	Optional.ofNullable(upperclass).ifPresent
    	              (param ->{ 
    	            	   String item = param.get(0);
    	            	   listValue.add(item);   
    	            	   GetInheritance(item,listValue);
    	              }  
    	          );
    	   	 
    	 return listValue;
    }
    
    List<MetadataFileItem> GetInheritanceList(String uid,List<MetadataFileItem> listValue)
    {
    	List<String> upperclass = getClassElement(uid).getInheritance();
    	List<MetadataFileItem> upperclasslist =  classRefList.stream().filter(i -> i.getUid().equalsIgnoreCase(uid)
                                                 && i.getType().equalsIgnoreCase("Class")).collect(Collectors.toList());
    
    	Optional.ofNullable(upperclasslist).ifPresent
    	                   (params -> { listValue.addAll(params);});
    	
    	Optional.ofNullable(upperclass).ifPresent
         (param ->{ 
	     	   String item = param.get(0);  
	     	   GetInheritanceList(item,listValue);
            }  
          );    
    	
    	 return listValue;
    }
    
    List<String> GetInheritanceMethod(String uid,List<String> listValue)
    {
    	List<String> upperclass = getClassElement(uid).getInheritance();
    	List<MetadataFileItem> upperclassmethod =  classRefList.stream().filter(i -> i.getParent().equalsIgnoreCase(uid)
    			                         && i.getType().equalsIgnoreCase("Method")).collect(Collectors.toList());
    	
    	Optional.ofNullable(upperclassmethod).ifPresent
    	              (params -> params.forEach(param->
    	            	    {listValue.add(param.getUid());}));
    	            	   
    	Optional.ofNullable(upperclass).ifPresent
                       (param ->{ 
				     	   String item = param.get(0);  
				     	   GetInheritanceMethod(item,listValue);
                        }  
                   );    
    	   	 
    	 return listValue;
    }
    
    List<MetadataFileItem> GetInheritanceMethodList(String uid,List<MetadataFileItem> listValue)
    {
    	List<String> upperclass = getClassElement(uid).getInheritance();
    	List<MetadataFileItem> upperclassmethod =  classRefList.stream().filter(i -> i.getParent().equalsIgnoreCase(uid)
    			                         && i.getType().equalsIgnoreCase("Method")).collect(Collectors.toList());
    	
    	Optional.ofNullable(upperclassmethod).ifPresent
    	              (params -> { listValue.addAll(params);});
    	            	   
    	Optional.ofNullable(upperclass).ifPresent
                       (param ->{ 
				     	   String item = param.get(0);  
				     	   GetInheritanceMethodList(item,listValue);
                        }  
                   );    
    	   	 
    	 return listValue;
    }
}
