package com.tezhm.wax.asm;

import com.tezhm.wax.internal.InjectionField;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

class MethodFactoryInjector extends MethodVisitor
{
    private final String cls;
    private final List<InjectionField> fields;

    /**
     *
     * @param api
     * @param mv
     * @param cls    "com/example/tez_desktop/myapplication/Injectable"
     * @param fields
     */
    public MethodFactoryInjector(int api, MethodVisitor mv, String cls, List<InjectionField> fields)
    {
        super(api, mv);
        this.cls = cls;
        this.fields = fields;
    }

    //This is the point we insert the code. Note that the instructions are added right after
    //the visitCode method of the super class. This ordering is very important.
    @Override
    public void visitCode()
    {
        super.visitCode();

        for (InjectionField field : this.fields)
        {
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    field.factory,              // Class containing static method
                    "make",                     // Method to statically call
                    "()L" + field.type + ";",   // Type returned by factory
                    false);                     // If it's an interface
            super.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    this.cls,                  // Parent class which holds field
                    field.name,                // Field name which will hold injected value
                    "L" + field.type + ";");   // Type to inject
        }
    }
}
