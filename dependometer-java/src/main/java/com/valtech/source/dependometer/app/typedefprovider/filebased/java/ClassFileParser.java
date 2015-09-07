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

package com.valtech.source.dependometer.app.typedefprovider.filebased.java;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.provider.AbstractTypeDefinitionProvider;
import com.valtech.source.dependometer.app.core.provider.RefactoringIf;

/**
 * Bytecode parser based on the ASM bytecode engineering library supporting new Java 5 language features like Generics,
 * Annotations etc.
 * 
 * @author Carsten Kaiser (carsten.kaiser@valtech.de)
 * @version 2.0
 */
public class ClassFileParser
{
   /**
    * Inspects the bytecode of a Java 5 class file for outgoing references
    * 
    * @author Carsten Kaiser (carsten.kaiser@valtech.de)
    * @version 2.0
    */
   private class BytecodeInspector implements ClassVisitor
   {
      /**
       * Inspects any kind of annotation contained in the parsed class file for outgoing references
       * 
       * @author Carsten Kaiser (carsten.kaiser@valtech.de)
       * @version 2.0
       */
      private class AnnotationInspector implements AnnotationVisitor
      {
         /**
          * Type descriptor
          */
         private String m_descriptor = null;

         /**
          * Indicator whether this annotation is visible on runtime
          */
         private boolean m_visible = false;

         /**
          * Creates a new annotation inspector holding the given type descriptor and the marker whether the represented
          * annotation is visible on runtime
          * 
          * @param descriptor Type descriptor
          * @param visible Indicator whether the annotation is visible on runtime
          */
         private AnnotationInspector(String descriptor, boolean visible)
         {
            m_descriptor = descriptor;
            m_visible = visible;
         }

         /**
          * @see org.objectweb.asm.AnnotationVisitor#visit(String, Object)
          */
         public void visit(String name, Object value)
         {
            if (value != null && value instanceof Type)
            {
               inspectType((Type)value);
            }
         }

         /**
          * @see org.objectweb.asm.AnnotationVisitor#visitAnnotation(String, String)
          */
         public AnnotationVisitor visitAnnotation(String name, String descriptor)
         {
            return new AnnotationInspector(descriptor, m_visible);
         }

         /**
          * @see org.objectweb.asm.AnnotationVisitor#visitArray(String)
          */
         public AnnotationVisitor visitArray(String name)
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.AnnotationVisitor#visitEnd()
          */
         public void visitEnd()
         {
            Type type = Type.getType(m_descriptor);

            if (type != null)
            {
               inspectType(type);
            }
         }

         /**
          * @see org.objectweb.asm.AnnotationVisitor#visitEnum(String, String, String)
          */
         public void visitEnum(String name, String descriptor, String value)
         {
            Type type = Type.getType(descriptor);

            if (type != null)
            {
               inspectType(type);
            }
         }
      }

      /**
       * Inspects any kind of field contained in the parsed class file for outgoing references
       * 
       * @author Carsten Kaiser (carsten.kaiser@valtech.de)
       * @version 2.0
       */
      private class FieldInspector implements FieldVisitor
      {
         /**
          * Associated type descriptor
          */
         private String m_descriptor = null;

         /**
          * Associated signature
          */
         private String m_signature = null;

         /**
          * Indicator whether this is a synthetic field
          */
         private boolean m_synthetic = false;

         /**
          * Creates a new field inspector holding type descriptor and signature if available
          * 
          * @param descriptor Type descriptor
          * @param signature Signature
          */
         private FieldInspector(String descriptor, String signature, boolean synthetic)
         {
            m_descriptor = descriptor;
            m_signature = signature;
            m_synthetic = synthetic;
         }

         /**
          * @see org.objectweb.asm.FieldVisitor#visitAnnotation(String, boolean)
          */
         public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
         {
            return new AnnotationInspector(descriptor, visible);
         }

         /**
          * @see org.objectweb.asm.FieldVisitor#visitAttribute(Attribute)
          */
         public void visitAttribute(Attribute attribute)
         {
            if (SYNTHETIC.equals(attribute.type))
            {
               m_synthetic = true;
            }
         }

         /**
          * @see org.objectweb.asm.FieldVisitor#visitEnd()
          */
         public void visitEnd()
         {
            Type type = Type.getType(m_descriptor);

            if (type != null && !m_synthetic)
            {
               inspectType(type);
            }

            if (m_signature != null)
            {
               new SignatureReader(m_signature).acceptType(new SignatureInspector());
            }
         }
      }

      /**
       * Inspects any kind of method contained in the parsed class file for outgoing references
       * 
       * @author Carsten Kaiser (carsten.kaiser@valtech.de)
       * @version 2.0
       */
      private class MethodInspector implements MethodVisitor
      {
         /**
          * @see org.objectweb.asm.MethodVisitor#visitAnnotation(String, boolean)
          */
         public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
         {
            return new AnnotationInspector(descriptor, visible);
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitAnnotationDefault()
          */
         public AnnotationVisitor visitAnnotationDefault()
         {
            // Return a dummy annotation inspector initialized with
            // the VOID basic type
            return new AnnotationInspector("V", false);
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitAttribute(Attribute)
          */
         public void visitAttribute(Attribute attribute)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitCode()
          */
         public void visitCode()
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitEnd()
          */
         public void visitEnd()
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitFieldInsn(int, String, String, String)
          */
         public void visitFieldInsn(int opcode, String owner, String name, String descriptor)
         {
            if (owner.charAt(0) == '[')
            {
               Type type = Type.getType(owner);
               inspectType(type);
            }
            else
            {
               String typeName = refactor(owner.replace('/', '.'));
               checkReference(typeName);
            }

            inspectType(Type.getType(descriptor));
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitFrame(int, int, Object[], int, Object[])
          */
         public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
          */
         public void visitIincInsn(int var, int increment)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitInsn(int)
          */
         public void visitInsn(int opcode)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitIntInsn(int, int)
          */
         public void visitIntInsn(int opcode, int operand)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitJumpInsn(int, Label)
          */
         public void visitJumpInsn(int opcode, Label label)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitLabel(Label)
          */
         public void visitLabel(Label label)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(Object)
          */
         public void visitLdcInsn(Object constant)
         {
            if (constant != null && constant instanceof Type)
            {
               inspectType((Type)constant);
            }
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitLineNumber(int, Label)
          */
         public void visitLineNumber(int line, Label start)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitLocalVariable(String, String, String, Label, Label, int)
          */
         public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end,
            int index)
         {
            Type type = Type.getType(descriptor);

            if (type != null)
            {
               inspectType(type);
            }

            if (signature != null)
            {
               new SignatureReader(signature).acceptType(new SignatureInspector());
            }
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitLookupSwitchInsn(Label, int[], Label[])
          */
         public void visitLookupSwitchInsn(Label defaultHandler, int[] keys, Label[] labels)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
          */
         public void visitMaxs(int maxStack, int maxLocals)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int, String, String, String)
          */
         public void visitMethodInsn(int opcode, String owner, String name, String descriptor)
         {
            if (owner.charAt(0) == '[')
            {
               Type type = Type.getType(owner);
               inspectType(type);
            }
            else
            {
               String typeName = refactor(owner.replace('/', '.'));
               checkReference(typeName);
            }

            inspectType(Type.getReturnType(descriptor));

            Type[] types = Type.getArgumentTypes(descriptor);

            for (int i = 0; i < types.length; i++)
            {
               inspectType(types[i]);
            }
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitMultiANewArrayInsn(String, int)
          */
         public void visitMultiANewArrayInsn(String descriptor, int dimensions)
         {
            Type type = Type.getType(descriptor);
            inspectType(type);
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitParameterAnnotation(int, String, boolean)
          */
         public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible)
         {
            return new AnnotationInspector(descriptor, visible);
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitTableSwitchInsn(int, int, Label, Label[])
          */
         public void visitTableSwitchInsn(int min, int max, Label defaultHandler, Label[] labels)
         {
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitTryCatchBlock(Label, Label, Label, String)
          */
         public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
         {
            if (type != null)
            {
               String typeName = refactor(type.replace('/', '.'));
               checkReference(typeName);
            }
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitTypeInsn(int, String)
          */
         public void visitTypeInsn(int opcode, String descriptor)
         {
            if (descriptor.charAt(0) == '[')
            {
               Type type = Type.getType(descriptor);
               inspectType(type);
            }
            else
            {
               String typeName = refactor(descriptor.replace('/', '.'));
               checkReference(typeName);
            }
         }

         /**
          * @see org.objectweb.asm.MethodVisitor#visitVarInsn(int, int)
          */
         public void visitVarInsn(int opcode, int var)
         {
         }
      }

      /**
       * Inspects any kind of signatures contained in the parsed class file for outgoing references
       * 
       * @author Carsten Kaiser (carsten.kaiser@valtech.de)
       * @version 2.0
       */
      private class SignatureInspector implements SignatureVisitor
      {
         /**
          * Indicates whether the type of an implemented interface is currently parsed
          */
         private boolean m_parsingInterfaceType = false;

         /**
          * Indicates whether the superclass type is currently parsed
          */
         private boolean m_parsingSuperclassType = false;

         /**
          * Checks whether the type with the given name has to be added as reference, superclass or interface
          * 
          * @param name Type name
          */
         private void checkType(String name)
         {
            String typeName = name.replace('/', '.');

            if (m_parsingSuperclassType)
            {
               if (!typeName.equals(Object.class.getName()))
               {
                  m_typeDefinition.setSuperClassName(refactor(typeName));
               }

               m_parsingSuperclassType = false;
            }
            else if (m_parsingInterfaceType)
            {
               m_typeDefinition.addSuperInterfaceName(refactor(typeName));

               m_parsingInterfaceType = false;
            }

            checkReference(refactor(typeName));
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitArrayType()
          */
         public SignatureVisitor visitArrayType()
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitBaseType(char)
          */
         public void visitBaseType(char descriptor)
         {
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitClassBound()
          */
         public SignatureVisitor visitClassBound()
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitClassType(String)
          */
         public void visitClassType(String name)
         {
            checkType(name);
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitEnd()
          */
         public void visitEnd()
         {
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitExceptionType()
          */
         public SignatureVisitor visitExceptionType()
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitFormalTypeParameter(String)
          */
         public void visitFormalTypeParameter(String name)
         {
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitInnerClassType(String)
          */
         public void visitInnerClassType(String name)
         {
            // Skip situations wherein the inner class type has been already visited by parsing the
            // descriptor
            if (!m_typeName.endsWith(name))
               checkType(name);
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitInterface()
          */
         public SignatureVisitor visitInterface()
         {
            m_parsingInterfaceType = true;

            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitInterfaceBound()
          */
         public SignatureVisitor visitInterfaceBound()
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitParameterType()
          */
         public SignatureVisitor visitParameterType()
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitReturnType()
          */
         public SignatureVisitor visitReturnType()
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitSuperclass()
          */
         public SignatureVisitor visitSuperclass()
         {
            m_parsingSuperclassType = true;

            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitTypeArgument()
          */
         public void visitTypeArgument()
         {
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitTypeArgument(char)
          */
         public SignatureVisitor visitTypeArgument(char wildcard)
         {
            return this;
         }

         /**
          * @see org.objectweb.asm.signature.SignatureVisitor#visitTypeVariable(String)
          */
         public void visitTypeVariable(String name)
         {
         }
      }

      /**
       * Synthetic attribute identifier
       */
      private static final String SYNTHETIC = "Synthetic";

      /**
       * Inspects the given type recursively and add it as outgoing reference if necessary
       * 
       * @param type Referenced type
       */
      private void inspectType(Type type)
      {
         switch (type.getSort())
         {
            case Type.ARRAY:
               inspectType(type.getElementType());
               break;
            case Type.OBJECT:
               String typeName = refactor(type.getClassName());
               checkReference(typeName);
               break;
         }
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visit(int, int, String, String, String, String[])
       */
      public void visit(int version, int access, String name, String signature, String supername, String[] interfaces)
      {
         assert version >= 45;
         assert m_typeDefinition != null;
         assert name != null;

         boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
         boolean isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
         boolean isAccessible = (access & Opcodes.ACC_PUBLIC) != 0;
         boolean isFinal = (access & Opcodes.ACC_FINAL) != 0;

         if (isInterface && !isAbstract)
         {
            s_logger.warn(m_typeDefinition.getSource()
               + "' not VMSpec compliant - is an interface but not abstract - correcting abstract attribute");
            isAbstract = true;
         }

         m_typeDefinition.setType((isInterface ? TypeDefinition.INTERFACE_TYPE : TypeDefinition.CLASS_TYPE),
            isAbstract, isAccessible, !isFinal);

         m_typeName = name.replace('/', '.');
         m_unrefactoredTypeName = null;
         m_typeName = refactor(m_typeName);
         m_outerTypeName = getOuterTypeName(m_typeName);

         m_typeDefinition.setTypeName(m_typeName);

         if (m_unrefactoredTypeName != null)
         {
            m_typeDefinition.setUnrefactoredTypeName(m_unrefactoredTypeName);
         }

         if (signature == null)
         {
            if (supername != null)
            {
               String superTypeName = supername.replace('/', '.');

               if (!superTypeName.equals(Object.class.getName()))
               {
                  superTypeName = refactor(superTypeName);
                  m_typeDefinition.setSuperClassName(superTypeName);
                  checkReference(superTypeName);
               }
            }

            if (interfaces != null)
            {
               for (int i = 0; i < interfaces.length; i++)
               {
                  String superInterfaceName = refactor(interfaces[i].replace('/', '.'));
                  m_typeDefinition.addSuperInterfaceName(superInterfaceName);
                  checkReference(superInterfaceName);
               }
            }
         }
         else
         {
            new SignatureReader(signature).accept(new SignatureInspector());
         }
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitAnnotation(String, boolean)
       */
      public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
      {
         return new AnnotationInspector(descriptor, visible);
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitAttribute(Attribute)
       */
      public void visitAttribute(Attribute attribute)
      {

      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitEnd()
       */
      public void visitEnd()
      {
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitField(int, String, String, String, Object)
       */
      public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value)
      {
         assert name != null;

         if ((access & Opcodes.ACC_FINAL) != 0 && (access & Opcodes.ACC_PRIVATE) == 0)
         {
            m_typeDefinition.addInlineableField(name);
         }

         if (value != null && value instanceof Type)
         {
            inspectType((Type)value);
         }

         return new FieldInspector(descriptor, signature, (access & Opcodes.ACC_SYNTHETIC) != 0);
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitInnerClass(String, String, String, int)
       */
      public void visitInnerClass(String name, String outerName, String innerName, int access)
      {
         String typeName = refactor(name.replace('/', '.'));
         checkReference(typeName);
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitMethod(int, String, String, String, String[])
       */
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
      {

         inspectType(Type.getReturnType(descriptor));

         Type[] types = Type.getArgumentTypes(descriptor);

         for (int i = 0; i < types.length; i++)
         {
            inspectType(types[i]);
         }

         if (signature != null)
         {
            new SignatureReader(signature).accept(new SignatureInspector());
         }

         if (exceptions != null)
         {
            for (int i = 0; i < exceptions.length; i++)
            {
               String typeName = refactor(exceptions[i].replace('/', '.'));
               checkReference(typeName);
            }
         }

         return new MethodInspector();
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitOuterClass(String, String, String)
       */
      public void visitOuterClass(String owner, String name, String descriptor)
      {
         if (m_outerTypeName == null)
         {
            String typeName = refactor(owner.replace('/', '.'));
            checkReference(typeName);
         }
      }

      /**
       * @see org.objectweb.asm.ClassVisitor#visitSource(String, String)
       */
      public void visitSource(String source, String debug)
      {
         m_typeDefinition.setCompilationUnitName(source);
      }
   }

   /**
    * Nested class indicator
    */
   private final static char NESTED = '$';

   /**
    * Logging facility
    */
   private final static Logger s_logger = Logger.getLogger(ClassFileParser.class.getName());

   /**
    * Declared Java type name of the outer class
    */
   private String m_outerTypeName;

   /**
    * Refactorings to apply
    */
   private RefactoringIf[] m_refactorings;

   /**
    * Generated type definition
    */
   private TypeDefinition m_typeDefinition;

   /**
    * Declared Java type name
    */
   private String m_typeName;

   /**
    * Declared Java type name before refactoring
    */
   private String m_unrefactoredTypeName;

   private final AbstractTypeDefinitionProvider typeDefinitionProvider;

   public ClassFileParser(AbstractTypeDefinitionProvider typeDefinitionProvider)
   {
      this.typeDefinitionProvider = typeDefinitionProvider;
   }

   /**
    * Checks whether the given type name can be considered as an outgoing reference or has to be skipped or ignored
    * according to the current configuration settings
    * 
    * @param typeName Type name
    */
   private void checkReference(String typeName)
   {
      assert typeName != null;
      assert typeName.length() > 0;
      assert m_typeDefinition != null;

      if (!typeName.equals(m_typeName))
      {
         if (!typeName.startsWith(m_typeName + NESTED)
            && (m_outerTypeName == null || (!typeName.equals(m_outerTypeName) && !m_outerTypeName.startsWith(typeName
               + NESTED))))
         {
            if (!typeDefinitionProvider.skipType(typeName))
            {
               if (!typeDefinitionProvider.ignoreType(m_typeName, typeName))
               {
                  m_typeDefinition.addImportedTypeName(typeName);
               }
               else
               {
                  m_typeDefinition.addIgnoredTypeName(typeName);
               }
            }
            else
            {
               m_typeDefinition.addSkippedTypeName(typeName);
            }
         }
      }
   }

   /**
    * Attempts to retrieve the outer type name using the gvien fully qualified type name
    * 
    * @param fullyQualifiedTypeName Fully qualified type name
    * @return Outer type name
    */
   private String getOuterTypeName(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;

      String outerTypeName = null;
      int pos = fullyQualifiedTypeName.lastIndexOf(NESTED);
      if (pos != -1)
      {
         outerTypeName = fullyQualifiedTypeName.substring(0, pos);
      }

      return outerTypeName;
   }

   /**
    * Parses the java class provided as bytecode stream representing the specified source
    * 
    * @param inputStream Input stream Class bytecode
    * @param source Represented Source
    * @return Generated type definition
    * @throws IOException
    */
   TypeDefinition parse(InputStream inputStream, String source) throws IOException
   {
      assert inputStream != null;
      assert inputStream.available() > 0;

      m_typeDefinition = new TypeDefinition(source);
      m_typeName = null;
      m_unrefactoredTypeName = null;
      m_outerTypeName = null;

      try
      {
         ClassReader classReader = new ClassReader(inputStream);
         classReader.accept(new BytecodeInspector(), 0);
      }
      finally
      {
         inputStream.close();
      }

      assert m_typeDefinition != null;
      assert m_typeDefinition.isValid();

      return m_typeDefinition;
   }

   /**
    * Attempts to refactor the given type name according to the configured refactoring rules
    * 
    * @param typeName Type name to refactor
    * @return Refactored type name
    */
   public String refactor(String typeName)
   {
      assert typeName != null;
      assert typeName.length() > 0;

      if (m_refactorings != null)
      {
         for (int i = 0; i < m_refactorings.length; ++i)
         {
            RefactoringIf next = m_refactorings[i];

            if (next.match(typeName))
            {
               m_unrefactoredTypeName = typeName;
               return next.refactor(typeName);
            }
         }
      }

      return typeName;
   }

   /**
    * Assigns the list of refactorings supposed to be applid when generationg the type definition
    * 
    * @param refactorings Refactorings to apply
    */
   void refactorings(RefactoringIf[] refactorings)
   {
      assert AssertionUtility.checkArray(refactorings);
      m_refactorings = refactorings;
   }
}
