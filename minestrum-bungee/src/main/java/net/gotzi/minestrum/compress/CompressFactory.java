package net.gotzi.minestrum.compress;

import net.gotzi.minestrum.jni.NativeCode;
import net.gotzi.minestrum.jni.zlib.BungeeZlib;
import net.gotzi.minestrum.jni.zlib.JavaZlib;
import net.gotzi.minestrum.jni.zlib.NativeZlib;

public class CompressFactory
{

    public static final NativeCode<BungeeZlib> zlib = new NativeCode<>( "native-compress", JavaZlib::new, NativeZlib::new );
}
