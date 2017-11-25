package com.tezm.wax.asm;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MethodFactoryInjector extends MethodVisitor
{
    private final String cls;
    private final JSONArray fields;

    /**
     *
     * @param api
     * @param mv
     * @param cls    "com/example/tez_desktop/myapplication/Injectable"
     * @param fields
     */
    public MethodFactoryInjector(int api, MethodVisitor mv, String cls, JSONArray fields)
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

        for (Object entry : this.fields)
        {
            JSONObject field = (JSONObject) entry;

            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    (String) field.get("factory"),      // Class containing static method
                    "make",                             // Method to statically call
                    "()L" + field.get("type") + ";",    // Type returned by factory
                    false                               // If it's an interface
            );
            super.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    this.cls,                       // Parent class which holds field
                    (String) field.get("name"),     // Field name which will hold injected value
                    "L" + field.get("type") + ";"   // Type to inject
            );
        }
    }
}
