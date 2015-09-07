/*
 * Copyright 2009 Valtech GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.valtech.source.dependometer.app.typedefprovider.filebased.cpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IEnumerator;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree.IASTInclusionNode;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignatedInitializer;
import org.eclipse.cdt.core.dom.ast.c.ICASTTypeIdInitializerExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeleteExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExplicitTemplateInstantiation;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLinkageSpecification;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceAlias;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplatedTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypenameExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTCompoundStatementExpression;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.IGPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.ISourceCodeParser;
import org.eclipse.cdt.core.dom.parser.c.ANSICParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.GCCScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.ANSICPPParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.GPPParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.GPPScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.NullLogService;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousExpression;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousStatement;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.c.GNUCSourceParser;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassScope;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumeration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumerator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef;
import org.eclipse.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser;
import org.eclipse.cdt.internal.core.parser.FileContentProviderAdapter;
import org.eclipse.cdt.internal.core.parser.SavedFilesProvider;
import org.eclipse.cdt.internal.core.parser.scanner.AbstractCharArray;
import org.eclipse.cdt.internal.core.parser.scanner.CPreprocessor;
import org.eclipse.cdt.internal.core.parser.scanner.FileCharArray;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;

/**
 * Parses a C++ source file and extracts all defined or referenced C++ types. It
 * therefore uses the CDT parser for C++. CDT is an eclipse plugin or
 * development tool for the C/C++ environment.
 *
 * @author Carsten Kaiser (carsten.kaiser@valtech.de)
 * @version 1.0
 */
public class SourceFileParser {

    /**
     * Indicator whether ANSI or GNU C/C++ sources are supposed to be parsed
     */
    public static boolean s_isGNU = true;

    /**
     * System defines symbol table
     */
    public static Map<String, String> s_systemDefines = Collections.emptyMap();

    /**
     * List of system include paths
     */
    public static String[] s_systemIncludes = new String[0];

    /**
     * Logging facility
     */
    private static final Logger s_logger = Logger.getLogger(SourceFileParser.class.getName());

    /**
     * List of abstract classes
     */
    private List<String> m_abstractClasses = new ArrayList<String>();

    /**
     * List of detected classes
     */
    private List<String> m_classes = new ArrayList<String>();

    /**
     * Currently parsed compilation unit (.cpp file)
     */
    private String m_currentCompilationUnit = null;

    /**
     * List of current direct includes not representing a system include
     */
    private List<String> m_currentIncludes = null;

    /**
     * Filename wherein the detected types are declared
     */
    private Map<String, String> m_filenames = new HashMap<String, String>();

    /**
     * List of detected nested C++ types
     */
    private List<String> m_nestedTypes = new ArrayList<String>();

    /**
     * References of each detected type
     */
    private ReferenceCache m_references = new ReferenceCache();

    /**
     * List of superclasses for a detected class type
     */
    private Map<String, List<String>> m_superclasses = new HashMap<String, List<String>>();

    /**
     * Source associated with a detected type
     */
    private Map<String, String> m_sources = new HashMap<String, String>();

    /**
     * Scope separator
     */
    public static final String SCOPE_SEPARATOR = "::";

    /**
     * List of all detected C++ types
     */
    private Set<String> m_types = new LinkedHashSet<String>();

    /**
     * Stack of currently processed types
     */
    private Stack<String> m_typeStack = new Stack<String>();

    private Map<IASTNameOwner, String> m_nameOwners = new HashMap<IASTNameOwner, String>();

    private Charset fileEncoding;

    /**
     * Creates a parser instance using the given input directories for qualified
     * name resolution
     */
    public SourceFileParser() {
        this(null);
    }

    /**
     * Creates a parser instance using the given input directories for qualified
     * name resolution
     *
     * @param fileEncoding file encoding of source code files
     */
    public SourceFileParser(Charset fileCoding) {
        this.fileEncoding = fileCoding;
    }

    /**
     * Associates the given reference to the currently inspected type
     *
     * @param reference Fully qualified type name
     */
    private void addReference(String reference) {
        assert reference != null;
        assert reference.length() > 0;

        if (m_typeStack.isEmpty()) {
            return;
        }

      // This is C++ .
        // Nested classes do not access the enclosing class.
        String currentType = m_typeStack.peek();

        if (currentType != null && !reference.equals(currentType)) {
            {
                m_references.addRef(currentType, reference);
            }
        }
    }

    /**
     * Calculates the fully qualified names using the given parts
     *
     * @param qualifiedName Parts
     * @return Fully qualified name
     */
    private String calculateFullyQualifiedName(String[] qualifiedName) {
        assert qualifiedName != null;
        assert qualifiedName.length > 0;

        StringBuilder fullyQualifiedName = new StringBuilder();

        for (int i = 0; i < qualifiedName.length; i++) {
            if (qualifiedName[i] == null) {
                continue; // ???
            }
            if (fullyQualifiedName.length() > 0) {
                fullyQualifiedName.append(SCOPE_SEPARATOR);
            }

            if (qualifiedName[i] == s_AnonymousNamespace) {
                String name = new File(m_currentCompilationUnit).getName();
                if (name.indexOf('.') > 0) {
                    name = name.substring(0, name.lastIndexOf('.'));
                }

                fullyQualifiedName.append(name);
                fullyQualifiedName.append(SCOPE_SEPARATOR);
            }

            fullyQualifiedName.append(qualifiedName[i]);
        }

        return fullyQualifiedName.toString().intern();
    }

    /**
     * Attempts to evaluate the given declaration
     *
     * @param declaration Declaration
     */
    private void evaluate(IASTDeclaration declaration) throws DOMException {
        if (declaration == null) {
            return;
        } else if (declaration instanceof IASTAmbiguousDeclarator) {
            IASTAmbiguousDeclarator ambiguousDeclaration = (IASTAmbiguousDeclarator) declaration;

            for (IASTDeclarator childDeclaration : ambiguousDeclaration.getDeclarators()) {
                evaluate(childDeclaration);
            }
        } else if (declaration instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition) declaration;

            s_logger.log(Priority.DEBUG, "FUNCTION DEFINITION: " + functionDefinition.getRawSignature());

            if (declaration.getParent() != null && declaration.getParent() instanceof CPPASTCompositeTypeSpecifier) {
                assert m_Member;
            // // This is a member declaration
                //				
                // evaluate(functionDefinition.getDeclarator());
                //				
                // CPPASTCompositeTypeSpecifier parent = (CPPASTCompositeTypeSpecifier) declaration.getParent();
                //								
                // String fqn = m_nameOwners.get(functionDefinition.getDeclarator());
                // assert fqn != null;
                //				
                // if(m_currentCompilationUnit.equals(functionDefinition.getContainingFilename())
                // || m_currentIncludes.contains(functionDefinition.getContainingFilename()))
                // {
                // m_sources.put(fqn, m_currentCompilationUnit);
                // }
                // m_filenames.put(fqn, m_currentCompilationUnit);
                //
                // m_typeStack.push(fqn);

                boolean oldBody = m_Body;
                m_Body = true;

                evaluate(functionDefinition.getBody());

                m_Body = oldBody;

                // m_typeStack.pop();
            }

            if (m_typeStack.empty()) {
                assert !m_Member;

                boolean isStatic = false;
                if (functionDefinition.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
                    CPPASTSimpleDeclSpecifier simpleDeclSpecifier = (CPPASTSimpleDeclSpecifier) functionDefinition
                            .getDeclSpecifier();

                    isStatic = (CPPASTSimpleDeclSpecifier.sc_static == simpleDeclSpecifier.getStorageClass());
                }

                String fqn = null;
                IASTName name = functionDefinition.getDeclarator().getName();
                if (name instanceof CPPASTQualifiedName) {

                    IASTName[] names = ((CPPASTQualifiedName) name).getNames();
                    for (IASTName n : names) {
                        String nn = resolveFullyQualifiedTypeName(n);
                        if (nn != null) {
                            fqn = nn;
                        }
                    }
                }

                if (fqn == null) {
                    if (isStatic) {
                        // static function
                        s_logger.warn(new StringBuilder(functionDefinition.getFileLocation().getFileName()).append(": ")
                                .append(functionDefinition.getFileLocation().getStartingLineNumber()).append(
                                        ": Static functions are deprecated in ISO C++. Please use anonymous namespaces instead.")
                                .toString());

                        fqn = calculateFullyQualifiedName(new String[]{
                            s_AnonymousNamespace, s_freeFunctions, functionDefinition.getDeclarator().getName().toString()});
                    } else {
                        fqn = calculateFullyQualifiedName(getNamespaceQualifiedName(s_freeFunctions, functionDefinition
                                .getDeclarator().getName().toString()));
                    }
                }

                if (!m_Member) {
                    m_types.add(fqn);
                    m_nameOwners.put(functionDefinition.getDeclarator(), fqn);
                    addTypeSource(fqn, functionDefinition);

                    m_typeStack.push(fqn);
                }

                boolean oldBody = m_Body;
                m_Body = true;
                evaluate(functionDefinition.getBody());
                m_Body = oldBody;

                if (!m_Member) {
                    m_typeStack.pop();
                }
            }
        } else if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;

            evaluate(simpleDeclaration.getDeclSpecifier());

            boolean oldStatic = m_Static;
            boolean oldExtern = m_Extern;
            boolean oldFriend = m_Friend;
            boolean oldTypedef = m_Typedef;
            try {
                if (simpleDeclaration.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
                    CPPASTSimpleDeclSpecifier simpleDeclSpecifier = (CPPASTSimpleDeclSpecifier) simpleDeclaration
                            .getDeclSpecifier();

                    m_Static = (CPPASTSimpleDeclSpecifier.sc_static == simpleDeclSpecifier.getStorageClass());
                    m_Extern = (CPPASTSimpleDeclSpecifier.sc_extern == simpleDeclSpecifier.getStorageClass());
                    m_Friend = simpleDeclSpecifier.isFriend();
                    m_Typedef = (CPPASTSimpleDeclSpecifier.sc_typedef == simpleDeclSpecifier.getStorageClass());
                }

                if (simpleDeclaration.getDeclSpecifier() instanceof CPPASTNamedTypeSpecifier) {

                    CPPASTNamedTypeSpecifier specifier = (CPPASTNamedTypeSpecifier) simpleDeclaration.getDeclSpecifier();
                    m_Referred = specifier.getName();
                }

                for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
                    evaluate(declarator);
                }

                m_Referred = null;

            } finally {
                m_Static = oldStatic;
                m_Extern = oldExtern;
                m_Friend = oldFriend;
                m_Typedef = oldTypedef;
            }
        } else if (declaration instanceof ICPPASTExplicitTemplateInstantiation) {
            ICPPASTExplicitTemplateInstantiation explicitTemplateInstantiation = (ICPPASTExplicitTemplateInstantiation) declaration;

            evaluate(explicitTemplateInstantiation.getDeclaration());
        } else if (declaration instanceof ICPPASTLinkageSpecification) {
            ICPPASTLinkageSpecification linkageSpecification = (ICPPASTLinkageSpecification) declaration;

            for (IASTDeclaration childDeclaration : linkageSpecification.getDeclarations()) {
                evaluate(childDeclaration);
            }
        } else if (declaration instanceof ICPPASTNamespaceAlias) {
            ICPPASTNamespaceAlias namespaceAlias = (ICPPASTNamespaceAlias) declaration;

            s_logger.log(Priority.DEBUG, "NAMESPACE ALIAS: " + namespaceAlias.getAlias());
        } else if (declaration instanceof ICPPASTNamespaceDefinition) {
            ICPPASTNamespaceDefinition namespace = (ICPPASTNamespaceDefinition) declaration;

            try {
                String name = namespace.getName().toString();

                m_CurrentNamespace.add(name.length() > 0 ? name : s_AnonymousNamespace);

                for (IASTDeclaration childDeclaration : namespace.getDeclarations()) {
                    evaluate(childDeclaration);
                }

            } finally {
                m_CurrentNamespace.remove(m_CurrentNamespace.size() - 1);
            }
        } else if (declaration instanceof ICPPASTTemplateDeclaration) {
            ICPPASTTemplateDeclaration templateDeclaration = (ICPPASTTemplateDeclaration) declaration;

            s_logger.log(Priority.DEBUG, "TEMPLATE DECLARATION: " + templateDeclaration.getRawSignature());
        } else if (declaration instanceof ICPPASTTemplateSpecialization) {
            ICPPASTTemplateSpecialization templateSpecialization = (ICPPASTTemplateSpecialization) declaration;

            evaluate(templateSpecialization.getDeclaration());
        } else if (declaration instanceof ICPPASTUsingDeclaration) {
            ICPPASTUsingDeclaration usingDeclaration = (ICPPASTUsingDeclaration) declaration;

            s_logger.log(Priority.DEBUG, "USING DECLARATION: " + usingDeclaration.getName());
        } else if (declaration instanceof ICPPASTUsingDirective) {
            ICPPASTUsingDirective usingDirective = (ICPPASTUsingDirective) declaration;

            s_logger.log(Priority.DEBUG, "USING DIRECTIVE: " + usingDirective.getQualifiedName());
        } // else if(declaration instanceof ICPPASTVisibilityLabel)
        // {
        // ICPPASTVisibilityLabel visiblityLabel = (ICPPASTVisibilityLabel) declaration;
        //
        // if(ICPPASTVisibilityLabel.v_private == visiblityLabel.getVisibility())
        // s_logger.log(Priority.DEBUG, "MEMBER VISIBILITY: private");
        // else if(ICPPASTVisibilityLabel.v_protected== visiblityLabel.getVisibility())
        // s_logger.log(Priority.DEBUG, "MEMBER VISIBILITY: protected");
        // else if(ICPPASTVisibilityLabel.v_public == visiblityLabel.getVisibility())
        // s_logger.log(Priority.DEBUG, "MEMBER VISIBILITY: public");
        // }
        else if (declaration instanceof IASTProblemDeclaration) {
            s_logger.error(((IASTProblemDeclaration) declaration).getFileLocation().getFileName() + ":"
                    + ((IASTProblemDeclaration) declaration).getFileLocation().getStartingLineNumber() + " "
                    + ((IASTProblemDeclaration) declaration).getProblem().getMessage());

        }
    }

    /**
     * Attempts to evaluate the given declarator
     *
     * @param declarator Declarator
     */
    private void evaluate(IASTDeclarator declarator) throws DOMException {
        if (declarator == null) {
            return;
        }

        String typeForInitializer = null;

        String name = declarator.getName().toString();
        if (s_logger.isDebugEnabled()) {
            s_logger.log(Priority.DEBUG, "DECLARATOR: " + name);
        }

        if (declarator instanceof CPPASTFunctionDeclarator) {
            CPPASTFunctionDeclarator functionDeclarator = ((CPPASTFunctionDeclarator) declarator);

            String fqn;
            if (m_Member) {
                fqn = calculateFullyQualifiedName(new String[]{
                    m_typeStack.peek(), declarator.getName().toString()});
            } else if (m_Static) {
                // static function
                s_logger.warn(new StringBuilder(declarator.getFileLocation().getFileName()).append(": ").append(
                        declarator.getFileLocation().getStartingLineNumber()).append(
                                ": Static functions are deprecated in ISO C++. Please use anonymous namespaces instead.").toString());

                fqn = calculateFullyQualifiedName(new String[]{
                    s_AnonymousNamespace, s_freeFunctions, declarator.getName().toString()});
            } else {
                fqn = calculateFullyQualifiedName(getNamespaceQualifiedName(s_freeFunctions, declarator.getName()
                        .toString()));
            }

            if (functionDeclarator.isPureVirtual()) {
                if (fqn.lastIndexOf("::") > 0) {
                    String className = fqn.substring(0, fqn.lastIndexOf("::")); // cut off function name
                    m_abstractClasses.add(className); // 1 abstract function means, that whole class is abstract
                    m_classes.remove(className);
                }
            }

            m_types.add(fqn);
            m_nameOwners.put(declarator, fqn);
         // register first declaration, not definition
            // this.addTypeSource(fqn, declarator.getName());

        } else if (m_Extern || m_Friend) {
            s_logger.debug("Extern or friend");
        } else if (m_Body) {
            // auto variables
            if (m_Referred != null) {
                processReference(m_Referred);
            }
        } else if (m_Member) {
            // fqn = calculateFullyQualifiedName(new String[] { m_typeStack.peek(), declarator.getName().toString() });
        } else {
            String fqn;
         // if (m_Member)
            // {
            // fqn = calculateFullyQualifiedName(new String[] { m_typeStack.peek(), declarator.getName().toString() });
            // }
            // else
            // {
            fqn = calculateFullyQualifiedName(getNamespaceQualifiedName(m_Typedef ? s_globalTypedefs : s_globalVariables,
                    declarator.getName().toString()));

            typeForInitializer = fqn;

            if (m_Referred != null) {
                m_typeStack.push(fqn);
                processReference(m_Referred);
                m_typeStack.pop();
            }
            // }
            m_types.add(fqn);
            m_nameOwners.put(declarator, fqn);

            addTypeSource(fqn, declarator);
        }

        // evaluate(declarator.getInitializer());
        evaluate(declarator.getNestedDeclarator());

        if (typeForInitializer != null) {
            m_typeStack.push(typeForInitializer);
            evaluate(declarator.getInitializer());
            m_typeStack.pop();
        }
    }

    /**
     * Attempts to evaluate the given declaration specifier
     *
     * @param declSpecifier Declaration specifier
     */
    private void evaluate(IASTDeclSpecifier declSpecifier) throws DOMException {
        if (declSpecifier instanceof IASTCompositeTypeSpecifier) {
            IASTCompositeTypeSpecifier compositeTypeSpecifier = (IASTCompositeTypeSpecifier) declSpecifier;

            String fullyQualifiedName = resolveFullyQualifiedTypeName(compositeTypeSpecifier.getName());

            // Only consider composite types with explicit name
            if (fullyQualifiedName != null && fullyQualifiedName.length() > 0
                    && !fullyQualifiedName.endsWith(SCOPE_SEPARATOR)) {
                if (!m_types.contains(fullyQualifiedName)) {
                    if (!m_typeStack.empty()) {
                        m_nestedTypes.add(fullyQualifiedName);
                    }

                    m_types.add(fullyQualifiedName);
                    m_nameOwners.put(compositeTypeSpecifier, fullyQualifiedName);

                    if (ICPPASTCompositeTypeSpecifier.k_class == compositeTypeSpecifier.getKey()) {
                        m_classes.add(fullyQualifiedName);
                    }
                }

                m_typeStack.push(fullyQualifiedName);

                if (compositeTypeSpecifier instanceof ICPPASTCompositeTypeSpecifier) {
                    ICPPASTCompositeTypeSpecifier cppCompositeTypeSpecifier = (ICPPASTCompositeTypeSpecifier) compositeTypeSpecifier;

                    if (!m_superclasses.containsKey(fullyQualifiedName)) {
                        List<String> superclasses = new ArrayList<String>();
                        m_superclasses.put(fullyQualifiedName, superclasses);

                        for (ICPPASTBaseSpecifier baseSpecifier : cppCompositeTypeSpecifier.getBaseSpecifiers()) {
                            String fqn = resolveFullyQualifiedTypeName(baseSpecifier.getName());
                            if (fqn != null) {
                                superclasses.add(fqn);
                            }
                            s_logger.log(Priority.DEBUG, "BASE SPECIFIER: " + baseSpecifier.getName());
                        }

                        if (cppCompositeTypeSpecifier.isVirtual()) {
                            m_abstractClasses.add(fullyQualifiedName);
                        }
                    }
                }

                addTypeSource(fullyQualifiedName, compositeTypeSpecifier);

                boolean oldMember = m_Member;
                m_Member = true;

                for (IASTDeclaration childDeclaration : compositeTypeSpecifier.getMembers()) {
                    evaluate(childDeclaration);
                }

                m_Member = oldMember;
                m_typeStack.pop();
            }

            s_logger.log(Priority.DEBUG, "COMPOSITE TYPE: " + compositeTypeSpecifier.getName());
        } else if (declSpecifier instanceof IASTElaboratedTypeSpecifier) {
            IASTElaboratedTypeSpecifier elaboratedTypeSpecifier = (IASTElaboratedTypeSpecifier) declSpecifier;

            String fullyQualifiedName = resolveFullyQualifiedTypeName(elaboratedTypeSpecifier.getName());

            // Only consider elaborated types with explicit name
            if (fullyQualifiedName != null && fullyQualifiedName.length() > 0
                    && !fullyQualifiedName.endsWith(SCOPE_SEPARATOR)) {
                if (!m_types.contains(fullyQualifiedName)) {
                    if (!m_typeStack.empty()) {
                        m_nestedTypes.add(fullyQualifiedName);
                    }

                    m_types.add(fullyQualifiedName);

                    m_nameOwners.put(elaboratedTypeSpecifier, fullyQualifiedName);

                    if (ICPPASTElaboratedTypeSpecifier.k_class == elaboratedTypeSpecifier.getKind()) {
                        m_classes.add(fullyQualifiedName);
                    }
                }
            }

            s_logger.log(Priority.DEBUG, "ELABORATED TYPE: " + elaboratedTypeSpecifier.getName());
        } else if (declSpecifier instanceof IASTEnumerationSpecifier) {
            IASTEnumerationSpecifier enumerationSpecifier = (IASTEnumerationSpecifier) declSpecifier;

            String fqn = null;
            if (!m_Member) {
                IASTName name = enumerationSpecifier.getName();

                String n = name.toString();

                if (n.length() == 0) {
                    n = "anonymous";
                }

                fqn = calculateFullyQualifiedName(getNamespaceQualifiedName("global_enums", n));

                m_types.add(fqn);

                m_nameOwners.put(enumerationSpecifier, fqn);

                addTypeSource(fqn, enumerationSpecifier);
            }

            IASTEnumerator[] enumerators = enumerationSpecifier.getEnumerators();

            boolean empty = m_typeStack.isEmpty() && fqn != null;
            if (empty) {
                m_typeStack.push(fqn);
            }
            for (IASTEnumerator enumerator : enumerators) {
                if (enumerator.getValue() != null) {
                    evaluate(enumerator.getValue());
                }
            }
            if (empty) {
                m_typeStack.pop();
            }

         // String fullyQualifiedName = resolveFullyQualifiedTypeName(enumerationSpecifier.getName());
            //
            // //Only consider enumeration types with explicit name
            // if(fullyQualifiedName.length() > 0
            // && !fullyQualifiedName.endsWith(SCOPE_SEPARATOR))
            // {
            // if(!m_types.contains(fullyQualifiedName))
            // {
            // if(!m_typeStack.empty())
            // m_nestedTypes.add(fullyQualifiedName);
            //
            // m_types.add(fullyQualifiedName);
            // m_nameOwners.put(enumerationSpecifier, fullyQualifiedName);
            // }
            //
            // if(m_currentCompilationUnit.equals(enumerationSpecifier.getContainingFilename())
            // || m_currentIncludes.contains(enumerationSpecifier.getContainingFilename()))
            // {
            // m_sources.put(fullyQualifiedName, m_currentCompilationUnit);
            //
            // //Map the current type to the compilation unit in order
            // //to be able to resolve static functions afterwards
            // List<String> types = m_containedTypes.get(m_currentCompilationUnit);
            //
            // if(types == null)
            // {
            // types = new ArrayList<String>();
            // m_containedTypes.put(m_currentCompilationUnit, types);
            // }
            //
            // types.add(fullyQualifiedName);
            // }
            //
            // m_filenames.put(fullyQualifiedName, enumerationSpecifier.getContainingFilename());
            //
            // m_typeStack.push(fullyQualifiedName);
            //
            // for(IASTEnumerator enumerator : enumerationSpecifier.getEnumerators())
            // evaluate(enumerator.getValue());
            //
            // m_typeStack.pop();
            // }
            s_logger.log(Priority.DEBUG, "ENUMERATION TYPE: " + enumerationSpecifier.getName());
        } else if (declSpecifier instanceof IASTNamedTypeSpecifier) {
            IASTNamedTypeSpecifier namedTypeSpecifier = (IASTNamedTypeSpecifier) declSpecifier;

         // Check whether this reference appears within any type definition
            // Otherwise it might be a reference to a third party class which
            // could not be loaded
         // With global variables, there is a hen-egg-problem,
            // so the reference will be processed later.
            if (!m_typeStack.empty()) {
                processReference(namedTypeSpecifier.getName());
            }

            s_logger.log(Priority.DEBUG, "NAMED TYPE: " + namedTypeSpecifier.getName());
        } else if (declSpecifier instanceof IGPPASTSimpleDeclSpecifier) {
            IGPPASTSimpleDeclSpecifier simpleTypeSpecifier = (IGPPASTSimpleDeclSpecifier) declSpecifier;

            evaluate(simpleTypeSpecifier.getTypeofExpression());
        }

    }

    /**
     * Attempts to evaluate the given expression by evaluating each of its parts
     *
     * @param expression Expression
     */
    private void evaluate(IASTExpression expression) throws DOMException {
        if (expression == null) {
            return;
        } else if (expression instanceof IASTAmbiguousExpression) {
            IASTAmbiguousExpression ambiguousExpression = (IASTAmbiguousExpression) expression;

            for (IASTExpression childExpression : ambiguousExpression.getExpressions()) {
                evaluate(childExpression);
            }
        } else if (expression instanceof IASTArraySubscriptExpression) {
            IASTArraySubscriptExpression arraySubscriptExpression = (IASTArraySubscriptExpression) expression;

            evaluate(arraySubscriptExpression.getArrayExpression());
            evaluate(arraySubscriptExpression.getSubscriptExpression());
        } else if (expression instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) expression;

            evaluate(binaryExpression.getOperand1());
            evaluate(binaryExpression.getOperand2());
        } else if (expression instanceof IASTCastExpression) {
            IASTCastExpression castExpression = (IASTCastExpression) expression;

            evaluate(castExpression.getTypeId());
            evaluate(castExpression.getOperand());
        } else if (expression instanceof IASTConditionalExpression) {
            IASTConditionalExpression conditionalExpression = (IASTConditionalExpression) expression;

            evaluate(conditionalExpression.getLogicalConditionExpression());
            evaluate(conditionalExpression.getNegativeResultExpression());
            evaluate(conditionalExpression.getPositiveResultExpression());
        } else if (expression instanceof IASTExpressionList) {
            IASTExpressionList expressionList = (IASTExpressionList) expression;

            for (IASTExpression childExpression : expressionList.getExpressions()) {
                evaluate(childExpression);
            }
        } else if (expression instanceof IASTFieldReference) {
            IASTFieldReference fieldReference = (IASTFieldReference) expression;

            evaluate(fieldReference.getFieldOwner());
        } else if (expression instanceof IASTFunctionCallExpression) {
            IASTFunctionCallExpression functionCallExpression = (IASTFunctionCallExpression) expression;

            evaluate(functionCallExpression.getFunctionNameExpression());
            evaluate(functionCallExpression.getParameterExpression());
        } else if (expression instanceof IASTIdExpression) {
            IASTIdExpression idExpression = (IASTIdExpression) expression;

            if (idExpression instanceof CPPASTIdExpression) {
                evaluate(((CPPASTIdExpression) idExpression).getExpressionType());
            }

            processReference(idExpression.getName());

            s_logger.log(Priority.DEBUG, "ID Expression: " + idExpression.getName());
        } else if (expression instanceof IASTTypeIdExpression) {
            IASTTypeIdExpression typeIdExpression = (IASTTypeIdExpression) expression;

            evaluate(typeIdExpression.getTypeId());
        } else if (expression instanceof IASTUnaryExpression) {
            IASTUnaryExpression unaryExpression = (IASTUnaryExpression) expression;

            evaluate(unaryExpression.getOperand());
        } else if (expression instanceof ICASTTypeIdInitializerExpression) {
            ICASTTypeIdInitializerExpression typeIdInitializerExpression = (ICASTTypeIdInitializerExpression) expression;

            evaluate(typeIdInitializerExpression.getTypeId());
            evaluate(typeIdInitializerExpression.getInitializer());
        } else if (expression instanceof ICPPASTDeleteExpression) {
            ICPPASTDeleteExpression deleteExpression = (ICPPASTDeleteExpression) expression;

            evaluate(deleteExpression.getOperand());
        } else if (expression instanceof ICPPASTNewExpression) {
            ICPPASTNewExpression newExpression = (ICPPASTNewExpression) expression;

            evaluate(newExpression.getNewInitializer());
            evaluate(newExpression.getNewPlacement());
            evaluate(newExpression.getTypeId());

            for (IASTExpression childExpression : newExpression.getNewTypeIdArrayExpressions()) {
                evaluate(childExpression);
            }
        } else if (expression instanceof ICPPASTSimpleTypeConstructorExpression) {
            ICPPASTSimpleTypeConstructorExpression simpleTypeConstructorExpression = (ICPPASTSimpleTypeConstructorExpression) expression;

            evaluate(simpleTypeConstructorExpression.getInitialValue());
        } else if (expression instanceof ICPPASTTypenameExpression) {
            ICPPASTTypenameExpression typenameExpression = (ICPPASTTypenameExpression) expression;

            evaluate(typenameExpression.getInitialValue());
        } else if (expression instanceof IGNUASTCompoundStatementExpression) {
            IGNUASTCompoundStatementExpression compoundStatementExpression = (IGNUASTCompoundStatementExpression) expression;

            evaluate(compoundStatementExpression.getCompoundStatement());
        }
    }

    /**
     * Attempts to evaluate the given initializer by evaluating each of its
     * parts
     *
     * @param initializer Initializer
     */
    private void evaluate(IASTInitializer initializer) throws DOMException {
        if (initializer == null) {
            return;
        } else if (initializer instanceof IASTInitializerExpression) {
            IASTInitializerExpression initializerExpression = (IASTInitializerExpression) initializer;

            evaluate(initializerExpression.getExpression());
        } else if (initializer instanceof IASTInitializerList) {
            IASTInitializerList initializerList = (IASTInitializerList) initializer;

            for (IASTInitializer childInitializer : initializerList.getInitializers()) {
                evaluate(childInitializer);
            }
        } else if (initializer instanceof ICASTDesignatedInitializer) {
            ICASTDesignatedInitializer designatedInitializer = (ICASTDesignatedInitializer) initializer;

            evaluate(designatedInitializer.getOperandInitializer());
        } else if (initializer instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer constructorInitializer = (ICPPASTConstructorInitializer) initializer;

            evaluate(constructorInitializer.getExpression());
        }
    }

    /**
     * Attempts to evaluate the given statement by evaluating each of its parts
     *
     * @param statement Statement
     */
    private void evaluate(IASTStatement statement) throws DOMException {
        if (statement instanceof IASTAmbiguousStatement) {
            IASTAmbiguousStatement ambiguousStatement = (IASTAmbiguousStatement) statement;

            for (IASTStatement childStatement : ambiguousStatement.getStatements()) {
                evaluate(childStatement);
            }
        } else if (statement instanceof IASTCompoundStatement) {
            IASTCompoundStatement compoundStatement = (IASTCompoundStatement) statement;

            for (IASTStatement childStatement : compoundStatement.getStatements()) {
                evaluate(childStatement);
            }
        } else if (statement instanceof IASTDeclarationStatement) {
            IASTDeclarationStatement declarationStatement = (IASTDeclarationStatement) statement;

            evaluate(declarationStatement.getDeclaration());
        } else if (statement instanceof IASTDoStatement) {
            IASTDoStatement doStatement = (IASTDoStatement) statement;

            evaluate(doStatement.getCondition());
            evaluate(doStatement.getBody());
        } else if (statement instanceof IASTExpressionStatement) {
            IASTExpressionStatement expressionStatement = (IASTExpressionStatement) statement;

            evaluate(expressionStatement.getExpression());
        } else if (statement instanceof IASTForStatement) {
            IASTForStatement forStatement = (IASTForStatement) statement;

            evaluate(forStatement.getInitializerStatement());
            evaluate(forStatement.getConditionExpression());
            evaluate(forStatement.getIterationExpression());
            evaluate(forStatement.getBody());

            if (forStatement instanceof ICPPASTForStatement) {
                evaluate(((ICPPASTForStatement) forStatement).getConditionDeclaration());
            }
        } else if (statement instanceof IASTIfStatement) {
            IASTIfStatement ifStatement = (IASTIfStatement) statement;

            evaluate(ifStatement.getConditionExpression());
            evaluate(ifStatement.getThenClause());
            evaluate(ifStatement.getElseClause());

            if (ifStatement instanceof ICPPASTIfStatement) {
                evaluate(((ICPPASTIfStatement) ifStatement).getConditionDeclaration());
            }
        } else if (statement instanceof IASTLabelStatement) {
            IASTLabelStatement labelStatement = (IASTLabelStatement) statement;

            evaluate(labelStatement.getNestedStatement());
        } else if (statement instanceof IASTReturnStatement) {
            IASTReturnStatement returnStatement = (IASTReturnStatement) statement;

            evaluate(returnStatement.getReturnValue());
        } else if (statement instanceof IASTSwitchStatement) {
            IASTSwitchStatement switchStatement = (IASTSwitchStatement) statement;

            evaluate(switchStatement.getControllerExpression());
            evaluate(switchStatement.getBody());

            if (switchStatement instanceof ICPPASTSwitchStatement) {
                evaluate(((ICPPASTSwitchStatement) switchStatement).getControllerDeclaration());
            }
        } else if (statement instanceof IASTWhileStatement) {
            IASTWhileStatement whileStatement = (IASTWhileStatement) statement;

            evaluate(whileStatement.getCondition());
            evaluate(whileStatement.getBody());

            if (whileStatement instanceof ICPPASTWhileStatement) {
                evaluate(((ICPPASTWhileStatement) whileStatement).getConditionDeclaration());
            }
        } else if (statement instanceof ICPPASTCatchHandler) {
            ICPPASTCatchHandler catchHandler = (ICPPASTCatchHandler) statement;

            evaluate(catchHandler.getDeclaration());
            evaluate(catchHandler.getCatchBody());
        } else if (statement instanceof ICPPASTTryBlockStatement) {
            ICPPASTTryBlockStatement tryBlockStatement = (ICPPASTTryBlockStatement) statement;

            for (ICPPASTCatchHandler catchHandler : tryBlockStatement.getCatchHandlers()) {
                evaluate(catchHandler);
            }

            evaluate(tryBlockStatement.getTryBody());
        }
    }

    /**
     * Attempts to evaluate the given type id by evaluating each of its parts
     *
     * @param typeId Type Id
     */
    private void evaluate(IASTTypeId typeId) throws DOMException {
        if (typeId != null) {
            evaluate(typeId.getAbstractDeclarator());
            evaluate(typeId.getDeclSpecifier());
        }
    }

    /**
     * Attempts to evaluate the given template parameter
     *
     * @param parameter Template parameter
     */
    @SuppressWarnings("unused")
    private void evaluate(ICPPASTTemplateParameter parameter) throws DOMException {
        if (parameter == null) {
            return;
        } else if (parameter instanceof CPPASTParameterDeclaration) {
            CPPASTParameterDeclaration parameterDeclaration = (CPPASTParameterDeclaration) parameter;

            evaluate(parameterDeclaration.getDeclarator());
            evaluate(parameterDeclaration.getDeclSpecifier());
        } else if (parameter instanceof ICPPASTSimpleTypeTemplateParameter) {
            ICPPASTSimpleTypeTemplateParameter simpleTypeTemplateParameter = (ICPPASTSimpleTypeTemplateParameter) parameter;

            evaluate(simpleTypeTemplateParameter.getDefaultType());
        } else if (parameter instanceof ICPPASTTemplatedTypeTemplateParameter) {
            ICPPASTTemplatedTypeTemplateParameter templatedTypeTemplateParameter = (ICPPASTTemplatedTypeTemplateParameter) parameter;

            evaluate(templatedTypeTemplateParameter.getDefaultValue());

            for (ICPPASTTemplateParameter childParameter : templatedTypeTemplateParameter.getTemplateParameters()) {
                evaluate(childParameter);
            }
        }
    }

    /**
     * Processes a detected reference given as type by determining its qualified
     * name and adding it to the current type definition
     *
     * @param type Referenced type
     */
    private void evaluate(IType type) throws DOMException {
        if (type == null) {
            return;
        } else if (type instanceof IArrayType) {
            evaluate(((IArrayType) type).getType());
        } else if (type instanceof ICompositeType) {
            ICompositeType compositeType = (ICompositeType) type;

            if (compositeType.getName() != null && compositeType.getName().length() > 0) {
                addReference(compositeType.getName());
            }
        } else if (type instanceof ICPPReferenceType) {
            evaluate(((ICPPReferenceType) type).getType());
        } else if (type instanceof IEnumeration) {
            // is that needed?
            for (IEnumerator enumerator : ((IEnumeration) type).getEnumerators()) {
                if (!type.isSameType(enumerator.getType())) {
                    evaluate(enumerator.getType());
                }
            }
        } else if (type instanceof IFunctionType) {
            IFunctionType functionType = (IFunctionType) type;

            for (IType parameterType : functionType.getParameterTypes()) {
                evaluate(parameterType);
            }

            evaluate(functionType.getReturnType());
        } else if (type instanceof IPointerType) {
            evaluate(((IPointerType) type).getType());
        } else if (type instanceof IQualifierType) {
            evaluate(((IQualifierType) type).getType());
        } else if (type instanceof ITypeContainer) {
            evaluate(((ITypeContainer) type).getType());
        } else if (type instanceof ITypedef) {
            evaluate(((ITypedef) type).getType());
        }
    }

    /**
     * Retrieves the name of the file wherein the type represented by given
     * fully qualified name has been declared
     *
     * @param fullyQualifiedName Fully qualified type name
     * @return Name of the file
     */
    public String getFilename(String fullyQualifiedName) {
        assert fullyQualifiedName != null;

        return m_filenames.get(fullyQualifiedName);
    }

    /**
     * Retrieves the references collected for the type represented by given
     * fully qualified name
     *
     * @param fullyQualifiedName Fully qualified type name
     * @return References
     */
    public Set<String> getReferences(String fullyQualifiedName) {
        assert fullyQualifiedName != null;

        return m_references.getRefs(fullyQualifiedName);
    }

    /**
     * Retrieves the compilation unit wherein the type represented by given
     * fully qualified name has been defined
     *
     * @param fullyQualifiedName Fully qualified type name
     * @return Name of the compilation unit
     */
    public String getSource(String fullyQualifiedName) {
        assert fullyQualifiedName != null;

        return m_sources.get(fullyQualifiedName);
    }

    /**
     * Retrieves the superclasses the type represented by given fully qualified
     * name has been derived from
     *
     * @param fullyQualifiedName Fully qualified type name
     * @return Superclasses
     */
    public List<String> getSuperclasses(String fullyQualifiedName) {
        assert fullyQualifiedName != null;

        return m_superclasses.get(fullyQualifiedName);
    }

    /**
     * Retrieves all detected types
     *
     * @return Detected types
     */
    public List<String> getTypes() {
        // seldom called
        return Arrays.asList(m_types.toArray(new String[m_types.size()]));
    }

    /**
     * Checks whether the type with the given fully qualified name is abstract
     *
     * @param fullyQualifiedName Fully qualified type name
     * @return TRUE, if the type is abstract, FALSE otherwise
     */
    public boolean isAbstract(String fullyQualifiedName) {
        assert fullyQualifiedName != null;

        return m_abstractClasses.contains(fullyQualifiedName);
    }

    /**
     * Checks whether the type with the given fully qualified name represents a
     * class
     *
     * @param fullyQualifiedName Fully qualified type name
     * @return TRUE, if the type is a class, FALSE otherwise
     */
    public boolean isClass(String fullyQualifiedName) {
        assert fullyQualifiedName != null;

        return m_classes.contains(fullyQualifiedName);
    }

    /**
     * Checks whether the type with the given fully qualified name is nested
     * within another type
     *
     * @param fullyQualifiedName Fully qualified type name
     * @return TRUE, if the type is nested, FALSE otherwise
     */
    public boolean isNested(String fullyQualifiedName) {
        assert fullyQualifiedName != null;

        return m_nestedTypes.contains(fullyQualifiedName);
    }

    /**
     * Attempts to parse the given compilation unit in order to retrieve all
     * declared type definitions
     *
     * @param compilationUnit Compilation unit
     */
    public void parse(File compilationUnit) throws IOException {
        assert compilationUnit != null;

        if (m_AssertionPattern != null) {
            m_NumberOfAssertions = 0;
        }

        IScannerInfo scannerInfo = new ScannerInfo(s_systemDefines, s_systemIncludes);

        ParserLanguage language = ParserLanguage.CPP;
        IParserLogService logService = new NullLogService();

        ICodeReaderFactory factory = null;
        if (fileEncoding != null) {
            factory = FileCodeReaderFactory.getInstance(fileEncoding.name());
        } else {
            factory = FileCodeReaderFactory.getInstance();
        }

        IScannerExtensionConfiguration scannerConfiguration = (language == ParserLanguage.C
                ? new GCCScannerExtensionConfiguration() : new GPPScannerExtensionConfiguration());
        
        CodeReader codeReader = factory.createCodeReaderForTranslationUnit(compilationUnit.getPath());
        //IScanner scanner = new CPreprocessor(codeReader, scannerInfo, language, logService, scannerConfiguration, factory);
                
        IncludeFileContentProvider provider = FileContentProviderAdapter.adapt(factory);
        FileContent fileContent = FileContent.adapt(codeReader);
        IScanner scanner = new CPreprocessor(fileContent, scannerInfo, language, logService, scannerConfiguration, provider);

        ISourceCodeParser parser = (language == ParserLanguage.CPP ? (s_isGNU ? new GNUCPPSourceParser(scanner,
                ParserMode.COMPLETE_PARSE, logService, new GPPParserExtensionConfiguration()) : new GNUCPPSourceParser(
                        scanner, ParserMode.COMPLETE_PARSE, logService, new ANSICPPParserExtensionConfiguration()))
                : (s_isGNU ? new GNUCSourceParser(scanner, ParserMode.COMPLETE_PARSE, logService,
                                new GCCParserExtensionConfiguration()) : new GNUCSourceParser(scanner, ParserMode.COMPLETE_PARSE,
                                logService, new ANSICParserExtensionConfiguration())));

        traverse(parser.parse());

        factory.getCodeReaderCache().remove(compilationUnit.getPath());
    }

    /**
     * Processes a detected reference given as name by determining its qualified
     * name and adding it to the current type definition
     *
     * @param name Name of referenced type
     */
    private void processReference(IASTName name) throws DOMException {
        String fullyQualifiedName = resolveFullyQualifiedTypeName(name);
        if ((fullyQualifiedName == null) || (fullyQualifiedName.length() == 0)) {
            // referred name not registered, so it is probably irrelevant.

            String text = new StringBuilder(name.getFileLocation().getFileName()).append(":").append(
                    name.getFileLocation().getStartingLineNumber()).append(" ").append(name).append(" not registered.")
                    .toString();

            s_logger.debug(text);

            return;
        }

        addReference(fullyQualifiedName);
    }

    /**
     * Attempts to resolve the fully qualified name of the type the given AST
     * name refers directly or a member of
     *
     * @param name AST name
     * @return Fully qualified type name
     */
    private String resolveFullyQualifiedTypeName(IASTName name) throws DOMException {
        assert name != null;

        String fullyQualifiedTypeName = null;

        IBinding binding = (name.getBinding() != null ? name.getBinding() : name.resolveBinding());

        if (binding != null) {
            if (binding instanceof IProblemBinding) {
                s_logger.log(Priority.WARN, "Parsing problem: " + ((IProblemBinding) binding).getMessage()
                        + " -> CompilationUnit[" + m_currentCompilationUnit + "]");
            } else if (binding.getScope() instanceof CPPClassScope) {
                CPPClassScope scope = (CPPClassScope) binding.getScope();
                if (scope.getPhysicalNode() instanceof CPPASTCompositeTypeSpecifier) {
                    CPPASTCompositeTypeSpecifier specifier = (CPPASTCompositeTypeSpecifier) scope.getPhysicalNode();
                    IASTName specName = specifier.getName();
                    if (specName.getBinding() instanceof CPPClassType) {
                        return resolveFullyQualifiedTypeName(specName);
                    }
                }
            } else if (binding instanceof ICPPMember && !(binding instanceof IProblemBinding)) {
                fullyQualifiedTypeName = calculateFullyQualifiedName(((ICPPMember) binding).getClassOwner()
                        .getQualifiedName());
                if (binding instanceof CPPMethod) {
                    IASTNode definition = ((CPPMethod) binding).getDefinition();
                    if (definition != null && definition instanceof CPPASTFunctionDeclarator) {
                        fullyQualifiedTypeName = m_nameOwners.get(definition);

                    }

                }
            } else if (binding instanceof ICPPClassType && !(binding instanceof IProblemBinding)) {
                String[] qualifiedName = ((ICPPClassType) binding).getQualifiedName();

                fullyQualifiedTypeName = calculateFullyQualifiedName(qualifiedName);

                String ano = calculateFullyQualifiedName(new String[]{
                    s_AnonymousNamespace, fullyQualifiedTypeName});
                if (m_types.contains(ano)) {
                    fullyQualifiedTypeName = ano;
                } else if (m_types.contains(fullyQualifiedTypeName)) {

                } else {
               // FIXME extras method for resolving already defined fqn,
                    // and creating new fqn
                    if (m_CurrentNamespace.size() > 0 && m_CurrentNamespace.peek() == s_AnonymousNamespace) {
                        fullyQualifiedTypeName = calculateFullyQualifiedName(new String[]{
                            s_AnonymousNamespace, fullyQualifiedTypeName});
                    }
                }
            } else if (binding instanceof CPPFunction && !(binding instanceof IProblemBinding)) {

            // Name represents a function, which is not within any
                // class scope thus not a member like e.g. a static main
                // function.
                IASTNode definition = ((CPPFunction) binding).getDefinition();
                IASTNode declarator = null;
                if (definition != null && definition instanceof CPPASTFunctionDefinition) {
                    declarator = ((CPPASTFunctionDefinition) definition).getDeclarator();
                } else if (definition != null && definition instanceof CPPASTFunctionDeclarator) {
                    declarator = definition;
                } else {
                    IASTNode[] declarations = ((CPPFunction) binding).getDeclarations();
                    if (declarations != null) {
                        for (IASTNode i : declarations) {
                            declarator = i;
                            break;
                        }
                    } else {
                        s_logger.debug("No declaration!");
                    }
                }

                fullyQualifiedTypeName = m_nameOwners.get(declarator);
            } else if (binding instanceof CPPTypedef && !(binding instanceof IProblemBinding)) {
                /*
                 * fullyQualifiedTypeName = calculateFullyQualifiedName(((CPPTypedef) binding).getQualifiedName());
                 * 
                 * if(binding.getScope() instanceof ICPPClassScope && !m_nestedTypes.contains(fullyQualifiedTypeName))
                 * m_nestedTypes.add(fullyQualifiedTypeName);
                 */

                if (((CPPTypedef) binding).getDefinition() != null
                        && ((CPPTypedef) binding).getDefinition().getParent() != null
                        && ((CPPTypedef) binding).getDefinition().getParent() instanceof IASTDeclarator) {
                    IASTNameOwner declarator = (IASTNameOwner) ((CPPTypedef) binding).getDefinition().getParent();
                    fullyQualifiedTypeName = m_nameOwners.get(declarator);
                } else {
                    s_logger.warn("Did not resolve typedef: " + name);

                    fullyQualifiedTypeName = calculateFullyQualifiedName(((CPPTypedef) binding).getQualifiedName());

                    if (binding.getScope() instanceof ICPPClassScope && !m_nestedTypes.contains(fullyQualifiedTypeName)) {
                        m_nestedTypes.add(fullyQualifiedTypeName);
                    }

                }
            } else if (binding instanceof CPPEnumeration) {
                // FIXME
                IASTName scopeName = (IASTName) binding.getScope().getScopeName();

                if (scopeName != null) {
                    return resolveFullyQualifiedTypeName(scopeName);
                } else {
                    return null;
                }

            } else if (binding instanceof CPPEnumerator) {

                CPPEnumerator enumerator = (CPPEnumerator) binding; // referrer

                if (enumerator.getDefinition() != null) {
                    IASTNode definition = enumerator.getDefinition();
                    if (definition instanceof CPPASTName && ((CPPASTName) definition).getBinding() instanceof CPPEnumerator) {
                        enumerator = (CPPEnumerator) ((CPPASTName) definition).getBinding(); // referee

                        IASTNode astNode = enumerator.getPhysicalNode();
                        if (astNode != null) {
                            IASTNode astEnumerator = astNode.getParent();
                            if (astEnumerator != null) {
                                IASTNode astEnumerationSpecifier = astEnumerator.getParent();
                                if (astEnumerationSpecifier != null) {
                                    if (astEnumerationSpecifier instanceof IASTNameOwner) {
                                        IASTNameOwner specifier = (IASTNameOwner) astEnumerationSpecifier;

                                        fullyQualifiedTypeName = m_nameOwners.get(specifier);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Determine fully qualified name via scope instead
                s_logger.log(Priority.DEBUG, binding.getClass().getCanonicalName());
            }
        }

        return fullyQualifiedTypeName;
    }

    /**
     * Traverses the AST of the given translation units and collects all static
     * type references and definitions
     *
     * @param translationUnit Translation unit
     */
    private void traverse(IASTTranslationUnit translationUnit) {
        assert translationUnit != null;

        try {
            for (IASTProblem problem : translationUnit.getPreprocessorProblems()) {
                // inform user about missing include files
                if (problem.getID() == IASTProblem.PREPROCESSOR_INCLUSION_NOT_FOUND) {
                    s_logger.error(problem.getMessage());
                }
            }

            // Compile all direct includes
            IDependencyTree dependencyTree = translationUnit.getDependencyTree();
            m_currentCompilationUnit = dependencyTree.getTranslationUnitPath().intern();
            m_currentIncludes = new ArrayList<String>();

            for (IASTInclusionNode inclusionNode : dependencyTree.getInclusions()) {
                IASTPreprocessorIncludeStatement preprocessorIncludeStatement = inclusionNode.getIncludeDirective();

                if (preprocessorIncludeStatement.isActive() && preprocessorIncludeStatement.isResolved()
                        && !preprocessorIncludeStatement.isSystemInclude()) {
                    m_currentIncludes.add(preprocessorIncludeStatement.getPath());
                }
            }

            for (IASTDeclaration declaration : translationUnit.getDeclarations()) {
                evaluate(declaration);
            }

            translationUnit.accept(new ExprASTVisitor());

        } catch (Exception e) {
            s_logger.log(Priority.ERROR, e.getMessage());
            e.printStackTrace();
        } finally {
            // release the Abstract Syntax Tree after each Compilation Unit
            m_nameOwners.clear();
        }
    }

    /**
     * Calculate qualified name for free functions
     *
     * @param fantasy "free_functions"
     * @param name function name
     * @return namespace::pseudotype::functionname
     */
    private String[] getNamespaceQualifiedName(String fantasy, String name) {
        String[] splittedName = name.split("::");
        String[] qualifiedName = m_CurrentNamespace.toArray(new String[m_CurrentNamespace.size() + 1
                + splittedName.length]);
        for (int i = 0; i < splittedName.length - 1; i++) {
            qualifiedName[m_CurrentNamespace.size() + i] = splittedName[i];
        }
        qualifiedName[m_CurrentNamespace.size() + splittedName.length - 1] = fantasy;
        qualifiedName[m_CurrentNamespace.size() + splittedName.length] = splittedName[splittedName.length - 1];
        return qualifiedName;
    }

    /**
     * Register source file and compilation unit of a type definition.
     *
     * @param fullyQualifiedName newly defined type
     * @param node source location
     */
    private void addTypeSource(String fullyQualifiedName, IASTNode node) {
        String containingFilename = node.getContainingFilename();
        if (m_currentCompilationUnit.equals(containingFilename) || m_currentIncludes.contains(containingFilename)) {
            // m_sources.put(fullyQualifiedName, m_currentCompilationUnit);
            if (m_sources.get(fullyQualifiedName) == null) {
                m_sources.put(fullyQualifiedName, containingFilename);
            }
        }
        if (m_filenames.get(fullyQualifiedName) == null) {
            m_filenames.put(fullyQualifiedName, containingFilename);
        }
    }

    public void setAssertionPattern(String pattern) {
        assert m_AssertionPattern == null;
        assert pattern != null;
        assert pattern.length() > 0;
        m_AssertionPattern = Pattern.compile(pattern);
        m_NumberOfAssertions = 0;

    }

    public int getNumberOfAssertions() {
        return m_NumberOfAssertions;
    }

    private boolean isAssertion(String word) {
        assert word != null;

        if ((m_AssertionPattern != null) && (word.length() > 0)) {
            boolean matches = m_AssertionPattern.matcher(word).matches();
            if (matches) {
                matches = true;
            }
            return matches;
        }

        return false;
    }

    class ExprASTVisitor extends ASTVisitor {

        public ExprASTVisitor() {
            this.shouldVisitExpressions = true;
        }

        public int visit(IASTExpression expression) {
            // if it�s a function call
            if (expression instanceof IASTFunctionCallExpression) {
                IASTFunctionCallExpression fce = (IASTFunctionCallExpression) expression;
                IASTExpression astExpr = fce.getFunctionNameExpression();
                String fnSig = astExpr.getRawSignature();
                if (isAssertion(fnSig)) {
                    ++m_NumberOfAssertions;
                }
            }
            return PROCESS_CONTINUE;
        }

    }

    private int m_NumberOfAssertions = -1;

    private Pattern m_AssertionPattern;

    private static String s_AnonymousNamespace = "anonymous_namespace";

    private static String s_freeFunctions = "free_functions";

    private static String s_globalVariables = "global_variables";

    private static String s_globalTypedefs = "global_typedefs";

    private Stack<String> m_CurrentNamespace = new Stack<String>();

    private boolean m_Static = false;

    private boolean m_Extern = false;

    private boolean m_Friend = false;

    private boolean m_Member = false;

    private boolean m_Body = false;

    private boolean m_Typedef = false;

    private IASTName m_Referred = null;

    public void setFileEncoding(Charset fileEncoding) {
        this.fileEncoding = fileEncoding;
    }
}
