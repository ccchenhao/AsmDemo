package com.a.plugin.privacycheck.asm;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.SIPUSH;

import com.a.plugin.privacycheck.PrivacyConfig;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.List;


public class PrivacyMethodVisitor extends LocalVariablesSorter {

    private final boolean isAllow = PrivacyConfig.isAllow;
    private String currentClass = "";
    private String currentMethod = "";

    public PrivacyMethodVisitor(final int access, final String descriptor, final MethodVisitor methodVisitor, String className, String methodName) {
        //注意三个参数的父类构造器会抛异常
        super(Opcodes.ASM7, access, descriptor, methodVisitor);
        currentMethod = methodName;
        currentClass = className;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }


    /**
     * hook访问字段
     *
     * @param opcode
     * @param owner
     * @param name
     * @param descriptor
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        String mLongName = (owner + "/" + name).replace("/", ".");
        if (PrivacyConfig.fieldHookValueSet.contains(mLongName)) {
            System.out.println("Asm-visitFieldInsn" + " opcode=" + opcode + " owner=" + owner + " name=" + name + " descriptor=" + descriptor);
//        if (mLongName.equals("com.example.testgradle.MainActivity.aa")){
            if (opcode == Opcodes.GETFIELD) {
                mv.visitInsn(Opcodes.POP);
            }
            mv.visitLdcInsn(mLongName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, PrivacyConfig.IgnoreClass_PrivacyProxy.replace(".", "/"), PrivacyConfig.Statement_Reject_SIMPLE_Field, "(Ljava/lang/String;)Ljava/lang/Object;", false);
            AsmUtils.typeCastUnBox(mv, Type.getType(descriptor).getInternalName());
            //调用PrivacyProxy的privacyLog
            mv.visitInsn(isAllow ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
            mv.visitLdcInsn(mLongName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, PrivacyConfig.ProxyClass, PrivacyConfig.Statement_Log_SIMPLE_Method, "(ZLjava/lang/String;)V", false);
        } else {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    /**
     * hook方法
     *
     * @param opcode
     * @param owner       所在class全限定名，比如com/example/testgradle/TestActivity
     * @param name        调用的方法名
     * @param descriptor  调用的方法的参数和返回值，比如findViewById是(I)Landroid/view/View
     * @param isInterface
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        String mLongName = (owner + "/" + name).replace("/", ".");
        String pageName = owner.replace("/", ".");
//        if (mLongName.equals("com.example.testgradle.MainActivity.test")) {
        if (PrivacyConfig.methodHookValueSet.contains(mLongName)) {
            //这样就不是通过代码动态控制，只能一开始通过配置修改，但避免了静态代码检测会出问题
            if (isAllow) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            } else {
                //本来想调用Desc的getParams来获取参数个数，可能是现在加载class会报错
                Type methodType = Type.getMethodType(descriptor);
                //下面调用方法需要原来方法的实参数组，如何获取参数的实参？
                //首先明白原来方法的参数现在都已进入操作数栈了，但这里无法直接获取
                // 这里有两种方案
                //1、既然参数入操作数栈了，调用参数个数相同的自定义方法，让操作数栈中的变量填入方法中
                //继承LocalVariablesSorter，通过newLocal设置一个新的局部变量，那这个局部变量当前的最大的下标，
                //这个局部变量类型应该是Object数组，但类型找不到，所以这里随便设置了一个普通类型，那数组存的不是这个下标，存的是加1的下标，类型是我们存进去再去决定。
                //等调用方法时再根据下标加载变量。但也存在不确定参数个数，虽然可以通过下标和参数个数可以加载全部变量再加到数组中，但参数类型要和存取指令一一匹配，比较麻烦
                //所以设置了几个方法去匹配参数个数，但基本类型需装箱，所以不行
                //之前的，基本类型需装箱，所以不行
//                int tempLocalIndex = newLocal(Type.LONG_TYPE) + 1;
//                Type[] paramsTypes = methodType.getArgumentTypes();
//                String[] paramMethod = AsmUtils.getPrivacyMethodParamsDes(paramsTypes.length);
//                //这个必须放最前面，因为要获取原来方法参数
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC, PrivacyConfig.ProxyClass, paramMethod[0], paramMethod[1], false);
//                mv.visitVarInsn(Opcodes.ASTORE, tempLocalIndex);

                //2,倒序取出所有参数保存在局部变量表中，然后保存在数组中
                List<String> parameterTypeList = AsmUtils.getParams(descriptor);
                int parameterCount = parameterTypeList.size();
                int tempLocalIndex = newLocal(Type.LONG_TYPE) + 1;
                int arrayIndex = tempLocalIndex + parameterCount;
                // 操作数栈的变量现在是倒着来的，第一个碰到，其实是最后一个参数，所以倒着遍历参数类型
                for (int i = parameterCount - 1; i >= 0; i--) {
                    int index = tempLocalIndex + i;
                    AsmUtils.typeCastBox(parameterTypeList.get(i), mv);
                    //保存到局部变量
                    mv.visitVarInsn(ASTORE, index);
                }
                AsmUtils.initArray("java/lang/Object", parameterCount, mv);
                //将上面保存到局部变量的方法的参数存到数组
                for (int i = 0; i < parameterCount; i++) {
                    //new完通过dup复制操作数栈的栈顶变量，所以多了个变量，所以最后ASTORE是保存new完的，
                    //不能先ASTORE，会出栈变量
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(SIPUSH, i);
                    mv.visitVarInsn(ALOAD, tempLocalIndex + i);  //获取对应的参数
                    mv.visitInsn(AASTORE);
                }
                mv.visitVarInsn(ASTORE, arrayIndex);
//                或者这样写
//                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
//                mv.visitVarInsn(ASTORE, arrayIndex);
//                for (int i = 0; i < parameterCount; i++) {
//                    mv.visitVarInsn(ALOAD, arrayIndex);
//                    mv.visitIntInsn(SIPUSH, i);
//                    mv.visitVarInsn(ALOAD, tempLocalIndex + i);  //获取对应的参数
//                    mv.visitInsn(AASTORE);
//                }
                //出栈调用该方法的实例
                if (opcode == Opcodes.INVOKEVIRTUAL) {
                    mv.visitInsn(Opcodes.POP);
                }
                //privacyRejectMethod的五个参数入操作数栈
                mv.visitLdcInsn(pageName);
                mv.visitLdcInsn(name);
                //这个参数没用到
                mv.visitInsn(Opcodes.ACONST_NULL);
                //调用Desc的getParams，将参数从descriptor转化成Class[]
                mv.visitLdcInsn(descriptor);
                //注意一定要用/分割包，这个类是抄javasist的
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, PrivacyConfig.DescClass, "getParams", "(Ljava/lang/String;)[Ljava/lang/Class;", false);
                //原来方法的参数数组
//                mv.visitVarInsn(Opcodes.ALOAD, tempLocalIndex);
                mv.visitVarInsn(Opcodes.ALOAD, arrayIndex);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, PrivacyConfig.ProxyClass, PrivacyConfig.Statement_Reject_SIMPLE_Method, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;", false);
                //privacyRejectMethod返回的是Object，得转成原方法返回类型
                Type returnType = methodType.getReturnType();
                //需要的是分号隔开的，而且没有L，比如java/lang/String，returnType.toString返回的是有L，returnType.getClassName是用点分割
                AsmUtils.typeCastUnBox(mv, returnType.getInternalName());
            }
            mv.visitInsn(isAllow ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
            mv.visitLdcInsn(mLongName);
            //调用PrivacyProxy的privacyLog
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, PrivacyConfig.ProxyClass, PrivacyConfig.Statement_Log_SIMPLE_Method, "(ZLjava/lang/String;)V", false);
            //这里不需要pop，返回是void
            //mv.visitInsn(Opcodes.POP);
            systemOutPrintln(mLongName, -1, currentMethod, currentClass);
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    private void systemOutPrintln(String mLongName, int lineNumber, String currentMethod, String currentClass) {
        String sb = "\n========" +
                "\ncall: " + mLongName +
                "\n  at: " + currentMethod + "(" + currentClass + ".java:" + lineNumber + ")";
        System.out.println(sb);
    }
}