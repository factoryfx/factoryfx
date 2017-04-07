package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.StaticSourceFile;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.*;
import de.factoryfx.javascript.data.attributes.types.Javascript;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ContentAssist {


    public NavigableMap<Integer,List<Proposal>> findProposals(List<SourceFile> externalSources, Javascript<?> text) {
        NavigableMap<Integer,List<Proposal>> ret = new TreeMap<Integer,List<Proposal>>() {
            @Override
            public List<Proposal> put(Integer key, List<Proposal> value) {
                return super.put(key, value);
            }
        };
        try {
            final Compiler compiler = createCompiler();
            ArrayList<SourceFile> internalSource = new ArrayList<>();
            internalSource.add(SourceFile.fromCode("intern", text.getCode()));
            ArrayList<SourceFile> externalSource = new ArrayList<>(externalSources);
            externalSource.add(SourceFile.fromCode("decl",text.getHeaderCode()));
            compiler.compile(externalSource,
                    internalSource, creataCompilerOptions());

            Node root = compiler.getRoot();
            NodeTraversal.ScopedCallback scopedCallback = new NodeTraversal.ScopedCallback() {

                Stack<TypedScope> scopes = new Stack<>();
                @Override
                public void enterScope(NodeTraversal t) {
                    scopes.add(t.getTypedScope());
                }

                @Override
                public void exitScope(NodeTraversal t) {
                    scopes.pop();
                }

                @Override
                public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
                    if ("intern".equals(n.getSourceFileName()))
                        proposals(text.getCode(),scopes.peek(),n,ret);
                    return true;
                }

                @Override
                public void visit(NodeTraversal t, Node n, Node parent) {
                }
            };
            NodeTraversal.traverseTyped(compiler,root,scopedCallback);

        } catch (RuntimeException ignored) {
            ignored.printStackTrace();
        }
        return ret;

    }

    private void proposals(String code, TypedScope scope, Node inspectedNode, Map<Integer,List<Proposal>> proposals) {
        List<Var> vars = getVars(scope);
        ArrayList<Var> internalVars = new ArrayList<>();
        ArrayList<Var> externalVars = new ArrayList<>();
        vars.forEach(s->{
            if (Optional.ofNullable(s.getSourceFile()).map(f->f.isExtern() || "extern".equals(f.getName()) || f.getName().startsWith("library")).orElse(false)) {
                externalVars.add(s);
            } else {
                if (s.getNameNode().getStaticSourceFile() != null)
                    internalVars.add(s);
            }
        });
        Collections.sort(externalVars,(v1,v2)->{
            boolean ext1 = Optional.ofNullable(v1.getSourceFile()).map(v-> isFromProjectSources(v)).orElse(false);
            boolean ext2 = Optional.ofNullable(v2.getSourceFile()).map(v-> isFromProjectSources(v)).orElse(false);
            if (ext1) {
                if (ext2) {
                    return compareJsType(v1, v2);
                }
                return -1;
            }
            if (ext2)
                return 1;
            return compareJsType(v1, v2);
        });
        Collections.sort(internalVars,(v1,v2)->{
            return compareJsType(v1,v2);
        });
        if (inspectedNode.getToken() == com.google.javascript.rhino.Token.SCRIPT) {
            List<Proposal> ret = createScriptProposals(externalVars);
            proposals.put(0,ret);
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.BLOCK) {
            createBlockProposals(code,inspectedNode, proposals, internalVars, externalVars);
        } else if (inspectedNode.getToken() == Token.EXPR_RESULT) {
            createExprResultProposals(code, inspectedNode, proposals, internalVars, externalVars);
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.NEW) {
            createNewProposals(inspectedNode, proposals, internalVars, externalVars);
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.FUNCTION_TYPE) {
            List<Proposal> ret = new ArrayList<>();
            proposals.put(inspectedNode.getSourceOffset(),ret);
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.GETPROP) {
            createGetPropProposals(code, inspectedNode, proposals, internalVars, externalVars);
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.FUNCTION) {
            /*
            Node firstChild = inspectedNode.getFirstChild();
            int offset = firstChild.getSourceOffset();
            if (offset > -1) {
                proposals.put(offset,Collections.emptyList());
            }
            */
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.CALL) {
            createCallProposals(inspectedNode, proposals, internalVars, externalVars);
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.RETURN) {
            createReturnProposals(inspectedNode, proposals, internalVars, externalVars);
        } else if (inspectedNode.getToken() == com.google.javascript.rhino.Token.VAR) {
            createVarProposals(code, inspectedNode, proposals, internalVars, externalVars);
        } else if (Arrays.asList(com.google.javascript.rhino.Token.SUB, Token.ADD, Token.MUL, Token.DIV).contains(inspectedNode.getToken())) {
            createArithmeticProposals(code, inspectedNode, proposals, internalVars, externalVars);
        } else if (Arrays.asList(Token.ASSIGN, Token.ASSIGN_ADD, Token.ASSIGN_DIV, Token.ASSIGN_SUB, Token.ASSIGN_MUL).contains(inspectedNode.getToken())) {
            createAssignProposals(code, inspectedNode, proposals, internalVars, externalVars);
        } else {
//            System.out.println("Unhandled: "+inspectedNode.getToken()+" at "+inspectedNode.getLineno()+"/"+inspectedNode.getStaticSourceFile().getColumnOfOffset(inspectedNode.getSourceOffset()));
        }
    }

    private boolean isFromProjectSources(StaticSourceFile v) {
        return "extern".equals(v.getName()) || v.getName().equals("decl");
    }

    private void createVarProposals(String code, Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        Node currentNode = inspectedNode.getFirstChild();
        if (currentNode != null) {
            createExprResultProposals(code, currentNode, proposals, internalVars, externalVars);
            currentNode = currentNode.getFirstChild();
            if (currentNode != null && isIntern(currentNode)) {
                ArrayList<Var> visibleVars = new ArrayList<>();
                internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < inspectedNode.getSourceOffset())
                        .forEach(visibleVars::add);
                externalVars.stream()
                        .sorted((v1,v2)->{
                            boolean decl1 = isFromDeclarations(v1.getNameNode());
                            boolean decl2 = isFromDeclarations(v1.getNameNode());
                            if (decl1 == decl2)
                                return 0;
                            if (decl1)
                                return -1;
                            return 1;
                        })
                        .forEach(visibleVars::add);
                Collections.sort(visibleVars,this::compareTypeQuality);
                int idx = skipWhitespaceBefore(code, currentNode);
                proposals.put(idx,visibleVars.stream().map(v->new Proposal(v.getName())).collect(Collectors.toList()));
            }
        }
    }

    private int skipWhitespaceBefore(String code, Node currentNode) {
        int idx = currentNode.getSourceOffset();
        while (idx > 0 && Character.isWhitespace(code.charAt(idx-1))) {
            --idx;
        }
        return idx;
    }

    private void createArithmeticProposals(String code, Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        JSType returnType = inspectedNode.getJSType();
        Node currentNode = inspectedNode.getFirstChild();
        while (currentNode != null) {
            if (isIntern(currentNode)) {
                addArithmeticAndAssignProposals(code, inspectedNode, proposals, internalVars, externalVars, returnType, currentNode);
            }
            currentNode = currentNode.getNext();
        }
    }

    private void addArithmeticAndAssignProposals(String code, Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars, JSType returnType, Node currentNode) {
        ArrayList<Var> visibleVars = new ArrayList<>();
        internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < inspectedNode.getSourceOffset())
                .filter(v1->canCastTo(v1.getNameNode().getJSType(),returnType))
                .forEach(visibleVars::add);
        externalVars.stream()
                .filter(v1->canCastTo(v1.getNameNode().getJSType(),returnType))
                .forEach(visibleVars::add);
        Collections.sort(visibleVars,this::compareTypeQuality);
        int idx = skipWhitespaceBefore(code, currentNode);
        proposals.put(idx,visibleVars.stream().map(v->new Proposal(v.getName())).collect(Collectors.toList()));
    }

    private void createAssignProposals(String code, Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        JSType returnType = inspectedNode.getJSType();
        Node currentNode = inspectedNode.getFirstChild();
        if (currentNode != null)
            currentNode = currentNode.getNext();
        if (currentNode != null && isIntern(currentNode)) {
            addArithmeticAndAssignProposals(code, inspectedNode, proposals, internalVars, externalVars, returnType, currentNode);
        }
    }

    private void createReturnProposals(Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        Node currentNode = inspectedNode;
        while (currentNode != null && isIntern(currentNode) && currentNode.getToken() != Token.FUNCTION) {
            currentNode = currentNode.getParent();
        }
        if (currentNode != null && isIntern(currentNode)) {
            ArrayList<Var> visibleVars = new ArrayList<>();
            internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < inspectedNode.getSourceOffset())
                    .forEach(visibleVars::add);
            externalVars.stream()
                    .forEach(visibleVars::add);
            JSType functionType = currentNode.getJSType();
            Optional<JSType> functionReturnType = Optional.ofNullable(functionType).filter(f->functionType instanceof FunctionType)
                    .map(f->(FunctionType)f).flatMap(ft->Optional.ofNullable(ft.getReturnType()));
            functionReturnType.ifPresent(ret->{
                visibleVars.removeIf(v->v.getNameNode().getJSType() == null);
                visibleVars.removeIf(v->!canCastTo(v.getNameNode().getJSType(),ret));
            });
            Collections.sort(visibleVars,this::compareTypeQuality);
            proposals.put(inspectedNode.getSourceOffset()+ Token.RETURN.name().length()+1,visibleVars.stream().map(v->new Proposal(v.getName())).collect(Collectors.toList()));
        }
    }

    private void createCallProposals(Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        Node firstChild = inspectedNode.getFirstChild();
        ArrayList<Var> visiableVars = new ArrayList<>();
        internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < inspectedNode.getSourceOffset())
                .forEach(visiableVars::add);
        externalVars.stream()
                .forEach(visiableVars::add);
        if (firstChild != null && firstChild.getJSType() != null && firstChild.getJSType() instanceof FunctionType) {
            FunctionType ft = (FunctionType)firstChild.getJSType();
            int paramNum = 0;
            parameterLoop: for (Node paramterNode : ft.getParameters()) {
                ++paramNum;
                ArrayList<Var> filteredVars = new ArrayList<>();
                JSType parameterNodeType = fixNullType(paramterNode.getJSType());
                visiableVars.stream().filter(v -> noTypeFound(v) || viableAssignment(v.getNameNode().getJSType(), parameterNodeType)).forEach(filteredVars::add);
                Node argumentNode = inspectedNode.getChildCount() > paramNum?inspectedNode.getChildAtIndex(paramNum):null;
                Collections.sort(filteredVars, this::compareTypeQuality);
                ArrayList<Proposal> proposalList = new ArrayList<>(filteredVars.stream().map(v -> new Proposal(v.getName())).collect(Collectors.toList()));
                if (parameterNodeType.isFunctionType() && (argumentNode == null || !argumentNode.getJSType().isFunctionType())) {
                    String newFunction = createFunctionDeclaration(parameterNodeType.toMaybeFunctionType());
                    proposalList.add(0,new Proposal(newFunction));
                }
                if (argumentNode != null) {
                    proposals.put(argumentNode.getSourceOffset(), proposalList);
                } else {
                    proposals.put(inspectedNode.getSourceOffset()+inspectedNode.getLength()-1, proposalList);
                    break parameterLoop;
                }
            }
        } else {
            proposals.put(inspectedNode.getSourceOffset()+1,visiableVars.stream().map(v->new Proposal(v.getName())).collect(Collectors.toList()));
        }
    }

    private String createFunctionDeclaration(FunctionType functionType) {
        StringBuilder sb = new StringBuilder();
        sb.append("function(");
        HashSet<String> usedParameterNames = new HashSet<>();
        functionType.getParameters().forEach(n->{
            JSType argumentType = n.getJSType();
            sb.append("/** @type {");
            String displayName = argumentType.getDisplayName();
            if (!argumentType.isNullable())
                sb.append("!");
            sb.append(displayName).append("} */ ");
            String parameterName = n.getOriginalName();
            if (parameterName == null)
                parameterName = Character.toLowerCase(displayName.charAt(0))+displayName.substring(1);
            String tryName = parameterName;
            int i = 2;
            while (!usedParameterNames.add(tryName)) {
                tryName = parameterName + i;
                ++i;
            }
            sb.append(tryName);
            sb.append(", ");
        });
        if (functionType.getParameters().iterator().hasNext()) {
            sb.setLength(sb.length()-2);
        }
        sb.append(") { }");
        return sb.toString();
    }

    private void createGetPropProposals(String code, Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        Node firstChild = inspectedNode.getFirstChild();
        Optional<JSType> firstChildType = Optional.ofNullable(firstChild.getJSType());
        if (firstChildType.isPresent()) {
            JSType t = fixNullType(firstChildType.get());
            if (t instanceof ObjectType) {
                ObjectType ot = ObjectType.cast(t);

                ArrayList<String> internalProperties = new ArrayList<>();
                ArrayList<String> externalProperties = new ArrayList<>();
                Comparator<String> sortProps = (s1,s2)->{
                    Optional<JSType> propertyType1 = Optional.ofNullable(ot.getPropertyType(s1));
                    Optional<JSType> propertyType2 = Optional.ofNullable(ot.getPropertyType(s2));
                    if (propertyType1.isPresent()) {
                        if (!propertyType2.isPresent())
                            return -1;
                        return compareJsType(propertyType1.get(),propertyType2.get());
                    }
                    if (propertyType2.isPresent())
                        return 1;
                    return 0;
                };

                ot.getPropertyNames().stream().filter(n -> !n.startsWith("__"))
                        .forEach(n -> {
                            if (isIntern(ot.getPropertyDefSite(n))) {
                                internalProperties.add(n);
                            } else {
                                externalProperties.add(n);
                            }
                        });
                internalProperties.sort(sortProps);
                externalProperties.sort(sortProps);
                UnaryOperator<String> addFunctionBrackets = s -> {
                    Optional<JSType> propertyType = Optional.ofNullable(ot.getPropertyType(s));
                    boolean isFunction = propertyType.map(pt -> pt instanceof FunctionType).orElse(false);
                    return isFunction ? s + "()" : s;
                };
                internalProperties.replaceAll(addFunctionBrackets);
                externalProperties.replaceAll(addFunctionBrackets);
                List<Proposal> ret = new ArrayList<>();
                int sourcePosition = firstChild.getSourceOffset();
                if (sourcePosition > -1) {
                    sourcePosition += firstChild.getLength();
                    while (sourcePosition < code.length() &&
                            (Character.isWhitespace(code.charAt(sourcePosition)))) {
                        ++sourcePosition;
                    }
                    if (sourcePosition < code.length() && '.' == code.charAt(sourcePosition))
                        ++sourcePosition;
                }
                int sp = sourcePosition;
                if (sp > -1) {
                    if (ot instanceof FunctionType) {
                        internalVars.stream().filter(v -> v.getNameNode().getSourceOffset() < sp).map(v -> new Proposal(v.getName())).forEach(ret::add);
                        internalProperties.stream().map(s -> new Proposal(s)).forEach(ret::add);
                        externalVars.stream().map(v -> new Proposal(v.getName())).forEach(ret::add);
                        externalProperties.stream().map(s -> new Proposal(s)).forEach(ret::add);
                    } else {
                        internalProperties.stream().map(s -> new Proposal(s)).forEach(ret::add);
                        externalProperties.stream().map(s -> new Proposal(s)).forEach(ret::add);
                    }
                    proposals.put(sourcePosition, ret);
                }
            }
        }
    }

    private void createNewProposals(Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        Node firstChild = inspectedNode.getFirstChild();
        List<Proposal> ret = new ArrayList<>();
        Predicate<Var> isClass = v -> v.getNode().getJSType().isConstructor();
        internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < firstChild.getSourceOffset())
                .filter(isClass)
                .map(v->new Proposal(v.getName())).forEach(ret::add);
        externalVars.stream()
                .filter(isClass)
                .map(v->new Proposal(v.getName())).forEach(ret::add);
        int sourceOffset = firstChild.getSourceOffset()+1;
        if (sourceOffset < 1) {
            sourceOffset = inspectedNode.getSourceOffset()+inspectedNode.getLength();
        }
        proposals.put(sourceOffset,ret);
    }

    private void createBlockProposals(String code, Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        List<Proposal> ret = new ArrayList<>();
        internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < inspectedNode.getSourceOffset()).map(v->new Proposal(v.getName())).forEach(ret::add);
        externalVars.stream().map(v->new Proposal(v.getName())).forEach(ret::add);
        proposals.put(inspectedNode.getSourceOffset()+1,ret);
        Node lastChild = inspectedNode.getLastChild();
        ret = new ArrayList<>();
        internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < inspectedNode.getSourceOffset()).map(v->new Proposal(v.getName())).forEach(ret::add);
        externalVars.stream().map(v->new Proposal(v.getName())).forEach(ret::add);
        int sourceOffset = lastChild != null?lastChild.getSourceOffset()+lastChild.getLength():inspectedNode.getSourceOffset()+inspectedNode.getLength();
        if (code.length() > sourceOffset && Character.isWhitespace(code.charAt(sourceOffset)))
            ++sourceOffset;
        if (code.length() > sourceOffset && !proposals.containsKey(sourceOffset)) {
            proposals.put(sourceOffset, ret);
        }
    }

    private void createExprResultProposals(String code, Node inspectedNode, Map<Integer, List<Proposal>> proposals, ArrayList<Var> internalVars, ArrayList<Var> externalVars) {
        List<Proposal> ret = new ArrayList<>();
        Node lastChild = inspectedNode.getLastChild();
        internalVars.stream().filter(v->v.getNameNode().getSourceOffset() < inspectedNode.getSourceOffset()).map(v->new Proposal(v.getName())).forEach(ret::add);
        externalVars.stream().map(v->new Proposal(v.getName())).forEach(ret::add);
        int sourceOffset = lastChild != null?lastChild.getSourceOffset()+lastChild.getLength():inspectedNode.getSourceOffset()+inspectedNode.getLength();
        while (sourceOffset < code.length() && (!Character.isWhitespace(sourceOffset))) {
            ++sourceOffset;
            if (code.charAt(sourceOffset-1) == ';')
                break;
        }
        if (sourceOffset < code.length() && !proposals.containsKey(sourceOffset)) {
            proposals.put(sourceOffset, ret);
        }
        sourceOffset = inspectedNode.getSourceOffset();
        if (sourceOffset >= 0) {
            proposals.put(sourceOffset, ret);
        }
    }

    private List<Proposal> createScriptProposals(ArrayList<Var> externalVars) {
        List<Proposal> ret = new ArrayList<>();
        externalVars.stream().map(v->new Proposal(v.getName())).forEach(ret::add);
        return ret;
    }

    private int compareJsType(Var v1, Var v2) {
        JSType t1 = fixNullType(v1.getNameNode().getJSType());
        JSType t2 = fixNullType(v2.getNameNode().getJSType());
        return compareJsType(t1, t2);
    }

    private int compareJsType(JSType t1, JSType t2) {
        t1 = fixNullType(t1);
        t2 = fixNullType(t2);
        if (isPreferredValueType(t1)) {
            if (isPreferredValueType(t2)) {
                return 0;
            }
            return -1;
        }
        if (isPreferredValueType(t2)) {
            return 1;
        }
        if (t1.isConstructor()) {
            if (t2.isConstructor())
                return 0;
            return 1;
        }
        if (t2.isConstructor())
            return -1;
        if (t1.isFunctionType()) {
            if (t2.isFunctionType()) {
                return compareSource(((FunctionType)t1).getSource(),((FunctionType)t2).getSource());
            }
            return -1;
        }
        if (t2.isFunctionType())
            return 1;
        return 0;
    }

    private int compareSource(Node source1, Node source2) {
        if (source1 == null) {
            return source2 == null?0:1;
        }
        if (source2 == null)
            return -1;
        if (isFromProjectSources(source1.getStaticSourceFile())) {
            if (isFromProjectSources(source2.getStaticSourceFile())) {
                return 0;
            }
            return -1;
        }
        if (isFromProjectSources(source2.getStaticSourceFile()))
            return 1;
        return 0;


    }

    private boolean isPreferredValueType(JSType t1) {
        if (t1.isConstructor())
            return false;
        return t1.isInstanceType() || t1.isNumberValueType() || t1.isBooleanValueType() || t1.isStringValueType();
    }

    private int compareTypeQuality(Var v1, Var v2) {
        if (v1.getNameNode().getJSType() == null) {
            if (v1.getNameNode().getJSType() != null)
                return 1;
            return 0;
        }
        if (v2.getNameNode().getJSType() == null) {
            return -1;
        }
        JSType v1Type = v1.getNameNode().getJSType();
        JSType v2Type = v2.getNameNode().getJSType();
        return compareTypeQuality(v1Type, v2Type);
    }

    private int compareTypeQuality(JSType v1Type, JSType v2Type) {
        if (v1Type.isUnknownType()) {
            if (!v2Type.isUnknownType())
                return 1;
            return 0;
        }
        if (v2Type.isUnknownType())
            return -1;
        if (v1Type.isUnionType()) {
            if (!v2Type.isUnionType())
                return 1;
            return 0;
        }
        if (v2Type.isUnionType())
            return -1;
        return 0;
    }

    private boolean noTypeFound(Var v) {
        return v.getNameNode().getJSType() == null;
    }

    private boolean viableAssignment(JSType rhsType, JSType lhsType) {
        if (lhsType == null || lhsType.isUnknownType())
            return true;
        if (rhsType == null || rhsType.isUnknownType())
            return false;
        if (lhsType.isFunctionType()) {
            if (!rhsType.isFunctionType())
                return false;
            JSType lhsReturnType = lhsType.toMaybeFunctionType().getReturnType();
            JSType rhsReturnType = rhsType.toMaybeFunctionType().getReturnType();
            if (!viableAssignment(rhsReturnType,lhsReturnType))
                return false;
            ArrayList<JSType> arguments = new ArrayList<>();
            rhsType.toMaybeFunctionType().getParameters().forEach(n->arguments.add(n.getJSType()));
            return lhsType.toMaybeFunctionType().acceptsArguments(arguments);
        }
        boolean canCast = canCastTo(rhsType,lhsType);
        return canCast;
    }

    boolean canCastTo(JSType rhsType, JSType lhsType) {
        if (fixNullType(rhsType).isUnknownType())
            return fixNullType(lhsType).isUnknownType();
        return rhsType.canCastTo(lhsType);
    }


    private JSType fixNullType(JSType jsType) {
        if (jsType instanceof UnionType) {
            UnionType ut = (UnionType)jsType;
            if (ut.getAlternates().size() == 2 && ut.getAlternates().stream().anyMatch(t->t instanceof NullType)) {
                    return ut.getAlternates().stream().filter(t->!(t instanceof NullType)).findAny().get();
            }
        }
        return jsType;
    }


    private CompilerOptions creataCompilerOptions() {
        CompilerOptions options = new CompilerOptions();

        options.setNewTypeInference(true);
//        options.setCheckTypes(true);
        options.setCheckSymbols(true);
        options.setCheckSuspiciousCode(false);
        options.setInferTypes(true);
        options.setInferConst(true);
        options.setClosurePass(true);
        options.setPreserveDetailedSourceInfo(true);
        options.setContinueAfterErrors(true);
        options.setSkipNonTranspilationPasses(false);
        options.setIncrementalChecks(CompilerOptions.IncrementalCheckMode.OFF);
        options.setChecksOnly(true);
        options.setCheckTypes(true);
//        options.setExtractPrototypeMemberDeclarations(false);
//        options.setDevirtualizePrototypeMethods(false);

        WarningLevel.QUIET.setOptionsForWarningLevel(options);
        return options;
    }

    private Compiler createCompiler() {
        try {
            Compiler closureCompiler  = new Compiler(new PrintStream(new DiscardOutputStream(), false, "UTF-8"));
            closureCompiler.disableThreads();
            closureCompiler.setLoggingLevel(Level.INFO);
            return closureCompiler;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isIntern(Node node) {
        return !node.isFromExterns();
    }

    private boolean isFromDeclarations(Node node) {
        return "decl".equals(node.getSourceFileName());
    }


    public List<Var> getVars(Scope scope) {
        ArrayList<Var> ret = new ArrayList<>();
        while (scope != null) {
            scope.getVarIterable().forEach(ret::add);
            scope = scope.getParent();
        }
        return ret;
    }
}
