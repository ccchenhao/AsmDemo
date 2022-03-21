package com.a.plugin.privacycheck.asm;

import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmUtils {

    public static String[] getPrivacyMethodParamsDes(int length) {
        String methodName = "privacyMethodParams" + length;
        String methodDes = "(";
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                methodDes += "Ljava/lang/Object;";
            }
        }
        methodDes = methodDes + ")" + "[Ljava/lang/Object;";
        return new String[]{methodName, methodDes};
    }

    public static List<String> getParams(String desc){
        List<String> parameterTypeList=new ArrayList<>();
        Matcher m = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})").matcher(desc.substring(0, desc.lastIndexOf(')') + 1));
        while (m.find()) {
            String block = m.group(1);
            parameterTypeList.add(block);
        }
        return parameterTypeList;
    }

    /**
     * 注意拆箱操作是发生在返回是基本类型比如int，需要先转成封装类型Integer，然后调用intValue返回int,
     * 直接返回I是错误的。而且返回类型不能是null,否则intValue空指针，这里没判断空指针情况
     * 如果是本身是Integer类型，那就直接转，不需要装箱和拆箱操作,而且返回值是null也可以强转
     *
     * @param mv
     * @param type
     */
    public static void typeCastUnBox(MethodVisitor mv, String type) {
        switch (type) {
            case "B":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
                break;
            case "Z":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
                break;
            case "I":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                break;
            case "J":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                break;
            case "D":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
                break;
            case "F":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
                break;
            case "C":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
                break;
            case "S":
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
                break;
            default:
                mv.visitTypeInsn(Opcodes.CHECKCAST, type);
                break;

        }
    }

    /**
     * 装箱操作
     * @param type
     * @param mv
     */
    public static void typeCastBox(String type, MethodVisitor mv){
        if ("Z".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if ("C".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if ("B".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if ("S".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if ("I".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if ("F".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if ("J".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if ("D".equals(type)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        }
    }


    public static void initArray(String type,int count, MethodVisitor mv) {
        if (count >= 4) {
            mv.visitVarInsn(Opcodes.BIPUSH, count);//初始化数组长度
        } else {
            switch (count) {
                case 1:
                    mv.visitInsn(Opcodes.ICONST_1);
                    break;
                case 2:
                    mv.visitInsn(Opcodes.ICONST_2);
                    break;
                case 3:
                    mv.visitInsn(Opcodes.ICONST_3);
                    break;
                default:
                    mv.visitInsn(Opcodes.ICONST_0);
            }
        }
        mv.visitTypeInsn(ANEWARRAY, Type.getObjectType(type).getInternalName());
    }
}
