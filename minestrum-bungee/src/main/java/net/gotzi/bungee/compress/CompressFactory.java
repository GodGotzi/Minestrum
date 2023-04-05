package net.gotzi.bungee.compress;

import net.gotzi.bungee.jni.NativeCode;
import net.gotzi.bungee.jni.zlib.BungeeZlib;
import net.gotzi.bungee.jni.zlib.JavaZlib;
import net.gotzi.bungee.jni.zlib.NativeZlib;

public class CompressFactory
{

    public static final NativeCode<BungeeZlib> zlib = new NativeCode<>( "native-compress", JavaZlib::new, NativeZlib::new );
}
