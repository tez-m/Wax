package asm1;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

class InsertInitCodeBeforeReturnMethodVisitor extends AdviceAdapter
{
    public InsertInitCodeBeforeReturnMethodVisitor(MethodVisitor mv, int access, String name, String desc)
    {
        super(Opcodes.ASM4, mv, access, name, desc);
    }

    @Override
    protected void onMethodExit(int opcode)
    {
        if (opcode != Opcodes.ATHROW)
        {
            String type = "com/test/other/Hello";
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "asm1/Factory",
                    "make",
                    "()L"+type+";",
                    false);
            super.visitFieldInsn(
                    Opcodes.PUTFIELD, "asm1/ClassModificationDemo", "hello1", "L" + type + ";");
        }
    }
}


public class ClassModifierDemo
{
    public static class ModifierMethodWriter extends MethodVisitor
    {
        private String methodName;

        public ModifierMethodWriter(int api, MethodVisitor mv, String methodName)
        {
            super(api, mv);
            this.methodName = methodName;
        }

        //This is the point we insert the code. Note that the instructions are added right after
        //the visitCode method of the super class. This ordering is very important.
        @Override
        public void visitCode()
        {
            super.visitCode();
            String type = "com/test/other/Hello";
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "asm1/Factory",
                    "make",
                    "()L"+type+";",
                    false);
            super.visitFieldInsn(
                    Opcodes.PUTFIELD, "asm1/ClassModificationDemo", "hello1", "L" + type + ";");
        }
    }

    //Our class modifier class visitor. It delegate all calls to the super class
    //Only makes sure that it returns our MethodVisitor for every method
    public static class ModifierClassWriter extends ClassVisitor
    {
        private int api;

        public ModifierClassWriter(int api, ClassWriter cv)
        {
            super(api, cv);
            this.api = api;
        }

        @Override
        public MethodVisitor visitMethod(
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

            if ("<init>".equals(name))
            {
                //InsertInitCodeBeforeReturnMethodVisitor mvw = new InsertInitCodeBeforeReturnMethodVisitor(mv, access, name, desc);
                ModifierMethodWriter mvw = new ModifierMethodWriter(api, mv, name);
                return mvw;
            }

            return mv;
        }
    }

    public static void main(String[] args) throws IOException
    {
        String classToEdit = "/asm1/ClassModificationDemo.class";
        InputStream in = ASMHelloWorld.class.getResourceAsStream(classToEdit);
        ClassReader classReader = new ClassReader(in);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //Wrap the ClassWriter with our custom ClassVisitor
        ModifierClassWriter mcw = new ModifierClassWriter(Opcodes.ASM4, cw);
        classReader.accept(mcw, 0);

        //Write the output to a class file
        File outputDir = new File("build/classes/main/asm1");
        outputDir.mkdirs();
        DataOutputStream dout = new DataOutputStream(
                new FileOutputStream(new File(outputDir, "ClassModificationDemo.class")));
        dout.write(cw.toByteArray());
    }

}
