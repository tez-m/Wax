package com.tezhm.wax.asm;

import com.tezhm.wax.internal.InjectionField;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

//Our class modifier class visitor. It delegate all calls to the super class
//Only makes sure that it returns our MethodVisitor for every method
public class ClassFactoryInjector extends ClassVisitor
{
    private final int api;
    private final String cls;
    private final List<InjectionField> fields;

    public ClassFactoryInjector(int api, ClassWriter cv, String cls, List<InjectionField> fields)
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
                    this.fields);
            return mvw;
        }

        return mv;
    }
}
