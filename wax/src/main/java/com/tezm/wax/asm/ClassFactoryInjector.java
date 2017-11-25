package com.tezm.wax.asm;

import org.json.simple.JSONArray;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

// Overrides MethodVisitor with ours
public class ClassFactoryInjector extends ClassVisitor
{
    private final int api;
    private final String cls;
    private final JSONArray fields;

    public ClassFactoryInjector(int api, ClassWriter cv, String cls, JSONArray fields)
    {
        super(api, cv);
        this.api = api;
        this.cls = cls;
        this.fields = fields;
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
            MethodFactoryInjector mvw = new MethodFactoryInjector(
                    this.api,
                    mv,
                    this.cls,
                    this.fields
            );
            return mvw;
        }

        return mv;
    }
}
